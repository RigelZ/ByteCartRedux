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
package com.github.catageek.bytecart.routing;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.updater.UpdaterContent;
import com.github.catageek.bytecart.util.DirectionRegistry;
import org.spongepowered.api.util.Direction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class for routing tables 
 */
abstract class AbstractRoutingTable {


    /**
     * Performs the IGP protocol to update the routing table
     *
     * @param neighbour the IGP packet received
     * @param from the direction from where we received it
     */
    public void update(UpdaterContent neighbour, DirectionRegistry from) {
        // Djikstra algorithm
        // search for better routes in the received ones
        int interfacedelay = neighbour.getInterfaceDelay();

        for (Map.Entry<Integer, Metric> entry : neighbour.getEntrySet()) {

            int ring = entry.getKey();
            Metric metric = entry.getValue();
            int computedmetric = metric.value();
            if (interfacedelay > 0) {
                computedmetric += interfacedelay;
            }
            int routermetric = this.getMetric(ring, from);
            boolean directlyconnected = (this.getMinMetric(ring) == 0);

            if (!directlyconnected && (routermetric > computedmetric || routermetric < 0)) {
                this.setEntry(ring, from, new Metric(computedmetric));
                if (ByteCartRedux.debug) {
                    ByteCartRedux.myPlugin.getLog()
                            .info("update : ring = " + ring + ", metric = " + computedmetric + ", direction " + from.ToString());
                }
                neighbour.updateTimestamp();
            }
        }
        // search for routes that are no more announced and not directly connected
        // to remove them
        for (int i : this.getNotDirectlyConnectedList(from)) {
            Integer route;
            if (!neighbour.hasRouteTo(route = i)) {
                this.removeEntry(route, from);
                if (ByteCartRedux.debug) {
                    ByteCartRedux.myPlugin.getLog().info("Remove : ring = " + route + " from " + from.ToString());
                }
                neighbour.updateTimestamp();
            }
        }
    }

    /**
     * Tells if a track is directly connected to a router at a specific direction
     *
     * @param ring the track number
     * @param direction the direction
     * @return true if the track is directly connected at this direction
     */
    public final boolean isDirectlyConnected(int ring, DirectionRegistry direction) {
        return this.getDirection(ring) != null && this.getMetric(ring, direction) == 0;
    }


    /**
     * Get the track number at the specific direction
     *
     * @param direction the direction
     * @return the track number
     */
    public final int getDirectlyConnected(DirectionRegistry direction) {
        Set<Integer> rings = getDirectlyConnectedList(direction);
        return rings.size() == 1 ? rings.iterator().next() : -1;
    }

    /**
     * Get a direction that has not been configured, or null if all directions are configured
     *
     * @return the direction
     */
    public final Direction getFirstUnknown() {
        return Arrays.asList(Direction.values()).stream()
                .filter(Direction::isCardinal)
                .filter(direction -> this.getDirectlyConnectedList(new DirectionRegistry(direction)).isEmpty())
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a list of tracks that have records with a metric 0 and at the specific direction
     *
     * @param direction the direction
     * @return a list of track numbers
     */
    protected abstract Set<Integer> getDirectlyConnectedList(DirectionRegistry direction);

    /**
     * Get the metric associated with this entry and this direction
     *
     * @param entry the track number
     * @param direction the direction
     * @return the metric
     */
    protected abstract int getMetric(int entry, DirectionRegistry direction);

    /**
     * Get the minimum metric for a specific entry
     *
     * @param entry the track number
     * @return the minimum metric recorded, or -1
     */
    protected abstract int getMinMetric(int entry);

    /**
     * Store a line in the routing table
     *
     * @param entry the track number
     * @param direction the direction to associate
     * @param metric the metric to associate
     */
    protected abstract void setEntry(int entry, DirectionRegistry direction, Metric metric);

    /**
     * Tells if there is no record for an entry
     *
     * @param entry the track number
     * @return true if there is no record
     */
    abstract public boolean isEmpty(int entry);

    /**
     * Get an iterator on the entries
     *
     * @return an iterator
     */
    abstract protected <T extends RouteValue> Iterator<T> getOrderedRouteNumbers();

    /**
     * Get a set of track numbers that are seen in a direction, but not directly connected
     *
     * @param direction the direction
     * @return a set of track numbers
     */
    abstract protected Set<Integer> getNotDirectlyConnectedList(DirectionRegistry direction);

    /**
     * Remove a line from the routing table
     *
     * @param entry the track number
     * @param from the direction to remove
     */
    protected abstract void removeEntry(int entry, DirectionRegistry from);

    /**
     * Return the best direction matching the entry
     *
     * @param entry the track number
     * @return the direction
     */
    protected abstract DirectionRegistry getDirection(int entry);
}
