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

import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_AUTHORIZATION;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableRecord;

import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceAuthorizationRecord;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.collections.OaHashSet;

/**
 * @author hhildebrand
 *
 */
@JsonPropertyOrder({ "definingProduct", "frontier", "records" })
public class WorkspaceSnapshot {

    public static Result<WorkspaceAuthorizationRecord> getAuthorizations(UUID definingProduct,
                                                                         DSLContext create) {
        return create.selectFrom(WORKSPACE_AUTHORIZATION)
                     .where(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.eq(definingProduct))
                     .fetch();
    }

    @JsonProperty
    private UUID                 definingProduct;
    @JsonProperty
    private List<UUID>           frontier;
    @JsonProperty
    private List<TableRecord<?>> records;

    public WorkspaceSnapshot() {
        records = new ArrayList<>();
        definingProduct = null;
        frontier = new ArrayList<>();
    }

    public WorkspaceSnapshot(UUID definingProduct, DSLContext em) {
        this(definingProduct, getAuthorizations(definingProduct, em), em);
    }

    public WorkspaceSnapshot(UUID definingProduct,
                             Result<WorkspaceAuthorizationRecord> auths,
                             DSLContext create) {
        this.definingProduct = definingProduct;
        frontier = new ArrayList<>();
        records = new WorkspaceClosure(definingProduct,
                                       frontier).compute(create, auths);
    }

    @SuppressWarnings("unchecked")
    public void load(DSLContext create) {
        records.forEach(r -> {
            @SuppressWarnings("rawtypes")
            TableRecord r2 = r;
            create.executeInsert(r2);
        });
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

        Set<UUID> exclude = new OaHashSet<UUID>(1024);
        for (TableRecord<?> record : otherVersion.records) {
            UUID id = (UUID) record.getValue("id");
            if (!definingProduct.equals(id)) {
                exclude.add(id);
            }
        }

        for (TableRecord<?> record : records) {
            UUID id = (UUID) record.getValue("id");
            if (!exclude.contains(id)) {
                delta.records.add(record);
            }
        }

        delta.frontier = new ArrayList<>();
        return delta;
    }

    public UUID getDefiningProduct() {
        return definingProduct;
    }

    public List<UUID> getFrontier() {
        return frontier;
    }

    public List<TableRecord<?>> getRecords() {
        return records;
    }

    public void serializeTo(OutputStream os) throws JsonGenerationException,
                                             JsonMappingException, IOException {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new CoREModule());
        objMapper.writerWithDefaultPrettyPrinter()
                 .writeValue(os, this);
    }
}
