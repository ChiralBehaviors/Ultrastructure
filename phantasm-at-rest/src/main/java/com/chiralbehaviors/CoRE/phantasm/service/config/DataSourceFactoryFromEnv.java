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
import java.sql.Driver;
import java.util.regex.Pattern;

import io.dropwizard.db.DataSourceFactory;

/**
 * @author hhildebrand
 *
 */
public class DataSourceFactoryFromEnv extends DataSourceFactory {
    private static final String HEROKU_DATABASE_URL = "DATABASE_URL";
    private static final String JDBC_POSTGRESQL     = "jdbc:postgresql";
    private String              driverScheme        = JDBC_POSTGRESQL;
    private String              envVariable         = HEROKU_DATABASE_URL;

    public DataSourceFactoryFromEnv() {
        setDriverClass(Driver.class.getCanonicalName());
        String url = System.getenv(envVariable);
        if (url != null)
            configureFrom(url);
    }

    private void configureFrom(String uri) {
        try {
            URI database = new URI(uri);
            setCredentials(database);
            setUrl(database);
        } catch (URISyntaxException urise) {
            throw new IllegalArgumentException(urise);
        }
    }

    private void setCredentials(URI uri) {
        String user = uri.getUserInfo();
        if (user == null)
            return;
        String[] parts = user.split(Pattern.quote(":"), 2);

        if (parts.length >= 1)
            setUser(parts[0]);
        if (parts.length >= 2)
            setPassword(parts[1]);
    }

    private void setUrl(URI uri) throws URISyntaxException {
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        String query = uri.getQuery();
        URI connection = new URI(driverScheme, null, host, port, path, query,
                                 null);
        setUrl(connection.toString());
    }
}