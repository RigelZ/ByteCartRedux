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
package com.github.catageek.ByteCart.AddressLayer;

import com.github.catageek.ByteCart.AddressLayer.AddressBook.Parameter;
import com.github.catageek.ByteCart.FileStorage.BookFile;
import com.github.catageek.ByteCart.FileStorage.BookProperties.Conf;
import org.bukkit.inventory.Inventory;

/**
 * Factory class to create a return address from various supports
 */
public final class ReturnAddressFactory {

    /**
     * Creates a return address from a ticket
     *
     * @param inv the inventory containing the ticket
     * @return the return address
     */
    @SuppressWarnings("unchecked")
    public final static <T extends Address> T getAddress(Inventory inv) {
        int slot;
        if ((slot = Ticket.getTicketslot(inv)) != -1) {
            return (T) new ReturnAddressBook(new Ticket(new BookFile(inv, slot, false), Conf.NETWORK), Parameter.RETURN);
        }
        return null;
    }
}
