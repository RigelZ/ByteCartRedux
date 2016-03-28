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
/**
 *
 */
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.AddressFactory;
import com.github.catageek.bytecart.address.AddressString;
import com.github.catageek.bytecart.address.TicketFactory;
import com.github.catageek.bytecart.hardware.AbstractIC;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.io.InputPinFactory;
import com.github.catageek.bytecart.io.InputPin;
import com.github.catageek.bytecart.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.io.IOException;

/**
 * A cart spawner
 */
final class BC7004 extends AbstractIC implements Powerable {

    private final String type;
    private final String address;

    public BC7004(org.bukkit.block.Block block, String type, String address) {
        super(block);
        this.type = type;
        this.address = address;
    }

    @Override
    public void power() throws ClassNotFoundException, IOException {
        org.bukkit.block.Block block = this.getBlock();
        // check if we are really powered
        if (!block.getRelative(MathUtil.clockwise(getCardinal())).isBlockPowered() && !block.getRelative(MathUtil.anticlockwise(getCardinal()))
                .isBlockPowered()) {
            return;
        }

        // add input command = redstone

        InputPin[] wire = new InputPin[2];

        // Right
        wire[0] = InputPinFactory.getInput(block.getRelative(BlockFace.UP).getRelative(MathUtil.clockwise(getCardinal())));
        // left
        wire[1] = InputPinFactory.getInput(block.getRelative(BlockFace.UP).getRelative(MathUtil.anticlockwise(getCardinal())));

        // InputRegistry[0] = wire
        this.addInputRegistry(new PinRegistry<InputPin>(wire));

        // if wire is on, we spawn a cart
        if (this.getInput(0).getValue() != 0) {
            org.bukkit.block.Block rail = block.getRelative(BlockFace.UP, 2);
            org.bukkit.Location loc = rail.getLocation();
            // check that it is a track, and no cart is there
            if (rail.getType().equals(Material.RAILS) && MathUtil.getVehicleByLocation(loc) == null) {
                Entity entity = block.getWorld().spawnEntity(loc, getType());
                // put a ticket in the inventory if necessary
                if (entity instanceof InventoryHolder && AddressString.isResolvableAddressOrName(address)) {
                    Inventory inv = ((InventoryHolder) entity).getInventory();
                    TicketFactory.getOrCreateTicket(inv);
                    Address dst = AddressFactory.getAddress(inv);
                    dst.setAddress(address);
                    dst.finalizeAddress();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "BC7004";
    }

    @Override
    public String getFriendlyName() {
        return "Cart spawner";
    }

    /**
     * Get the type of cart to spawn
     *
     * @return the type
     */
    private EntityType getType() {
        if (type.equalsIgnoreCase("storage")) {
            return EntityType.MINECART_CHEST;
        }
        if (type.equalsIgnoreCase("furnace")) {
            return EntityType.MINECART_FURNACE;
        }
        if (type.equalsIgnoreCase("hopper")) {
            return EntityType.MINECART_HOPPER;
        }

        return EntityType.MINECART;
    }
}
