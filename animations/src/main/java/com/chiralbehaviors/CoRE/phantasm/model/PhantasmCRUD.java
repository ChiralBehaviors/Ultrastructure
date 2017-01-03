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

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.UpdatableRecord;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.PhantasmModel;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.Aspect;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.NetworkAttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria.NetworkAuthorization;

/**
 * CRUD for Phantasms. This class is the animation procedure that maintains and
 * mediates the Phantasm/Facet constructs in Ultrastructure. It's a bit
 * unwieldy, because of the type signatures required for erasure. Provides a
 * centralized implementation of Phantasm CRUD and the security model for such.
 *
 * @author hhildebrand
 *
 */
public class PhantasmCRUD {
    protected final Model model;

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
     * Answer the attribute value of the instance
     *
     * @param facet
     * @param instance
     * @param stateAuth
     *
     * @return
     */
    public Object getAttributeValue(Aspect facet, ExistentialRuleform instance,
                                    AttributeAuthorization stateAuth) {
        if (instance == null) {
            return null;
        }
        if (!model.checkRead(facet.getFacet())
            || !model.checkRead(stateAuth.getAuth())) {
            return null;
        }
        Attribute authorizedAttribute = stateAuth.getAttribute();
        if (authorizedAttribute.getIndexed()) {
            return getIndexedAttributeValue(instance, authorizedAttribute);
        } else if (authorizedAttribute.getKeyed()) {
            return getMappedAttributeValue(instance, authorizedAttribute);
        }
        ExistentialAttributeRecord attributeValue = model.getPhantasmModel()
                                                         .getAttributeValue(instance,
                                                                            authorizedAttribute);
        return attributeValue != null ? model.getPhantasmModel()
                                             .getValue(attributeValue)
                                      : model.getPhantasmModel()
                                             .getValue(stateAuth.getAuth());
    }

    public Object getAttributeValue(Aspect facet, ExistentialRuleform rf,
                                    NetworkAttributeAuthorization stateAuth,
                                    ExistentialRuleform child) {
        if (rf == null || child == null) {
            return null;
        }
        if (!model.checkRead(facet.getFacet())
            || !model.checkRead(stateAuth.getAuth())) {
            return null;
        }
        Attribute authorizedAttribute = stateAuth.getAttribute();
        ExistentialNetworkRecord edge = model.getPhantasmModel()
                                             .getImmediateChildLink(rf,
                                                                    stateAuth.getNetworkAuth()
                                                                             .getRelationship(),
                                                                    child);
        if (authorizedAttribute.getIndexed()) {
            return getIndexedAttributeValue(edge, authorizedAttribute);
        } else if (authorizedAttribute.getKeyed()) {
            return getMappedAttributeValue(edge, authorizedAttribute);
        }
        ExistentialNetworkAttributeRecord attributeValue = model.getPhantasmModel()
                                                                .getAttributeValue(edge,
                                                                                   authorizedAttribute);
        return attributeValue != null ? model.getPhantasmModel()
                                             .getValue(attributeValue)
                                      : model.getPhantasmModel()
                                             .getValue(stateAuth.getAuth());
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

    public List<ExistentialRuleform> lookup(List<String> ids) {
        return ids.stream()
                  .map(id -> existential(id))
                  .map(r -> model.records()
                                 .resolve(r))
                  .filter(child -> model.checkRead((UpdatableRecord<?>) child))
                  .collect(Collectors.toList());
    }

    public ExistentialRuleform lookup(String id) {
        return Optional.ofNullable(existential(id))
                       .map(r -> model.records()
                                      .resolve(r))
                       .filter(rf -> rf != null)
                       .filter(child -> model.checkRead((UpdatableRecord<?>) child))
                       .orElse(null);
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

    public ExistentialRuleform setAttributeValue(Aspect facet,
                                                 ExistentialRuleform instance,
                                                 AttributeAuthorization stateAuth,
                                                 List<Object> value) {
        return setAttributeValue(facet, instance, stateAuth, value.toArray());
    }

    public ExistentialRuleform setAttributeValue(Aspect facet,
                                                 ExistentialRuleform instance,
                                                 AttributeAuthorization stateAuth,
                                                 Map<String, Object> value) {
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(stateAuth.getAuth())) {
            return instance;
        }
        setAttributeMap(instance, stateAuth.getAttribute(), value);
        return instance;
    }

    public ExistentialRuleform setAttributeValue(Aspect facet,
                                                 ExistentialRuleform instance,
                                                 AttributeAuthorization stateAuth,
                                                 Object value) {
        if (instance == null) {
            return null;
        }
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(stateAuth.getAuth())) {
            return instance;
        }
        model.getPhantasmModel()
             .setValue(model.getPhantasmModel()
                            .getAttributeValue(instance,
                                               stateAuth.getAttribute()),
                       value);
        return instance;
    }

    public ExistentialRuleform setAttributeValue(Aspect facet,
                                                 ExistentialRuleform instance,
                                                 AttributeAuthorization stateAuth,
                                                 Object[] value) {
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(stateAuth.getAuth())) {
            return instance;
        }
        setAttributeArray(instance, stateAuth.getAttribute(), value);
        return instance;
    }

    public ExistentialRuleform setAttributeObjectValue(Aspect facet,
                                                       ExistentialRuleform instance,
                                                       NetworkAttributeAuthorization stateAuth,
                                                       ExistentialRuleform child,
                                                       Object value) {
        if (instance == null) {
            return null;
        }
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(stateAuth.getAuth())) {
            return instance;
        }
        PhantasmModel pm = model.getPhantasmModel();
        ExistentialNetworkRecord immediateLink = pm.getImmediateLink(instance,
                                                                     stateAuth.getNetworkAuth()
                                                                              .getRelationship(),
                                                                     child);
        ExistentialNetworkAttributeRecord attributeValue = pm.getAttributeValue(immediateLink,
                                                                                stateAuth.getAttribute());
        if (attributeValue == null) {
            attributeValue = model.records()
                                  .newExistentialNetworkAttribute(immediateLink,
                                                                  stateAuth.getAttribute());
            attributeValue.insert();
        }
        pm.setValue(attributeValue, value);
        return instance;
    }

    public ExistentialRuleform setAttributeValue(Aspect facet,
                                                 ExistentialRuleform instance,
                                                 NetworkAttributeAuthorization stateAuth,
                                                 ExistentialRuleform child,
                                                 Map<String, Object> value) {
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(stateAuth.getAuth())) {
            return instance;
        }
        setAttributeMap(instance, child, stateAuth, value);
        return instance;
    }

    public ExistentialRuleform setAttributeValue(Aspect facet,
                                                 ExistentialRuleform instance,
                                                 NetworkAttributeAuthorization stateAuth,
                                                 ExistentialRuleform child,
                                                 List<Object> value) {
        setAttributeArray(instance, child, stateAuth, value.toArray());
        return instance;
    }

    public ExistentialRuleform setAttributeValue(Aspect facet,
                                                 ExistentialRuleform instance,
                                                 NetworkAttributeAuthorization stateAuth,
                                                 ExistentialRuleform child,
                                                 Object[] value) {
        if (!model.checkUpdate(facet.getFacet())
            || !model.checkUpdate(stateAuth.getAuth())) {
            return instance;
        }
        setAttributeArray(instance, child, stateAuth, value);
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
     * @param id
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

    private ExistentialRecord existential(String id) {
        return model.create()
                    .selectFrom(EXISTENTIAL)
                    .where(EXISTENTIAL.ID.eq(UUID.fromString(id)))
                    .fetchOne();
    }

    private Object[] getIndexedAttributeValue(ExistentialNetworkRecord edge,
                                              Attribute authorizedAttribute) {

        ExistentialNetworkAttributeRecord[] attributeValues = getValueArray(edge,
                                                                            authorizedAttribute);

        Object[] values = (Object[]) Array.newInstance(model.getPhantasmModel()
                                                            .valueClass(authorizedAttribute),
                                                       attributeValues.length);
        for (ExistentialNetworkAttributeRecord value : attributeValues) {
            values[value.getSequenceNumber()] = model.getPhantasmModel()
                                                     .getValue(value);
        }
        return values;
    }

    private Object[] getIndexedAttributeValue(ExistentialRuleform instance,
                                              Attribute authorizedAttribute) {

        ExistentialAttributeRecord[] attributeValues = getValueArray(instance,
                                                                     authorizedAttribute);

        Object[] values = (Object[]) Array.newInstance(model.getPhantasmModel()
                                                            .valueClass(authorizedAttribute),
                                                       attributeValues.length);
        for (ExistentialAttributeRecord value : attributeValues) {
            values[value.getSequenceNumber()] = model.getPhantasmModel()
                                                     .getValue(value);
        }
        return values;
    }

    private Map<String, Object> getMappedAttributeValue(ExistentialNetworkRecord edge,
                                                        Attribute authorizedAttribute) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, ExistentialNetworkAttributeRecord> entry : getValueMap(edge,
                                                                                      authorizedAttribute).entrySet()) {
            map.put(entry.getKey(), model.getPhantasmModel()
                                         .getValue(entry.getValue()));
        }
        return map;
    }

    private Map<String, Object> getMappedAttributeValue(ExistentialRuleform instance,
                                                        Attribute authorizedAttribute) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, ExistentialAttributeRecord> entry : getValueMap(instance,
                                                                               authorizedAttribute).entrySet()) {
            map.put(entry.getKey(), model.getPhantasmModel()
                                         .getValue(entry.getValue()));
        }
        return map;
    }

    private ExistentialNetworkAttributeRecord[] getNetworkValueArray(ExistentialNetworkRecord edge,
                                                                     Attribute attribute) {
        List<? extends ExistentialNetworkAttributeRecord> values = model.getPhantasmModel()
                                                                        .getAttributeValues(edge,
                                                                                            attribute);
        int max = 0;
        for (ExistentialNetworkAttributeRecord value : values) {
            max = Math.max(max, value.getSequenceNumber() + 1);
        }
        ExistentialNetworkAttributeRecord[] returnValue = new ExistentialNetworkAttributeRecord[max];
        for (ExistentialNetworkAttributeRecord form : values) {
            returnValue[form.getSequenceNumber()] = form;
        }
        return returnValue;
    }

    private ExistentialNetworkAttributeRecord[] getValueArray(ExistentialNetworkRecord edge,
                                                              Attribute attribute) {
        List<? extends ExistentialNetworkAttributeRecord> values = model.getPhantasmModel()
                                                                        .getAttributeValues(edge,
                                                                                            attribute);
        int max = 0;
        for (ExistentialNetworkAttributeRecord value : values) {
            max = Math.max(max, value.getSequenceNumber() + 1);
        }
        ExistentialNetworkAttributeRecord[] returnValue = new ExistentialNetworkAttributeRecord[max];
        for (ExistentialNetworkAttributeRecord form : values) {
            returnValue[form.getSequenceNumber()] = form;
        }
        return returnValue;
    }

    private ExistentialAttributeRecord[] getValueArray(ExistentialRuleform instance,
                                                       Attribute attribute) {
        List<? extends ExistentialAttributeRecord> values = model.getPhantasmModel()
                                                                 .getAttributeValues(instance,
                                                                                     attribute);
        int max = 0;
        for (ExistentialAttributeRecord value : values) {
            max = Math.max(max, value.getSequenceNumber() + 1);
        }
        ExistentialAttributeRecord[] returnValue = new ExistentialAttributeRecord[max];
        for (ExistentialAttributeRecord form : values) {
            returnValue[form.getSequenceNumber()] = form;
        }
        return returnValue;
    }

    private Map<String, ExistentialNetworkAttributeRecord> getValueMap(ExistentialNetworkRecord edge,
                                                                       Attribute attribute) {
        Map<String, ExistentialNetworkAttributeRecord> map = new HashMap<>();
        for (ExistentialNetworkAttributeRecord value : model.getPhantasmModel()
                                                            .getAttributeValues(edge,
                                                                                attribute)) {
            map.put(value.getKey(), value);
        }
        return map;
    }

    private Map<String, ExistentialAttributeRecord> getValueMap(ExistentialRuleform instance,
                                                                Attribute attribute) {
        Map<String, ExistentialAttributeRecord> map = new HashMap<>();
        for (ExistentialAttributeRecord value : model.getPhantasmModel()
                                                     .getAttributeValues(instance,
                                                                         attribute)) {
            map.put(value.getKey(), value);
        }
        return map;
    }

    private ExistentialNetworkAttributeRecord newAttributeValue(ExistentialNetworkRecord edge,
                                                                Attribute attribute,
                                                                int i) {
        ExistentialNetworkAttributeRecord value = model.getPhantasmModel()
                                                       .create(edge, attribute);
        value.setSequenceNumber(i);
        return value;
    }

    private ExistentialAttributeRecord newAttributeValue(ExistentialRuleform instance,
                                                         Attribute attribute,
                                                         int i) {
        ExistentialAttributeRecord value = model.getPhantasmModel()
                                                .create(instance, attribute);
        value.setSequenceNumber(i);
        return value;
    }

    private void setAttributeArray(ExistentialRuleform instance,
                                   Attribute authorizedAttribute,
                                   Object[] values) {
        ExistentialAttributeRecord[] old = getValueArray(instance,
                                                         authorizedAttribute);
        if (values == null) {
            if (old != null) {
                for (ExistentialAttributeRecord value : old) {
                    value.delete();
                }
            }
        } else if (old == null) {
            for (int i = 0; i < values.length; i++) {
                setValue(instance, authorizedAttribute, i, null, values[i]);
            }
        } else if (old.length == values.length) {
            for (int i = 0; i < values.length; i++) {
                setValue(instance, authorizedAttribute, i, old[i], values[i]);
            }
        } else if (old.length < values.length) {
            int i;
            for (i = 0; i < old.length; i++) {
                setValue(instance, authorizedAttribute, i, old[i], values[i]);
            }
            for (; i < values.length; i++) {
                setValue(instance, authorizedAttribute, i, null, values[i]);
            }
        } else if (old.length > values.length) {
            int i;
            for (i = 0; i < values.length; i++) {
                setValue(instance, authorizedAttribute, i, old[i], values[i]);
            }
            for (; i < old.length; i++) {
                old[i].delete();
            }
        }
    }

    private void setAttributeArray(ExistentialRuleform instance,
                                   ExistentialRuleform child,
                                   NetworkAttributeAuthorization auth,
                                   Object[] values) {
        ExistentialNetworkRecord edge = model.getPhantasmModel()
                                             .getImmediateChildLink(instance,
                                                                    auth.getNetworkAuth()
                                                                        .getRelationship(),
                                                                    child);
        Attribute authorizedAttribute = auth.getAttribute();
        ExistentialNetworkAttributeRecord[] old = getNetworkValueArray(edge,
                                                                       authorizedAttribute);
        if (values == null) {
            if (old != null) {
                for (ExistentialNetworkAttributeRecord value : old) {
                    value.delete();
                }
            }
        } else if (old == null) {
            for (int i = 0; i < values.length; i++) {
                setValue(edge, authorizedAttribute, i, null, values[i]);
            }
        } else if (old.length == values.length) {
            for (int i = 0; i < values.length; i++) {
                setValue(edge, authorizedAttribute, i, old[i], values[i]);
            }
        } else if (old.length < values.length) {
            int i;
            for (i = 0; i < old.length; i++) {
                setValue(edge, authorizedAttribute, i, old[i], values[i]);
            }
            for (; i < values.length; i++) {
                setValue(edge, authorizedAttribute, i, null, values[i]);
            }
        } else if (old.length > values.length) {
            int i;
            for (i = 0; i < values.length; i++) {
                setValue(edge, authorizedAttribute, i, old[i], values[i]);
            }
            for (; i < old.length; i++) {
                old[i].delete();
            }
        }
    }

    private void setAttributeMap(ExistentialRuleform instance,
                                 Attribute authorizedAttribute,
                                 Map<String, Object> values) {
        Map<String, ExistentialAttributeRecord> valueMap = getValueMap(instance,
                                                                       authorizedAttribute);
        values.keySet()
              .stream()
              .filter(keyName -> !valueMap.containsKey(keyName))
              .forEach(keyName -> valueMap.remove(keyName));
        int maxSeq = 0;
        for (ExistentialAttributeRecord value : valueMap.values()) {
            maxSeq = Math.max(maxSeq, value.getSequenceNumber());
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            ExistentialAttributeRecord value = valueMap.get(entry.getKey());
            if (value == null) {
                value = newAttributeValue(instance, authorizedAttribute,
                                          ++maxSeq);
                value.insert();
                value.setKey(entry.getKey());
            }
            model.getPhantasmModel()
                 .setValue(value, entry.getValue());
        }
    }

    private void setAttributeMap(ExistentialRuleform instance,
                                 ExistentialRuleform child,
                                 NetworkAttributeAuthorization auth,
                                 Map<String, Object> values) {
        ExistentialNetworkRecord edge = model.getPhantasmModel()
                                             .getImmediateChildLink(instance,
                                                                    auth.getNetworkAuth()
                                                                        .getRelationship(),
                                                                    child);
        Attribute authorizedAttribute = auth.getAttribute();
        Map<String, ExistentialNetworkAttributeRecord> valueMap = getValueMap(edge,
                                                                              authorizedAttribute);
        values.keySet()
              .stream()
              .filter(keyName -> !valueMap.containsKey(keyName))
              .forEach(keyName -> valueMap.remove(keyName));
        int maxSeq = 0;
        for (ExistentialNetworkAttributeRecord value : valueMap.values()) {
            maxSeq = Math.max(maxSeq, value.getSequenceNumber());
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            ExistentialNetworkAttributeRecord value = valueMap.get(entry.getKey());
            if (value == null) {
                value = newAttributeValue(edge, authorizedAttribute, ++maxSeq);
                value.insert();
                value.setKey(entry.getKey());
            }
            model.getPhantasmModel()
                 .setValue(value, entry.getValue());
        }
    }

    private void setValue(ExistentialNetworkRecord edge, Attribute attribute,
                          int i, ExistentialNetworkAttributeRecord existing,
                          Object newValue) {
        if (existing == null) {
            existing = newAttributeValue(edge, attribute, i);
            existing.insert();
        }
        model.getPhantasmModel()
             .setValue(existing, newValue);
    }

    private void setValue(ExistentialRuleform instance, Attribute attribute,
                          int i, ExistentialAttributeRecord existing,
                          Object newValue) {
        if (existing == null) {
            existing = newAttributeValue(instance, attribute, i);
            existing.insert();
        }
        model.getPhantasmModel()
             .setValue(existing, newValue);
    }
}
