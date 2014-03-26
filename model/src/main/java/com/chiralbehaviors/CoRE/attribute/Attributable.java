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

package com.chiralbehaviors.CoRE.attribute;

import java.util.Set;

import com.chiralbehaviors.CoRE.Ruleform;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The interface for data types that have attributes
 * 
 * @author hhildebrand
 * 
 */
public interface Attributable<AttributeType extends Ruleform> {
    @JsonIgnore
    Set<AttributeType> getAttributes();

    @JsonIgnore
    Class<AttributeType> getAttributeType();

    @JsonIgnore
    void setAttributes(Set<AttributeType> attributes);
}
