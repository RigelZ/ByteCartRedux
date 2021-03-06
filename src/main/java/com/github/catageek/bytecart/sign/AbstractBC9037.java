/**
 * ByteCart, ByteCart Redux
 * Copyright (C) Catageek
 * Copyright (C) phroa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.collision.IntersectionSide;
import com.github.catageek.bytecart.hardware.RegistryBoth;
import com.github.catageek.bytecart.hardware.RegistryInput;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;


/**
 * Match IP ranges.
 *
 * Example sign content:
 * 1. Empty
 * 2. [BCxxxx]
 * 3. AA.BB.CC
 * 4. XX.YY.ZZ
 *
 * Line 3 and 4 name the start and end of the range respectively.
 * There are two possible implementations: normal and negated.
 *
 * - Example on-state with normal implementation and configuration from above:
 *   onState <=> AA.BB.CC <= IP <= XX.YY.ZZ
 *
 * - Example on-state with negated implementation and configuration from above:
 *   onState <=> !(AA.BB.CC <= IP <= XX.YY.ZZ)
 */
abstract class AbstractBC9037 extends AbstractSimpleCrossroad implements Triggerable {

    AbstractBC9037(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    /**
     *
     * @return True if the sign uses the negated result of @{@link #isAddressMatching()}.
     */
    protected abstract boolean negated();

    @Override
    protected void addIO() {
        super.addIO();
        // add input [0] to [2] from vehicle
        addIOInv();
        // add input [3], [4] and [5] from 4th line
        this.addAddressAsInputs(AddressFactory.getAddress(getBlock(), 3));

        // add input [6], [7] and [8] from 3th line
        this.addAddressAsInputs(AddressFactory.getAddress(getBlock(), 2));
    }

    /**
     * Utility method to check whether a integer is between lower bound l and upper bound u.
     */
    private boolean in(int l, int v, int u) {
        return l <= v && v <= u;
    }

    /**
     * Check if the vehicle IP is in the configured range.
     *
     * The return value depends on the return value of @{@link #negated()}.
     * The result is negated if said method returns true.
     *
     */
    private boolean isAddressMatching() {
        try {
            int startRegion = getInput(6).getValue();
            int region = getInput(0).getValue();
            int endRegion = getInput(3).getValue();

            int startTrack = getInput(7).getValue();
            int track = getInput(1).getValue();
            int endTrack = getInput(4).getValue();

            int startStation = getInput(8).getValue();
            int station = getInput(2).getValue();
            int endStation = getInput(5).getValue();

            boolean value = in(startRegion, region, endRegion) &&
                    in(startTrack, track, endTrack) &&
                    in(startStation, station, endStation);

            if (negated()) {
                return !value;
            }
            return value;
        } catch (NullPointerException e) {
            // There is no address on sign
        }
        return false;
    }

    @Override
    protected IntersectionSide.Side route() {
        if (!WandererContentFactory.isWanderer(getInventory()) && this.isAddressMatching()) {
            return IntersectionSide.Side.LEVER_ON;
        }
        return IntersectionSide.Side.LEVER_OFF;
    }

    private void addAddressAsInputs(Address addr) {
        if (addr.isValid()) {
            RegistryInput region = addr.getRegion();
            this.addInputRegistry(region);

            RegistryInput track = addr.getTrack();
            this.addInputRegistry(track);

            RegistryBoth station = addr.getStation();
            this.addInputRegistry(station);
        }
    }
}
