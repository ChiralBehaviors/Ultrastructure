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
package com.hellblazer.CoRE.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;

/**
 * Represents an aspect of a networked existential ruleform instance. This facet
 * of the whole is determined by the authorized attributes that are classified
 * by the aspect
 * 
 * @author hhildebrand
 * 
 */
abstract public class Facet<RuleForm extends ExistentialRuleform<RuleForm, ?>, AttributeType extends AttributeValue<RuleForm>> {
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
