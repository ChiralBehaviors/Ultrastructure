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

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;

/**
 * @author hhildebrand
 *
 */
public class PhantasmModelTest extends AbstractModelTest {

    @Test
    public void testGetFacets() {
        Product kernelWorkspace = model.getKernel()
                                       .getKernelWorkspace();
        assertEquals(14, model.getPhantasmModel()
                              .getFacets(kernelWorkspace)
                              .size());
    }

    @Test
    public void testNotInGroup() {
        Relationship classifier = model.records()
                                       .newRelationship("test not in group classifier");
        classifier.insert();
        Relationship inverse = model.records()
                                    .newRelationship("inverse test not in group classifier");
        inverse.insert();
        classifier.setInverse(inverse.getId());
        inverse.setInverse(classifier.getId());
        Agency classification = model.records()
                                     .newAgency("test not in group agency classification");
        classification.insert();
        List<ExistentialRuleform> notInGroup = model.getPhantasmModel()
                                                    .getNotInGroup(classification,
                                                                   inverse,
                                                                   ExistentialDomain.Agency);
        assertNotNull(notInGroup);
        assertTrue(!notInGroup.isEmpty());
    }
}
