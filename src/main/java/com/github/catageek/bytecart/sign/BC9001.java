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
import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.collision.IntersectionSide;
import com.github.catageek.bytecart.collision.IntersectionSide.Side;
import com.github.catageek.bytecart.event.custom.SignPostStationEvent;
import com.github.catageek.bytecart.event.custom.SignPreStationEvent;
import com.github.catageek.bytecart.hardware.PinRegistry;
import com.github.catageek.bytecart.io.InputFactory;
import com.github.catageek.bytecart.io.InputPin;
import com.github.catageek.bytecart.util.MathUtil;
import com.github.catageek.bytecart.updater.Wanderer;
import com.github.catageek.bytecart.updater.WandererContentFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.io.IOException;


/**
 * A station sign
 */
public final class BC9001 extends AbstractBC9000 implements Station, Powerable, Triggerable {


    BC9001(org.bukkit.block.Block block, org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
        this.netmask = 8;
    }

    @Override
    public void trigger() {
        try {

            Address sign = AddressFactory.getAddress(this.getBlock(), 3);

            this.addIO();

            // input[6] = redstone for "full station" signal

            InputPin[] wire = new InputPin[2];

            // Right
            wire[0] = InputFactory
                    .getInput(this.getBlock().getRelative(BlockFace.UP).getRelative(getCardinal(), 2).getRelative(MathUtil.clockwise(getCardinal())));
            // left
            wire[1] = InputFactory.getInput(
                    this.getBlock().getRelative(BlockFace.UP).getRelative(getCardinal(), 2).getRelative(MathUtil.anticlockwise(getCardinal())));

            // InputRegistry[0] = start/stop command
            this.addInputRegistry(new PinRegistry<InputPin>(wire));

            triggerBC7003();

            if (!WandererContentFactory.isWanderer(getInventory())) {

                // if this is a cart in a train
                if (this.wasTrain(this.getLocation())) {
                    ByteCartRedux.myPlugin.getIsTrainManager().getMap().reset(getLocation());
                    return;
                }

                // if this is the first car of a train
                // we keep the state during 2 s
                if (AbstractTriggeredSign.isTrain(getDestinationAddress())) {
                    this.setWasTrain(this.getLocation(), true);
                }

                this.route();

                if (this.isAddressMatching() && this.getName().equals("BC9001") && this.getInventory().getHolder() instanceof Player) {
                    ((Player) this.getInventory().getHolder()).sendMessage(
                            ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.GREEN + ByteCartRedux.rootNode.getNode("Info", "Destination").getString() +
                                    " "
                                    + this.getFriendlyName() + " (" + sign + ")");

                }
                return;
            }

            // it's an wanderer
            Wanderer wanderer;
            try {
                wanderer = ByteCartRedux.myPlugin.getWandererManager().getFactory(this.getInventory()).getWanderer(this, this.getInventory());
                // here we perform wanderer action
                wanderer.doAction(IntersectionSide.Side.LEVER_OFF);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // routing
            this.getOutput(0).setAmount(0); // unpower levers


        } catch (ClassCastException e) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : " + e.toString());
            }

            // Not the good blocks to build the signs
            return;
        } catch (NullPointerException e) {
            if (ByteCartRedux.debug) {
                ByteCartRedux.log.info("ByteCartRedux : " + e.toString());
            }
            e.printStackTrace();

            // there was no inventory in the cart
            return;
        }


    }

    @Override
    public void power() {
        this.powerBC7003();
    }


    /**
     * Manage the red light signal when triggered
     *
     */
    protected void triggerBC7003() {
        (new BC7003(this.getBlock())).trigger();
    }

    /**
     * Manage the red light signal when powered
     *
     */
    protected void powerBC7003() {
        (new BC7003(this.getBlock())).power();
    }


    @Override
    protected Side route() {
        SignPreStationEvent event;
        SignPostStationEvent event1;
        // test if every destination field matches sign field
        if (this.isAddressMatching() && this.getInput(6).getAmount() == 0) {
            event = new SignPreStationEvent(this, Side.LEVER_ON); // power levers if matching
        } else {
            event = new SignPreStationEvent(this, Side.LEVER_OFF); // unpower levers if not matching
        }
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.getSide().equals(Side.LEVER_ON) && this.getInput(6).getAmount() == 0) {
            this.getOutput(0).setAmount(3); // power levers if matching
            event1 = new SignPostStationEvent(this, Side.LEVER_ON);
        } else {
            this.getOutput(0).setAmount(0); // unpower levers if not matching
            event1 = new SignPostStationEvent(this, Side.LEVER_ON);
        }
        Bukkit.getServer().getPluginManager().callEvent(event1);
        return null;
    }

    @Override
    public final String getName() {
        return "BC9001";
    }

    public final String getStationName() {
        return ((Sign) this.getBlock().getState()).getLine(2);
    }
}
