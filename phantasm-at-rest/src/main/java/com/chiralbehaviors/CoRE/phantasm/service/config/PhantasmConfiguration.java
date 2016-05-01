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

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.bendb.dropwizard.jooq.JooqBundle;
import com.bendb.dropwizard.jooq.JooqFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilderSpec;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

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

    private static final String               DEFAULT_ASSETS_NAME       = "assets";

    private static final String               DEFAULT_INDEX_FILE        = "index.htm";

    private static final String               DEFAULT_PATH              = "/assets";

    @NotNull
    private List<Asset>                       assets                    = new ArrayList<>();
    @NotNull
    private AssetsConfiguration               assetsConfiguration       = new AssetsConfiguration();
    @NotNull
    private AuthType                          auth                      = AuthType.BEARER_TOKEN;

    @NotNull
    private CacheBuilderSpec                  authenticationCachePolicy = CacheBuilderSpec.parse("maximumSize=10000, expireAfterAccess=10m");

    @NotNull
    private CORSConfiguration                 CORS                      = new CORSConfiguration();

    private DataSourceFactory                 database;

    @NotNull
    private List<String>                      executionScope            = Collections.emptyList();

    @NotNull
    private JooqFactory                       jooq                      = new JooqFactory();

    private JooqBundle<PhantasmConfiguration> jooqBundle                = new JooqBundle<PhantasmConfiguration>() {
                                                                            @Override
                                                                            public DataSourceFactory getDataSourceFactory(PhantasmConfiguration configuration) {
                                                                                return configuration.getDatabase();
                                                                            }

                                                                            @Override
                                                                            public JooqFactory getJooqFactory(PhantasmConfiguration configuration) {
                                                                                return configuration.getJooq();
                                                                            }
                                                                        };

    private boolean                           useCORS                   = false;

    {
        setServerFactory(new SinglePortServerFactory());
        setLoggingFactory(new ConsoleOnlyLoggingFactory());
        setDatabase(new DataSourceFactoryFromEnv());
    }

    public DSLContext create() {
        return DSL.using(jooqBundle.getConfiguration());
    }

    public List<Asset> getAssets() {
        return assets;
    }

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assetsConfiguration;
    }

    public AuthType getAuth() {
        return auth;
    }

    public CacheBuilderSpec getAuthenticationCachePolicy() {
        return authenticationCachePolicy;
    }

    public CORSConfiguration getCORS() {
        return CORS;
    }

    public DataSourceFactory getDatabase() {
        database.setAutoCommitByDefault(false);
        return database;
    }

    public List<String> getExecutionScope() {
        return executionScope;
    }

    public JooqFactory getJooq() {
        jooq.setExecuteWithOptimisticLocking(true);
        return jooq;
    }

    public org.jooq.Configuration getJooqConfiguration() {
        return jooqBundle.getConfiguration();
    }

    public boolean isUseCORS() {
        return useCORS;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public void setAssetsConfiguration(AssetsConfiguration assetsConfiguration) {
        this.assetsConfiguration = assetsConfiguration;
    }

    public void setAuth(AuthType auth) {
        this.auth = auth;
    }

    public void setAuthenticationCachePolicy(CacheBuilderSpec authenticationCachePolicy) {
        this.authenticationCachePolicy = authenticationCachePolicy;
    }

    public void setCORS(CORSConfiguration cORS) {
        CORS = cORS;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public void setExecutionScope(List<String> executionScope) {
        this.executionScope = executionScope;
    }

    public void setJooq(JooqFactory jooq) {
        this.jooq = jooq;
    }

    public void setJooqBundle(JooqBundle<PhantasmConfiguration> jooqBundle) {
        this.jooqBundle = jooqBundle;
    }

    public void setUseCORS(boolean useCORS) {
        this.useCORS = useCORS;
    }
}
