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

package com.hellblazer.CoRE.jsp;

import java.util.Iterator;

import com.hellblazer.CoRE.Ruleform;

/**
 * A simple class to allow pl/java to return ids of a list of ruleforms
 * 
 * @author hhildebrand
 * 
 */
public class RuleformIdIterator implements Iterator<Long> {

    private final Iterator<? extends Ruleform> list;

    /**
     * @param list
     */
    public RuleformIdIterator(Iterator<? extends Ruleform> list) {
        this.list = list;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return list.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public Long next() {
        Ruleform next = list.next();
        return next.getId();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        list.remove();
    }

}
