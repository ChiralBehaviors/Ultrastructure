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

import org.junit.Test;

import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author Halloran Parry
 * @author hhildebrand
 * 
 */
public class TransitiveRelationshipAuthorizationTest extends DatabaseTest {

    private Resource     core;
    private Relationship isA;
    private Relationship expresses;

    @Test
    public void insertNetwork() {
        loadTestData();
        em.getTransaction().begin();
        Resource core = em.find(Resource.class, 1L);

        TransitiveRelationshipAuthorization net = new TransitiveRelationshipAuthorization(
                                                                                          isA,
                                                                                          expresses,
                                                                                          true,
                                                                                          core);

        em.persist(net);

        em.getTransaction().commit();

    }

    private void loadTestData() {
        em.getTransaction().begin();
        core = new Resource("CoRE", "der CoRE", null);
        core.setUpdatedBy(core);
        em.persist(core);

        isA = new Relationship("isA", "A is a B", core, true);
        em.persist(isA);

        expresses = new Relationship("expresses", "A expresses B", core, true);
        em.persist(expresses);

        em.getTransaction().commit();
    }

}
