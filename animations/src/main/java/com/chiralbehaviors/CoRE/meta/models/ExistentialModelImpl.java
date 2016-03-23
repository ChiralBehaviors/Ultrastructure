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

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;
import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_AUTHORIZATION;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.ExistentialModel;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * @author hhildebrand
 *
 */
abstract public class ExistentialModelImpl<RuleForm extends ExistentialRuleform>
        implements ExistentialModel<RuleForm> {
    protected final DSLContext     create;
    protected final Kernel         kernel;
    protected final Model          model;
    protected final RecordsFactory factory;

    public ExistentialModelImpl(Model model) {
        this.model = model;
        this.create = model.create();
        this.kernel = model.getKernel();
        factory = new RecordsFactory() {
            @Override
            public DSLContext create() {
                return create;
            }
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RuleForm create(RuleForm prototype) {
        ExistentialRecord copy = ((ExistentialRecord) prototype).into(ExistentialRecord.class);
        //        copy.setUpdatedBy(model.getCurrentPrincipal()
        //                               .getPrincipal()
        //                               .getId());
        //        for (AgencyNetwork network : prototype.getNetworkByParent()) {
        //            network.getParent()
        //                   .link(network.getRelationship(), copy,
        //                         model.getCurrentPrincipal()
        //                              .getPrincipal(),
        //                         model.getCurrentPrincipal()
        //                              .getPrincipal(),
        //                         em);
        //        }
        //        for (AttributeValue<Agency> attribute : prototype.getAttributes()) {
        //            AgencyAttribute clone = (AgencyAttribute) attribute.clone();
        //            em.detach(clone);
        //            em.persist(clone);
        //            clone.setAgency(copy);
        //            clone.setUpdatedBy(model.getCurrentPrincipal()
        //                                    .getPrincipal());
        //        }
        return (RuleForm) copy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final RuleForm create(String name, String description) {
        return (RuleForm) model.records()
                               .newExistential(domain(), name, description,
                                               model.getCurrentPrincipal()
                                                    .getPrincipal());
    }

    @SafeVarargs
    @Override
    public final RuleForm create(String name, String description,
                                 FacetRecord aspect, Agency updatedBy,
                                 FacetRecord... aspects) {
        //        RuleForm agency = new Agency(name, description,
        //                                     model.getCurrentPrincipal()
        //                                          .getPrincipal());
        //        em.persist(agency);
        //        initialize(agency, aspect);
        //        if (aspects != null) {
        //            for (Aspect<Agency> a : aspects) {
        //                initialize(agency, a);
        //            }
        //        }
        //        return agency;
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#find(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RuleForm find(UUID id) {
        return (RuleForm) create.selectFrom(EXISTENTIAL)
                                .where(EXISTENTIAL.ID.equal(id))
                                .and(EXISTENTIAL.DOMAIN.equal(domain()))
                                .fetchOne()
                                .into(domainClass());
    }

    @Override
    public List<FacetRecord> getAllFacets() {
        return create.selectDistinct(FACET.fields())
                     .from(FACET)
                     .join(EXISTENTIAL)
                     .on(FACET.CLASSIFICATION.equal(EXISTENTIAL.ID))
                     .where(EXISTENTIAL.DOMAIN.equal(domain()))
                     .and(FACET.CLASSIFIER.isNull())
                     .and(FACET.AUTHORITY.isNull())
                     .fetch()
                     .into(FacetRecord.class)
                     .stream()
                     .collect(Collectors.toList());
    }

    @Override
    public List<FacetRecord> getFacets(Product workspace) {
        return create.selectDistinct(FACET.fields())
                     .from(FACET)
                     .join(WORKSPACE_AUTHORIZATION)
                     .on(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(workspace.getId()))
                     .and(WORKSPACE_AUTHORIZATION.REFERENCE.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.ID))
                     .join(EXISTENTIAL)
                     .on(FACET.CLASSIFICATION.equal(EXISTENTIAL.ID))
                     .where(EXISTENTIAL.DOMAIN.equal(domain()))
                     .and(FACET.AUTHORITY.isNull())
                     .fetch()
                     .into(FacetRecord.class);
    }

    abstract protected ExistentialDomain domain();

    abstract protected Class<? extends ExistentialRecord> domainClass();
}
