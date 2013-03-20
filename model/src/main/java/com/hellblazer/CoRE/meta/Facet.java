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
package com.hellblazer.CoRE.meta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.network.Networked;

/**
 * Represents an aspect of a networked existential ruleform instance. This facet
 * of the whole is determined by the authorized attributes that are classified
 * by the aspect
 * 
 * @author hhildebrand
 * 
 */
abstract public class Facet<RuleForm extends Networked<RuleForm, ?>, AttributeType extends AttributeValue<?>> {
    private final Aspect<RuleForm>              aspect;
    private final Map<Attribute, AttributeType> attributeMap;
    private final RuleForm                      instance;

    /**
     * @param aspect
     *            - the classifying aspect of this facet
     * @param instance
     *            - the underlying ruleform instance of this facet
     * @param attributeMap
     *            - the authorized attributeMap for this facet
     */
    protected Facet(Aspect<RuleForm> aspect, RuleForm instance,
                    List<AttributeType> attributes) {
        this.aspect = aspect;
        this.instance = instance;
        HashMap<Attribute, AttributeType> map = new HashMap<Attribute, AttributeType>(
                                                                                      attributes.size());
        for (AttributeType value : attributes) {
            map.put(value.getAttribute(), value);
        }
        attributeMap = Collections.unmodifiableMap(map);
    }

    /**
     * Convienence type erasure
     * 
     * @return the underlying instance as the type of RuleForm
     */
    public RuleForm asRuleform() {
        return instance;
    }

    /**
     * @return The aspect of this Facet
     */
    public Aspect<RuleForm> getAspect() {
        return aspect;
    }

    /**
     * 
     * @return the authorized attribute values for this facet
     */
    public Map<Attribute, AttributeType> getAttributes() {
        return attributeMap;
    }

    /**
     * Answer the attribute value for the attribute.
     * 
     * @param attribute
     * @return the attribute value for the attribute, or null if this is not an
     *         attribute of this facet
     */
    public AttributeType getValue(Attribute attribute) {
        return attributeMap.get(attribute);
    }

    @Override
    public String toString() {
        return String.format("Facet [%s %s %s]", instance.getName(),
                             aspect.getClassification().getName(),
                             aspect.getClassifier().getName());
    }
}
