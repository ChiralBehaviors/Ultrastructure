/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.phantasm.impl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyLocation;
import com.chiralbehaviors.CoRE.agency.AgencyLocation_;
import com.chiralbehaviors.CoRE.agency.AgencyProduct;
import com.chiralbehaviors.CoRE.agency.AgencyProduct_;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductLocation;
import com.chiralbehaviors.CoRE.product.ProductLocation_;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class StateImpl<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>>
        implements InvocationHandler, ScopedPhantasm<RuleForm> {

    private final Map<Method, StateFunction<RuleForm>> methods;
    private final Model                                model;
    private final RuleForm                             ruleform;
    private final WorkspaceScope                       scope;

    public StateImpl(RuleForm ruleform, Model model,
                     Map<Method, StateFunction<RuleForm>> methods,
                     WorkspaceScope scope) {
        this.ruleform = ruleform;
        this.model = model;
        this.methods = methods;
        this.scope = scope;
    }

    @Override
    public String getDescription() {
        return ruleform.getDescription();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.Phantasm#getModel()
     */
    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public String getName() {
        return ruleform.getName();
    }

    @Override
    public String getNotes() {
        return ruleform.getNotes();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.Phantasm#getRuleform()
     */
    @Override
    public RuleForm getRuleform() {
        return ruleform;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.Phantasm#getScope()
     */
    @Override
    public WorkspaceScope getScope() {
        return scope;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.Phantasm#getUpdatedBy()
     */
    @Override
    public Agency getUpdatedBy() {
        return ruleform.getUpdatedBy();
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
                                                                    throws Throwable {
        // Hard override (final) equals() and hashCode().  Becauase invariance.
        if (method.getName().equals("equals") && args.length == 1
            && method.getParameterTypes()[0].equals(Object.class)) {
            return args[0] instanceof Phantasm ? ruleform.equals(((Phantasm<?>) args[0]).getRuleform())
                                              : false;
        } else if (method.getName().equals("hashCode") && args.length == 0) {
            return ruleform.hashCode();
        }
        if (method.isDefault()) {
            final Class<?> declaringClass = method.getDeclaringClass();
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class,
                                                                                                              int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(declaringClass,
                                           MethodHandles.Lookup.PRIVATE).unreflectSpecial(method,
                                                                                          declaringClass).bindTo(proxy).invokeWithArguments(args);
        }
        StateFunction<RuleForm> function = methods.get(method);
        Object returnValue = function != null ? function.invoke(this, args)
                                             : method.invoke(this, args);

        // always maintain proxy discipline.  Because identity.
        return returnValue == this ? proxy : returnValue;
    }

    private Attribute getAttribute(String namespace, String key) {
        Attribute attribute = scope.lookup(namespace, key);
        if (attribute == null) {
            throw new IllegalStateException(
                                            String.format("The attribute %s:%s does not exist in the workspace",
                                                          namespace == null ? ""
                                                                           : namespace,
                                                          key));
        }
        return attribute;
    }

    private Relationship getRelationship(String s, String key) {
        return (Relationship) scope.lookup(s, key);
    }

    private AttributeValue<RuleForm>[] getValueArray(Attribute attribute) {
        @SuppressWarnings("unchecked")
        List<AttributeValue<RuleForm>> values = (List<AttributeValue<RuleForm>>) model.getNetworkedModel(ruleform).getAttributeValues(ruleform,
                                                                                                                                      attribute);
        int max = 0;
        for (AttributeValue<RuleForm> value : values) {
            max = Math.max(max, value.getSequenceNumber() + 1);
        }
        @SuppressWarnings("unchecked")
        AttributeValue<RuleForm>[] returnValue = new AttributeValue[max];
        for (AttributeValue<RuleForm> form : values) {
            returnValue[form.getSequenceNumber()] = form;
        }
        return returnValue;
    }

    private Map<String, AttributeValue<RuleForm>> getValueMap(Attribute attribute) {
        Map<String, AttributeValue<RuleForm>> map = new HashMap<>();
        for (AttributeValue<RuleForm> value : model.getNetworkedModel(ruleform).getAttributeValues(ruleform,
                                                                                                   attribute)) {
            map.put(value.getKey(), value);
        }
        return map;
    }

    private AttributeValue<RuleForm> newAttributeValue(Attribute attribute,
                                                       int i) {
        AttributeValue<RuleForm> value = model.getNetworkedModel(ruleform).create(ruleform,
                                                                                  attribute,
                                                                                  model.getKernel().getCoreAnimationSoftware());
        value.setSequenceNumber(i);
        return value;
    }

    private void removeImmediateChild(NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel,
                                      Relationship relationship,
                                      Phantasm<RuleForm> child) {
        NetworkRuleform<RuleForm> link = networkedModel.getImmediateLink(ruleform,
                                                                         relationship,
                                                                         child.getRuleform());
        if (link != null) {
            model.getEntityManager().remove(link);
        }
    }

    private void setValue(Attribute attribute, int i,
                          AttributeValue<RuleForm> existing, Object newValue) {
        if (existing == null) {
            existing = newAttributeValue(attribute, i);
            model.getEntityManager().persist(existing);
        }
        existing.setValue(newValue);
    }

    private List<Phantasm<? super RuleForm>> wrap(List<RuleForm> queryResult,
                                                  Class<Phantasm<? extends RuleForm>> phantasm) {
        List<Phantasm<? super RuleForm>> result = new ArrayList<>(
                                                                  queryResult.size());
        for (RuleForm ruleform : queryResult) {
            result.add(model.wrap(phantasm, ruleform));
        }
        return result;
    }

    protected Object addChild(String scope, String key, RuleForm child) {
        model.getNetworkedModel(ruleform).link(ruleform,
                                               getRelationship(scope, key),
                                               child,
                                               model.getKernel().getCore());
        return null;
    }

    protected Object addChildren(String s, String key,
                                 List<Phantasm<RuleForm>> children) {
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = model.getNetworkedModel(ruleform);
        Relationship relationship = getRelationship(s, key);
        for (Phantasm<RuleForm> child : children) {
            networkedModel.link(ruleform, relationship, child.getRuleform(),
                                model.getKernel().getCore());
        }
        return null;
    }

    protected Object[] getAttributeArray(String namespace, String key,
                                         Class<?> type) {
        Attribute attribute = getAttribute(namespace, key);
        if (!attribute.getIndexed()) {
            throw new IllegalStateException(
                                            String.format("Attribute %s:%s is not indexed",
                                                          namespace, key));
        }
        AttributeValue<RuleForm>[] attributeValues = getValueArray(attribute);

        Object[] values = (Object[]) Array.newInstance(type,
                                                       attributeValues.length);
        for (AttributeValue<RuleForm> value : attributeValues) {
            values[value.getSequenceNumber()] = value.getValue();
        }
        return values;
    }

    /**
     * @param object
     * @param key
     * @param returnType
     * @return
     */
    protected Map<String, ?> getAttributeMap(String namespace, String key,
                                             Class<?> returnType) {
        Attribute attribute = getAttribute(namespace, key);
        if (!attribute.getKeyed()) {
            throw new IllegalStateException(
                                            String.format("Attribute %s:%s is not keyed",
                                                          namespace, key));
        }
        Map<String, ?> map = new HashMap<>();
        for (Map.Entry<String, AttributeValue<RuleForm>> entry : getValueMap(
                                                                             attribute).entrySet()) {
            map.put(entry.getKey(), entry.getValue().getValue());
        }
        return map;
    }

    protected Object getAttributeValue(String s, String key) {
        @SuppressWarnings("unchecked")
        List<AttributeValue<?>> values = (List<AttributeValue<?>>) model.getNetworkedModel(ruleform).getAttributeValues(ruleform,
                                                                                                                        getAttribute(s,
                                                                                                                                     key));
        if (values.size() == 0) {
            throw new IllegalArgumentException(
                                               String.format("No such attribute: %s:%s",
                                                             scope == null ? ""
                                                                          : scope,
                                                             key));
        } else if (values.size() > 1) {
            throw new IllegalArgumentException(
                                               String.format("Multiple values for attribute: %s:%s",
                                                             scope == null ? ""
                                                                          : scope,
                                                             key));
        }
        return values.get(0).getValue();
    }

    protected Object getChild(String scope, String key,
                              Class<Phantasm<? extends RuleForm>> phantasm) {
        return model.wrap(phantasm,
                          model.getNetworkedModel(ruleform).getSingleChild(ruleform,
                                                                           getRelationship(scope,
                                                                                           key)));
    }

    protected List<Phantasm<? super RuleForm>> getChildren(String scope,
                                                           String key,
                                                           Class<Phantasm<? extends RuleForm>> phantasm) {
        List<RuleForm> queryResult = model.getNetworkedModel(ruleform).getChildren(ruleform,
                                                                                   getRelationship(scope,
                                                                                                   key));
        return wrap(queryResult, phantasm);
    }

    protected Object getImmediateChild(String namespace,
                                       String name,
                                       Class<Phantasm<? extends RuleForm>> phantasm) {
        return model.wrap(phantasm,
                          model.getNetworkedModel(ruleform).getImmediateChild(ruleform,
                                                                              getRelationship(namespace,
                                                                                              name)));
    }

    protected List<Phantasm<? super RuleForm>> getImmediateChildren(String scope,
                                                                    String key,
                                                                    Class<Phantasm<? extends RuleForm>> phantasm) {
        List<RuleForm> queryResult = model.getNetworkedModel(ruleform).getImmediateChildren(ruleform,
                                                                                            getRelationship(scope,
                                                                                                            key));
        return wrap(queryResult, phantasm);
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularAgencyLocation(String namespace,
                                               String name,
                                               Class<? extends Phantasm<Location>> phantasmReturned) {
        Relationship relationship = getRelationship(namespace, name);
        EntityManager em = model.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Location> query = cb.createQuery(Location.class);
        Root<AgencyLocation> plRoot = query.from(AgencyLocation.class);
        Path<Location> path;
        try {
            path = plRoot.get(AgencyLocation_.location);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(plRoot.get(AgencyLocation_.agency),
                                                 ruleform),
                                        cb.equal(plRoot.get(AgencyLocation_.relationship),
                                                 relationship)));
        TypedQuery<Location> q = em.createQuery(query);
        return model.wrap(phantasmReturned, q.getSingleResult());
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularAgencyProduct(String namespace,
                                              String name,
                                              Class<? extends Phantasm<Product>> phantasmReturned) {
        Relationship relationship = getRelationship(namespace, name);
        EntityManager em = model.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<AgencyProduct> plRoot = query.from(AgencyProduct.class);
        Path<Product> path;
        try {
            path = plRoot.get(AgencyProduct_.product);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(plRoot.get(AgencyProduct_.agency),
                                                 ruleform),
                                        cb.equal(plRoot.get(AgencyProduct_.relationship),
                                                 relationship)));
        TypedQuery<Product> q = em.createQuery(query);
        return model.wrap(phantasmReturned, q.getSingleResult());
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularLocationAgency(String namespace,
                                               String name,
                                               Class<? extends Phantasm<Agency>> phantasmReturned) {
        Relationship relationship = getRelationship(namespace, name);
        EntityManager em = model.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Agency> query = cb.createQuery(Agency.class);
        Root<AgencyLocation> plRoot = query.from(AgencyLocation.class);
        Path<Agency> path;
        try {
            path = plRoot.get(AgencyLocation_.agency);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(plRoot.get(AgencyLocation_.location),
                                                 ruleform),
                                        cb.equal(plRoot.get(AgencyLocation_.relationship),
                                                 relationship)));
        TypedQuery<Agency> q = em.createQuery(query);
        return model.wrap(phantasmReturned, q.getSingleResult());
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularLocationProduct(String namespace,
                                                String name,
                                                Class<? extends Phantasm<Product>> phantasmReturned) {
        Relationship relationship = getRelationship(namespace, name);
        EntityManager em = model.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<ProductLocation> plRoot = query.from(ProductLocation.class);
        Path<Product> path;
        try {
            path = plRoot.get(ProductLocation_.product);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(plRoot.get(ProductLocation_.location),
                                                 ruleform),
                                        cb.equal(plRoot.get(ProductLocation_.relationship),
                                                 relationship)));
        TypedQuery<Product> q = em.createQuery(query);
        return model.wrap(phantasmReturned, q.getSingleResult());
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularProductAgency(String namespace,
                                              String name,
                                              Class<? extends Phantasm<Agency>> phantasmReturned) {
        Relationship relationship = getRelationship(namespace, name);
        EntityManager em = model.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Agency> query = cb.createQuery(Agency.class);
        Root<AgencyProduct> plRoot = query.from(AgencyProduct.class);
        Path<Agency> path;
        try {
            path = plRoot.get(AgencyProduct_.agency);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(plRoot.get(AgencyProduct_.product),
                                                 ruleform),
                                        cb.equal(plRoot.get(AgencyProduct_.relationship),
                                                 relationship)));
        TypedQuery<Agency> q = em.createQuery(query);
        return model.wrap(phantasmReturned, q.getSingleResult());
    }

    /**
     * @param namespace
     * @param name
     * @param phantasmReturned
     * @return
     */
    protected Object getSingularProductLocation(String namespace,
                                                String name,
                                                Class<? extends Phantasm<Location>> phantasmReturned) {
        Relationship relationship = getRelationship(namespace, name);
        EntityManager em = model.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Location> query = cb.createQuery(Location.class);
        Root<ProductLocation> plRoot = query.from(ProductLocation.class);
        Path<Location> path;
        try {
            path = plRoot.get(ProductLocation_.location);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(plRoot.get(ProductLocation_.product),
                                                 ruleform),
                                        cb.equal(plRoot.get(ProductLocation_.relationship),
                                                 relationship)));
        TypedQuery<Location> q = em.createQuery(query);
        return model.wrap(phantasmReturned, q.getSingleResult());
    }

    protected Object removeChild(String scope, String name, RuleForm child) {
        removeImmediateChild(model.getNetworkedModel(ruleform),
                             getRelationship(scope, name), child);
        return null;
    }

    protected Object removeChildren(String s, String key,
                                    List<Phantasm<RuleForm>> children) {
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = model.getNetworkedModel(ruleform);
        Relationship relationship = getRelationship(s, key);
        for (Phantasm<RuleForm> child : children) {
            removeImmediateChild(networkedModel, relationship, child);
        }
        return null;
    }

    protected Object setAttributeArray(String namespace, String key,
                                       Object[] values) {
        Attribute attribute = getAttribute(namespace, key);
        if (!attribute.getIndexed()) {
            throw new IllegalStateException(
                                            String.format("Attribute %s:%s is not indexed",
                                                          namespace, key));
        }
        AttributeValue<RuleForm>[] old = getValueArray(attribute);
        if (values == null) {
            if (old != null) {
                for (AttributeValue<RuleForm> value : old) {
                    model.getEntityManager().remove(value);
                }
            }
        } else if (old == null) {
            for (int i = 0; i < values.length; i++) {
                setValue(attribute, i, null, values[i]);
            }
        } else if (old.length == values.length) {
            for (int i = 0; i < values.length; i++) {
                setValue(attribute, i, old[i], values[i]);
            }
        } else if (old.length < values.length) {
            int i;
            for (i = 0; i < old.length; i++) {
                setValue(attribute, i, old[i], values[i]);
            }
            for (; i < values.length; i++) {
                setValue(attribute, i, null, values[i]);
            }
        } else if (old.length > values.length) {
            int i;
            for (i = 0; i < values.length; i++) {
                setValue(attribute, i, old[i], values[i]);
            }
            for (; i < old.length; i++) {
                model.getEntityManager().remove(old[i]);
            }
        }
        return null;
    }

    protected Object setAttributeMap(String namespace, String key,
                                     Map<String, Object> values) {
        Attribute attribute = getAttribute(namespace, key);
        if (!attribute.getKeyed()) {
            throw new IllegalStateException(
                                            String.format("Attribute %s:%s is not keyed",
                                                          namespace, key));
        }
        Map<String, AttributeValue<RuleForm>> valueMap = getValueMap(attribute);
        values.keySet().stream().filter(keyName -> !valueMap.containsKey(keyName)).forEach(keyName -> valueMap.remove(keyName));
        int maxSeq = 0;
        for (AttributeValue<RuleForm> value : valueMap.values()) {
            maxSeq = Math.max(maxSeq, value.getSequenceNumber());
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            AttributeValue<RuleForm> value = valueMap.get(entry.getKey());
            if (value == null) {
                value = newAttributeValue(attribute, ++maxSeq);
                model.getEntityManager().persist(value);
                value.setKey(entry.getKey());
            }
            value.setValue(entry.getValue());
        }
        return null;
    }

    protected Object setAttributeValue(String namespace, String key,
                                       Object value) {
        Attribute attribute = getAttribute(namespace, key);
        @SuppressWarnings("unchecked")
        List<AttributeValue<?>> values = (List<AttributeValue<?>>) model.getNetworkedModel(ruleform).getAttributeValues(ruleform,
                                                                                                                        attribute);
        if (values.size() == 0) {
            throw new IllegalArgumentException(
                                               String.format("No such attribute: %s:%s",
                                                             namespace == null ? ""
                                                                              : namespace,
                                                             key));
        } else if (values.size() > 1) {
            throw new IllegalArgumentException(
                                               String.format("Multiple values for attribute: %s:%s",
                                                             namespace == null ? ""
                                                                              : namespace,
                                                             key));
        }
        values.get(0).setValue(value);
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param object
     * @return
     */
    protected Object setImmediateChild(String scope, String key,
                                       Phantasm<RuleForm> phantasm) {
        RuleForm child = phantasm.getRuleform();
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = model.getNetworkedModel(ruleform);
        networkedModel.setImmediateChild(ruleform,
                                         getRelationship(scope, key),
                                         child,
                                         model.getKernel().getCoreAnimationSoftware());
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param object
     * @return
     */
    protected Object setImmediateChildren(String scope, String key,
                                          List<RuleForm> children) {
        Relationship relationship = getRelationship(scope, key);
        for (Phantasm<RuleForm> phantasm : children) {
            RuleForm child = phantasm.getRuleform();
            NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networkedModel = model.getNetworkedModel(ruleform);
            networkedModel.setImmediateChild(ruleform,
                                             relationship,
                                             child,
                                             model.getKernel().getCoreAnimationSoftware());
        }
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object setSingularAgencyLocation(String namespace, String name,
                                               Phantasm<Location> phantasm) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object setSingularAgencyProduct(String namespace, String name,
                                              Phantasm<Product> phantasm) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object setSingularLocationAgency(String namespace, String name,
                                               Phantasm<Agency> arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object setSingularLocationProduct(String namespace, String name,
                                                Phantasm<Product> arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param arguments
     * @return
     */
    protected Object setSingularProductAgency(String namespace, String name,
                                              Phantasm<Agency> arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param namespace
     * @param name
     * @param phantasm
     * @return
     */
    protected Object setSingularProductLocation(String namespace, String name,
                                                Phantasm<Location> phantasm) {
        // TODO Auto-generated method stub
        return null;
    }
}
