/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.internal.SessionImpl;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Interval;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.domain.Unit;
import com.chiralbehaviors.CoRE.jooq.tables.ChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.Job;
import com.chiralbehaviors.CoRE.jooq.tables.ParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.SelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.SiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.TriggerException;

/**
 * @author hhildebrand
 *
 *         This class implements the animations logic for the Ultrastructure
 *         model. Abstractly, this logic is driven by state events of an
 *         Ultrastructure instance. Conceptually, this is equivalent to database
 *         triggers. This class models a simple state model of persist, update,
 *         delete style events. The animations model is conceptually simple and
 *         unchanging, thus we don't need a general mechanism of dynamically
 *         registering triggers n' such. We just inline the animation logic in
 *         the state methods, delegating to the appropriate model for
 *         implementation. What this means in practice is that this is the class
 *         that creates the high level logic around state change of an
 *         Ultrastructure instance. This is the high level, disambiguation logic
 *         of Ultrastructure animation.
 *
 *         This is the Rule Engine (tm).
 */
public class Animations {

    private static final int                          MAX_JOB_PROCESSING = 10;

    private final Set<AttributeValue<?>>              attributeValues    = new HashSet<>();
    private final Set<ChildSequencingAuthorization>   childSequences     = new HashSet<>();
    private final EntityManager                       em;
    private boolean                                   inferAgencyNetwork;
    private boolean                                   inferAttributeNetwork;
    private boolean                                   inferIntervalNetwork;
    private boolean                                   inferLocationNetwork;
    private boolean                                   inferProductNetwork;
    private boolean                                   inferRelationshipNetwork;
    private boolean                                   inferStatusCodeNetwork;
    private boolean                                   inferUnitNetwork;
    private final Set<Job>                            jobs               = new LinkedHashSet<>();
    private final Model                               model;
    private final Set<Product>                        modifiedServices   = new HashSet<>();
    private final Set<ParentSequencingAuthorization>  parentSequences    = new HashSet<>();
    private final Set<SelfSequencingAuthorization>    selfSequences      = new HashSet<>();
    private final Set<SiblingSequencingAuthorization> siblingSequences   = new HashSet<>();

    public Animations(Model model, EntityManager em) {
        this.model = model;
        this.em = em;
        new AnimationsInterceptor((SessionImpl) em.getDelegate(), this);
    }

    /**
     * 
     */
    public void begin() {
        model.flushWorkspaces();
    }

    public void commit() throws TriggerException {
        flush();
        reset();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.agency.Agency)
     */

    public void delete(Agency a) {
        inferAgencyNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.agency.AgencyNetwork)
     */

    public void delete(AgencyNetwork a) {
        inferAgencyNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.attribute.Attribute)
     */

    public void delete(Attribute a) {
        inferAttributeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.attribute.AttributeNetwork)
     */

    public void delete(AttributeNetwork a) {
        inferAttributeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.time.Interval)
     */

    public void delete(Interval i) {
        inferIntervalNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.time.IntervalNetwork)
     */

    public void delete(IntervalNetwork i) {
        inferAttributeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.location.Location)
     */

    public void delete(Location l) {
        inferLocationNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.location.LocationNetwork)
     */

    public void delete(LocationNetwork l) {
        inferLocationNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.network.NetworkInference)
     */

    public void delete(Inference inference) {
        propagateAll();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.product.Product)
     */

    public void delete(Product p) {
        inferProductNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.product.ProductNetwork)
     */

    public void delete(ProductNetwork p) {
        inferProductNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.network.Relationship)
     */

    public void delete(Relationship r) {
        inferRelationshipNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.network.RelationshipNetwork)
     */

    public void delete(RelationshipNetwork r) {
        inferRelationshipNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.event.status.StatusCode)
     */

    public void delete(StatusCode s) {
        inferStatusCodeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork)
     */

    public void delete(StatusCodeNetwork s) {
        inferStatusCodeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.attribute.unit.Unit)
     */

    public void delete(Unit u) {
        inferUnitNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork)
     */

    public void delete(UnitNetwork u) {
        inferUnitNetwork = true;
    }

    public void flush() {
        em.flush();
        try {
            model.getJobModel()
                 .validateStateGraph(modifiedServices);
        } catch (SQLException e) {
            throw new TriggerException("StatusCodeSequencing validation failed",
                                       e);
        }
        validateAttributeValues();
        validateSequenceAuthorizations();
        propagate();
        int cycles = 0;
        Set<Job> processed = new HashSet<>(jobs.size());
        while (!jobs.isEmpty()) {
            if (cycles > MAX_JOB_PROCESSING) {
                throw new IllegalStateException("Processing more inserted job cycles than the maximum number of itterations allowed");
            }
            cycles++;
            List<Job> inserted = new ArrayList<>(jobs);
            jobs.clear();
            for (Job j : inserted) {
                if (processed.add(j)) {
                    process(j);
                }
            }
        }
        em.flush();
    }

    public EntityManager getEm() {
        return model.create();
    }

    /**
     * @return
     */
    public Model getModel() {
        return model;
    }

    public void inferNetworks() {
        if (ruleform instanceof Agency) {
            inferAgencyNetwork = true;
            return;
        }
        if (ruleform instanceof Attribute) {
            inferAttributeNetwork = true;
            return;
        }
        if (ruleform instanceof Interval) {
            inferIntervalNetwork = true;
            return;
        }
        if (ruleform instanceof Location) {
            inferLocationNetwork = true;
            return;
        }
        if (ruleform instanceof Product) {
            inferProductNetwork = true;
            return;
        }
        if (ruleform instanceof Relationship) {
            inferRelationshipNetwork = true;
            return;
        }
        if (ruleform instanceof StatusCode) {
            inferStatusCodeNetwork = true;
            return;
        }
        if (ruleform instanceof Unit) {
            inferUnitNetwork = true;
            return;
        }
        throw new IllegalStateException(String.format("Not a valid existential entity class %s",
                                                      ruleform.getClass()
                                                              .getSimpleName()));
    }

    public void log(StatusCodeSequencing scs) {
        modifiedServices.add(scs.getService());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.agency.AgencyNetwork)
     */

    public void persist(AgencyNetwork a) {
        inferAgencyNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.attribute.AttributeNetwork)
     */

    public void persist(AttributeNetwork a) {
        inferAttributeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.time.IntervalNetwork)
     */

    public void persist(IntervalNetwork i) {
        inferIntervalNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.Job)
     */

    public void persist(Job j) {
        jobs.add(j);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.location.LocationNetwork)
     */

    public void persist(LocationNetwork l) {
        inferLocationNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization)
     */

    public void persist(ChildSequencingAuthorization pcsa) {
        childSequences.add(pcsa);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.product.ProductNetwork)
     */

    public void persist(ProductNetwork p) {
        inferProductNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization)
     */

    public void persist(ParentSequencingAuthorization ppsa) {
        parentSequences.add(ppsa);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization)
     */

    public void persist(SelfSequencingAuthorization pssa) {
        selfSequences.add(pssa);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization)
     */

    public void persist(SiblingSequencingAuthorization pssa) {
        siblingSequences.add(pssa);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.network.RelationshipNetwork)
     */

    public void persist(RelationshipNetwork r) {
        inferRelationshipNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork)
     */

    public void persist(StatusCodeNetwork sc) {
        inferStatusCodeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing)
     */

    public void persist(StatusCodeSequencing scs) {
        modifiedServices.add(scs.getService());
    }

    public <T extends AttributeValue<?>> void persist(T value) {
        attributeValues.add(value);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork)
     */

    public void persist(UnitNetwork u) {
        inferUnitNetwork = true;
    }

    public void rollback() {
        em.getTransaction()
          .setRollbackOnly();
        reset();
        model.flushWorkspaces();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#update(com.chiralbehaviors.CoRE.event.Job)
     */

    public void update(Job j) {
        jobs.add(j);
    }

    private void clearPropagation() {
        inferIntervalNetwork = false;
        inferRelationshipNetwork = false;
        inferStatusCodeNetwork = false;
        inferUnitNetwork = false;
        inferProductNetwork = false;
        inferLocationNetwork = false;
        inferAttributeNetwork = false;
        inferAgencyNetwork = false;
        inferRelationshipNetwork = false;
    }

    private void clearSequences() {
        parentSequences.clear();
        childSequences.clear();
        siblingSequences.clear();
        selfSequences.clear();
    }

    private void process(Job j) {
        JobModel jobModel = model.getJobModel();
        jobModel.generateImplicitJobsForExplicitJobs(j,
                                                     model.getCurrentPrincipal()
                                                          .getPrincipal());
        jobModel.processJobSequencing(j);
    }

    private void propagate() {
        boolean initial = true;
        if (inferAgencyNetwork) {
            model.getAgencyModel()
                 .propagate(initial);
            initial = false;
        }
        if (inferAttributeNetwork) {
            model.getAttributeModel()
                 .propagate(initial);
            initial = false;
        }
        if (inferIntervalNetwork) {
            model.getIntervalModel()
                 .propagate(initial);
            initial = false;
        }
        if (inferLocationNetwork) {
            model.getLocationModel()
                 .propagate(initial);
            initial = false;
        }
        if (inferProductNetwork) {
            model.getProductModel()
                 .propagate(initial);
            initial = false;
        }
        if (inferRelationshipNetwork) {
            model.getRelationshipModel()
                 .propagate(initial);
            initial = false;
        }
        if (inferStatusCodeNetwork) {
            model.getStatusCodeModel()
                 .propagate(initial);
            initial = false;
        }
        if (inferUnitNetwork) {
            model.getUnitModel()
                 .propagate(initial);
        }
    }

    private void propagateAll() {
        inferIntervalNetwork = true;
        inferRelationshipNetwork = true;
        inferStatusCodeNetwork = true;
        inferUnitNetwork = true;
        inferProductNetwork = true;
        inferLocationNetwork = true;
        inferAttributeNetwork = true;
        inferAgencyNetwork = true;
        inferRelationshipNetwork = true;
    }

    private void reset() {
        clearPropagation();
        clearSequences();
        modifiedServices.clear();
        jobs.clear();
        attributeValues.clear();
    }

    private void validateAttributeValues() {
        validateEnums();
    }

    private void validateChildSequencing() {
        for (ChildSequencingAuthorization pcsa : childSequences) {

            try {
                model.getJobModel()
                     .ensureValidServiceAndStatus(pcsa.getNextChild(),
                                                  pcsa.getNextChildStatus());
            } catch (SQLException e) {
                throw new TriggerException(String.format("Invalid sequence: %s",
                                                         pcsa),
                                           e);
            }
        }
    }

    private void validateEnums() {
        for (AttributeValue<?> value : attributeValues) {
            Attribute attribute = value.getAttribute();
            Attribute validatingAttribute = model.getAttributeModel()
                                                 .getSingleChild(attribute,
                                                                 model.getKernel()
                                                                      .getIsValidatedBy());
            if (validatingAttribute != null) {
                List<AttributeMetaAttribute> attrs = model.getAttributeModel()
                                                          .getAttributeValues(validatingAttribute,
                                                                              attribute);
                if (attrs == null || attrs.size() == 0) {
                    throw new IllegalArgumentException("No valid values for attribute "
                                                       + attribute.getName());
                }
                boolean valid = false;
                for (AttributeMetaAttribute ama : attrs) {
                    if (ama.getValue() != null && ama.getValue()
                                                     .equals(value.getValue())) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    throw new IllegalArgumentException(String.format("%s is not a valid value for attribute %s",
                                                                     value,
                                                                     attribute));
                }
            }
        }
    }

    private void validateParentSequencing() {
        for (ParentSequencingAuthorization ppsa : parentSequences) {
            try {
                model.getJobModel()
                     .ensureValidServiceAndStatus(ppsa.getParent(),
                                                  ppsa.getParentStatusToSet());
            } catch (SQLException e) {
                throw new TriggerException("Invalid sequence", e);
            }
        }
    }

    private void validateSelfSequencing() {
        for (SelfSequencingAuthorization pssa : selfSequences) {
            try {
                model.getJobModel()
                     .ensureValidServiceAndStatus(pssa.getService(),
                                                  pssa.getStatusToSet());
            } catch (SQLException e) {
                throw new TriggerException("Invalid sequence", e);
            }
        }
    }

    private void validateSequenceAuthorizations() {
        validateParentSequencing();
        validateSiblingSequencing();
        validateChildSequencing();
        validateSelfSequencing();
    }

    private void validateSiblingSequencing() {
        for (SiblingSequencingAuthorization pssa : siblingSequences) {
            try {
                model.getJobModel()
                     .ensureValidServiceAndStatus(pssa.getNextSibling(),
                                                  pssa.getNextSiblingStatus());
            } catch (SQLException e) {
                throw new TriggerException("Invalid sequence", e);
            }
        }
    }
}
