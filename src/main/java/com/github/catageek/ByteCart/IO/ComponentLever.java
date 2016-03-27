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
package com.github.catageek.ByteCart.IO;

import com.github.catageek.ByteCart.HAL.RegistryInput;
import com.github.catageek.ByteCart.Util.MathUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;

/**
 * A lever
 */
public class ComponentLever extends AbstractComponent implements OutputPin, InputPin, RegistryInput {

    /**
     * @param block the block containing the component
     */
    public ComponentLever(Block block) {
        super(block);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.IO.OutputPin#write(boolean)
     */
    @Override
    public void write(boolean bit) {
        BlockState block = this.getBlock().getState();
        Lever lever = (Lever) block.getData();
        if (lever.isPowered() ^ bit) {
            lever.setPowered(bit);
            block.setData(lever);
            block.update(false, true);
            MathUtil.forceUpdate(this.getBlock().getRelative(lever.getAttachedFace()));
        }
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.IO.InputPin#read()
     */
    @Override
    public boolean read() {
        MaterialData md = this.getBlock().getState().getData();
        if (md instanceof Lever) {
            return ((Lever) md).isPowered();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.HAL.RegistryInput#getBit(int)
     */
    @Override
    public boolean getBit(int index) {
        return read();
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.HAL.Registry#getAmount()
     */
    @Override
    public int getAmount() {
        return (read() ? 15 : 0);
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCartRedux.HAL.Registry#length()
     */
    @Override
    public int length() {
        return 4;
    }


}
