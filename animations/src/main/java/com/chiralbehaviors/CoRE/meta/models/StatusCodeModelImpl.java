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

import java.util.Collection;
import java.util.List;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.StatusCodeModel;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeModelImpl extends ExistentialModelImpl<StatusCode>
        implements StatusCodeModel {

    /**
     * @param em
     */
    public StatusCodeModelImpl(Model model) {
        super(model);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodes(com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public Collection<StatusCode> getStatusCodes(Product service) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencing(com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public List<StatusCodeSequencingRecord> getStatusCodeSequencing(Product service) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencingChild(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.event.status.StatusCode)
     */
    @Override
    public List<StatusCodeSequencingRecord> getStatusCodeSequencingChild(Product service,
                                                                         StatusCode child) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencingChild(com.chiralbehaviors.CoRE.event.status.StatusCode)
     */
    @Override
    public Collection<StatusCodeSequencingRecord> getStatusCodeSequencingChild(StatusCode child) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencingParent(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.event.status.StatusCode)
     */
    @Override
    public List<StatusCodeSequencingRecord> getStatusCodeSequencingParent(Product service,
                                                                          StatusCode parent) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencingParent(com.chiralbehaviors.CoRE.event.status.StatusCode)
     */
    @Override
    public List<StatusCodeSequencingRecord> getStatusCodeSequencingParent(StatusCode parent) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.ExistentialModel#authorize(com.chiralbehaviors.CoRE.meta.Aspect, com.chiralbehaviors.CoRE.domain.Attribute[])
     */
    @Override
    public void authorize(Aspect<StatusCode> aspect, Attribute... attributes) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.ExistentialModelImpl#domain()
     */
    @Override
    protected ExistentialDomain domain() {
        return ExistentialDomain.S;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.ExistentialModelImpl#domainClass()
     */
    @Override
    protected Class<? extends ExistentialRecord> domainClass() {
        return StatusCode.class;
    }

}
