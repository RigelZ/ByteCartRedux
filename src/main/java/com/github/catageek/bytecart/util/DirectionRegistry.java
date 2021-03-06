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
package com.github.catageek.bytecart.util;

import com.github.catageek.bytecart.collection.Partitionable;
import com.github.catageek.bytecart.hardware.Registry;
import com.github.catageek.bytecart.hardware.RegistryOutput;
import com.github.catageek.bytecart.hardware.VirtualRegistry;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.spongepowered.api.util.Direction;

/**
 * A 4-bit registry with 1 cardinal direction per bit
 */
public final class DirectionRegistry implements Partitionable {

    /**
     *
     */
    private final VirtualRegistry registry;

    private DirectionRegistry() {
        this.registry = new VirtualRegistry(4);
    }

    /**
     * Build the registry with an initial value
     *
     * @param value initial value
     */
    public DirectionRegistry(int value) {
        this();
        this.registry.setAmount(value);
    }

    /**
     * Build the registry matching the blockface
     *
     * @param direction blockface
     */
    public DirectionRegistry(Direction direction) {
        this();
        this.setCardinal(direction, true);
    }

    /**
     * Set a cardinal direction bit individually
     *
     * @param direction the cardinal direction
     * @param value true or false
     */
    private void setCardinal(Direction direction, boolean value) {

        switch (direction) {
            case EAST:
                this.registry.setBit(0, value);
                break;
            case NORTH:
                this.registry.setBit(1, value);
                break;
            case WEST:
                this.registry.setBit(2, value);
                break;
            case SOUTH:
                this.registry.setBit(3, value);
            default:
                break;
        }

    }

    // TODO: wtf, seriously?
    public final Direction ToString() {
        switch (this.getAmount()) {
            case 1:
                return Direction.SOUTH;
            case 2:
                return Direction.WEST;
            case 4:
                return Direction.NORTH;
            case 8:
                return Direction.EAST;
        }
        return Direction.NONE;
    }

    /**
     * Return the cardinal direction represented by this registry
     *
     * @return the direction, or self if there is no direction, or several directions are mixed
     */
    public final Direction getBlockFace() {
        switch (this.getAmount()) {
            case 1:
                return Direction.SOUTH;
            case 2:
                return Direction.WEST;
            case 4:
                return Direction.NORTH;
            case 8:
                return Direction.EAST;
        }
        return Direction.NONE;
    }

    @Override
    public final int getAmount() {
        return this.registry.getValue();
    }

    /**
     * Get the value of the registry
     *
     * @param value the value
     */
    public final void setAmount(int value) {
        this.registry.setAmount(value);
    }

    /**
     * Return the registry
     *
     * @return the registry
     */
    public final Registry getRegistry() {
        RegistryOutput reg = new VirtualRegistry(4);
        reg.setAmount(this.getAmount());
        return reg;
    }

    @Override
    public int hashCode() {
        return getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof DirectionRegistry)) {
            return false;
        }

        DirectionRegistry rhs = (DirectionRegistry) o;

        return new EqualsBuilder().append(getAmount(), rhs.getAmount()).isEquals();
    }
}
