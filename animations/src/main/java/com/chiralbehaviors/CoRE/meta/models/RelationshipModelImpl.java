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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.postgresql.pljava.TriggerData;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.jsp.JSP;
import com.chiralbehaviors.CoRE.jsp.StoredProcedure;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.RelationshipModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipAttribute;
import com.chiralbehaviors.CoRE.network.RelationshipAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.workspace.DatabaseBackedWorkspace;

/**
 * @author hhildebrand
 *
 */
public class RelationshipModelImpl
        extends
        AbstractNetworkedModel<Relationship, RelationshipNetwork, RelationshipAttributeAuthorization, RelationshipAttribute>
        implements RelationshipModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new RelationshipModelImpl(em));
        }

        @Override
        public String toString() {
            return "Call [" + procedure + "]";
        }
    }

    private static interface Procedure<T> {
        T call(RelationshipModelImpl productModel) throws Exception;
    }

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(RelationshipModelImpl agencyModel)
                                                               throws Exception {
                agencyModel.propagate();
                return null;
            }
        });
    }

    private static <T> T execute(Procedure<T> procedure) throws SQLException {
        return JSP.call(new Call<T>(procedure));
    }

    /**
     * @param em
     */
    public RelationshipModelImpl(EntityManager em) {
        super(em, DatabaseBackedWorkspace.getKernel(em));
    }

    /**
     * @param em
     */
    public RelationshipModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE
     * .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Relationship> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            RelationshipAttributeAuthorization authorization = new RelationshipAttributeAuthorization(
                                                                                                      aspect.getClassification(),
                                                                                                      aspect.getClassifier(),
                                                                                                      attribute,
                                                                                                      kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
     */
    @Override
    public Relationship create(Relationship prototype) {
        Relationship copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (RelationshipNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (RelationshipAttribute attribute : prototype.getAttributes()) {
            RelationshipAttribute clone = (RelationshipAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setRelationship(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Relationship, RelationshipAttribute> create(String name,
                                                             String description,
                                                             Aspect<Relationship> aspect) {
        Relationship relationship = new Relationship(name, description,
                                                     kernel.getCoreModel());
        em.persist(relationship);
        return new Facet<Relationship, RelationshipAttribute>(
                                                              aspect,
                                                              relationship,
                                                              initialize(relationship,
                                                                         aspect)) {
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.meta
     * .Aspect<RuleForm>[])
     */
    @SafeVarargs
    @Override
    public final Relationship create(String name, String description,
                                     Aspect<Relationship> aspect,
                                     Aspect<Relationship>... aspects) {
        Relationship relationship = new Relationship(name, description,
                                                     kernel.getCoreModel());
        em.persist(relationship);
        initialize(relationship, aspect);
        if (aspects != null) {
            for (Aspect<Relationship> a : aspects) {
                initialize(relationship, a);
            }
        }
        return relationship;
    }

    @Override
    public final Relationship create(String rel1Name, String rel1Description,
                                     String rel2Name, String rel2Description) {
        Relationship relationship = new Relationship(rel1Name, rel1Description,
                                                     kernel.getCoreModel());

        Relationship relationship2 = new Relationship(rel2Name,
                                                      rel2Description,
                                                      kernel.getCoreModel());

        relationship.setInverse(relationship2);
        relationship2.setInverse(relationship);
        em.persist(relationship);
        em.persist(relationship2);

        return relationship;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public List<RelationshipNetwork> getInterconnections(Collection<Relationship> parents,
                                                         Collection<Relationship> relationships,
                                                         Collection<Relationship> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<RelationshipNetwork> query = em.createNamedQuery(RelationshipNetwork.GET_NETWORKS,
                                                                    RelationshipNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /**
     * @param agency
     * @param aspect
     */
    protected List<RelationshipAttribute> initialize(Relationship agency,
                                                     Aspect<Relationship> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        List<RelationshipAttribute> attributes = new ArrayList<>();
        for (RelationshipAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            RelationshipAttribute attribute = new RelationshipAttribute(
                                                                        authorization.getAuthorizedAttribute(),
                                                                        kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setRelationship(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }
}
