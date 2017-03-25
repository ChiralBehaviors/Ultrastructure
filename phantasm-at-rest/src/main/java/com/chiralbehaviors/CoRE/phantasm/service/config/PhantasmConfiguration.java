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

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

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
public class PhantasmConfiguration extends Configuration {

    public static enum AuthType {
        @JsonProperty
        BASIC_DIGEST, @JsonProperty
        BEARER_TOKEN, @JsonProperty
        NULL_AUTH;
    }

    @NotNull
    private AuthType                          auth                      = AuthType.BEARER_TOKEN;

    @NotNull
    private CacheBuilderSpec                  authenticationCachePolicy = CacheBuilderSpec.parse("maximumSize=10000, expireAfterAccess=10m");

    private boolean                           clear                     = false;

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

    @NotNull
    private List<String>                      snapshots                 = Collections.emptyList();
    private boolean                           useCORS                   = false;

    @NotNull
    private List<String>                      workspaces                = Collections.emptyList();

    {
        setServerFactory(new SinglePortServerFactory());
        setDatabase(new DataSourceFactoryFromEnv());
    }

    public DSLContext create() {
        return DSL.using(jooqBundle.getConfiguration());
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

    public List<String> getSnapshots() {
        return snapshots;
    }

    public List<String> getWorkspaces() {
        return workspaces;
    }

    public boolean isClear() {
        return clear;
    }

    public boolean isUseCORS() {
        return useCORS;
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
