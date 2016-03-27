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
package com.github.catageek.ByteCart.EventManagement;

import com.github.catageek.ByteCart.ByteCart;
import com.github.catageek.ByteCart.Event.UpdaterRemoveEvent;
import com.github.catageek.ByteCart.Storage.ExpirableSet;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.Iterator;

/**
 * A set for integers with a timeout of 1h
 */
final class UpdaterSet {

    private final ExpirableSet<Integer> updateSet;

    // entries stay for 1h
    UpdaterSet() {
        long duration = ByteCart.myPlugin.getConfig().getInt("updater.timeout", 60) * 1200;
        updateSet = new ExpirableSet<Integer>(duration, false, "UpdaterRoutes");
    }

    ExpirableSet<Integer> getMap() {
        return updateSet;
    }

    boolean isUpdater(Integer id) {
        return updateSet.contains(id);
    }

    void addUpdater(int id) {
        this.updateSet.add(id);
    }

    void clear() {
        Iterator<Integer> it = updateSet.getIterator();
        while (it.hasNext()) {
            Bukkit.getServer().getPluginManager().callEvent((Event) new UpdaterRemoveEvent(it.next()));
        }
        updateSet.clear();
    }
}