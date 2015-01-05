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
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationAttribute;
import com.chiralbehaviors.CoRE.location.LocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.meta.LocationModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class LocationModelImpl
        extends
        AbstractNetworkedModel<Location, LocationNetwork, LocationAttributeAuthorization, LocationAttribute>
        implements LocationModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new LocationModelImpl(em));
        }

        @Override
        public String toString() {
            return "Call [" + procedure + "]";
        }
    }

    private static interface Procedure<T> {
        T call(LocationModelImpl locationModel) throws Exception;
    }

    public static void onAbort() {
        deducing = false;
    }

    public static void onCommit() {
        deducing = false;
    }

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        if (deducing) {
            return;
        }
        deducing = true;
        execute(new Procedure<Void>() {
            @Override
            public Void call(LocationModelImpl locationModel) throws Exception {
                locationModel.propagate();
                return null;
            }
        });
    }

    private static <T> T execute(Procedure<T> procedure) throws SQLException {
        return JSP.call(new Call<T>(procedure));
    }

    private static boolean deducing;

    /**
     * @param em
     */
    public LocationModelImpl(EntityManager em) {
        super(em, KernelUtil.getKernel());
    }

    /**
     * @param em
     */
    public LocationModelImpl(EntityManager em, Kernel kernel) {
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
    public void authorize(Aspect<Location> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            LocationAttributeAuthorization authorization = new LocationAttributeAuthorization(
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
    public Location create(Location prototype) {
        Location copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (LocationNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (LocationAttribute attribute : prototype.getAttributes()) {
            LocationAttribute clone = (LocationAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setLocation(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Location, LocationAttribute> create(String name,
                                                     String description,
                                                     Aspect<Location> aspect) {
        Location location = new Location(name, description,
                                         kernel.getCoreModel());
        em.persist(location);
        return new Facet<Location, LocationAttribute>(aspect, location,
                                                      initialize(location,
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
    public final Location create(String name, String description,
                                 Aspect<Location> aspect,
                                 Aspect<Location>... aspects) {
        Location location = new Location(name, description,
                                         kernel.getCoreModel());
        em.persist(location);
        initialize(location, aspect);
        if (aspects != null) {
            for (Aspect<Location> a : aspects) {
                initialize(location, a);
            }
        }
        return location;
    }

    @Override
    public List<LocationNetwork> getInterconnections(Collection<Location> parents,
                                                     Collection<Relationship> relationships,
                                                     Collection<Location> children) {
        TypedQuery<LocationNetwork> query = em.createNamedQuery(LocationNetwork.GET_NETWORKS,
                                                                LocationNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationship", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /**
     * @param location
     * @param aspect
     */
    protected List<LocationAttribute> initialize(Location location,
                                                 Aspect<Location> aspect) {
        location.link(aspect.getClassification(), aspect.getClassifier(),
                      kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        List<LocationAttribute> attributes = new ArrayList<>();
        for (LocationAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            LocationAttribute attribute = new LocationAttribute(
                                                                authorization.getAuthorizedAttribute(),
                                                                kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setLocation(location);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }
}
