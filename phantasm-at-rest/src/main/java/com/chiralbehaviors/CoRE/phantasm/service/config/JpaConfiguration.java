/** 
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.phantasm.service.config;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * @author hhildebrand
 * 
 */
public class JpaConfiguration extends Configuration {
    public static Map<String, String> getDefaultProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect",
                       "com.chiralbehaviors.CoRE.attribute.json.JsonPostgreSqlDialect");
        properties.put("hibernate.cache.use_second_level_cache", "true");
        properties.put("hibernate.c3p0.max_size", "20");
        properties.put("hibernate.c3p0.min_size", "5");
        properties.put(" hibernate.c3p0.timeout", "5000");
        properties.put("hibernate.c3p0.max_statements", "100");
        properties.put("hibernate.c3p0.idle_test_period", "300");
        properties.put("hibernate.c3p0.acquire_increment", "2");
        return properties;
    }

    @JsonProperty
    private boolean debug = false;

    @NotEmpty
    @JsonProperty
    private String persistenceUnit = "CoRE";

    @NotEmpty
    @JsonProperty
    private Map<String, String> properties = new HashMap<>();

    public String getPersistenceUnit() {
        return persistenceUnit;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public boolean isDebug() {
        return debug;
    }
}
