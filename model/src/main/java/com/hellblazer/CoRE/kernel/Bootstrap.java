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

package com.hellblazer.CoRE.kernel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownAction;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownAttribute;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownProduct;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownLocation;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownLocationContext;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownRelationship;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownResource;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownStatusCode;

/**
 * @author hhildebrand
 * 
 */
public class Bootstrap {
    private static final String SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    private final Connection    connection;

    /**
     * @param connection
     */
    public Bootstrap(Connection connection) {
        this.connection = connection;
    }

    public void bootstrap() throws SQLException {
        alterTriggers(false);
        for (WellKnownObject wko : WellKnownResource.values()) {
            insert(wko);
        }
        for (WellKnownObject wko : WellKnownAction.values()) {
            insert(wko);
        }
        for (WellKnownAttribute wko : WellKnownAttribute.values()) {
            insert(wko);
        }
        for (WellKnownObject wko : WellKnownProduct.values()) {
            insert(wko);
        }
        for (WellKnownObject wko : WellKnownLocationContext.values()) {
            insert(wko);
        }
        for (WellKnownLocation wko : WellKnownLocation.values()) {
            insert(wko);
        }
        for (WellKnownRelationship wko : WellKnownRelationship.values()) {
            insert(wko);
        }
        for (WellKnownObject wko : WellKnownStatusCode.values()) {
            insert(wko);
        }
        adjustIdSeq(WellKnownResource.ANY);
        adjustIdSeq(WellKnownAction.ANY);
        adjustIdSeq(WellKnownAttribute.ANY);
        adjustIdSeq(WellKnownProduct.ANY);
        adjustIdSeq(WellKnownLocation.ANY);
        adjustIdSeq(WellKnownLocationContext.ANY);
        adjustIdSeq(WellKnownRelationship.ANY);
        adjustIdSeq(WellKnownStatusCode.UNSET);
        alterTriggers(true);
    }

    public void clear() throws SQLException {
        alterTriggers(false);
        ResultSet r = connection.createStatement().executeQuery(SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("DELETE FROM %s", table);
            connection.createStatement().execute(query);
        }
        r.close();
        alterTriggers(true);
    }

    public void insert(WellKnownAttribute wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, pinned, updated_by, value_type) VALUES (?, ?, ?, ?, ?, ?)",
                                                                        wko.tableName()));
        try {
            s.setLong(1, wko.id());
            s.setString(2, wko.productName());
            s.setString(3, wko.description());
            s.setBoolean(4, true);
            s.setLong(5, WellKnownResource.CORE.id());
            s.setInt(6, wko.valueType().ordinal());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    public void insert(WellKnownLocation wkl) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, pinned, updated_by, context) VALUES (?, ?, ?, ?, ?, ?)",
                                                                        wkl.tableName()));
        try {
            s.setLong(1, wkl.id());
            s.setString(2, wkl.productName());
            s.setString(3, wkl.description());
            s.setBoolean(4, true);
            s.setLong(5, WellKnownResource.CORE.id());
            s.setLong(6, WellKnownLocationContext.LOCATION_CONTEXT.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert  %s", wkl),
                                   e);
        }
    }

    public void insert(WellKnownObject wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, pinned, updated_by) VALUES (?, ?, ?, ?, ?)",
                                                                        wko.tableName()));
        try {
            s.setLong(1, wko.id());
            s.setString(2, wko.productName());
            s.setString(3, wko.description());
            s.setBoolean(4, true);
            s.setLong(5, WellKnownResource.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    public void insert(WellKnownRelationship wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, pinned, updated_by, inverse, preferred) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                                                        wko.tableName()));
        try {
            s.setLong(1, wko.id());
            s.setString(2, wko.productName());
            s.setString(3, wko.description());
            s.setBoolean(4, true);
            s.setLong(5, WellKnownResource.CORE.id());
            s.setLong(6, wko.inverse().id());
            s.setBoolean(7, wko.preferred());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    protected void adjustIdSeq(WellKnownObject wko) throws SQLException {

        PreparedStatement update = connection.prepareStatement(String.format("SELECT setval('%s_id_seq', (SELECT max(net.id) FROM %s as net))",
                                                                             wko.tableName(),
                                                                             wko.tableName()));
        update.execute();
    }

    protected void alterTriggers(boolean enable) throws SQLException {
        ResultSet r = connection.createStatement().executeQuery(SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        r.close();
    }
}
