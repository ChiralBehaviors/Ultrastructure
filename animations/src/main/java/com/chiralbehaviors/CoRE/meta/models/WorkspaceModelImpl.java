/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

import static com.chiralbehaviors.CoRE.meta.models.ModelImpl.clearPhantasmCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetPropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceLabelRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.meta.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceModelImpl implements WorkspaceModel {

    private final Model                     model;
    private final Map<UUID, WorkspaceScope> scopes = new HashMap<>();

    public WorkspaceModelImpl(Model model) {
        this.model = model;
    }

    @Override
    public WorkspaceScope createWorkspace(String name, String description,
                                          Product definingProduct, String iri,
                                          int version) {
        EditableWorkspace workspace = new DatabaseBackedWorkspace(definingProduct,
                                                                  model);
        workspace.add(definingProduct);
        Kernel kernel = model.getKernel();
        FacetRecord aspect = model.getPhantasmModel()
                                  .getFacetDeclaration(kernel.getIsA(),
                                                       kernel.getWorkspace());
        model.getPhantasmModel()
             .initialize(definingProduct, aspect, workspace);
        WorkspaceScope scope = workspace.getScope();
        scopes.put(definingProduct.getId(), scope);

        FacetPropertyRecord properties = model.records()
                                              .newFacetProperty();
        properties.setExistential(definingProduct.getId());
        properties.setFacet(aspect.getId());
        ObjectNode props = JsonNodeFactory.instance.objectNode();
        props.put("iri", iri);
        props.put("version", version);
        props.put("name", name);
        props.put("description", description);
        properties.setProperties(props);
        properties.insert();
        workspace.add(properties);
        return scope;
    }

    @Override
    public void flush() {
        for (WorkspaceScope scope : scopes.values()) {
            scope.getWorkspace()
                 .flushCache();
        }
    }

    @Override
    public WorkspaceLabelRecord get(Product definingProduct, String key) {
        return null;
    }

    @Override
    public List<WorkspaceLabelRecord> getByType(Product definingProduct,
                                                String type) {
        return null;
    }

    @Override
    public WorkspaceScope getScoped(Product definingProduct) {
        WorkspaceScope cached = scopes.get(definingProduct.getId());
        if (cached != null) {
            return cached;
        }
        WorkspaceScope scope = new DatabaseBackedWorkspace(definingProduct,
                                                           model).getScope();
        scopes.put(definingProduct.getId(), scope);
        return scope;
    }

    @Override
    public WorkspaceScope getScoped(UUID definingProduct) {
        Product product = model.records()
                               .resolve(definingProduct);
        if (product == null) {
            return null;
        }
        return getScoped(product);
    }

    @Override
    public void unload(Product definingProduct) {
        model.create()
             .batchDelete(WorkspaceSnapshot.selectForDelete(model.create(),
                                                            definingProduct))
             .execute();
        clearPhantasmCache();
    }
}
