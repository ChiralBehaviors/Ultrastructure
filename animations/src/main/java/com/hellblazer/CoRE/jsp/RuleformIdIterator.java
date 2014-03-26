/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.hellblazer.CoRE.jsp;

import java.util.Iterator;

import com.chiralbehaviors.CoRE.Ruleform;

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
