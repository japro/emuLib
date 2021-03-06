/*
 * KISS, YAGNI, DRY
 *
 * (c) Copyright 2006-2017, Peter Jakubčo
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package emulib.plugins.cpu;

/**
 * Simple implementation of DebugColumn.
 */
public abstract class AbstractDebugColumn implements DebugColumn {
    private final String title;
    private final Class<?> classType;
    private final boolean editable;

    /**
     * Creates a new instance of AbstractDebugColumn class. This class represents
     * a column that will be shown in debug table in emuStudio.
     *
     * @param title
     *   Title of the column
     * @param classType
     *   Type of the column
     * @param editable
     *   Whether this column is editable
     */
    public AbstractDebugColumn(String title, Class<?> classType, boolean editable) {
        this.title = title;
        this.classType = classType;
        this.editable = editable;
    }

    /**
     * Return the type of the column.
     *
     * @return java class
     */
    @Override
    public Class<?> getClassType() {
        return this.classType;
    }

    /**
     * Get title of the column.
     *
     * @return the column title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Returns true if the column is editable.
     * @return true if the column is editable, false otherwise
     */
    @Override
    public boolean isEditable() {
        return editable;
    }

}
