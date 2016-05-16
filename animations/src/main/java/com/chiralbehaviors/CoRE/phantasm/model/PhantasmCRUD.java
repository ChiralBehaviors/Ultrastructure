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
import java.util.function.Function;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.Aspect;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.NetworkAuthorization;

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

    private final Relationship apply;
    private final Relationship create;
    private final Relationship delete;
    private final Relationship invoke;
    private final Relationship read;
    private final Relationship remove;
    private final Relationship update;
    protected final Model      model;

    public PhantasmCRUD(Model model) {
        this.model = model;
        create = model.getKernel()
                      .getCREATE();
        delete = model.getKernel()
                      .getDELETE();
        invoke = model.getKernel()
                      .getINVOKE();
        read = model.getKernel()
                    .getREAD();
        remove = model.getKernel()
                      .getREMOVE();
        update = model.getKernel()
                      .getUPDATE();
        apply = model.getKernel()
                     .getAPPLY();
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
        if (!checkUPDATE(facet) || !checkUPDATE(auth)) {
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
        if (!checkUPDATE(facet) || !checkUPDATE(auth)) {
            return instance;
        }
        children.stream()
                .filter(child -> checkREAD(child))
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
        if (!model.getPhantasmModel()
                  .checkCapability(facet.getFacet(), getAPPLY())) {
            return instance;
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
                  .isAccessible(ruleform.getId(), facet.getClassifier().getId(),
                                facet.getClassification().getId())) {
            throw new ClassCastException(String.format("%s not of facet type %s",
                                                       ruleform.getId(),
                                                       facet));
        }
    }

    public boolean checkInvoke(Aspect facet, ExistentialRuleform instance) {
        Relationship invoke = getINVOKE();
        return model.getPhantasmModel()
                    .checkCapability(instance, invoke)
               && model.getPhantasmModel()
                       .checkCapability(facet.getFacet(), invoke);
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
                                              Function<ExistentialRuleform, ExistentialRuleform> constructor) {
        if (!model.getPhantasmModel()
                  .checkCapability(facet.getFacet(), getCREATE())) {
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
        return constructor.apply(instance);
    }

    public Relationship getAPPLY() {
        return apply;
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
        if (!checkREAD(facet) || !checkREAD(stateAuth)) {
            return null;
        }
        Attribute authorizedAttribute = stateAuth.getAttribute();
        if (authorizedAttribute.getIndexed()) {
            return getIndexedAttributeValue(instance, authorizedAttribute);
        } else if (authorizedAttribute.getKeyed()) {
            return getMappedAttributeValue(instance, authorizedAttribute);
        }
        return model.getPhantasmModel()
                    .getValue(model.getPhantasmModel()
                                   .getAttributeValue(instance,
                                                      authorizedAttribute));
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
        if (!checkREAD(facet) || !checkREAD(auth)) {
            return Collections.emptyList();
        }
        return model.getPhantasmModel()
                    .getConstrainedChildren(instance, auth.getRelationship(), auth.getChild(),
                                 auth.getDomain())
                    .stream()
                    .filter(child -> model.getPhantasmModel()
                                          .checkCapability(child, getREAD()))
                    .collect(Collectors.toList());

    }

    public Relationship getCREATE() {
        return create;
    }

    public Relationship getDELETE() {
        return delete;
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
        if (!checkREAD(facet) || !checkREAD(auth)) {
            return Collections.emptyList();
        }
        return model.getPhantasmModel()
                    .getImmediateConstrainedChildren(instance, auth.getRelationship(), auth.getChild(),
                                          auth.getDomain())
                    .stream()
                    .map(r -> r)
                    .filter(child -> model.getPhantasmModel()
                                          .checkCapability(child, getREAD()))
                    .collect(Collectors.toList());
    }

    /**
     * Answer the list of instances of this facet.
     *
     * @param facet
     * @return
     */
    public List<ExistentialRuleform> getInstances(Aspect facet) {
        if (!model.getPhantasmModel()
                  .checkCapability(facet.getFacet(), getREAD())) {
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
                    .filter(instance -> checkREAD(instance))
                    .collect(Collectors.toList());
    }

    public Relationship getINVOKE() {
        return invoke;
    }

    public Model getModel() {
        return model;
    }

    public Relationship getREAD() {
        return read;
    }

    public Relationship getREMOVE() {
        return remove;
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
        if (!checkREAD(facet) || !checkREAD(auth)) {
            return null;
        }
        ExistentialRuleform child = model.getPhantasmModel()
                                         .getImmediateChild(instance,
                                                            auth.getRelationship(),
                                                            auth.getDomain());
        return checkREAD(child) ? child : null;
    }

    public Relationship getUPDATE() {
        return update;
    }

    public List<ExistentialRuleform> lookup(List<String> ids) {
        return ids.stream()
                  .map(id -> existential(id))
                  .map(r -> model.records()
                                 .resolve(r))
                  .filter(child -> model.getPhantasmModel()
                                        .checkCapability(child, getREAD()))
                  .collect(Collectors.toList());
    }

    public ExistentialRuleform lookup(String id) {
        return Optional.ofNullable(existential(id))
                       .map(r -> model.records()
                                      .resolve(r))
                       .filter(rf -> rf != null)
                       .filter(child -> model.getPhantasmModel()
                                             .checkCapability(child, getREAD()))
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
        if (!model.getPhantasmModel()
                  .checkCapability(facet.getFacet(), getREMOVE())) {
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
        if (!checkUPDATE(facet) || !checkUPDATE(auth)) {
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
        if (!checkUPDATE(facet) || !checkUPDATE(auth)) {
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
        if (!checkUPDATE(facet) || !checkUPDATE(stateAuth)) {
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
        if (!checkUPDATE(facet) || !checkUPDATE(stateAuth)) {
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
        if (!checkUPDATE(facet) || !checkUPDATE(stateAuth)) {
            return instance;
        }
        setAttributeArray(instance, stateAuth.getAttribute(), value);
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
        if (!checkUPDATE(facet) || !checkUPDATE(auth)) {
            return instance;
        }

        model.getPhantasmModel()
             .unlinkImmediate(instance, auth.getRelationship());
        children.stream()
                .filter(child -> checkREAD(child))
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
        if (!checkUPDATE(instance)) {
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
        if (!checkUPDATE(instance)) {
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

        if (!checkUPDATE(facet) || !checkUPDATE(auth)) {
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

    private boolean checkREAD(Aspect auth) {
        return model.getPhantasmModel()
                    .checkCapability(auth.getFacet(), getREAD());
    }

    private boolean checkREAD(AttributeAuthorization stateAuth) {
        return model.getPhantasmModel()
                    .checkCapability(stateAuth.getAuth(), getREAD());
    }

    private boolean checkREAD(ExistentialRuleform child) {
        return model.getPhantasmModel()
                    .checkCapability(child, getREAD());
    }

    private boolean checkREAD(NetworkAuthorization stateAuth) {
        return model.getPhantasmModel()
                    .checkCapability(stateAuth.getAuth(), getREAD());
    }

    private boolean checkUPDATE(Aspect stateAuth) {
        return model.getPhantasmModel()
                    .checkCapability(stateAuth.getFacet(), getUPDATE());
    }

    private boolean checkUPDATE(AttributeAuthorization stateAuth) {
        return model.getPhantasmModel()
                    .checkCapability(stateAuth.getAuth(), getUPDATE());
    }

    private boolean checkUPDATE(ExistentialRuleform child) {
        return model.getPhantasmModel()
                    .checkCapability(child, getUPDATE());
    }

    private boolean checkUPDATE(NetworkAuthorization auth) {
        return model.getPhantasmModel()
                    .checkCapability(auth.getAuth(), getUPDATE());
    }

    private ExistentialRecord existential(String id) {
        return model.create()
                    .selectFrom(EXISTENTIAL)
                    .where(EXISTENTIAL.ID.eq(UUID.fromString(id)))
                    .fetchOne();
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
