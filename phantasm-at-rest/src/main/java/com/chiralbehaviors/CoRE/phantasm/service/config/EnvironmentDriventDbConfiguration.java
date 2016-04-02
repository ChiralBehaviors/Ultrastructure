/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.service.config;

import java.net.URI;
import java.net.URISyntaxException;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;

/**
 * @author hhildebrand
 *
 */
public class EnvironmentDriventDbConfiguration<T extends Configuration>
        implements DatabaseConfiguration<T> {

    private DatabaseConfiguration<T> databaseConfiguration;

    @Override
    public PooledDataSourceFactory getDataSourceFactory(T configuration) {
        if (databaseConfiguration == null) {
            databaseConfiguration = create(System.getenv(System.getenv("DATABASE_URL")));
        }
        return databaseConfiguration.getDataSourceFactory(configuration);
    }

    private DatabaseConfiguration<T> create(String databaseUrl) {
        if (databaseUrl == null) {
            throw new IllegalArgumentException("The DATABASE_URL environment variable must be set before running the app");
        }
        URI dbUri;
        try {
            dbUri = new URI(databaseUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Invalid database url: %s",
                                                             databaseUrl),
                                               e);
        }
        final String user = dbUri.getUserInfo()
                                 .split(":")[0];
        final String password = dbUri.getUserInfo()
                                     .split(":")[1];
        final String url = "jdbc:postgresql://" + dbUri.getHost() + ':'
                           + dbUri.getPort() + dbUri.getPath()
                           + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        return new DatabaseConfiguration<T>() {
            DataSourceFactory dataSourceFactory;

            @Override
            public DataSourceFactory getDataSourceFactory(Configuration configuration) {
                if (dataSourceFactory != null) {
                    return dataSourceFactory;
                }
                DataSourceFactory dsf = new DataSourceFactory();
                dsf.setUser(user);
                dsf.setPassword(password);
                dsf.setUrl(url);
                dsf.setDriverClass("org.postgresql.Driver");
                dataSourceFactory = dsf;
                return dsf;
            }
        };
    }
}