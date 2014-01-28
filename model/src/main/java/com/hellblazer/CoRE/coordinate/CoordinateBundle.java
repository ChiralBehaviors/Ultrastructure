/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
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
    private final Product                   finalContext;

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
