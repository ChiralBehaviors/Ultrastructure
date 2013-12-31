/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.meta.graph.impl;

import com.hellblazer.CoRE.meta.graph.Edge;
import com.hellblazer.CoRE.meta.graph.Node;

/**
 * @author hhildebrand
 * 
 */
public class EdgeImpl<T> implements Edge<T> {

    private final Node<?> parent;
    private final Node<?> child;
    private final T       model;

    public EdgeImpl(Node<?> parent, T model, Node<?> child) {
        super();
        this.parent = parent;
        this.model = model;
        this.child = child;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.graph.Edge#getChild()
     */
    @Override
    public Node<?> getChild() {
        return child;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.graph.Edge#getEdgeObject()
     */
    @Override
    public T getEdgeObject() {
        return model;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.graph.Edge#getParent()
     */
    @Override
    public Node<?> getParent() {
        return parent;
    }

}
