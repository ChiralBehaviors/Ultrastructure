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

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.jsp.JSP;
import com.hellblazer.CoRE.jsp.StoredProcedure;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.meta.IntervalModel;
import com.hellblazer.CoRE.meta.graph.Graph;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.time.Interval;
import com.hellblazer.CoRE.time.IntervalAttribute;
import com.hellblazer.CoRE.time.IntervalAttributeAuthorization;
import com.hellblazer.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 * 
 */
public class IntervalModelImpl
        extends
        AbstractNetworkedModel<Interval, IntervalAttributeAuthorization, IntervalAttribute>
        implements IntervalModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new IntervalModelImpl(em));
        }
    }

    private static interface Procedure<T> {
        T call(IntervalModelImpl productModel) throws Exception;
    }

    private static final String INTERVAL_NETWORK_PROPAGATE = "intervalNetwork.propagate";

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        if (!markPropagated(INTERVAL_NETWORK_PROPAGATE)) {
            return; // We be done
        }
        execute(new Procedure<Void>() {
            @Override
            public Void call(IntervalModelImpl agencyModel) throws Exception {
                agencyModel.propagate();
                return null;
            }
        });
    }

    public static void track_network_deleted(final TriggerData data)
                                                                    throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(IntervalModelImpl agencyModel) throws Exception {
                agencyModel.networkEdgeDeleted(data.getOld().getLong("parent"),
                                               data.getOld().getLong("relationship"));
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
    public IntervalModelImpl(EntityManager em) {
        super(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public IntervalModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#authorize(com.hellblazer.CoRE
     * .meta.Aspect, com.hellblazer.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Interval> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            IntervalAttributeAuthorization authorization = new IntervalAttributeAuthorization(
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
     * com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.network
     * .Networked)
     */
    @Override
    public Interval create(Interval prototype) {
        Interval copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (IntervalNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (IntervalAttribute attribute : prototype.getAttributes()) {
            IntervalAttribute clone = (IntervalAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setInterval(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.meta
     * .Aspect<RuleForm>[])
     */
    @Override
    public final Interval create(String name, String description,
                                 Aspect<Interval> aspect,
                                 Aspect<Interval>... aspects) {
        Interval agency = new Interval(name, description, kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Interval> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.NetworkedModel#findUnlinkedNodes()
     */
    @Override
    public List<Interval> findUnlinkedNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#getImmediateRelationships(com
     * .hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public List<Relationship> getImmediateRelationships(Interval parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#getNetwork(com.hellblazer.CoRE
     * .ExistentialRuleform, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Graph getNetwork(Interval parent, Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#getTransitiveRelationships(com
     * .hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public List<Relationship> getTransitiveRelationships(Interval parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.NetworkedModel#isAccessible(com.hellblazer.CoRE.ExistentialRuleform, com.hellblazer.CoRE.network.Relationship, com.hellblazer.CoRE.network.Relationship,
     * com.hellblazer.CoRE.ExistentialRuleform,
     * com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public boolean isAccessible(Interval parent,
                                Relationship parentRelationship,
                                Relationship authorizingRelationship,
                                ExistentialRuleform<?, ?> child,
                                Relationship childRelationship) {
        if (parent == null || child == null || authorizingRelationship == null) {
            throw new IllegalArgumentException(
                                               "parent, authorizingRelationship, and child cannot be null");
        }
        if (child instanceof Location) {

            return isLocationAccessible(parent, parentRelationship,
                                        authorizingRelationship,
                                        (Location) child, childRelationship);
        } else if (child instanceof Product) {
            return isProductAccessible(parent, parentRelationship,
                                       authorizingRelationship,
                                       (Product) child, childRelationship);
        } else {
            throw new IllegalArgumentException(
                                               "child type is not supported for this query");
        }

    }

    /**
     * @param parent
     * @param parentRelationship
     * @param authorizingRelationship
     * @param child
     * @param childRelationship
     * @return
     */
    private boolean isLocationAccessible(Interval parent,
                                         Relationship parentRelationship,
                                         Relationship authorizingRelationship,
                                         Location child,
                                         Relationship childRelationship) {
        //        Query query;
        //
        //        if (parentRelationship == null && childRelationship == null) {
        //            query = em.createNamedQuery(IntervalLocationAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD);
        //            query.setParameter("parent", parent);
        //            query.setParameter("relationship", authorizingRelationship);
        //            query.setParameter("child", child);
        //        } else if (childRelationship == null) {
        //            query = em.createNamedQuery(IntervalLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT);
        //            query.setParameter("relationship", authorizingRelationship);
        //            query.setParameter("child", child);
        //            query.setParameter("netRelationship", parentRelationship);
        //            query.setParameter("netChild", parent);
        //
        //        } else if (parentRelationship == null) {
        //            query = em.createNamedQuery(IntervalLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD);
        //            query.setParameter("relationship", authorizingRelationship);
        //            query.setParameter("parent", parent);
        //            query.setParameter("netRelationship", childRelationship);
        //            query.setParameter("netChild", child);
        //
        //        } else {
        //            query = em.createNamedQuery(IntervalLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD);
        //            query.setParameter("relationship", authorizingRelationship);
        //            query.setParameter("parentNetRelationship", parentRelationship);
        //            query.setParameter("parentNetChild", parent);
        //            query.setParameter("childNetRelationship", childRelationship);
        //            query.setParameter("childNetChild", child);
        //
        //        }
        //        List<?> results = query.getResultList();
        //
        //        return results.size() > 0;
        return false;

    }

    /**
     * @param parent
     * @param parentRelationship
     * @param authorizingRelationship
     * @param child
     * @param childRelationship
     * @return
     */
    private boolean isProductAccessible(Interval parent,
                                        Relationship parentRelationship,
                                        Relationship authorizingRelationship,
                                        Product child,
                                        Relationship childRelationship) {
        //        Query query;
        //
        //        if (parentRelationship == null && childRelationship == null) {
        //            query = em.createNamedQuery(IntervalProductAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD);
        //            query.setParameter("parent", parent);
        //            query.setParameter("relationship", authorizingRelationship);
        //            query.setParameter("child", child);
        //        } else if (childRelationship == null) {
        //            query = em.createNamedQuery(IntervalProductAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT);
        //            query.setParameter("relationship", authorizingRelationship);
        //            query.setParameter("child", child);
        //            query.setParameter("netRelationship", parentRelationship);
        //            query.setParameter("netChild", parent);
        //
        //        } else if (parentRelationship == null) {
        //            query = em.createNamedQuery(IntervalProductAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD);
        //            query.setParameter("relationship", authorizingRelationship);
        //            query.setParameter("parent", parent);
        //            query.setParameter("netRelationship", childRelationship);
        //            query.setParameter("netChild", child);
        //
        //        } else {
        //            query = em.createNamedQuery(IntervalProductAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD);
        //            query.setParameter("relationship", authorizingRelationship);
        //            query.setParameter("parentNetRelationship", parentRelationship);
        //            query.setParameter("parentNetChild", parent);
        //            query.setParameter("childNetRelationship", childRelationship);
        //            query.setParameter("childNetChild", child);
        //
        //        }
        //        List<?> results = query.getResultList();
        //
        //        return results.size() > 0;
        return false;
    }

    /**
     * @param agency
     * @param aspect
     */
    protected void initialize(Interval agency, Aspect<Interval> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (IntervalAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            IntervalAttribute attribute = new IntervalAttribute(
                                                                authorization.getAuthorizedAttribute(),
                                                                kernel.getCoreModel());
            attribute.setInterval(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
    }
}
