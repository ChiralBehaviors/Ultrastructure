/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

/**
 * @author hhildebrand
 *
 */
public class RuleformIdGenerator extends ObjectIdGenerator<String> {

    private static final long               serialVersionUID = 1L;
    private final Class<? extends Ruleform> scope;

    public RuleformIdGenerator() {
        this(Ruleform.class);
    }

    /**
     * @param scope
     */
    public RuleformIdGenerator(Class<? extends Ruleform> scope) {
        super();
        this.scope = scope;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.fasterxml.jackson.annotation.ObjectIdGenerator#canUseFor(com.fasterxml
     * .jackson.annotation.ObjectIdGenerator)
     */
    @Override
    public boolean canUseFor(ObjectIdGenerator<?> gen) {
        return gen.getClass() == getClass() && gen.getScope() == scope;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.fasterxml.jackson.annotation.ObjectIdGenerator#forScope(java.lang
     * .Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public ObjectIdGenerator<String> forScope(Class<?> scope) {
        return this.scope == scope ? this
                                  : new RuleformIdGenerator(
                                                            (Class<? extends Ruleform>) scope);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.fasterxml.jackson.annotation.ObjectIdGenerator#generateId(java.lang
     * .Object)
     */
    @Override
    public String generateId(Object forPojo) {
        return String.format("%s-%s", forPojo.getClass().getSimpleName(),
                             ((Ruleform) forPojo).getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.fasterxml.jackson.annotation.ObjectIdGenerator#getScope()
     */
    @Override
    public Class<?> getScope() {
        return scope;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.fasterxml.jackson.annotation.ObjectIdGenerator#key(java.lang.Object)
     */
    @Override
    public IdKey key(Object key) {
        return new IdKey(getClass(), scope, key);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.fasterxml.jackson.annotation.ObjectIdGenerator#newForSerialization
     * (java.lang.Object)
     */
    @Override
    public ObjectIdGenerator<String> newForSerialization(Object context) {
        return new RuleformIdGenerator(scope);
    }
}
