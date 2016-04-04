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

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_AUTHORIZATION;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceAuthorizationRecord;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.collections.OaHashSet;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshot {
    private static final Logger log = LoggerFactory.getLogger(WorkspaceSnapshot.class);

    public static Result<WorkspaceAuthorizationRecord> getAuthorizations(UUID definingProduct,
                                                                         DSLContext create) {
        return create.selectFrom(WORKSPACE_AUTHORIZATION)
                     .where(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.eq(definingProduct))
                     .fetch();
    }

    public static void load(DSLContext create,
                            List<URL> resources) throws IOException,
                                                 SQLException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule(create));
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
                         definingProduct.getName(),
                         definingProduct.getVersion(),
                         resource.toExternalForm());
                workspace.load(create);
            } else if (existing.getVersion() < definingProduct.getVersion()) {
                log.info("Updating workspace [{}] from version:{} to version: {} from: {}",
                         definingProduct.getName(), existing.getVersion(),
                         definingProduct.getVersion(),
                         resource.toExternalForm());
                workspace.load(create);
            } else {
                log.info("Not updating workspace [{}] existing version: {} is higher than version: {} from: {}",
                         definingProduct.getName(), existing.getVersion(),
                         definingProduct.getVersion(),
                         resource.toExternalForm());
            }
        }
    }

    public static void load(DSLContext create, URL resource) throws IOException,
                                                             SQLException {
        load(create, Collections.singletonList(resource));
    }

    @SuppressWarnings("unchecked")
    public static List<UpdatableRecord<? extends UpdatableRecord<?>>> selectWorkspaceClosure(DSLContext create,
                                                                                             Product definingProduct) {
        List<UpdatableRecord<? extends UpdatableRecord<?>>> records = new ArrayList<>();
        Ruleform.RULEFORM.getTables()
                         .forEach(t -> {
                             if (!t.equals(WORKSPACE_AUTHORIZATION)) {
                                 records.addAll(create.selectDistinct(t.fields())
                                                      .from(t)
                                                      .join(WORKSPACE_AUTHORIZATION)
                                                      .on(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(definingProduct.getId()))
                                                      .and(((Field<UUID>) t.field("id")).notEqual(definingProduct.getId()))
                                                      .and(WORKSPACE_AUTHORIZATION.ID.equal((Field<UUID>) t.field("workspace")))
                                                      .fetchInto(t.getRecordType())
                                                      .stream()
                                                      .map(r -> (UpdatableRecord<?>) r)
                                                      .collect(Collectors.toList()));
                             }
                         });
        records.addAll(create.selectFrom(WORKSPACE_AUTHORIZATION)
                             .where(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.eq(definingProduct.getId()))
                             .fetch());
        return records;
    }

    protected Product                                             definingProduct;

    protected List<UpdatableRecord<? extends UpdatableRecord<?>>> records = new ArrayList<>();

    public WorkspaceSnapshot() {
        definingProduct = null;
    }

    public WorkspaceSnapshot(Product definingProduct, DSLContext create) {
        this.definingProduct = definingProduct;
        records = selectWorkspaceClosure(create, definingProduct);
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

        Set<UUID> exclude = new OaHashSet<>(otherVersion.records.size());
        for (Record record : otherVersion.records) {
            exclude.add((UUID) record.getValue("id"));
        }

        for (UpdatableRecord<? extends UpdatableRecord<?>> record : records) {
            UUID id = (UUID) record.getValue("id");
            if (!exclude.contains(id)) {
                delta.records.add(record);
            }
        }
        return delta;
    }

    public Product getDefiningProduct() {
        return definingProduct;
    }

    public List<UpdatableRecord<? extends UpdatableRecord<?>>> getRecords() {
        return records;
    }

    public void load(DSLContext create) throws SQLException {
        loadDefiningProduct(create);
        try {
            create.batchInsert(records)
                  .execute();
        } catch (DataAccessException e) {
            BatchUpdateException be = (BatchUpdateException) e.getCause();
            throw be.getNextException();
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
            log.info("Creating workspace [{}] version: {}",
                     definingProduct.getName(), definingProduct.getVersion());
            create.executeInsert(definingProduct);
        } else if (existing.getVersion() < definingProduct.getVersion()) {
            log.info("Updating workspace [{}] from version:{} to version: {} from: {}",
                     definingProduct.getName(), existing.getVersion(),
                     definingProduct.getVersion());
            create.executeUpdate(definingProduct);
        }
    }
}
