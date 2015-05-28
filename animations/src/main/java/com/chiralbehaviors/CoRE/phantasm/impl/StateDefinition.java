/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.annotations.Edge;
import com.chiralbehaviors.CoRE.annotations.Facet;
import com.chiralbehaviors.CoRE.annotations.Inferred;
import com.chiralbehaviors.CoRE.annotations.Instantiation;
import com.chiralbehaviors.CoRE.annotations.Key;
import com.chiralbehaviors.CoRE.annotations.State;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class StateDefinition<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>> {

    private static final String                          GET            = "get";
    private static final String                          SET            = "set";
    private final List<ScopedFacet>                      facets         = new ArrayList<>();
    private final List<Method>                           instantiations = new ArrayList<>();
    private final Class<Phantasm<RuleForm>>              stateInterface;
    private final UUID                                   workspace;
    protected final Map<Method, StateFunction<RuleForm>> methods        = new HashMap<>();

    public StateDefinition(Class<Phantasm<RuleForm>> stateInterface) {
        this.stateInterface = stateInterface;
        State state = stateInterface.getAnnotation(State.class);
        workspace = Workspace.uuidOf(state.workspace());
        construct();
    }

    @SuppressWarnings("unchecked")
    /**
     * Constrain the ruleform to have the required facets.  
     * @param model
     * @param ruleform
     * @throws ClassCastException - if the ruleform is not classified as required by the facets of this state definition
     */
    public void constrain(Model model, RuleForm ruleform) {
        if (ruleform == null) {
            throw new IllegalStateException("Ruleform cannot be null");
        }
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networked = model.getNetworkedModel(ruleform);
        WorkspaceScope scope = model.getWorkspaceModel().getScoped(workspace);
        if (scope == null) {
            throw new IllegalStateException(
                                            String.format("Cannot obtain workspace for state interface %s",
                                                          stateInterface));
        }
        List<String> failures = new ArrayList<>();
        for (ScopedFacet constraint : facets) {
            RuleForm classification = (RuleForm) constraint.resolveClassification(scope);
            if (classification == null) {
                throw new IllegalStateException(
                                                String.format("Cannot obtain classification %s for %s",
                                                              constraint.toClassificationString(),
                                                              stateInterface));
            }
            Relationship classifier = (Relationship) constraint.resolveClassifier(scope);
            if (classifier == null) {
                throw new IllegalStateException(
                                                String.format("Cannot obtain classifier %s for %s",
                                                              constraint.toClassifierString(),
                                                              stateInterface));
            }
            if (!networked.isAccessible(ruleform, classifier, classification)) {
                failures.add(constraint.toFacetString());
            }
        }
        if (!failures.isEmpty()) {
            throw new ClassCastException(
                                         String.format("%s does not have required facets %s of state %s",
                                                       ruleform, failures,
                                                       stateInterface));
        }
    }

    /**
     * @param model
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Aspect<RuleForm>> getAspects(Model model) {
        WorkspaceScope scope = model.getWorkspaceModel().getScoped(workspace);
        List<Aspect<RuleForm>> specs = new ArrayList<>();
        for (ScopedFacet facet : facets) {
            specs.add(new Aspect<RuleForm>(
                                           (Relationship) facet.resolveClassifier(scope),
                                           (RuleForm) facet.resolveClassification(scope)));
        }
        return specs;
    }

    /**
     * @return
     */
    public List<Method> getInstantiations() {
        return instantiations;
    }

    public Map<Method, StateFunction<RuleForm>> getMethods() {
        return methods;
    }

    public Class<Phantasm<RuleForm>> getStateInterface() {
        return stateInterface;
    }

    public UUID getWorkspace() {
        return workspace;
    }

    /**
     * Construct the map of methods to functions that implement the state
     * defition behavior
     */
    private void construct() {
        State state = stateInterface.getAnnotation(State.class);
        for (Facet facet : state.facets()) {
            facets.add(ScopedFacet.from(facet));
        }
        if (facets.isEmpty()) {
            facets.add(ScopedFacet.from(stateInterface));
        }
        for (Method method : stateInterface.getDeclaredMethods()) {
            if (!method.isDefault()) {
                process(method);
            } else {
                if (method.getAnnotation(Instantiation.class) != null) {
                    instantiations.add(method);
                }
            }
        }
        for (Class<?> iFace : stateInterface.getInterfaces()) {
            for (Method method : iFace.getDeclaredMethods()) {
                if (!method.isDefault()) {
                    process(method);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void getInferred(Method method, Key value,
                             Class<ExistentialRuleform<?, ?>> rulformClass) {
        if (!rulformClass.equals(getRuleformClass())) {
            throw new IllegalStateException(
                                            String.format("Use of @Inferred can only be applied to network relationship methods: %s",
                                                          method.toGenericString()));
        }
        methods.put(method,
                    (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                     Object[] arguments) -> state.getChild(value.namespace(),
                                                           value.name(),
                                                           (Class<Phantasm<? extends RuleForm>>) method.getReturnType(),
                                                           scope));
    }

    @SuppressWarnings("unchecked")
    private Class<RuleForm> getRuleformClass() {
        return (Class<RuleForm>) Model.getExistentialRuleform(stateInterface);
    }

    private void process(Edge edge, Method method) {
        if (method.getName().startsWith("add")) {
            processAdd(edge, method);
        } else if (method.getName().startsWith("remove")) {
            processRemove(edge, method);
        } else if (method.getParameterTypes().length == 0
                   && List.class.isAssignableFrom(method.getReturnType())) {
            processGetList(edge, method);
        } else if (method.getParameterTypes().length == 1
                   && List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            processSetList(edge, method);
        } else {
            processSingular(edge, method);
        }
    }

    private void process(Key annotation, Method method) {
        if (method.getName().startsWith(GET)) {
            processPrimitiveGetter(annotation.namespace(), annotation.name(),
                                   method);
        } else if (method.getName().startsWith(SET)) {
            processPrimitiveSetter(annotation.namespace(), annotation.name(),
                                   method);
        } else {
            throw new IllegalStateException(
                                            String.format("The method is neither a primitive setter/getter: %s",
                                                          method.toGenericString()));
        }
    }

    private void process(Method method) {
        if (method.getName().equals("getScope")
            && method.getDeclaringClass().equals(ScopedPhantasm.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getScope(this));
            return;
        }
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(Phantasm.class)
            || declaringClass.equals(ScopedPhantasm.class)) {
            return;
        }
        if (method.getAnnotation(Edge.class) != null) {
            process(method.getAnnotation(Edge.class), method);
        } else if (method.getAnnotation(Key.class) != null) {
            process(method.getAnnotation(Key.class), method);
        } else {
            processUnknown(method);
        }
    }

    /**
     * @param annotation
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processAdd(Edge annotation, Method method) {
        if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            Class<? extends Phantasm<?>> returnPhantasm = (Class<Phantasm<?>>) ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
            Class<?> ruleformClass = Model.getExistentialRuleform(returnPhantasm);
            if (getRuleformClass().equals(ruleformClass)) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.addChildren(annotation.value().namespace(),
                                                                      annotation.value().name(),
                                                                      (List<Phantasm<RuleForm>>) arguments[0],
                                                                      scope));
            } else {
                processAddAuthorizations(annotation, method, ruleformClass);
            }

        } else if (Phantasm.class.isAssignableFrom(method.getParameterTypes()[0])) {
            Class<? extends Phantasm<?>> returnPhantasm = (Class<? extends Phantasm<?>>) method.getParameterTypes()[0];
            Class<?> ruleformClass = Model.getExistentialRuleform(returnPhantasm);
            if (getRuleformClass().equals(ruleformClass)) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.addChild(annotation.value().namespace(),
                                                                   annotation.value().name(),
                                                                   ((Phantasm<RuleForm>) arguments[0]).getRuleform(),
                                                                   scope));
            } else {
                processAddAuthorization(annotation, method, ruleformClass);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processAddAuthorization(Edge annotation, Method method,
                                         Class<?> ruleformClass) {
        if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.addAgencyAuth(annotation.value().namespace(),
                                                                    annotation.value().name(),
                                                                    (Phantasm<Agency>) arguments[0],
                                                                    scope));
        } else if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.addLocationAuth(annotation.value().namespace(),
                                                                      annotation.value().name(),
                                                                      (Phantasm<Location>) arguments[0],
                                                                      scope));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.addProductAuth(annotation.value().namespace(),
                                                                     annotation.value().name(),
                                                                     (Phantasm<Product>) arguments[0],
                                                                     scope));
        } else {
            throw new IllegalStateException(
                                            String.format("The authorization %s->%s is undefined",
                                                          getRuleformClass(),
                                                          ruleformClass));
        }
    }

    @SuppressWarnings("unchecked")
    private void processAddAuthorizations(Edge annotation, Method method,
                                          Class<?> ruleformClass) {
        if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.addAgencyAuths(annotation.value().namespace(),
                                                                     annotation.value().name(),
                                                                     (List<Phantasm<Agency>>) arguments[0],
                                                                     scope));
        } else if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.addLocationAuths(annotation.value().namespace(),
                                                                       annotation.value().name(),
                                                                       (List<Phantasm<Location>>) arguments[0],
                                                                       scope));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.addProductAuths(annotation.value().namespace(),
                                                                      annotation.value().name(),
                                                                      (List<Phantasm<Product>>) arguments[0],
                                                                      scope));
        } else {
            throw new IllegalStateException(
                                            String.format("The authorization %s->%s is undefined",
                                                          getRuleformClass(),
                                                          ruleformClass));
        }
    }

    /**
     * @param method
     * @param returnPhantasm
     * @param annotation
     * @param ruleformClass
     */
    @SuppressWarnings("unchecked")
    private void processGetAuthorizations(Method method,
                                          Class<? extends Phantasm<?>> phantasmReturned,
                                          Key relationship,
                                          Class<?> ruleformClass) {
        if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getLocationAuths(relationship.namespace(),
                                                                       relationship.name(),
                                                                       (Class<? extends Phantasm<Location>>) phantasmReturned,
                                                                       scope));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getProductAuths(relationship.namespace(),
                                                                      relationship.name(),
                                                                      (Class<? extends Phantasm<Product>>) phantasmReturned,
                                                                      scope));
        } else if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getAgencyAuths(relationship.namespace(),
                                                                     relationship.name(),
                                                                     (Class<? extends Phantasm<Agency>>) phantasmReturned,
                                                                     scope));
        } else {
            throw new IllegalStateException(
                                            String.format("No such authorization from Product to %s",
                                                          ruleformClass.getSimpleName()));
        }
    }

    /**
     * @param annotation
     * @param method
     * @return
     */
    @SuppressWarnings("unchecked")
    private void processGetList(Edge annotation, Method method) {
        Class<? extends Phantasm<?>> returnPhantasm = (Class<Phantasm<?>>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        Class<?> ruleformClass = Model.getExistentialRuleform(returnPhantasm);
        if (getRuleformClass().equals(ruleformClass)) {
            Class<Phantasm<? extends RuleForm>> phantasm = (Class<Phantasm<? extends RuleForm>>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            if (method.getAnnotation(Inferred.class) != null) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.getChildren(annotation.value().namespace(),
                                                                      annotation.value().name(),
                                                                      phantasm,
                                                                      scope));
            } else {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.getImmediateChildren(annotation.value().namespace(),
                                                                               annotation.value().name(),
                                                                               phantasm,
                                                                               scope));
            }
        } else {
            processGetAuthorizations(method, returnPhantasm,
                                     annotation.value(), ruleformClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void processGetSingularAuthorization(Method method,
                                                 Class<? extends Phantasm<?>> phantasmReturned,
                                                 Key value,
                                                 Class<ExistentialRuleform<?, ?>> ruleformClass) {
        if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getSingularLocationAuth(value.namespace(),
                                                                              value.name(),
                                                                              (Class<? extends Phantasm<Location>>) phantasmReturned,
                                                                              scope));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getSingularProductAuth(value.namespace(),
                                                                             value.name(),
                                                                             (Class<? extends Phantasm<Product>>) phantasmReturned,
                                                                             scope));
        } else if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getSingularAgencyAuth(value.namespace(),
                                                                            value.name(),
                                                                            (Class<? extends Phantasm<Agency>>) phantasmReturned,
                                                                            scope));
        } else {
            throw new IllegalStateException(
                                            String.format("No such authorization from Product to %s",
                                                          ruleformClass.getSimpleName()));
        }
    }

    private void processPrimitiveGetter(String namespace, String name,
                                        Method method) {
        if (method.getParameterCount() != 0) {
            throw new IllegalStateException(
                                            String.format("getter method has arguments %s",
                                                          method.toGenericString()));
        }
        if (method.getReturnType().isArray()) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getAttributeArray(namespace,
                                                                        name,
                                                                        method.getReturnType().getComponentType(),
                                                                        scope));
        } else if (Map.class.isAssignableFrom(method.getReturnType())) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getAttributeMap(namespace,
                                                                      name,
                                                                      method.getReturnType(),
                                                                      scope));
        } else if (List.class.isAssignableFrom(method.getReturnType())) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> Arrays.asList(state.getAttributeArray(namespace,
                                                                                      name,
                                                                                      method.getReturnType().getComponentType(),
                                                                                      scope)));
        } else {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.getAttributeValue(namespace,
                                                                        name,
                                                                        scope));
        }
    }

    @SuppressWarnings("unchecked")
    private void processPrimitiveSetter(String namespace, String name,
                                        Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalStateException(
                                            String.format("setter method does not have a singular argument %s",
                                                          method.toGenericString()));
        }
        if (method.getParameterTypes()[0].isArray()) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setAttributeArray(namespace,
                                                                        name,
                                                                        (Object[]) arguments[0],
                                                                        scope));
        } else if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setAttributeArray(namespace,
                                                                        name,
                                                                        ((List<?>) arguments[0]).toArray(),
                                                                        scope));
        } else if (Map.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setAttributeMap(namespace,
                                                                      name,
                                                                      (Map<String, Object>) arguments[0],
                                                                      scope));
        } else {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setAttributeValue(namespace,
                                                                        name,
                                                                        arguments[0],
                                                                        scope));
        }
    }

    /**
     * @param annotation
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processRemove(Edge annotation, Method method) {
        if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            Class<? extends Phantasm<?>> returnPhantasm = (Class<Phantasm<?>>) ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
            Class<?> ruleformClass = Model.getExistentialRuleform(returnPhantasm);
            if (getRuleformClass().equals(ruleformClass)) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.removeChildren(annotation.value().namespace(),
                                                                         annotation.value().name(),
                                                                         (List<Phantasm<RuleForm>>) arguments[0],
                                                                         scope));
            } else {
                processRemoveAuthorizations(annotation.value(), method,
                                            ruleformClass);
            }
        } else if (Phantasm.class.isAssignableFrom(method.getParameterTypes()[0])) {
            Class<? extends Phantasm<?>> returnPhantasm = (Class<? extends Phantasm<?>>) method.getParameterTypes()[0];
            Class<?> ruleformClass = Model.getExistentialRuleform(returnPhantasm);
            if (getRuleformClass().equals(ruleformClass)) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.removeChild(annotation.value().namespace(),
                                                                      annotation.value().name(),
                                                                      ((Phantasm<RuleForm>) arguments[0]).getRuleform(),
                                                                      scope));
            } else {
                processRemoveAuthorization(annotation.value(), method,
                                           ruleformClass);
            }
        }
    }

    /**
     * @param annotation
     * @param method
     * @param ruleformClass
     */
    @SuppressWarnings("unchecked")
    private void processRemoveAuthorization(Key key, Method method,
                                            Class<?> ruleformClass) {
        if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.removeAgencyAuth(key.namespace(),
                                                                       key.name(),
                                                                       (Phantasm<Agency>) arguments[0],
                                                                       scope));
        } else if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.removeLocationAuth(key.namespace(),
                                                                         key.name(),
                                                                         (Phantasm<Location>) arguments[0],
                                                                         scope));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.removeProductAuth(key.namespace(),
                                                                        key.name(),
                                                                        (Phantasm<Product>) arguments[0],
                                                                        scope));
        } else {
            throw new IllegalStateException(
                                            String.format("No such authorization from Product to %s",
                                                          ruleformClass.getSimpleName()));
        }
    }

    /**
     * @param key
     * @param method
     * @param ruleformClass
     */
    @SuppressWarnings("unchecked")
    private void processRemoveAuthorizations(Key key, Method method,
                                             Class<?> ruleformClass) {
        if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.removeAgencyAuths(key.namespace(),
                                                                        key.name(),
                                                                        (List<Phantasm<Agency>>) arguments[0],
                                                                        scope));
        } else if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.removeLocationAuths(key.namespace(),
                                                                          key.name(),
                                                                          (List<Phantasm<Location>>) arguments[0],
                                                                          scope));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.removeProductAuths(key.namespace(),
                                                                         key.name(),
                                                                         (List<Phantasm<Product>>) arguments[0],
                                                                         scope));
        } else {
            throw new IllegalStateException(
                                            String.format("No such authorization from Product to %s",
                                                          ruleformClass.getSimpleName()));
        }
    }

    /**
     * @param annotation
     * @param method
     * @param ruleformClass
     */
    @SuppressWarnings("unchecked")
    private void processSetAuthorizations(Edge annotation, Method method,
                                          Class<?> ruleformClass) {
        if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setAgencyAuths(annotation.value().namespace(),
                                                                     annotation.value().name(),
                                                                     (List<Phantasm<Agency>>) arguments[0],
                                                                     scope));
        } else if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setLocationAuths(annotation.value().namespace(),
                                                                       annotation.value().name(),
                                                                       (List<Phantasm<Location>>) arguments[0],
                                                                       scope));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setProductAuths(annotation.value().namespace(),
                                                                      annotation.value().name(),
                                                                      (List<Phantasm<Product>>) arguments[0],
                                                                      scope));
        } else {
            throw new IllegalStateException(
                                            String.format("The authorization %s->%s is undefined",
                                                          getRuleformClass(),
                                                          ruleformClass));
        }
    }

    /**
     * @param annotation
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processSetList(Edge annotation, Method method) {
        Class<? extends Phantasm<?>> returnPhantasm = (Class<Phantasm<?>>) ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
        Class<?> ruleformClass = Model.getExistentialRuleform(returnPhantasm);
        if (getRuleformClass().equals(ruleformClass)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setImmediateChildren(annotation.value().namespace(),
                                                                           annotation.value().name(),
                                                                           (List<Phantasm<RuleForm>>) arguments[0],
                                                                           scope));
        } else {
            processSetAuthorizations(annotation, method, ruleformClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void processSetSingular(Method method, Key value) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException(
                                               String.format("Not a valid Relationship setter: %s",
                                                             method));
        }
        Class<? extends Phantasm<?>> phantasmToSet = (Class<Phantasm<?>>) method.getParameterTypes()[0];
        Class<?> ruleformClass = Model.getExistentialRuleform(phantasmToSet);
        if (ruleformClass.equals(getRuleformClass())) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setImmediateChild(value.namespace(),
                                                                        value.name(),
                                                                        (Phantasm<RuleForm>) arguments[0],
                                                                        scope));
        } else {
            processSetSingularAuthorization(method, value, ruleformClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void processSetSingularAuthorization(Method method, Key value,
                                                 Class<?> ruleformClass) {
        if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setSingularLocationAuth(value.namespace(),
                                                                              value.name(),
                                                                              (Phantasm<Location>) arguments[0],
                                                                              scope));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setSingularProductAuth(value.namespace(),
                                                                             value.name(),
                                                                             (Phantasm<Product>) arguments[0],
                                                                             scope));
        } else if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                         Object[] arguments) -> state.setSingularAgencyAuth(value.namespace(),
                                                                            value.name(),
                                                                            (Phantasm<Agency>) arguments[0],
                                                                            scope));
        } else {
            throw new IllegalStateException(
                                            String.format("No such authorization from Product to %s",
                                                          ruleformClass.getSimpleName()));
        }
    }

    @SuppressWarnings("unchecked")
    private void processSingular(Edge annotation, Method method) {
        Key value = annotation.value();
        if (method.getReturnType().equals(Void.TYPE)) {
            processSetSingular(method, value);
            return;
        }

        Class<? extends Phantasm<?>> phantasmReturned = (Class<Phantasm<?>>) method.getReturnType();
        Class<ExistentialRuleform<?, ?>> ruleformClass = (Class<ExistentialRuleform<?, ?>>) Model.getExistentialRuleform(phantasmReturned);
        if (method.getAnnotation(Inferred.class) != null) {
            getInferred(method, value, ruleformClass);
        } else {
            if (ruleformClass.equals(getRuleformClass())) {
                methods.put(method,
                            (PhantasmTwo<RuleForm> state, WorkspaceScope scope,
                             Object[] arguments) -> state.getImmediateChild(value.namespace(),
                                                                            value.name(),
                                                                            (Class<Phantasm<? extends RuleForm>>) method.getReturnType(),
                                                                            scope));
            } else {
                processGetSingularAuthorization(method, phantasmReturned,
                                                value, ruleformClass);
            }
        }
    }

    private void processUnknown(Method method) {
        if (method.getName().startsWith(GET)) {
            processPrimitiveGetter(null,
                                   method.getName().substring(GET.length(),
                                                              method.getName().length()),
                                   method);
        } else if (method.getName().startsWith(SET)) {
            processPrimitiveSetter(null,
                                   method.getName().substring(SET.length(),
                                                              method.getName().length()),
                                   method);
        }
    }
}
