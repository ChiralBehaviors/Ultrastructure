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

package com.chiralbehaviors.CoRE.meta.workspace;

import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.enums.ReferenceType.Existential;
import static com.chiralbehaviors.CoRE.postgres.JsonExtensions.CONVERTER;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jooq.TableRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgePropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetPropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.Workspace;
import com.chiralbehaviors.CoRE.kernel.phantasm.workspaceProperties.WorkspaceProperties;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.json.Constraint;
import com.chiralbehaviors.CoRE.meta.workspace.json.Edge;
import com.chiralbehaviors.CoRE.meta.workspace.json.Existential;
import com.chiralbehaviors.CoRE.meta.workspace.json.Existential.Domain;
import com.chiralbehaviors.CoRE.meta.workspace.json.Facet;
import com.chiralbehaviors.CoRE.meta.workspace.json.Import;
import com.chiralbehaviors.CoRE.meta.workspace.json.JsonWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.json.Rel;
import com.chiralbehaviors.CoRE.meta.workspace.json.Sequencing;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.Tuple;

/**
 * @author halhildebrand
 *
 */
public class JsonImporter {
    private static class FacetLoad {
        public final FacetRecord auth;
        public final Facet       facet;

        public FacetLoad(Facet facet, FacetRecord auth) {
            this.facet = facet;
            this.auth = auth;
        }
    }

    private static final Logger log  = LoggerFactory.getLogger(JsonImporter.class);
    private static final String THIS = "this";

    public static JsonWorkspace from(InputStream is) {
        try {
            return new ObjectMapper().readerFor(JsonWorkspace.class)
                                     .readValue(is);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot deserialize json resource",
                                               e);
        }
    }

    public static JsonImporter manifest(InputStream source,
                                        Model model) throws IOException {
        JsonImporter importer = new JsonImporter(source, model);
        importer.manifest();
        return importer;
    }

    public static void manifest(List<URL> wsps, Model model) {
        wsps.forEach(url -> {
            manifest(url, model);
        });
    }

    public static JsonImporter manifest(URL url, Model model) {
        try (InputStream is = url.openStream()) {
            try {
                return manifest(is, model);
            } catch (Exception e) {
                throw new IllegalStateException(String.format("Cannot load %s",
                                                              url),
                                                e);
            }
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Cannot load %s",
                                                          url),
                                            e);
        }
    }

    private final JsonWorkspace dsl;
    private final Model         model;
    private WorkspaceScope      scope;
    private UUID                uuid;
    private EditableWorkspace   workspace;

    public JsonImporter(InputStream resource, Model model) {
        this(from(resource), model);
    }

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
        WorkspaceProperties props = phantasm.get_Properties();
        if (props == null) {
            props = new WorkspaceProperties();
        }
        props.setIri(dsl.uri);
        props.setName(dsl.name);
        props.setDescription(dsl.description);
        props.setVersion(dsl.version);
        phantasm.set_Properties(props);
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

        WorkspaceProperties properties = existing.get_Properties();
        if (properties.getVersion() >= dsl.version) {
            throw new IllegalStateException(String.format("Workspace %s is at version %s, unable to update to %s",
                                                          dsl.name,
                                                          properties.getVersion(),
                                                          dsl.version));
        }

        properties.setName(dsl.name);
        properties.setDescription(dsl.description);
        properties.setVersion(dsl.version);
        existing.set_Properties(properties);

        WorkspaceAccessor loaded = load(definingProduct);
        return loaded;
    }

    private void apply(String apply, String on, JsonNode properties) {
        log.debug("applying {} on {}", apply, on);
        ExistentialRuleform existential = resolveRecord(on);
        if (on == null) {
            throw new IllegalStateException("Unable to find existential: "
                                            + on);
        }
        FacetRecord facet = model.records()
                                 .findFacetRecord(resolveFacet(apply));
        if (facet == null) {
            throw new IllegalStateException("Unable to find facet: " + apply);
        }
        model.getPhantasmModel()
             .initialize(existential, facet, workspace);
        if (properties != null) {
            FacetPropertyRecord facetProperties = model.getPhantasmModel()
                                                       .getFacetProperties(facet,
                                                                           existential);
            if (facetProperties == null) {
                facetProperties = model.records()
                                       .newFacetProperty();
                facetProperties.setExistential(existential.getId());
                facetProperties.setFacet(facet.getId());
                facetProperties.setProperties(CONVERTER.to(properties));
                facetProperties.insert();
                workspace.add(facetProperties);
            } else {
                facetProperties.setProperties(CONVERTER.to(properties));
                facetProperties.update();
            }
        }
    }

    private void applyFacets() {
        dsl.applications.forEach(application -> apply(application.apply,
                                                      application.on,
                                                      application.properties));
    }

    private Cardinality cardinality(Constraint constraint) {
        switch (constraint.card) {
            case MANY:
                return Cardinality.N;
            case ONE:
                return Cardinality._1;
            case ZERO:
                return Cardinality.Zero;
            default:
                throw new IllegalArgumentException("unknown cardinality: "
                                                   + constraint.card);

        }
    }

    private WorkspaceAccessor createWorkspace() {
        if (getWorkspaceProduct() != null) {
            Workspace phantasm = model.wrap(Workspace.class,
                                            getWorkspaceProduct());
            throw new IllegalStateException(String.format("Workspace %s already exists at version %s, not created",
                                                          phantasm.get_Properties()
                                                                  .getName(),
                                                          phantasm.get_Properties()
                                                                  .getVersion()));
        }

        Product definingProduct = createWorkspaceProduct();
        scope = model.getWorkspaceModel()
                     .createWorkspace(dsl.name, dsl.description,
                                      definingProduct, dsl.uri, dsl.version);

        workspace = (EditableWorkspace) scope.getWorkspace();
        loadWorkspace();
        return workspace;
    }

    private Product createWorkspaceProduct() {
        Product workspaceProduct = model.records()
                                        .newProduct(dsl.name, dsl.description);

        workspaceProduct.setId(uuid);
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

    private void load(String name, Constraint constraint, FacetRecord auth,
                      Map<String, FacetLoad> loaded) {
        EdgeAuthorizationRecord authorization = model.records()
                                                     .newExistentialNetworkAuthorization();
        authorization.setName(name);
        authorization.setParent(auth.getId());
        authorization.setRelationship(resolve(constraint.rel));
        authorization.setSchema(CONVERTER.to(constraint.schema));
        authorization.setDefaultProperties(CONVERTER.to(constraint.defaultProperties));
        resolveChild(constraint, authorization, loaded);
        Cardinality cardinality = cardinality(constraint);
        authorization.setCardinality(cardinality);
        authorization.insert();
        workspace.add(authorization);
        log.debug("loading constraint {} on {}", name, auth.getName());
        authorization.setSchema(CONVERTER.to(constraint.schema));
    }

    private FacetRecord load(String name, Facet facet) {
        Relationship classifier = resolveRecord(facet.classifier);
        ExistentialRuleform classification = resolveRecord(facet.classification);

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
        authorization.setSchema(CONVERTER.to(facet.schema));
        authorization.setDefaultProperties(CONVERTER.to(facet.defaultProperties));
        authorization.insert();
        log.debug("loading Facet {}", name);
        workspace.put(name, authorization);
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

    private void load(String service, Sequencing sequence) {
        UUID product = resolve(service);
        StatusCodeSequencingRecord scs = model.records()
                                              .newStatusCodeSequencing();
        scs.setService(product);
        scs.setParent(resolve(sequence.parent));
        scs.setChild(resolve(sequence.child));
        scs.insert();
        workspace.add(scs);
    }

    private void loadChildSequencing() {
        dsl.childSequences.forEach(seq -> {
            ChildSequencingAuthorizationRecord auth = model.records()
                                                           .newChildSequencingAuthorization(resolve(seq.parent),
                                                                                            resolve(seq.status),
                                                                                            resolve(seq.child),
                                                                                            resolve(seq.next));
            auth.insert();
            workspace.add(auth);
        });
    }

    private void loadEdges() {
        dsl.edges.forEach(edge -> {
            ExistentialRuleform parent = resolveRecord(edge.p);
            Relationship relationship = resolveRecord(edge.r);
            ExistentialRuleform child = resolveRecord(edge.c);
            Tuple<EdgeRecord, EdgeRecord> link = model.getPhantasmModel()
                                                      .link(parent,
                                                            relationship,
                                                            child);
            workspace.add(link.a);
            workspace.add(link.b);

            setEdgeProperties(edge, link.a);

        });
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
                log.debug("loading existential {}:{}", name,
                          existential.description);
                workspace.put(name, record);
            }
        });
    }

    private void loadFacets() {
        Map<String, FacetLoad> loaded = new HashMap<>();
        dsl.facets.forEach((name, facet) -> {
            loaded.put(name, new FacetLoad(facet, load(name, facet)));
        });
        loaded.values()
              .forEach(pair -> {
                  pair.facet.constraints.forEach((name, constraint) -> {
                      load(name, constraint, pair.auth, loaded);
                  });
              });
    }

    private void loadInferences() {
        dsl.inferences.forEach(i -> {
            NetworkInferenceRecord inference = model.records()
                                                    .newNetworkInference(resolve(i.premise1),
                                                                         resolve(i.premise2),
                                                                         resolve(i.inference));
            inference.insert();
            log.debug("loading {}", i);
            workspace.add(inference);
        });

    }

    private void loadMetaprotocols() {
        dsl.metaProtocols.forEach(mpc -> {
            MetaProtocolRecord metaProtocol = model.getJobModel()
                                                   .newInitializedMetaProtocol(resolveRecord(mpc.transform));
            if (mpc.product != null) {
                metaProtocol.setProduct(resolve(mpc.product));
            }
            if (mpc.from != null) {
                metaProtocol.setDeliverFrom(resolve(mpc.from));
            }
            if (mpc.to != null) {
                metaProtocol.setDeliverTo(resolve(mpc.to));
            }
            if (mpc.requester != null) {
                metaProtocol.setRequester(resolve(mpc.requester));
            }
            if (mpc.assign != null) {
                metaProtocol.setAssignTo(resolve(mpc.assign));
            }
            metaProtocol.setStopOnMatch(mpc.stopOnMatch);
            metaProtocol.insert();
            workspace.add(metaProtocol);
        });
    }

    private void loadParentSequencing() {
        dsl.parentSequences.forEach(seq -> {
            ParentSequencingAuthorizationRecord auth = model.records()
                                                            .newParentSequencingAuthorization(resolve(seq.service),
                                                                                              resolve(seq.status),
                                                                                              resolve(seq.parent),
                                                                                              resolve(seq.next));
            auth.insert();
            workspace.add(auth);

        });
    }

    private void loadProtocols() {
        dsl.protocols.forEach(pc -> {
            ProtocolRecord protocol = model.getJobModel()
                                           .newInitializedProtocol(resolveRecord(pc.match.service));
            if (pc.match.product != null) {
                protocol.setProduct(resolve(pc.match.product));
            }
            if (pc.match.from != null) {
                protocol.setDeliverFrom(resolve(pc.match.from));
            }
            if (pc.match.to != null) {
                protocol.setDeliverTo(resolve(pc.match.to));
            }
            if (pc.match.quantity != null) {
                protocol.setQuantity(pc.match.quantity);
            }
            if (pc.match.requester != null) {
                protocol.setRequester(resolve(pc.match.requester));
            }
            if (pc.match.assign != null) {
                protocol.setAssignTo(resolve(pc.match.assign));
            }

            if (pc.child.service != null) {
                protocol.setChildService(resolve(pc.child.service));
            }
            if (pc.child.product != null) {
                protocol.setChildProduct(resolve(pc.child.product));
            }
            if (pc.child.from != null) {
                protocol.setChildDeliverFrom(resolve(pc.child.from));
            }
            if (pc.child.to != null) {
                protocol.setChildDeliverTo(resolve(pc.child.to));
            }
            if (pc.child.quantity != null) {
                protocol.setChildQuantity(pc.child.quantity);
            }
            if (pc.child.assign != null) {
                protocol.setChildAssignTo(resolve(pc.child.assign));
            }
            protocol.insert();
            workspace.add(protocol);
        });
    }

    private void loadSelfSequencing() {
        dsl.selfSequences.forEach(seq -> {
            SelfSequencingAuthorizationRecord auth = model.records()
                                                          .newSelfSequencingAuthorization(resolve(seq.service),
                                                                                          resolve(seq.status),
                                                                                          resolve(seq.next));
            auth.insert();
            workspace.add(auth);
        });
    }

    private void loadSequencing() {
        dsl.sequencing.forEach((service,
                                sequencing) -> sequencing.forEach(sequence -> load(service,
                                                                                   sequence)));
    }

    private void loadSequencingAuths() {
        loadParentSequencing();
        loadSiblingSequencing();
        loadChildSequencing();
        loadSelfSequencing();
    }

    private void loadSiblingSequencing() {
        dsl.siblingSequences.forEach(seq -> {
            SiblingSequencingAuthorizationRecord auth = model.records()
                                                             .newSiblingSequencingAuthorization(resolve(seq.parent),
                                                                                                resolve(seq.status),
                                                                                                resolve(seq.sibling),
                                                                                                resolve(seq.next));
            auth.insert();
            workspace.add(auth);
        });
    }

    private void loadWorkspace() {
        log.debug("loading workspace {} version {} : {}", dsl.name, dsl.version,
                  dsl.description);
        processImports();
        loadExistentials();
        loadInferences();
        loadFacets();
        loadEdges();
        loadSequencingAuths();
        loadProtocols();
        loadMetaprotocols();
        loadSequencing();
        applyFacets();
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
            log.debug("adding import of workspace {}:{}", w.alias, w.uri);
            workspace.addImport(w.alias, imported);
        }
    }

    private UUID resolve(String name) {
        UUID id;
        String[] qualifiedName = name.split("::");
        if (qualifiedName.length > 1) {
            return scope.lookupId(qualifiedName[0], Existential,
                                  qualifiedName[1]);
        } else if (qualifiedName[0].equals(THIS)) {
            return workspace.getDefiningProduct()
                            .getId();
        } else {
            id = scope.lookupId(Existential, qualifiedName[0]);
            if (id != null) {
                return id;
            }
        }
        throw new IllegalArgumentException(String.format("Cannot resolve %s%s",
                                                         qualifiedName.length < 2 ? ""
                                                                                  : qualifiedName[0]
                                                                                    + "::",
                                                         qualifiedName.length < 2 ? qualifiedName[0]
                                                                                  : qualifiedName[1]));
    }

    private void resolveChild(Constraint constraint,
                              EdgeAuthorizationRecord authorization,
                              Map<String, FacetLoad> loaded) {
        FacetLoad resolved = loaded.get(constraint.child);
        if (resolved != null) {
            authorization.setChild(resolved.auth.getId());
        } else {
            authorization.setChild(resolveFacet(constraint.child));
        }
    }

    private UUID resolveFacet(String name) {
        UUID id;
        String[] qualifiedName = name.split("::");
        if (qualifiedName.length > 1) {
            return scope.lookupId(qualifiedName[0], ReferenceType.Facet,
                                  qualifiedName[1]);
        } else if (qualifiedName[0].equals(THIS)) {
            return workspace.getDefiningProduct()
                            .getId();
        } else {
            id = scope.lookupId(ReferenceType.Facet, qualifiedName[0]);
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

    @SuppressWarnings("unchecked")
    private <T extends TableRecord<?>> T resolveRecord(String name) {
        T record;
        String[] qualifiedName = name.split("::");
        if (qualifiedName.length > 1) {
            return scope.lookup(qualifiedName[0], Existential,
                                qualifiedName[1]);
        } else if (qualifiedName[0].equals(THIS)) {
            return (T) workspace.getDefiningProduct();
        } else {
            record = scope.lookup(Existential, qualifiedName[0]);
            if (record != null) {
                return record;
            }
        }
        throw new IllegalArgumentException(String.format("Cannot resolve %s%s",
                                                         qualifiedName.length < 2 ? ""
                                                                                  : qualifiedName[0]
                                                                                    + "::",
                                                         qualifiedName.length < 2 ? qualifiedName[0]
                                                                                  : qualifiedName[1]));
    }

    private void setEdgeProperties(Edge edge, EdgeRecord record) {
        if (edge.properties == null) {
            return;
        }
        EdgePropertyRecord props = model.records()
                                        .newEdgeProperty();
        UUID relationship = resolve(edge.r);
        UUID facet = resolveFacet(edge.facet);
        UUID auth = model.create()
                         .select(EDGE_AUTHORIZATION.ID)
                         .from(EDGE_AUTHORIZATION)
                         .where(EDGE_AUTHORIZATION.PARENT.equal(facet))
                         .and(EDGE_AUTHORIZATION.RELATIONSHIP.equal(relationship))
                         .fetchSingle()
                         .component1();
        props.setAuth(auth);
        props.setProperties(CONVERTER.to(edge.properties));
        props.setEdge(record.getId());
        props.insert(); 
        workspace.add(props);
    }
}
