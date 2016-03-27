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

import static com.chiralbehaviors.CoRE.jooq.Tables.CHILD_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;
import static com.chiralbehaviors.CoRE.jooq.Tables.JOB;
import static com.chiralbehaviors.CoRE.jooq.Tables.NETWORK_INFERENCE;
import static com.chiralbehaviors.CoRE.jooq.Tables.PARENT_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.SELF_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.SIBLING_SEQUENCING_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.STATUS_CODE_SEQUENCING;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.jooq.RecordContext;
import org.jooq.RecordType;
import org.jooq.impl.DefaultRecordListener;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.TriggerException;

/**
 * @author hhildebrand
 *
 *         This class implements the animations logic for the Ultrastructure
 *         model. Abstractly, this logic is driven by state events of an
 *         Ultrastructure instance. Conceptually, this is equivalent to database
 *         triggers. This class models a simple state model of insert, update,
 *         delete style events. The animations model is conceptually simple and
 *         unchanging, thus we don't need a general mechanism of dynamically
 *         registering triggers n' such. We just inline the animation logic in
 *         the state methods, delegating to the appropriate model for
 *         implementation. What this means in practice is that this is the class
 *         that creates the high level logic around state change of an
 *         Ultrastructure instance.
 * 
 *         This is the high level, core disambiguation logic of Ultrastructure
 *         animation.
 *
 *         This is the Rule Engine (tm).
 */
public class Animations extends DefaultRecordListener {

    private static final int                                  MAX_JOB_PROCESSING = 10;
    private static final Consumer<RecordContext>              NULL_CONSUMER      = f -> {
                                                                                 };

    private final Map<RecordType<?>, Consumer<RecordContext>> afterDelete        = new HashMap<>();
    private final Map<RecordType<?>, Consumer<RecordContext>> afterInsert        = new HashMap<>();
    private final Map<RecordType<?>, Consumer<RecordContext>> afterUpdate        = new HashMap<>();
    private final Set<ExistentialAttributeRecord>             attributeValues    = new HashSet<>();
    private final Set<ChildSequencingAuthorizationRecord>     childSequences     = new HashSet<>();
    private final Inference                                   inference;
    private boolean                                           inferNetwork;
    private final List<JobRecord>                             jobs               = new ArrayList<>();
    private final Model                                       model;
    private final Set<Product>                                modifiedServices   = new HashSet<>();
    private final Set<ParentSequencingAuthorizationRecord>    parentSequences    = new HashSet<>();
    private final Set<SelfSequencingAuthorizationRecord>      selfSequences      = new HashSet<>();
    private final Set<SiblingSequencingAuthorizationRecord>   siblingSequences   = new HashSet<>();

    public Animations(Model model, Inference inference) {
        this.model = model;
        this.inference = inference;
        initializeTriggers();
    }

    public void begin() {
        model.flushWorkspaces();
    }

    public void commit() throws TriggerException {
        flush();
        reset();
    }

    @Override
    public void deleteEnd(RecordContext ctx) {
        afterDelete.computeIfAbsent(ctx.recordType(), k -> NULL_CONSUMER)
                   .accept(ctx);
    }

    public void flush() {
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
        Set<JobRecord> processed = new HashSet<>(jobs.size());
        while (!jobs.isEmpty()) {
            if (cycles > MAX_JOB_PROCESSING) {
                throw new IllegalStateException(String.format("Exceeded the maximum number of job cycles allowed [%s]",
                                                              MAX_JOB_PROCESSING));
            }
            cycles++;
            List<JobRecord> inserted = new ArrayList<>(jobs);
            jobs.clear();
            for (JobRecord j : inserted) {
                if (processed.add(j)) {
                    process(j);
                }
            }
        }
    }

    public Model getModel() {
        return model;
    }

    public void inferNetworks() {
        inferNetwork = true;
    }

    @Override
    public void insertEnd(RecordContext ctx) {
        afterInsert.computeIfAbsent(ctx.recordType(), k -> NULL_CONSUMER)
                   .accept(ctx);
    }

    public void rollback() {
        reset();
        model.flushWorkspaces();
    }

    @Override
    public void updateEnd(RecordContext ctx) {
        afterUpdate.computeIfAbsent(ctx.recordType(), k -> NULL_CONSUMER)
                   .accept(ctx);
    }

    private void clearSequences() {
        parentSequences.clear();
        childSequences.clear();
        siblingSequences.clear();
        selfSequences.clear();
    }

    private void delete(ExistentialNetworkRecord inference) {
        inferNetwork = true;
    }

    private void delete(ExistentialRecord existentialRecord) {
        inferNetwork = true;
    }

    private void delete(NetworkInferenceRecord inference) {
        inferNetwork = true;
    }

    private void initializeTriggers() {
        afterUpdate.put(JOB.recordType(),
                        ctx1 -> update((JobRecord) ctx1.record()));
        afterInsert.put(STATUS_CODE_SEQUENCING.recordType(),
                        ctx -> insert((StatusCodeSequencingRecord) ctx.record()));
        afterInsert.put(JOB.recordType(),
                        ctx2 -> insert((JobRecord) ctx2.record()));
        afterInsert.put(EXISTENTIAL.recordType(),
                        ctx -> insert((ExistentialRecord) ctx.record()));
        afterInsert.put(CHILD_SEQUENCING_AUTHORIZATION.recordType(),
                        ctx -> insert((ChildSequencingAuthorizationRecord) ctx.record()));
        afterInsert.put(EXISTENTIAL_NETWORK.recordType(),
                        ctx -> insert((ExistentialNetworkRecord) ctx.record()));
        afterInsert.put(EXISTENTIAL_ATTRIBUTE.recordType(),
                        ctx -> insert((ExistentialAttributeRecord) ctx.record()));
        afterInsert.put(NETWORK_INFERENCE.recordType(),
                        ctx -> insert((NetworkInferenceRecord) ctx.record()));
        afterInsert.put(PARENT_SEQUENCING_AUTHORIZATION.recordType(),
                        ctx -> insert((ParentSequencingAuthorizationRecord) ctx.record()));
        afterInsert.put(SELF_SEQUENCING_AUTHORIZATION.recordType(),
                        ctx -> insert((SelfSequencingAuthorizationRecord) ctx.record()));
        afterInsert.put(SIBLING_SEQUENCING_AUTHORIZATION.recordType(),
                        ctx -> insert((SiblingSequencingAuthorizationRecord) ctx.record()));
        afterUpdate.put(STATUS_CODE_SEQUENCING.recordType(),
                        ctx -> modify((StatusCodeSequencingRecord) ctx.record()));
        afterDelete.put(EXISTENTIAL.recordType(),
                        ctx -> delete((ExistentialRecord) ctx.record()));
        afterDelete.put(EXISTENTIAL_NETWORK.recordType(),
                        ctx -> delete((ExistentialNetworkRecord) ctx.record()));
        afterDelete.put(NETWORK_INFERENCE.recordType(),
                        ctx -> delete((NetworkInferenceRecord) ctx.record()));

    }

    private void insert(ChildSequencingAuthorizationRecord pcsa) {
        childSequences.add(pcsa);
    }

    private void insert(ExistentialAttributeRecord value) {
        attributeValues.add(value);
    }

    private void insert(ExistentialNetworkRecord a) {
        inferNetwork = true;
    }

    private void insert(ExistentialRecord a) {
        inferNetwork = true;
    }

    private void insert(JobRecord j) {
        model.getJobModel()
             .log(j, "Initial insertion of job");
        jobs.add(j);
    }

    private void insert(NetworkInferenceRecord a) {
        inferNetwork = true;
    }

    private void insert(ParentSequencingAuthorizationRecord ppsa) {
        parentSequences.add(ppsa);
    }

    private void insert(SelfSequencingAuthorizationRecord pssa) {
        selfSequences.add(pssa);
    }

    private void insert(SiblingSequencingAuthorizationRecord pssa) {
        siblingSequences.add(pssa);
    }

    private void insert(StatusCodeSequencingRecord scs) {
        modifiedServices.add(model.records()
                                  .resolve(scs.getService()));
    }

    private void modify(StatusCodeSequencingRecord scs) {
        modifiedServices.add(model.records()
                                  .resolve(scs.getService()));
    }

    private void process(JobRecord j) {
        JobModel jobModel = model.getJobModel();
        jobModel.generateImplicitJobsForExplicitJobs(j);
        jobModel.processJobSequencing(j);
    }

    private void propagate() {
        if (inferNetwork) {
            inference.propagate();
        }
    }

    private void reset() {
        inferNetwork = false;
        clearSequences();
        modifiedServices.clear();
        jobs.clear();
        attributeValues.clear();
    }

    private void update(JobRecord j) {
        jobs.add(j);
    }

    private void validateAttributeValues() {
        validateEnums();
    }

    private void validateChildSequencing() {
        for (ChildSequencingAuthorizationRecord pcsa : childSequences) {

            try {
                model.getJobModel()
                     .ensureValidServiceAndStatus(model.records()
                                                       .resolve(pcsa.getNextChild()),
                                                  model.records()
                                                       .resolve(pcsa.getNextChildStatus()));
            } catch (SQLException e) {
                throw new TriggerException(String.format("Invalid sequence: %s",
                                                         pcsa),
                                           e);
            }
        }
    }

    private void validateEnums() {
        //        for (ExistentialAttributeRecord value : attributeValues) {
        //            Attribute attribute = value.getAttribute();
        //            Attribute validatingAttribute = model.getAttributeModel()
        //                                                 .getSingleChild(attribute,
        //                                                                 model.getKernel()
        //                                                                      .getIsValidatedBy());
        //            if (validatingAttribute != null) {
        //                List<AttributeMetaAttribute> attrs = model.getAttributeModel()
        //                                                          .getAttributeValues(validatingAttribute,
        //                                                                              attribute);
        //                if (attrs == null || attrs.size() == 0) {
        //                    throw new IllegalArgumentException("No valid values for attribute "
        //                                                       + attribute.getName());
        //                }
        //                boolean valid = false;
        //                for (AttributeMetaAttribute ama : attrs) {
        //                    if (ama.getValue() != null && ama.getValue()
        //                                                     .equals(value.getValue())) {
        //                        valid = true;
        //                        break;
        //                    }
        //                }
        //                if (!valid) {
        //                    throw new IllegalArgumentException(String.format("%s is not a valid value for attribute %s",
        //                                                                     value,
        //                                                                     attribute));
        //                }
        //            }
        //        }
    }

    private void validateParentSequencing() {
        for (ParentSequencingAuthorizationRecord ppsa : parentSequences) {
            try {
                model.getJobModel()
                     .ensureValidServiceAndStatus(model.records()
                                                       .resolve(ppsa.getParent()),
                                                  model.records()
                                                       .resolve(ppsa.getParentStatusToSet()));
            } catch (SQLException e) {
                throw new TriggerException("Invalid sequence", e);
            }
        }
    }

    private void validateSelfSequencing() {
        for (SelfSequencingAuthorizationRecord pssa : selfSequences) {
            try {
                model.getJobModel()
                     .ensureValidServiceAndStatus(model.records()
                                                       .resolve(pssa.getService()),
                                                  model.records()
                                                       .resolve(pssa.getStatusToSet()));
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
        for (SiblingSequencingAuthorizationRecord pssa : siblingSequences) {
            try {
                model.getJobModel()
                     .ensureValidServiceAndStatus(model.records()
                                                       .resolve(pssa.getNextSibling()),
                                                  model.records()
                                                       .resolve(pssa.getNextSiblingStatus()));
            } catch (SQLException e) {
                throw new TriggerException("Invalid sequence", e);
            }
        }
    }
}
