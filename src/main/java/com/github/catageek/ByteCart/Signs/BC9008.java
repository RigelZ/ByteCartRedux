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
package com.github.catageek.ByteCart.Signs;


/**
 * A 8-station subnet bound
 */
final class BC9008 extends AbstractBC9000 implements Subnet, HasNetmask, Triggable {

    BC9008(org.bukkit.block.Block block, org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
        this.netmask = 5;
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCart.Signs.AbstractSimpleCrossroad#getName()
     */
    @Override
    public final String getName() {
        return "BC9008";
    }

    /* (non-Javadoc)
     * @see com.github.catageek.ByteCart.HAL.AbstractIC#getFriendlyName()
     */
    @Override
    public final String getFriendlyName() {
        return "8-station subnet";
    }
}