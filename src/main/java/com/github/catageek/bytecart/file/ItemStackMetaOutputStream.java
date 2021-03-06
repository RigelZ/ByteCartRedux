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
package com.github.catageek.bytecart.file;

import com.github.catageek.bytecart.ByteCartRedux;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.IOException;

/**
 * An outputstream for a book in an ItemStack. Write operations in the book update the ItemStack object 
 */
final class ItemStackMetaOutputStream extends ItemStackOutputStream {

    private final BookOutputStream outputStream;
    private boolean isClosed = false;


    /**
     * @param stack the stack containing the book
     * @param outputstream an output stream for the book
     */
    ItemStackMetaOutputStream(ItemStack stack, BookOutputStream outputstream) {
        super(stack);
        this.outputStream = outputstream;
    }

    @Override
    public void write(byte[] cbuf, int off, int len) throws IOException {
        if (isClosed) {
            throw new IOException("ItemStack has been already closed");
        }
        outputStream.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        if (isClosed) {
            throw new IOException("ItemStack has been already closed");
        }
        outputStream.flush();
        getItemStack().copyFrom(outputStream.getBook());
        if (ByteCartRedux.debug) {
            ByteCartRedux.myPlugin.getLog().info("Flushing meta to itemstack");
        }
    }

    @Override
    public void close() throws IOException {
        if (isClosed) {
            throw new IOException("ItemStack has been already closed");
        }
        if (ByteCartRedux.debug) {
            ByteCartRedux.myPlugin.getLog().info("Closing itemstack");
        }
        outputStream.close();
        isClosed = true;
    }

    @Override
    public void write(int b) throws IOException {
        if (isClosed) {
            throw new IOException("ItemStack has been already closed");
        }
        outputStream.write(b);
    }

    /**
     * Get the current buffer
     *
     * @return the buffer
     */
    final byte[] getBuffer() {
        return outputStream.getBuffer();
    }

    /**
     * Get the book
     *
     * @return the book
     */
    final ItemStack getBook() {
        return outputStream.getBook();
    }
}
