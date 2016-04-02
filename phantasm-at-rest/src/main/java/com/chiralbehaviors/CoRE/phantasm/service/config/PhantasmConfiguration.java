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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilderSpec;

import io.dropwizard.Configuration;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;

/**
 * @author hhildebrand
 * 
 */
public class PhantasmConfiguration extends Configuration
        implements AssetsBundleConfiguration {
    public static class Asset {

        @NotNull
        public String index = DEFAULT_INDEX_FILE;

        @NotNull
        public String name  = DEFAULT_ASSETS_NAME;

        @NotNull
        public String path  = DEFAULT_PATH;

        public String uri;

        @Override
        public String toString() {
            return String.format("Asset [name=%s, uri=%s, path=%s, index=%s]",
                                 name, uri, path, index);
        }
    }

    public static enum AuthType {
        @JsonProperty BASIC_DIGEST, @JsonProperty BEARER_TOKEN,
        @JsonProperty NULL;
    }

    private static final String                                      DEFAULT_ASSETS_NAME       = "assets";
    private static final String                                      DEFAULT_INDEX_FILE        = "index.htm";
    private static final String                                      DEFAULT_PATH              = "/assets";

    @NotNull
    public List<Asset>                                               assets                    = new ArrayList<>();

    @NotNull
    public AssetsConfiguration                                       assetsConfiguration       = new AssetsConfiguration();

    @NotNull
    public AuthType                                                  auth                      = AuthType.BEARER_TOKEN;

    @NotNull
    public CacheBuilderSpec                                          authenticationCachePolicy = CacheBuilderSpec.parse("maximumSize=10000, expireAfterAccess=10m");

    @NotNull
    public CORSConfiguration                                         CORS                      = new CORSConfiguration();

    @NotNull
    public List<String>                                              executionScope            = Collections.emptyList();

    public boolean                                                   randomPort                = false;

    public String                                                    realm;

    public boolean                                                   useCORS                   = false;

    @JsonProperty("database")
    private DatabaseConfiguration<PhantasmConfiguration>             databaseConfiguration;

    private EnvironmentDriventDbConfiguration<PhantasmConfiguration> environmentConfig         = new EnvironmentDriventDbConfiguration<>();

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assetsConfiguration;
    }

    public PooledDataSourceFactory getDatabaseConfiguration() {
        if (databaseConfiguration == null) {
            return environmentConfig.getDataSourceFactory(this);
        } else {
            return databaseConfiguration.getDataSourceFactory(this);
        }
    }
}
