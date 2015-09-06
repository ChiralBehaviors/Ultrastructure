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
package com.chiralbehaviors.CoRE.phantasm.browser;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * @author hhildebrand
 * 
 */
public class TestServiceConfiguration extends Configuration {
    @JsonProperty
    private Boolean randomPort = false;

    @NotNull
    @JsonProperty
    private JpaConfiguration jpa = new JpaConfiguration();

    public JpaConfiguration getCrudServiceConfiguration() {
        return jpa;
    }

    public Boolean isRandomPort() {
        return randomPort;
    }
}
