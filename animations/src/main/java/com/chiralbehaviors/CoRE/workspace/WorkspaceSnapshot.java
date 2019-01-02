/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.workspace;

import static com.chiralbehaviors.CoRE.jooq.Tables.AUTHENTICATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.TOKEN;
import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_LABEL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetPropertyRecord;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.kernel.phantasm.Workspace;
import com.chiralbehaviors.CoRE.kernel.phantasm.workspaceProperties.WorkspaceProperties;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshot {
    private static final Logger log = LoggerFactory.getLogger(WorkspaceSnapshot.class);

    public static void load(DSLContext create,
                            List<URL> resources) throws IOException,
                                                 SQLException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        for (URL resource : resources) {
            WorkspaceSnapshot workspace;
            try (InputStream is = resource.openStream();) {
                workspace = mapper.readValue(is, WorkspaceSnapshot.class);
            } catch (IOException e) {
                log.warn("Unable to load workspace: {}",
                         resource.toExternalForm(), e);
                throw e;
            }
            ExistentialRecord definingProduct = workspace.getDefiningProduct();
            ExistentialRecord existing = create.selectFrom(EXISTENTIAL)
                                               .where(EXISTENTIAL.ID.equal(definingProduct.getId()))
                                               .fetchOne();
            if (existing == null) {
                log.info("Creating workspace [{}] version: {} from: {}",
                         definingProduct.getName(), workspace.getVersion(),
                         resource.toExternalForm());
                workspace.load(create);
            } else {
                @SuppressWarnings("resource")
                Model model = new ModelImpl(create);
                Workspace existingWorkspace = model.wrap(Workspace.class,
                                                         model.records()
                                                              .resolve(definingProduct));
                WorkspaceProperties props = existingWorkspace.get_Properties();
                if (props.getVersion() < workspace.getVersion()) {
                    log.info("Updating workspace [{}] from version:{} to version: {} from: {}",
                             definingProduct.getName(), existing.getVersion(),
                             workspace.getVersion(), resource.toExternalForm());
                    workspace.load(create);
                } else {
                    log.info("Not updating workspace [{}] existing version: {} is equal to or higher than version: {} from: {}",
                             definingProduct.getName(), existing.getVersion(),
                             workspace.getVersion(), resource.toExternalForm());
                }
            }
        }
    }

    public static void load(DSLContext create, URL resource) throws IOException,
                                                             SQLException {
        load(create, Collections.singletonList(resource));
    }

    @SuppressWarnings("unchecked")
    public static List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> selectForDelete(DSLContext create,
                                                                                                                 Product definingProduct) {
        List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> records = new ArrayList<>();
        Ruleform.RULEFORM.getTables()
                         .stream()
                         .filter(t -> !t.equals(TOKEN))
                         .filter(t -> !t.equals(AUTHENTICATION))
                         .filter(t -> !t.equals(WORKSPACE_LABEL))
                         .forEach(t -> {
                             create.selectDistinct(t.fields())
                                   .from(t)
                                   .join(WORKSPACE_LABEL)
                                   .on(WORKSPACE_LABEL.WORKSPACE.eq(definingProduct.getId()))
                                   .and(((Field<UUID>) t.field("id")).equal(WORKSPACE_LABEL.REFERENCE))
                                   .and(((Field<UUID>) t.field("id")).notEqual(definingProduct.getId()))
                                   .fetchInto(t.getRecordType())
                                   .stream()
                                   .map(r -> (UpdatableRecord<?>) r)
                                   .forEach(r -> records.add(r));
                         });
        create.selectFrom(WORKSPACE_LABEL)
              .where(WORKSPACE_LABEL.WORKSPACE.eq(definingProduct.getId()))
              .forEach(r -> records.add(r));
        return records;
    }

    @SuppressWarnings("unchecked")
    public static List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> selectWorkspaceClosure(DSLContext create,
                                                                                                                        Product definingProduct) {
        List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> records = new ArrayList<>();
        Ruleform.RULEFORM.getTables()
                         .stream()
                         .filter(t -> !t.equals(TOKEN))
                         .filter(t -> !t.equals(AUTHENTICATION))
                         .filter(t -> !t.equals(WORKSPACE_LABEL))
                         .forEach(t -> {
                             create.selectDistinct(t.fields())
                                   .from(t)
                                   .join(WORKSPACE_LABEL)
                                   .on(((Field<UUID>) t.field("id")).equal(WORKSPACE_LABEL.REFERENCE))
                                   .where(WORKSPACE_LABEL.WORKSPACE.equal(definingProduct.getId()))
                                   .and(((Field<UUID>) t.field("id")).notEqual(definingProduct.getId()))
                                   .fetchInto(t.getRecordType())
                                   .stream()
                                   .map(r -> (UpdatableRecord<?>) r)
                                   .forEach(r -> records.add(r));
                         });
        create.selectFrom(WORKSPACE_LABEL)
              .where(WORKSPACE_LABEL.WORKSPACE.eq(definingProduct.getId()))
              .stream()
              .map(r -> (UpdatableRecord<?>) r)
              .forEach(r -> records.add(r));
        return records;
    }

    protected Product                                                                        definingProduct;
    protected List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> inserts = new ArrayList<>();
    protected List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> updates = new ArrayList<>();

    public WorkspaceSnapshot() {
        definingProduct = null;
    }

    public WorkspaceSnapshot(Product definingProduct, DSLContext create) {
        this.definingProduct = definingProduct;
        inserts = selectWorkspaceClosure(create, definingProduct);
        for (UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>> record : inserts) {
            @SuppressWarnings("unchecked")
            Field<Integer> version = (Field<Integer>) record.getTable()
                                                            .field("version");
            record.setValue(version, Integer.valueOf(1));
        }
    }

    /**
     * Calculate the delta graph between this workspace and a different version.
     * The delta graph's frontier will include any references to ruleforms
     * defined in the previous workspace.
     *
     * The delta graph is defined to be everything that is in this workspace
     * minus the things that are in the other version of the workspace.
     * Ruleforms remaining in this delta graph will have references to ruleforms
     * defined in the other version in the frontier of the returned workspace.
     *
     * @param otherVersion
     *            - the other version of the workspace
     * @return the workspace snapshot containing the delta graph between this
     *         version and the other version
     */
    public WorkspaceSnapshot deltaFrom(DSLContext create,
                                       WorkspaceSnapshot otherVersion) {
        if (!otherVersion.getDefiningProduct()
                         .equals(definingProduct)) {
            return this; // by workspace graph closure definition
        }

        WorkspaceSnapshot delta = new WorkspaceSnapshot();
        delta.definingProduct = definingProduct;

        Map<UUID, UpdatableRecord<? extends UpdatableRecord<?>>> existing = new HashMap<>(otherVersion.inserts.size());
        for (UpdatableRecord<? extends UpdatableRecord<?>> record : otherVersion.inserts) {
            existing.put((UUID) record.getValue("id"), record);
        }

        for (UpdatableRecord<? extends UpdatableRecord<?>> record : inserts) {
            UpdatableRecord<? extends UpdatableRecord<?>> existingRecord = existing.get((UUID) record.getValue("id"));
            if (existingRecord == null) {
                delta.inserts.add(record);
            } else if (changed(existingRecord, record)) {
                delta.updates.add(record);
            }
        }
        return delta;
    }

    public Product getDefiningProduct() {
        return definingProduct;
    }

    public List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> getInserts() {
        return inserts;
    }

    public List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> getUpdates() {
        return updates;
    }

    @JsonIgnore
    public int getVersion() {
        return getInserts().stream()
                           .filter(r -> r instanceof FacetPropertyRecord)
                           .map(r -> (FacetPropertyRecord) r)
                           .filter(a -> a.getExistential()
                                         .equals(definingProduct.getId()))
                           .filter(a -> a.getProperties() != null)
                           .map(a -> a.getProperties()
                                      .get("version"))
                           .filter(a -> a != null)
                           .map(a -> ((IntNode) a).intValue())
                           .findFirst()
                           .orElseGet(() -> getUpdates().stream()
                                                        .filter(r -> r instanceof FacetPropertyRecord)
                                                        .map(r -> (FacetPropertyRecord) r)
                                                        .filter(a -> a.getExistential()
                                                                      .equals(definingProduct.getId()))
                                                        .filter(a -> a.getProperties() != null)
                                                        .map(a -> a.getProperties()
                                                                   .get("version"))
                                                        .map(a -> ((IntNode) a).intValue())
                                                        .findFirst()
                                                        .orElse(0));
    }

    public void load(DSLContext create) throws SQLException {
        loadDefiningProduct(create);
        List<UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>>> failed = new ArrayList<>();
        try {

            for (UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>> record : inserts) {
                Table<? extends UpdatableRecord<? extends UpdatableRecord<?>>> table = record.getTable();
                @SuppressWarnings("unchecked")
                Field<Integer> versionField = (Field<Integer>) table.field("version");
                record.set(versionField, 0);
            }
            int index = 0;
            for (int result : create.batchInsert(inserts)
                                    .execute()) {
                if (result != 1) {
                    failed.add(inserts.get(index));
                }
                index++;
            }

            for (UpdatableRecord<? extends UpdatableRecord<? extends UpdatableRecord<?>>> record : updates) {
                Table<? extends UpdatableRecord<? extends UpdatableRecord<?>>> table = record.getTable();
                @SuppressWarnings("unchecked")
                Field<Integer> versionField = (Field<Integer>) table.field("version");
                @SuppressWarnings("unchecked")
                Field<UUID> idField = (Field<UUID>) table.field("id");
                Integer existingVersion = (Integer) create.select(versionField)
                                                          .from(table)
                                                          .where(idField.eq((UUID) record.get("id")))
                                                          .fetchOne()
                                                          .component1();
                record.set(versionField, existingVersion);
            }
            index = 0;
            for (int result : create.batchUpdate(updates)
                                    .execute()) {
                if (result != 1) {
                    failed.add(updates.get(index));
                }
                index++;
            }
        } catch (DataAccessException e) {
            BatchUpdateException be = (BatchUpdateException) e.getCause();
            throw be.getNextException();
        }
        if (!failed.isEmpty()) {
            failed.forEach(e -> {
                Table<? extends UpdatableRecord<? extends UpdatableRecord<?>>> table = e.getTable();
                @SuppressWarnings("unchecked")
                Field<UUID> idField = (Field<UUID>) table.field("id");
                UpdatableRecord<? extends UpdatableRecord<?>> existing = create.selectFrom(table)
                                                                               .where(idField.eq((UUID) e.get("id")))
                                                                               .fetchOne();
                log.warn("unable to update:\n {}\n{}", e, existing);
            });
        }
    }

    public void serializeTo(OutputStream os) throws JsonGenerationException,
                                             JsonMappingException, IOException {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new CoREModule());
        objMapper.writerWithDefaultPrettyPrinter()
                 .writeValue(os, this);
    }

    protected void loadDefiningProduct(DSLContext create) {
        ExistentialRecord existing = definingProduct == null ? null
                                                             : create.selectFrom(EXISTENTIAL)
                                                                     .where(EXISTENTIAL.ID.equal(definingProduct.getId()))
                                                                     .fetchOne();
        if (existing == null) {
            create.executeInsert(definingProduct);
        } else if (existing.getVersion() < definingProduct.getVersion()) {
            create.executeUpdate(definingProduct);
        }
    }

    private boolean changed(UpdatableRecord<? extends UpdatableRecord<?>> existing,
                            UpdatableRecord<? extends UpdatableRecord<?>> test) {
        for (Field<?> field : existing.getTable()
                                      .fields()) {
            if ("version".equals(field.getName())
                || "updated".equals(field.getName())) {
                continue;
            }
            Object previousValue = existing.getValue(field);
            Object currentValue = test.get(field);
            if (previousValue != null && currentValue != null) {
                if (!previousValue.equals(currentValue)) {
                    return true;
                }
            } else if (previousValue != null || currentValue != null) {
                return true;
            }
        }
        return false;
    }
}
