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
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Aspect;
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
        this.create = model.getDSLContext();
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

    @Override
    public final RuleForm create(String name, String description) {
        //        Agency agency = new Agency(name, description,
        //                                   model.getCurrentPrincipal()
        //                                        .getPrincipal());
        //        em.persist(agency);
        //        return agency;
        return null;
    }

    @SafeVarargs
    @Override
    public final RuleForm create(String name, String description,
                                 Aspect<RuleForm> aspect, Agency updatedBy,
                                 Aspect<RuleForm>... aspects) {
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

    @SuppressWarnings("unchecked")
    @Override
    public List<RuleForm> findAll() {
        return (List<RuleForm>) create.selectFrom(EXISTENTIAL)
                                      .where(EXISTENTIAL.DOMAIN.equal(domain()))
                                      .fetch()
                                      .into(domainClass());
    }

    @Override
    public List<Aspect<RuleForm>> getAllFacets() {
        return create.selectDistinct(EXISTENTIAL_NETWORK_AUTHORIZATION.fields())
                     .from(EXISTENTIAL_NETWORK_AUTHORIZATION)
                     .join(EXISTENTIAL)
                     .on(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION.equal(EXISTENTIAL.ID))
                     .where(EXISTENTIAL.DOMAIN.equal(domain()))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_RELATIONSHIP.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull())
                     .fetch()
                     .into(EXISTENTIAL_NETWORK_AUTHORIZATION)
                     .stream()
                     .map(auth -> new Aspect<RuleForm>(auth.getClassifier(),
                                                       auth.getClassification()))
                     .collect(Collectors.toList());
    }

    @Override
    public List<ExistentialNetworkAuthorizationRecord> getFacets(Product workspace) {
        return create.selectDistinct(EXISTENTIAL_NETWORK_AUTHORIZATION.fields())
                     .from(EXISTENTIAL_NETWORK_AUTHORIZATION)
                     .join(WORKSPACE_AUTHORIZATION)
                     .on(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(workspace.getId()))
                     .and(WORKSPACE_AUTHORIZATION.REFERENCE.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.ID))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_RELATIONSHIP.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CHILD_RELATIONSHIP.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull())
                     .fetch()
                     .into(ExistentialNetworkAuthorizationRecord.class);
    }

    abstract protected ExistentialDomain domain();

    abstract protected Class<? extends ExistentialRecord> domainClass();
}
