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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * A property file with a book as container
 */
public final class BookProperties implements Closeable, Flushable {

    private final Properties Properties = new Properties();
    private final Conf PageNumber;
    private final BCFile file;
    private boolean isClosed = false;

    /**
     * @param file the file
     * @param page the page
     */
    public BookProperties(BCFile file, Conf page) {
        super();
        this.file = file;
        try {
            Properties.load(file.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PageNumber = page;
    }

    /**
     * Set a property
     *
     * @param key the key
     * @param value the value
     */
    public void setProperty(String key, String value) {
        if (ByteCartRedux.debug) {
            ByteCartRedux.myPlugin.getLog().info("BookProperties : setting key " + key + " to " + value);
        }
        Properties.setProperty(key, value);
    }

    /**
     * Save the content of the property buffer to the book
     *
     * @throws IOException
     */
    private void save() throws IOException {
        file.clear();
        OutputStream os = file.getOutputStream();
        Properties.store(os, PageNumber.name);
    }

    /**
     * Removes a property
     *
     * @param key the key to remove
     */
    public void clearProperty(String key) {
        if (ByteCartRedux.debug) {
            ByteCartRedux.myPlugin.getLog().info("BookProperties : clearing key " + key);
        }
        Properties.remove(key);
    }

    /**
     * Get the property value
     *
     * @param key the property key
     * @return the value
     */
    public String getString(String key) {
        return Properties.getProperty(key);
    }

    /**
     * Get the property value or a default value
     *
     * @param key the property key
     * @param defaultvalue the default value
     * @return the value, or the default value
     */
    public String getString(String key, String defaultvalue) {
        return Properties.getProperty(key, defaultvalue);
    }

    /**
     * Get a property value as an integer
     *
     * @param key the property key
     * @return the value
     */
    public int getInt(String key) {
        return Integer.parseInt(Properties.getProperty(key));
    }

    /**
     * Get a property value as an integer or a default value
     *
     * @param key the property key
     * @param defaultvalue the default value
     * @return the value
     */
    public int getInt(String key, int defaultvalue) {
        if (ByteCartRedux.debug) {
            ByteCartRedux.myPlugin.getLog().info("property string : " + Properties.getProperty(key, "" + defaultvalue));
        }
        return Integer.parseInt(Properties.getProperty(key, "" + defaultvalue));

    }

    @Override
    public void flush() throws IOException {
        if (isClosed) {
            throw new IOException("Property file has been already closed");
        }
        save();
    }

    @Override
    public void close() throws IOException {
        if (isClosed) {
            throw new IOException("Property file has been already closed");
        }
        file.close();
        isClosed = true;
    }

    /**
     * Get the container that contains this property file
     *
     * @return the container file
     */
    public final BCFile getFile() {
        return file;
    }

    /**
     * The page names
     */
    public enum Conf {
        NETWORK("Network"),
        BILLING("Billing"),
        ACCESS("Access"),
        PROTECTION("Protection"),
        HISTORY("History");

        private final String name;

        Conf(String name) {
            this.name = name;
        }
    }


}
