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

package com.hellblazer.CoRE.example.orderProcessing;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.location.LocationNetwork;
import com.hellblazer.CoRE.meta.BootstrapLoader;
import com.hellblazer.CoRE.meta.Kernel;
import com.hellblazer.CoRE.meta.models.KernelImpl;

/**
 * @author hhildebrand
 * 
 */
public class ExampleLoaderTest {

    private EntityManager em;
    private ExampleLoader scenario;
    private Kernel        kernel;

    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        InputStream is = ExampleLoader.class.getResourceAsStream("/jpa.properties");
        properties.load(is);
        em = Persistence.createEntityManagerFactory("CoRE", properties).createEntityManager();
        EntityTransaction txn = em.getTransaction();
        BootstrapLoader bootstrap = new BootstrapLoader(em);
        txn.begin();
        bootstrap.clear();
        txn.commit();
        txn.begin();
        bootstrap.bootstrap();
        txn.commit();
        scenario = new ExampleLoader(em);
        txn.begin();
        scenario.load();
        txn.commit();
        kernel = new KernelImpl(em);
    }

    @Test
    public void testNetworkInference() {
        List<LocationNetwork> edges = em.createQuery("SELECT edge FROM LocationNetwork edge WHERE edge.inferred = TRUE",
                                                     LocationNetwork.class).getResultList();
        assertEquals(6, edges.size());

        TypedQuery<LocationNetwork> edgeQuery = em.createQuery("select edge FROM LocationNetwork edge WHERE edge.parent = :parent AND edge.relationship = :relationship AND edge.child = :child",
                                                               LocationNetwork.class);
        edgeQuery.setParameter("parent", scenario.factory1);
        edgeQuery.setParameter("relationship", scenario.city);
        edgeQuery.setParameter("child", scenario.dc);
        LocationNetwork edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getInverseSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.dc);
        edgeQuery.setParameter("relationship", scenario.cityOf);
        edgeQuery.setParameter("child", scenario.factory1);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getPropagationSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.us);
        edgeQuery.setParameter("relationship", scenario.areaOf);
        edgeQuery.setParameter("child", scenario.dc);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getPropagationSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.dc);
        edgeQuery.setParameter("relationship", scenario.area);
        edgeQuery.setParameter("child", scenario.us);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getInverseSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.paris);
        edgeQuery.setParameter("relationship", scenario.area);
        edgeQuery.setParameter("child", scenario.euro);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getInverseSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.euro);
        edgeQuery.setParameter("relationship", scenario.areaOf);
        edgeQuery.setParameter("child", scenario.paris);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getPropagationSoftware(), edge.getUpdatedBy());
    }
}
