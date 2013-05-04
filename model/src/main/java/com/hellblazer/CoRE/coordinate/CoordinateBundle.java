/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.coordinate;

import java.util.List;

import com.hellblazer.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */

public class CoordinateBundle {

    /** CoordinateAttributes that collectively denote some location. */
    private final List<CoordinateAttribute> coordinates;

    /**
     * The context in which the location denoted by <code>coordinates</code>
     * should be resolved.
     */
    private final Product                    finalContext;

    /**
     * Bundles the List of coordinates with the given Product as context. All
     * CoordinateAttributes should be interpreted relative to
     * <code>context</code>.
     * 
     * @param coordinates
     *            CoordinateAttributes that collectively denote some location
     * @param context
     *            The context in which the location denoted by
     *            <code>coordinates</code> should be resolved.
     */
    public CoordinateBundle(List<CoordinateAttribute> coordinates,
                            Product context) {
        this.coordinates = coordinates;
        finalContext = context;
    }

    public List<CoordinateAttribute> getCoordinateAttributes() {
        return coordinates;
    }

    public Product getFinalContext() {
        return finalContext;
    }
}
