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
package com.chiralbehaviors.CoRE.phantasm.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilderSpec;

import io.dropwizard.Configuration;

/**
 * @author hhildebrand
 * 
 */
public class PhantasmConfiguration extends Configuration {
    public static class Asset {
        public String index = DEFAULT_INDEX_FILE;
        public String name  = DEFAULT_ASSETS_NAME;
        public String path  = DEFAULT_PATH;
        @NotNull
        public String uri;
    }

    public static enum AuthType {
        @JsonProperty BASIC_DIGEST, @JsonProperty BEARER_TOKEN,
        @JsonProperty NULL;
    }

    private static final String DEFAULT_ASSETS_NAME = "assets";
    private static final String DEFAULT_INDEX_FILE  = "index.htm";
    private static final String DEFAULT_PATH        = "/assets";

    public boolean configureFromEnvironment = false;

    public List<Asset> assets = new ArrayList<>();
    @NotNull
    public AuthType    auth   = AuthType.BEARER_TOKEN;

    public boolean           useCORS = false;
    public CORSConfiguration CORS    = new CORSConfiguration();

    @NotNull
    public JpaConfiguration jpa = new JpaConfiguration();

    public String name;

    public boolean randomPort = false;

    public String realm;

    @NotNull
    public CacheBuilderSpec authenticationCachePolicy = CacheBuilderSpec.parse("maximumSize=10000, expireAfterAccess=10m");

}
