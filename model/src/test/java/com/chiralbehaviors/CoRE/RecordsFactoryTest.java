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

package com.chiralbehaviors.CoRE;

import java.util.UUID;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 *
 */
public class RecordsFactoryTest extends DatabaseTest {

    @Test
    public void testRecordsFactory() throws Exception {
        RECORDS.newAgency();
        RECORDS.newAgency("");
        RECORDS.newAttribute();
        RECORDS.newAttribute("", "");
        RECORDS.newAttribute("", "", ValueType.Integer);
        RECORDS.newChildSequencingAuthorization();
        RECORDS.newChildSequencingAuthorization(RECORDS.newProduct(),
                                                RECORDS.newStatusCode(),
                                                RECORDS.newProduct(),
                                                RECORDS.newStatusCode());
        RECORDS.newChildSequencingAuthorization(UUID.randomUUID(), null, null,
                                                null);
        RECORDS.newExistential(ExistentialDomain.Agency);
        RECORDS.newExistential(ExistentialDomain.Agency, "", "");
        RECORDS.newExistentialAttribute();
        RECORDS.newExistentialAttribute(RECORDS.newAttribute());
        RECORDS.newExistentialAttribute(RECORDS.newAttribute(),
                                        RECORDS.newAttribute());
        RECORDS.newExistentialAttributeAuthorization(RECORDS.newFacet(),
                                                     RECORDS.newAttribute());
        RECORDS.newExistentialGrouping();
        RECORDS.newExistentialNetwork();
        RECORDS.newExistentialNetworkAuthorization();
        RECORDS.newExistentialNetworkAttributeAuthorization();
        RECORDS.newExistentialNetwork(RECORDS.newAgency(),
                                      RECORDS.newRelationship(),
                                      RECORDS.newAgency());
        RECORDS.newExistentialNetworkAttribute();
        RECORDS.newExistentialNetworkAttribute(RECORDS.newAttribute());
        RECORDS.newExistentialAttributeAuthorization();
        RECORDS.newExistentialAttributeAuthorization(RECORDS.newFacet(),
                                                     RECORDS.newAttribute());
        RECORDS.newFacet();
        RECORDS.newFacet(RECORDS.newRelationship(), RECORDS.newAgency());
        RECORDS.newInterval();
        RECORDS.newJob();
        RECORDS.newJobChronology();
        RECORDS.newJobChronology(RECORDS.newJob(), "");
        RECORDS.newLocation();
        RECORDS.newLocation("");
        RECORDS.newLocation("", "");
        RECORDS.newMetaProtocol();
        RECORDS.newNetworkInferrence();
        RECORDS.newParentSequencingAuthorization();
        RECORDS.newParentSequencingAuthorization(RECORDS.newProduct(),
                                                 RECORDS.newStatusCode(),
                                                 RECORDS.newProduct(),
                                                 RECORDS.newStatusCode());
        RECORDS.newParentSequencingAuthorization(UUID.randomUUID(),
                                                 UUID.randomUUID(),
                                                 UUID.randomUUID(),
                                                 UUID.randomUUID());
        RECORDS.newProduct();
        RECORDS.newProduct("");
        RECORDS.newProduct("", "");
        RECORDS.newProtocol();
        RECORDS.newRelationship();
        RECORDS.newRelationship("");
        RECORDS.newRelationship("", "");
        RECORDS.newSelfSequencingAuthorization();
        RECORDS.newSelfSequencingAuthorization(RECORDS.newProduct(),
                                               RECORDS.newStatusCode(),
                                               RECORDS.newStatusCode());
        RECORDS.newSiblingSequencingAuthorization();
        RECORDS.newSiblingSequencingAuthorization(UUID.randomUUID(),
                                                  UUID.randomUUID(),
                                                  UUID.randomUUID(),
                                                  UUID.randomUUID());
        RECORDS.newStatusCode();
        RECORDS.newStatusCode("");
        RECORDS.newStatusCode("", "");
        RECORDS.newStatusCodeSequencing();
        RECORDS.newStatusCodeSequencing(RECORDS.newProduct(),
                                        RECORDS.newStatusCode(),
                                        RECORDS.newStatusCode());
        RECORDS.newUnit();
        RECORDS.newUnit("", "");
        RECORDS.newWorkspaceAuthorization();
        Product p = RECORDS.newProduct();
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newChildSequencingAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialAttribute());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialAttributeAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialGrouping());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialNetwork());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialNetworkAttribute());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialNetworkAttributeAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialNetworkAuthorization());
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newFacet());
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newJob());
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newJobChronology());
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newMetaProtocol());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newNetworkInferrence());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newParentSequencingAuthorization());
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newProtocol());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newSelfSequencingAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newSiblingSequencingAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newStatusCodeSequencing());
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newAgency());

    }
}
