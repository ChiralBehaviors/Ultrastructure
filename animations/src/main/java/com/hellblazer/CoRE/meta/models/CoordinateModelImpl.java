/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
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

package com.hellblazer.CoRE.meta.models;

import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.coordinate.Coordinate;
import com.hellblazer.CoRE.coordinate.CoordinateAttribute;
import com.hellblazer.CoRE.coordinate.CoordinateAttributeAuthorization;
import com.hellblazer.CoRE.coordinate.CoordinateNetwork;
import com.hellblazer.CoRE.jsp.JSP;
import com.hellblazer.CoRE.jsp.StoredProcedure;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.meta.CoordinateModel;
import com.hellblazer.CoRE.network.Aspect;

/**
 * @author hhildebrand
 * 
 */
public class CoordinateModelImpl
        extends
        AbstractNetworkedModel<Coordinate, CoordinateNetwork, CoordinateAttributeAuthorization, CoordinateAttribute>
        implements CoordinateModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new CoordinateModelImpl(em));
        }
    }

    private static interface Procedure<T> {
        T call(CoordinateModelImpl productModel) throws Exception;
    }

    private static final String COORDINATE_NETWORK_PROPAGATE = "coordinateNetwork.propagate";

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        if (!markPropagated(COORDINATE_NETWORK_PROPAGATE)) {
            return; // We be done
        }
        execute(new Procedure<Void>() {
            @Override
            public Void call(CoordinateModelImpl agencyModel) throws Exception {
                agencyModel.propagate();
                return null;
            }
        });
    }

    public static void track_network_deleted(final TriggerData data)
                                                                    throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(CoordinateModelImpl agencyModel) throws Exception {
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
    public CoordinateModelImpl(EntityManager em) {
        super(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public CoordinateModelImpl(EntityManager em, Kernel kernel) {
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
    public void authorize(Aspect<Coordinate> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            CoordinateAttributeAuthorization authorization = new CoordinateAttributeAuthorization(
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
    public Coordinate create(Coordinate prototype) {
        Coordinate copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (CoordinateNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (CoordinateAttribute attribute : prototype.getAttributes()) {
            CoordinateAttribute clone = (CoordinateAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setCoordinate(copy);
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
    @SafeVarargs
    @Override
    public final Coordinate create(String name, String description,
                                   Aspect<Coordinate> aspect,
                                   Aspect<Coordinate>... aspects) {
        Coordinate agency = new Coordinate(name, description,
                                           kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Coordinate> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /**
     * @param agency
     * @param aspect
     */
    protected void initialize(Coordinate agency, Aspect<Coordinate> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (CoordinateAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            CoordinateAttribute attribute = new CoordinateAttribute(
                                                                    authorization.getAuthorizedAttribute(),
                                                                    kernel.getCoreModel());
            attribute.setCoordinate(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
    }
}
