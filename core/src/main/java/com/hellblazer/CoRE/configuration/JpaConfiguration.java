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
package com.hellblazer.CoRE.configuration;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import com.yammer.dropwizard.config.Configuration;

/**
 * @author hhildebrand
 * 
 */
public class JpaConfiguration extends Configuration {
    @JsonProperty
    private boolean             debug = false;

    @NotEmpty
    @JsonProperty
    private String              persistenceUnit;

    @NotEmpty
    @JsonProperty
    private Map<String, String> properties;

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
