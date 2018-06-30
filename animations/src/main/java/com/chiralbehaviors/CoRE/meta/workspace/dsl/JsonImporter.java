/**
 * Copyright (c) 2018 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.workspace.dsl;

import java.util.UUID;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.Workspace;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.json.Existential;
import com.chiralbehaviors.CoRE.meta.workspace.json.Existential.Domain;
import com.chiralbehaviors.CoRE.meta.workspace.json.Facet;
import com.chiralbehaviors.CoRE.meta.workspace.json.Import;
import com.chiralbehaviors.CoRE.meta.workspace.json.JsonWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.json.Rel;
import com.hellblazer.utils.Tuple;

/**
 * @author halhildebrand
 *
 */
public class JsonImporter {
    private static final String THIS = "this";
    private final JsonWorkspace dsl;
    private final Model         model;
    private WorkspaceScope      scope;
    private UUID                uuid;

    private EditableWorkspace   workspace;

    public JsonImporter(JsonWorkspace dsl, Model model) {
        this.dsl = dsl;
        this.model = model;
    }

    public WorkspaceAccessor getWorkspace() {
        return workspace;
    }

    public JsonImporter initialize() {
        uuid = WorkspaceAccessor.uuidOf(dsl.uri);
        return this;
    }

    public WorkspaceAccessor load(Product definingProduct) {
        definingProduct.refresh();
        definingProduct.setName(dsl.name);
        definingProduct.setDescription(dsl.description);
        definingProduct.update();
        scope = model.getWorkspaceModel()
                     .getScoped(definingProduct);
        workspace = (EditableWorkspace) scope.getWorkspace();
        loadWorkspace();
        Workspace phantasm = model.wrap(Workspace.class, definingProduct);
        phantasm.setName(dsl.name);
        phantasm.setDescription(dsl.description);
        return workspace;
    }

    public JsonImporter manifest() {
        initialize();
        if (dsl.version == 1) {
            createWorkspace();
        } else {
            addToWorkspace();
        }
        return this;
    }

    public void setScope(WorkspaceScope scope) {
        this.scope = scope;
    }

    private WorkspaceAccessor addToWorkspace() {
        Product definingProduct = getWorkspaceProduct();
        if (definingProduct == null) {
            throw new IllegalStateException(String.format("Workspace %s does not exist, cannot update to version %s",
                                                          dsl.name,
                                                          dsl.version));
        }
        Workspace existing = model.wrap(Workspace.class, definingProduct);

        if (existing.getVersion() >= dsl.version) {
            throw new IllegalStateException(String.format("Workspace %s is at version %s, unable to update to %s",
                                                          dsl.name,
                                                          existing.getVersion(),
                                                          dsl.version));
        }

        WorkspaceAccessor loaded = load(definingProduct);
        existing.setVersion(dsl.version);
        return loaded;
    }

    private WorkspaceAccessor createWorkspace() {
        if (getWorkspaceProduct() != null) {
            Workspace phantasm = model.wrap(Workspace.class,
                                            getWorkspaceProduct());
            throw new IllegalStateException(String.format("Workspace %s already exists at version %s, not created",
                                                          phantasm.getName(),
                                                          phantasm.getVersion()));
        }

        Product definingProduct = createWorkspaceProduct();

        scope = model.getWorkspaceModel()
                     .createWorkspace(definingProduct);
        workspace = (EditableWorkspace) scope.getWorkspace();

        Workspace phantasm = model.wrap(Workspace.class, definingProduct);
        phantasm.setIRI(dsl.uri);
        loadWorkspace();
        return workspace;
    }

    private Product createWorkspaceProduct() {
        Product workspaceProduct = model.records()
                                        .newProduct(dsl.name, dsl.description);

        workspaceProduct.setId(uuid);
        workspaceProduct.setVersion(-1);
        workspaceProduct.insert();
        return workspaceProduct;
    }

    private ExistentialDomain domain(Existential existential) {
        switch (existential.domain) {
            case Agency:
                return ExistentialDomain.Agency;
            case Interval:
                return ExistentialDomain.Interval;
            case Location:
                return ExistentialDomain.Location;
            case Product:
                return ExistentialDomain.Product;
            case Relationship:
                return ExistentialDomain.Relationship;
            case StatusCode:
                return ExistentialDomain.StatusCode;
            default:
                throw new IllegalArgumentException("invalid domain: "
                                                   + existential.domain);

        }
    }

    private Product getWorkspaceProduct() {
        return model.records()
                    .resolve(uuid);
    }

    @SuppressWarnings("deprecation")
    private FacetRecord load(String name, Facet facet) {
        Relationship classifier = model.records()
                                       .resolve(resolve(facet.classifier));
        ExistentialRuleform classification = model.records()
                                                  .resolve(resolve(facet.classification));

        FacetRecord authorization = model.getPhantasmModel()
                                         .getFacetDeclaration(classifier,
                                                              classification);
        if (authorization != null) {
            return authorization;
        }
        authorization = model.records()
                             .newFacet();
        authorization.setClassifier(classifier.getId());
        authorization.setClassification(classification.getId());
        authorization.setName(name);
        authorization.setSchema(facet.schema);
        authorization.insert();
        workspace.add(authorization);
        return authorization;
    }

    private void load(String name, Rel relationship) {
        if (relationship.inverse == null) {
            Relationship rel = model.records()
                                    .newRelationship(name,
                                                     relationship.description);
            rel.setInverse(rel.getId());
            rel.insert();
            workspace.put(name, rel);
        } else {
            Tuple<Relationship, Relationship> relationships = model.records()
                                                                   .newRelationship(name,
                                                                                    relationship.description,
                                                                                    relationship.inverse.name,
                                                                                    relationship.inverse.description);

            relationships.a.insert();
            relationships.b.insert();
            workspace.put(name, relationships.a);
            workspace.put(relationship.inverse.name, relationships.b);
        }
    }

    private void loadEdges() {
        // TODO Auto-generated method stub

    }

    private void loadExistentials() {
        dsl.existentials.forEach((name, existential) -> {
            if (existential.domain == Domain.Relationship) {
                load(name, (Rel) existential);
            } else {
                ExistentialRecord record = model.records()
                                                .newExistential(domain(existential),
                                                                name);
                record.setDescription(existential.description);
                record.insert();
                workspace.put(name, record);
            }
        });
    }

    private void loadFacets() {
        dsl.facets.forEach((name, facet) -> {
            load(name, facet);
        });
    }

    private void loadInferences() {
        // TODO Auto-generated method stub

    }

    private void loadMetaprotocols() {
        // TODO Auto-generated method stub

    }

    private void loadProtocols() {
        // TODO Auto-generated method stub

    }

    private void loadSequencingAuths() {
        // TODO Auto-generated method stub

    }

    private void loadWorkspace() {
        processImports();
        loadExistentials();
        loadFacets();
        loadInferences();
        loadEdges();
        loadSequencingAuths();
        loadProtocols();
        loadMetaprotocols();
    }

    private void processImports() {
        for (Import w : dsl.imports) {
            UUID uuid = WorkspaceAccessor.uuidOf(w.uri);
            Product imported = model.records()
                                    .resolve(uuid);
            if (imported == null) {
                throw new IllegalStateException(String.format("the import is not found: %s:%s",
                                                              uuid, w.uri));
            }
            workspace.addImport(w.alias, imported);
        }
    }

    private UUID resolve(String name) {
        UUID id;
        String[] qualifiedName = name.split("::");
        if (qualifiedName.length > 1) {
            return scope.lookupId(qualifiedName[0], qualifiedName[1]);
        } else if (qualifiedName[0].equals(THIS)) {
            return workspace.getDefiningProduct()
                            .getId();
        } else {
            id = scope.lookupId(qualifiedName[0]);
            if (id != null) {
                return id;
            }
        }
        throw new IllegalArgumentException(String.format("Cannot resolve %s:%s",
                                                         qualifiedName.length < 2 ? ""
                                                                                  : qualifiedName[0],
                                                         qualifiedName.length < 2 ? qualifiedName[0]
                                                                                  : qualifiedName[1]));
    }
}
