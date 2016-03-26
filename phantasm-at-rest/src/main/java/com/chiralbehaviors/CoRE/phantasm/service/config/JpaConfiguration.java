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

import com.chiralbehaviors.CoRE.phantasm.service.PhantasmBundle;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * @author hhildebrand
 * 
 */
public class JpaConfiguration extends Configuration {
    public static final String HIBERNATE_C3P0_ACQUIRE_INCREMENT       = "hibernate.c3p0.acquire_increment";
    public static final String HIBERNATE_C3P0_IDLE_TEST_PERIOD        = "hibernate.c3p0.idle_test_period";
    public static final String HIBERNATE_C3P0_MAX_SIZE                = "hibernate.c3p0.max_size";
    public static final String HIBERNATE_C3P0_MAX_STATEMENTS          = "hibernate.c3p0.max_statements";
    public static final String HIBERNATE_C3P0_MIN_SIZE                = "hibernate.c3p0.min_size";
    public static final String HIBERNATE_C3P0_TIMEOUT                 = "hibernate.c3p0.timeout";
    public static final String HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
    public static final String HIBERNATE_DIALECT                      = "hibernate.dialect";
    public static final String PERSISTENCE_UNIT                       = "CoRE";

    public static Map<String, String> getDefaultProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put(HIBERNATE_DIALECT,
                       JsonPostgreSqlDialect.class.getCanonicalName());
        properties.put(HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE, "true");
        properties.put(HIBERNATE_C3P0_MAX_SIZE, "20");
        properties.put(HIBERNATE_C3P0_MIN_SIZE, "5");
        properties.put(HIBERNATE_C3P0_TIMEOUT, "5000");
        properties.put(HIBERNATE_C3P0_MAX_STATEMENTS, "100");
        properties.put(HIBERNATE_C3P0_IDLE_TEST_PERIOD, "300");
        properties.put(HIBERNATE_C3P0_ACQUIRE_INCREMENT, "2");
        return properties;
    }

    @NotEmpty
    @JsonProperty
    private String              persistenceUnit = PERSISTENCE_UNIT;

    @NotEmpty
    @JsonProperty
    private Map<String, String> properties      = new HashMap<>();

    public boolean configureFromEnvironment() {
        return !properties.containsKey(PhantasmBundle.JAVAX_PERSISTENCE_JDBC_PASSWORD);
    }

    public String getPersistenceUnit() {
        return persistenceUnit;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
