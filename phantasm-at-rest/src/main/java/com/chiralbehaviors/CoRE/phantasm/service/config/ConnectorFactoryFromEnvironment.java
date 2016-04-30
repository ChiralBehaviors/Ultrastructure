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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.jetty.HttpConnectorFactory;

/**
 * @author hhildebrand
 *
 */
public class ConnectorFactoryFromEnvironment extends HttpConnectorFactory {
    public static final int     DEFAULT_PORT = 5000;

    public static final String  PORT_ENV_VAR = "PORT";
    private static final Logger log          = LoggerFactory.getLogger(ConnectorFactoryFromEnvironment.class);

    public static int portFromEnv() {
        String environmentPort = System.getenv(PORT_ENV_VAR);
        if (environmentPort != null) {
            try {
                int port = Integer.parseInt(environmentPort);
                return port;
            } catch (NumberFormatException nfe) {
                log.error("Invalid number format for port env var {} : {}",
                          PORT_ENV_VAR, environmentPort);
                return 0;
            }
        }
        return DEFAULT_PORT;
    }

    {
        setPort(portFromEnv());
    }
}
