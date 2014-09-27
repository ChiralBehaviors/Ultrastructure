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
package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityTransaction;

import org.junit.BeforeClass;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.meta.graph.Graph;
import com.chiralbehaviors.CoRE.meta.graph.Node;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceModelTest extends AbstractModelTest {

    @BeforeClass
    public static void init() throws Exception {
        workspaceModel = model.getWorkspaceModel();
        EntityTransaction txn = em.getTransaction();
        scenario = new OrderProcessingLoader(em);
        txn.begin();
        scenario.load();
        txn.commit();
    }

    private static WorkspaceModel        workspaceModel;

    private static OrderProcessingLoader scenario;

    // @Test
    public void testStatusCodeGraph() {
        Graph<StatusCode, StatusCodeNetwork> statusCodeGraph = null;
        Collection<StatusCode> codes = model.getJobModel().getStatusCodesFor(scenario.pick);
        assertEquals(codes.size(), statusCodeGraph.getNodes().size());
        for (Node<StatusCode> node : statusCodeGraph.getNodes()) {
            assertTrue(codes.contains(node.getNode()));
        }
        Set<Node<StatusCode>> reduced = new HashSet<Node<StatusCode>>();
        reduced.addAll(statusCodeGraph.getNodes());
        assertEquals(codes.size(), reduced.size());
    }
}
