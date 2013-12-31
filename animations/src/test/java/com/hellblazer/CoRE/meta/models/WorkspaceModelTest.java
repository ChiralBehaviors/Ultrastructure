/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.meta.models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.meta.WorkspaceModel;
import com.hellblazer.CoRE.meta.graph.Graph;
import com.hellblazer.CoRE.meta.graph.Node;

/**
 * @author hhildebrand
 * 
 */
public class WorkspaceModelTest extends AbstractModelTest {

    private WorkspaceModel        workspaceModel;
    private OrderProcessingLoader scenario;

    @Override
    @Before
    public void initialize() throws Exception {
        super.initialize();
        workspaceModel = model.getWorkspaceModel();
        EntityTransaction txn = em.getTransaction();
        scenario = new OrderProcessingLoader(em);
        txn.begin();
        scenario.load();
        txn.commit();
    }

    @Test
    public void testStatusCodeGraph() {
        Graph statusCodeGraph = workspaceModel.getStatusCodeGraph(scenario.pick);
        Collection<StatusCode> codes = model.getJobModel().getStatusCodesFor(scenario.pick);
        assertEquals(codes.size(), statusCodeGraph.getNodes().size());
        for (Node<?> node : statusCodeGraph.getNodes()) {
            assertTrue(codes.contains(node.getNode()));
        }
        Set<Node<?>> reduced = new HashSet<Node<?>>();
        reduced.addAll(statusCodeGraph.getNodes());
        assertEquals(codes.size(), reduced.size());
    }
}
