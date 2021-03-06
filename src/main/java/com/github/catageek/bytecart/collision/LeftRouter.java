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
package com.github.catageek.bytecart.collision;

import com.github.catageek.bytecart.util.MathUtil;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.EnumSet;
import java.util.Set;

/**
 * A router where a cart turns left
 */
public final class LeftRouter extends AbstractRouter implements Router {

    public LeftRouter(Direction from, Location<World> loc) {
        super(from, loc);
        fromTo.put(Side.BACK, Side.LEFT);

        Set<Side> left = EnumSet.of(Side.LEFT, Side.STRAIGHT, Side.RIGHT);
        possibility.put(Side.LEFT, left);

        Set<Side> straight = EnumSet.of(Side.STRAIGHT, Side.LEFT, Side.BACK);
        possibility.put(Side.STRAIGHT, straight);

        Set<Side> right = EnumSet.of(Side.LEFT, Side.BACK);
        possibility.put(Side.RIGHT, right);

        setSecondpos(Integer.parseInt("01000000", 2));
        setPosmask(Integer.parseInt("11100000", 2));

    }

    @Override
    public Direction getTo() {
        return MathUtil.clockwise(this.getFrom());
    }

}
