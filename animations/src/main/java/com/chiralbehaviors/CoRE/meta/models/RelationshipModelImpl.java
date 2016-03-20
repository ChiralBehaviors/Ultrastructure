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

import static com.chiralbehaviors.CoRE.RecordsFactory.RECORDS;

import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.RelationshipModel;

/**
 * @author hhildebrand
 *
 */
public class RelationshipModelImpl extends ExistentialModelImpl<Relationship>
        implements RelationshipModel {

    /**
     * @param em
     */
    public RelationshipModelImpl(Model model) {
        super(model);
    }

    @Override
    public final Relationship create(String rel1Name, String rel1Description,
                                     String rel2Name, String rel2Description) {
        Relationship relationship = RECORDS.newRelationship(create, rel1Name,
                                                            rel1Description,
                                                            model.getCurrentPrincipal()
                                                                 .getPrincipal());

        Relationship relationship2 = RECORDS.newRelationship(create, rel2Name,
                                                             rel2Description,
                                                             model.getCurrentPrincipal()
                                                                  .getPrincipal());

        relationship.setInverse(relationship2.getId());
        relationship2.setInverse(relationship.getId());
        relationship.insert();
        relationship2.insert();

        return relationship;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.ExistentialModelImpl#domain()
     */
    @Override
    protected ExistentialDomain domain() {
        // TODO Auto-generated method stub
        return ExistentialDomain.R;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.ExistentialModelImpl#domainClass()
     */
    @Override
    protected Class<? extends ExistentialRecord> domainClass() {
        return Relationship.class;
    }
}
