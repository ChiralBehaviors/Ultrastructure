/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.kernel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownLocation;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownStatusCode;

/**
 * @author hhildebrand
 * 
 */
public class Bootstrap {
	private static final String SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

	private final Connection connection;

	/**
	 * @param connection
	 */
	public Bootstrap(Connection connection) {
		this.connection = connection;
	}

	public void bootstrap() throws SQLException {
		alterTriggers(false);
		for (WellKnownObject wko : WellKnownAgency.values()) {
			insert(wko);
		}
		for (WellKnownAttribute wko : WellKnownAttribute.values()) {
			insert(wko);
		}
		for (WellKnownObject wko : WellKnownProduct.values()) {
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
		adjustIdSeq(WellKnownAgency.ANY);
		adjustIdSeq(WellKnownAttribute.ANY);
		adjustIdSeq(WellKnownProduct.ANY);
		adjustIdSeq(WellKnownLocation.ANY);
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
		PreparedStatement s = connection
				.prepareStatement(String
						.format("INSERT into %s (id, name, description, pinned, updated_by, value_type) VALUES (?, ?, ?, ?, ?, ?)",
								wko.tableName()));
		try {
			s.setLong(1, wko.id());
			s.setString(2, wko.productName());
			s.setString(3, wko.description());
			s.setByte(4, (byte) 1);
			s.setLong(5, WellKnownAgency.CORE.id());
			s.setInt(6, wko.valueType().ordinal());
			s.execute();
		} catch (SQLException e) {
			throw new SQLException(String.format("Unable to insert %s", wko), e);
		}
	}

	public void insert(WellKnownLocation wkl) throws SQLException {
		PreparedStatement s = connection
				.prepareStatement(String
						.format("INSERT into %s (id, name, description, pinned, updated_by) VALUES (?, ?, ?, ?, ?)",
								wkl.tableName()));
		try {
			s.setLong(1, wkl.id());
			s.setString(2, wkl.productName());
			s.setString(3, wkl.description());
			s.setByte(4, (byte) 1);
			s.setLong(5, WellKnownAgency.CORE.id());
			s.execute();
		} catch (SQLException e) {
			throw new SQLException(String.format("Unable to insert  %s", wkl),
					e);
		}
	}

	public void insert(WellKnownObject wko) throws SQLException {
		PreparedStatement s = connection
				.prepareStatement(String
						.format("INSERT into %s (id, name, description, pinned, updated_by) VALUES (?, ?, ?, ?, ?)",
								wko.tableName()));
		try {
			s.setLong(1, wko.id());
			s.setString(2, wko.productName());
			s.setString(3, wko.description());
			s.setByte(4, (byte) 1);
			s.setLong(5, WellKnownAgency.CORE.id());
			s.execute();
		} catch (SQLException e) {
			throw new SQLException(String.format("Unable to insert %s", wko), e);
		}
	}

	public void insert(WellKnownRelationship wko) throws SQLException {
		PreparedStatement s = connection
				.prepareStatement(String
						.format("INSERT into %s (id, name, description, pinned, updated_by, inverse, preferred) VALUES (?, ?, ?, ?, ?, ?, ?)",
								wko.tableName()));
		try {
			s.setLong(1, wko.id());
			s.setString(2, wko.productName());
			s.setString(3, wko.description());
			s.setByte(4, (byte) 1);
			s.setLong(5, WellKnownAgency.CORE.id());
			s.setLong(6, wko.inverse().id());
			s.setByte(7, (byte) (wko.preferred() ? 1 : 0));
			s.execute();
		} catch (SQLException e) {
			throw new SQLException(String.format("Unable to insert %s", wko), e);
		}
	}

	protected void adjustIdSeq(WellKnownObject wko) throws SQLException {

		PreparedStatement update = connection
				.prepareStatement(String
						.format("SELECT setval('%s_id_seq', (SELECT max(net.id) FROM %s as net))",
								wko.tableName(), wko.tableName()));
		update.execute();
	}

	protected void alterTriggers(boolean enable) throws SQLException {
		for (String table : new String[] { "ruleform.agency",
				"ruleform.product", "ruleform.location" }) {
			String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
					table, enable ? "ENABLE" : "DISABLE");
			connection.createStatement().execute(query);
		}
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
