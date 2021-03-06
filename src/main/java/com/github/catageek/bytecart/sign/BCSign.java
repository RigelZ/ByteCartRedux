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
import com.github.catageek.bytecart.hardware.IC;
import com.github.catageek.bytecart.updater.Wanderer.Level;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;

/**
 * A network sign should implement this
 */
public interface BCSign extends IC {

    /**
     * Get the hierarchical level of the IC
     *
     * @return the level
     */
    Level getLevel();

    /**
     * Get the vehicle that uses this IC
     *
     * @return the vehicle
     */
    Entity getVehicle();

    /**
     * Get the address stored in the IC
     *
     * @return the address
     */
    Address getSignAddress();

    /**
     * Get the address stored in the ticket
     *
     * @return the address
     */
    String getDestinationIP();

    /**
     * Get the center of the IC.
     *
     * @return the center
     */
    BlockSnapshot getCenter();
}
