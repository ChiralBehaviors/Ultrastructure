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
import com.hellblazer.CoRE.attribute.unit.Unit;
import com.hellblazer.CoRE.attribute.unit.UnitAttribute;
import com.hellblazer.CoRE.attribute.unit.UnitAttributeAuthorization;
import com.hellblazer.CoRE.attribute.unit.UnitNetwork;
import com.hellblazer.CoRE.jsp.JSP;
import com.hellblazer.CoRE.jsp.StoredProcedure;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.meta.UnitModel;
import com.hellblazer.CoRE.network.Aspect;

/**
 * @author hhildebrand
 * 
 */
public class UnitModelImpl
        extends
        AbstractNetworkedModel<Unit, UnitNetwork, UnitAttributeAuthorization, UnitAttribute>
        implements UnitModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new UnitModelImpl(em));
        }
    }

    private static interface Procedure<T> {
        T call(UnitModelImpl productModel) throws Exception;
    }

    private static final String UNIT_NETWORK_PROPAGATE = "unitNetwork.propagate";

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        if (!markPropagated(UNIT_NETWORK_PROPAGATE)) {
            return; // We be done
        }
        execute(new Procedure<Void>() {
            @Override
            public Void call(UnitModelImpl agencyModel) throws Exception {
                agencyModel.propagate();
                return null;
            }
        });
    }

    public static void track_network_deleted(final TriggerData data)
                                                                    throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(UnitModelImpl agencyModel) throws Exception {
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
    public UnitModelImpl(EntityManager em) {
        super(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public UnitModelImpl(EntityManager em, Kernel kernel) {
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
    public void authorize(Aspect<Unit> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            UnitAttributeAuthorization authorization = new UnitAttributeAuthorization(
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
     * com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.meta
     * .Aspect<RuleForm>[])
     */
    @SafeVarargs
    @Override
    public final Unit create(String name, String description,
                             Aspect<Unit> aspect, Aspect<Unit>... aspects) {
        Unit agency = new Unit(name, description, kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Unit> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.network
     * .Networked)
     */
    @Override
    public Unit create(Unit prototype) {
        Unit copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (UnitNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (UnitAttribute attribute : prototype.getAttributes()) {
            UnitAttribute clone = (UnitAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setUnit(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /**
     * @param agency
     * @param aspect
     */
    protected void initialize(Unit agency, Aspect<Unit> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (UnitAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            UnitAttribute attribute = new UnitAttribute(
                                                        authorization.getAuthorizedAttribute(),
                                                        kernel.getCoreModel());
            attribute.setUnit(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
    }
}
