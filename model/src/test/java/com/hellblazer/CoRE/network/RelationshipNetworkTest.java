/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.network;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.test.DatabaseTestContext;

/**
 * @author Halloran Parry
 *
 */
public class RelationshipNetworkTest extends DatabaseTestContext {
	
	@Test
	public void insertNetwork() {
		beginTransaction();
		Relationship parent = em.find(Relationship.class, 1L);
		Relationship child = em.find(Relationship.class, 2L);
		Resource core = em.find(Resource.class, 1L);
		
		RelationshipNetwork net = new RelationshipNetwork(parent, child, true, core);
		
		em.persist(net);
		
		commitTransaction();
		
		
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.test.DatabaseTestContext#prepareSettings()
	 */
	@Override
    protected void prepareSettings() {
        dataSetLocation = "RelationshipNetworkTestData.xml";
        beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
    }

}
