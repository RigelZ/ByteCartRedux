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
package com.github.catageek.bytecart.updater;

import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.file.BookFile;
import com.github.catageek.bytecart.updater.Wanderer.Level;
import com.github.catageek.bytecart.updater.Wanderer.Scope;
import com.github.catageek.bytecart.util.Messaging;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.Date;

public abstract class UpdaterContentFactory {

    public static UpdaterContent getUpdaterContent(CarriedInventory<?> inv)
            throws IOException, ClassNotFoundException {
        UpdaterContent rte = null;
        try (BookFile file = new BookFile(inv, 0, true)) {
            if (!file.isEmpty()) {
                ObjectInputStream ois = new ObjectInputStream(file.getInputStream());
                rte = (UpdaterContent) ois.readObject();
            }
        }
        rte.setInventory(inv);
        return rte;
    }

    public static void createRoutingTableExchange(CarriedInventory<?> inv, int region, Level level, Player player
            , boolean isfullreset, boolean isnew) throws IOException {
        WandererContentFactory.createWanderer(inv, level, "Updater", level.type);
        UpdaterContent rte;
        if (level.scope.equals(Scope.LOCAL)) {
            rte = new UpdaterContent(inv, level, region, player, isfullreset);
        } else {
            rte = new UpdaterContent(inv, level, region, player, isfullreset, isnew);
        }
        saveContent(rte);
    }

    public static void saveContent(UpdaterContent rte)
            throws IOException {

        // delete content if expired
        long creation = rte.getCreationTime();
        long expiration = rte.getExpirationTime();
        if (creation != expiration && Calendar.getInstance().getTimeInMillis() > expiration) {
            Messaging.sendSuccess(rte.getPlayer(), Text.of(String
                    .format(ByteCartRedux.rootNode.getNode("messages", "info", "updaterexpired").getString(),
                            (new Date(creation)).toString())));
            WandererContentFactory.deleteContent(rte.getInventory());
            return;
        }

        WandererContentFactory.saveContent(rte);
    }

}
