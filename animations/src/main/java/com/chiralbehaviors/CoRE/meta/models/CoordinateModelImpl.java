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

import javax.persistence.EntityManager;

import org.postgresql.pljava.TriggerData;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.coordinate.Coordinate;
import com.chiralbehaviors.CoRE.coordinate.CoordinateAttribute;
import com.chiralbehaviors.CoRE.coordinate.CoordinateAttributeAuthorization;
import com.chiralbehaviors.CoRE.coordinate.CoordinateNetwork;
import com.chiralbehaviors.CoRE.jsp.JSP;
import com.chiralbehaviors.CoRE.jsp.StoredProcedure;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelImpl;
import com.chiralbehaviors.CoRE.meta.CoordinateModel;
import com.chiralbehaviors.CoRE.network.Aspect;

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

        @Override
        public String toString() {
            return "Call [" + procedure + "]";
        }
    }

    private static interface Procedure<T> {
        T call(CoordinateModelImpl productModel) throws Exception;
    }

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(CoordinateModelImpl agencyModel) throws Exception {
                agencyModel.propagate();
                return null;
            }

            public String toString() {
                return "CoordinateModel.propagate";
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
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE
     * .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
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
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
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
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.meta
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
