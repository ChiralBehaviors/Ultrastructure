/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.model;

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.postgres.JsonExtensions.CONVERTER;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.postgres.PostgresJSONJacksonJsonNodeConverter;
import org.jooq.UpdatableRecord;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgePropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetPropertyRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.Aspect;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.NetworkAuthorization;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * CRUD for Phantasms. This class is the animation procedure that maintains and
 * mediates the Phantasm/Facet constructs in Ultrastructure. Provides a
 * centralized implementation of Phantasm CRUD and the security model for such.
 *
 * @author hhildebrand
 *
 */
public class PhantasmCRUD {
    public static final String                               _INSTANCE = "_instance";
    public static final ObjectMapper MAPPER    = new ObjectMapper();

    protected final Model            model;

    public PhantasmCRUD(Model model) {
        this.model = model;
    }

    /**
     * Add the child to the list of children of the instance
     *
     * @param facet
     * @param instance
     * @param auth
     * @param child
     */
    public ExistentialRuleform addChild(Aspect facet,
                                        ExistentialRuleform instance,
                                        NetworkAuthorization auth,
                                        ExistentialRuleform child) {
        if (instance == null) {
            return null;
        }
        cast(child, auth.getChild());
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(auth.getAuth())) {
            return instance;
        }

        model.getPhantasmModel()
             .link(instance, auth.getRelationship(), child);
        return instance;
    }

    /**
     * Add the list of children to the instance
     *
     * @param facet
     * @param instance
     * @param auth
     * @param children
     */
    public ExistentialRuleform addChildren(Aspect facet,
                                           ExistentialRuleform instance,
                                           NetworkAuthorization auth,
                                           List<ExistentialRuleform> children) {
        if (instance == null) {
            return null;
        }
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(auth.getAuth())) {
            return instance;
        }
        children.stream()
                .filter(child -> model.checkRead((UpdatableRecord<?>) child))
                .peek(child -> cast(child, auth.getChild()))
                .forEach(child -> model.getPhantasmModel()
                                       .link(instance, auth.getRelationship(),
                                             child));
        return instance;
    }

    /**
     * Apply the facet to the instance
     *
     * @param facet
     * @param instance
     * @return
     * @throws SecurityException
     */
    public ExistentialRuleform apply(Aspect facet, ExistentialRuleform instance,
                                     Function<ExistentialRuleform, ExistentialRuleform> constructor) throws SecurityException {
        if (instance == null) {
            return null;
        }
        if (!model.checkApply(facet.getFacet())) {
            return null;
        }
        model.getPhantasmModel()
             .initialize(instance, facet.getFacet());
        if (!checkInvoke(facet, instance)) {
            return null;
        }
        return constructor.apply(instance);
    }

    /**
     * Throws ClassCastException if not an instance of the authorized facet type
     *
     * @param ruleform
     * @param facet
     */
    public void cast(ExistentialRuleform ruleform, Aspect facet) {
        if (!model.getPhantasmModel()
                  .isAccessible(ruleform.getId(), facet.getClassifier()
                                                       .getId(),
                                facet.getClassification()
                                     .getId())) {
            throw new ClassCastException(String.format("%s not of facet type %s",
                                                       ruleform.getId(),
                                                       facet));
        }
    }

    public boolean checkInvoke(Aspect facet, ExistentialRuleform instance) {
        return model.checkInvoke((UpdatableRecord<?>) instance)
               && model.checkInvoke(facet.getFacet());
    }

    /**
     * Create a new instance of the facet
     *
     * @param facet
     * @param name
     * @param description
     * @return
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public ExistentialRuleform createInstance(Aspect facet, String name,
                                              String description,
                                              Consumer<ExistentialRuleform> initializer) {
        if (!model.checkCreate(facet.getFacet())) {
            return null;
        }
        ExistentialRuleform instance;
        instance = model.records()
                        .createExistential(facet.getFacet()
                                                .getClassification(),
                                           name, description);
        ((ExistentialRecord) instance).insert();
        model.getPhantasmModel()
             .initialize(instance, facet.getFacet());
        if (!checkInvoke(facet, instance)) {
            return null;
        }
        initializer.accept(instance);
        return instance;
    }

    /**
     * Answer the inferred and immediate network children of the instance
     *
     * @param facet
     * @param instance
     * @param auth
     *
     * @return
     */
    public List<ExistentialRuleform> getChildren(Aspect facet,
                                                 ExistentialRuleform instance,
                                                 NetworkAuthorization auth) {
        if (instance == null) {
            return Collections.emptyList();
        }
        if (!model.checkRead(facet.getFacet())
            || !model.checkRead(auth.getAuth())) {
            return Collections.emptyList();
        }
        return model.getPhantasmModel()
                    .getConstrainedChildren(instance, auth.getRelationship(),
                                            auth.getChild()
                                                .getClassifier(),
                                            auth.getChild()
                                                .getClassification(),
                                            auth.getDomain())
                    .stream()
                    .filter(child -> model.checkRead((UpdatableRecord<?>) child))
                    .collect(Collectors.toList());

    }

    public JsonNode getEdgeProperty(ExistentialRuleform parent,
                                    NetworkAuthorization auth,
                                    ExistentialRuleform child) {
        EdgePropertyRecord edgeProperties = model.getPhantasmModel()
                                                 .getEdgeProperties(parent,
                                                                    auth.getAuth(),
                                                                    child);
        return edgeProperties == null ? null : CONVERTER.from(edgeProperties.getProperties());
    }

    public JsonNode getFacetProperty(Aspect facet,
                                     ExistentialRuleform ruleform) {
        FacetPropertyRecord record = model.getPhantasmModel()
                                          .getFacetProperties(facet.getFacet(),
                                                              ruleform);
        return record == null ? null : CONVERTER.from(record.getProperties());
    }

    /**
     * Answer the immediate, non inferred children of the instance
     *
     * @param facet
     * @param instance
     * @param auth
     *
     * @return
     */
    public List<ExistentialRuleform> getImmediateChildren(Aspect facet,
                                                          ExistentialRuleform instance,
                                                          NetworkAuthorization auth) {
        if (instance == null) {
            return Collections.emptyList();
        }
        if (!model.checkRead(facet.getFacet())
            || !model.checkRead(auth.getAuth())) {
            return Collections.emptyList();
        }
        return model.getPhantasmModel()
                    .getImmediateConstrainedChildren(instance,
                                                     auth.getRelationship(),
                                                     auth.getChild()
                                                         .getClassifier(),
                                                     auth.getChild()
                                                         .getClassification(),
                                                     auth.getDomain())
                    .stream()
                    .map(r -> r)
                    .filter(child -> model.checkRead((UpdatableRecord<?>) child))
                    .collect(Collectors.toList());
    }

    /**
     * Answer the list of instances of this facet.
     *
     * @param facet
     * @return
     */
    public List<ExistentialRuleform> getInstances(Aspect facet) {
        if (!model.checkRead(facet.getFacet())) {
            return Collections.emptyList();
        }
        return model.getPhantasmModel()
                    .getChildrenUuid(facet.getClassification()
                                          .getId(),
                                     facet.getClassifier()
                                          .getInverse(),
                                     facet.getDomain())
                    .stream()
                    .map(e -> e)
                    .filter(instance -> model.checkRead((UpdatableRecord<?>) instance))
                    .collect(Collectors.toList());
    }

    public Model getModel() {
        return model;
    }

    public JsonNode getProperties(ExistentialRuleform parent,
                                  NetworkAuthorization auth,
                                  ExistentialRuleform child) {
        EdgePropertyRecord edgeProperties = model.getPhantasmModel()
                                                 .getEdgeProperties(parent,
                                                                    auth.getAuth(),
                                                                    child);
        return edgeProperties == null ? null : CONVERTER.from(edgeProperties.getProperties());
    }

    /**
     * Answer the singular network child of the instance
     *
     * @param facet
     * @param instance
     * @param auth
     *
     * @return
     */
    public ExistentialRuleform getSingularChild(Aspect facet,
                                                ExistentialRuleform instance,
                                                NetworkAuthorization auth) {
        if (instance == null) {
            return null;
        }
        if (!model.checkRead(facet.getFacet())
            || !model.checkRead(auth.getAuth())) {
            return null;
        }
        ExistentialRuleform child = model.getPhantasmModel()
                                         .getImmediateChild(instance,
                                                            auth.getRelationship(),
                                                            auth.getDomain());
        return model.checkRead((UpdatableRecord<?>) child) ? child : null;
    }

    public ExistentialRuleform lookup(UUID id) {
        return Optional.ofNullable(existential(id))
                       .map(r -> model.records()
                                      .resolve(r))
                       .filter(rf -> rf != null)
                       .filter(child -> model.checkRead((UpdatableRecord<?>) child))
                       .orElse(null);
    }

    public List<ExistentialRuleform> lookupList(List<UUID> ids) {
        return ids.stream()
                  .map(id -> existential(id))
                  .map(r -> model.records()
                                 .resolve(r))
                  .filter(child -> model.checkRead((UpdatableRecord<?>) child))
                  .collect(Collectors.toList());
    }

    /**
     * Remove the facet from the instance
     *
     * @param facet
     * @param instance
     * @return
     * @throws SecurityException
     */
    public ExistentialRuleform remove(Aspect facet,
                                      ExistentialRuleform instance,
                                      boolean deleteAttributes) throws SecurityException {
        if (instance == null) {
            return null;
        }
        if (!model.checkRemove(facet.getFacet())) {
            return instance;
        }
        model.getPhantasmModel()
             .unlink(instance, facet.getClassifier(),
                     facet.getClassification());
        return instance;
    }

    /**
     * Remove a child from the instance
     *
     * @param facet
     * @param instance
     * @param auth
     * @param child
     */
    public ExistentialRuleform removeChild(Aspect facet,
                                           ExistentialRuleform instance,
                                           NetworkAuthorization auth,
                                           ExistentialRuleform child) {
        if (instance == null) {
            return null;
        }
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(auth.getAuth())) {
            return instance;
        }
        model.getPhantasmModel()
             .unlink(instance, auth.getRelationship(), child);
        return instance;
    }

    /**
     * Remove the immediate child links from the instance
     *
     * @param facet
     * @param instance
     * @param auth
     * @param children
     */
    public ExistentialRuleform removeChildren(Aspect facet,
                                              ExistentialRuleform instance,
                                              NetworkAuthorization auth,
                                              List<ExistentialRuleform> children) {
        if (instance == null) {
            return null;
        }
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(auth.getAuth())) {
            return instance;
        }
        for (ExistentialRuleform child : children) {
            model.getPhantasmModel()
                 .unlink(instance, auth.getRelationship(), child);
        }
        return instance;
    }

    /**
     * Set the immediate children of the instance to be the list of supplied
     * children. No inferred links will be explicitly added or deleted.
     *
     * @param facet
     * @param instance
     * @param auth
     * @param children
     */
    public ExistentialRuleform setChildren(Aspect facet,
                                           ExistentialRuleform instance,
                                           NetworkAuthorization auth,
                                           List<ExistentialRuleform> children) {
        if (instance == null) {
            return null;
        }
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(auth.getAuth())) {
            return instance;
        }

        model.getPhantasmModel()
             .unlinkImmediate(instance, auth.getRelationship());
        children.stream()
                .filter(child -> model.checkRead((UpdatableRecord<?>) child))
                .peek(child -> cast(child, auth.getChild()))
                .forEach(child -> model.getPhantasmModel()
                                       .link(instance, auth.getRelationship(),
                                             child));
        return instance;
    }

    /**
     * @param description
     * @return
     */
    public ExistentialRuleform setDescription(ExistentialRuleform instance,
                                              String description) {
        if (instance == null) {
            return null;
        }
        if (!model.checkUpdate((UpdatableRecord<?>) instance)) {
            return instance;
        }
        instance.setDescription(description);
        return instance;
    }

    public Object setEdgeProperty(ExistentialRuleform parent,
                                  NetworkAuthorization auth,
                                  ExistentialRuleform child, Object object) {
        EdgePropertyRecord properties = model.getPhantasmModel()
                                             .getEdgeProperties(parent,
                                                                auth.getAuth(),
                                                                child);
        if (properties == null) {
            EdgeRecord edge = model.getPhantasmModel()
                                   .getImmediateChildLink(parent,
                                                          auth.getRelationship(),
                                                          child);
            if (edge == null) {
                throw new IllegalStateException(String.format("No edge {%s, %s, %s}",
                                                              parent.getName(),
                                                              auth.getRelationship()
                                                                  .getName(),
                                                              child.getName()));
            }
            properties = model.records()
                              .newEdgeProperty();
            properties.setForward(true);
            properties.setAuth(auth.getAuth()
                                   .getId());
            properties.setEdge(edge.getId());
            properties.setProperties(CONVERTER.to(MAPPER.valueToTree(object)));
            properties.insert();
        } else {
            properties.setProperties(CONVERTER.to(MAPPER.valueToTree(object)));
            properties.update();
        }
        return null;
    }

    public Object setFacetProperty(Aspect facet, ExistentialRuleform ruleform,
                                   Object object) {
        FacetPropertyRecord properties = model.getPhantasmModel()
                                              .getFacetProperties(facet.getFacet(),
                                                                  ruleform);
        boolean create = false;
        if (properties == null) {
            properties = model.records()
                              .newFacetProperty();
            properties.setFacet(facet.getFacet()
                                     .getId());
            properties.setExistential(ruleform.getId());
            create = true;
        }
        properties.setProperties(object == null ? null
                                                : CONVERTER.to(MAPPER.valueToTree(object)));
        if (create) {
            properties.insert();
        } else {
            properties.update();
        }

        return null;
    }

    public ExistentialRuleform setName(ExistentialRuleform instance,
                                       String name) {
        if (instance == null) {
            return null;
        }
        if (!model.checkUpdate((UpdatableRecord<?>) instance)) {
            return instance;
        }
        instance.setName(name);
        instance.update();
        return instance;
    }

    /**
     * Set the singular child of the instance.
     *
     * @param facet
     * @param instance
     * @param auth
     * @param child
     */
    public ExistentialRuleform setSingularChild(Aspect facet,
                                                ExistentialRuleform instance,
                                                NetworkAuthorization auth,
                                                ExistentialRuleform child) {
        if (instance == null) {
            return null;
        }

        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(auth.getAuth())) {
            return instance;
        }

        if (child == null) {
            model.getPhantasmModel()
                 .unlinkImmediate(instance, auth.getRelationship());
        } else {
            cast(child, auth.getChild());
            model.getPhantasmModel()
                 .setImmediateChild(instance, auth.getRelationship(), child);
        }
        return instance;
    }

    private ExistentialRecord existential(UUID id) {
        return model.create()
                    .selectFrom(EXISTENTIAL)
                    .where(EXISTENTIAL.ID.eq(id))
                    .fetchOne();
    }
}
