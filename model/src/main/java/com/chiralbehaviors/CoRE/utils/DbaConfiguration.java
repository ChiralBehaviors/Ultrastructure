/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author hhildebrand
 * 
 */
public class DbaConfiguration extends CoreDbConfiguration {

    /**
     * the dba database
     * 
     * @parameter
     */
    public String  dbaDb        = "postgres";

    /**
     * the dba password
     * 
     * @parameter
     */
    public String  dbaPassword;

    /**
     * the port of the dba database
     * 
     * @parameter
     */
    public int     dbaPort;

    /**
     * the host name of the dba database
     * 
     * @parameter
     */
    public String  dbaServer;

    /**
     * the dba username
     * 
     * @parameter
     */
    public String  dbaUsername;

    /**
     * drop the database
     * 
     * @parameter
     */
    public boolean dropDatabase = false;

    public Connection getDbaConnection() throws SQLException {
        String url = String.format(JDBC_URL, dbaServer, dbaPort, dbaDb);
        System.out.println(String.format("DBA connection: %s", url));
        return DriverManager.getConnection(url, dbaUsername, dbaPassword);
    }
}
