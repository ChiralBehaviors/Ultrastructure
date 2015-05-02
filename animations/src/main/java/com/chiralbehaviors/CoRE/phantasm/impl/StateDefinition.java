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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.annotations.Edge;
import com.chiralbehaviors.annotations.Facet;
import com.chiralbehaviors.annotations.Inferred;
import com.chiralbehaviors.annotations.Key;
import com.chiralbehaviors.annotations.State;

/**
 * @author hhildebrand
 *
 */
public class StateDefinition<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>> {

    private static final String                          GET     = "get";
    private static final String                          SET     = "set";
    private final List<Facet>                            facets  = new ArrayList<Facet>();
    private final Class<Phantasm<RuleForm>>              stateInterface;
    private final UUID                                   workspace;
    protected final Map<Method, StateFunction<RuleForm>> methods = new HashMap<>();

    public StateDefinition(Class<Phantasm<RuleForm>> stateInterface) {
        this.stateInterface = stateInterface;
        State state = stateInterface.getAnnotation(State.class);
        workspace = Workspace.uuidOf(state.workspace());
        construct();
    }

    /**
     * @param model
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Aspect<RuleForm>> getAspects(Model model) {
        WorkspaceScope scope = model.getWorkspaceModel().getScoped(workspace);
        List<Aspect<RuleForm>> specs = new ArrayList<>();
        for (Facet facet : facets) {
            specs.add(new Aspect<RuleForm>(
                                           (Relationship) scope.lookup(facet.classification()),
                                           (RuleForm) scope.lookup(facet.classifier())));
        }
        return specs;
    }

    public Class<Phantasm<RuleForm>> getStateInterface() {
        return stateInterface;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object wrap(RuleForm ruleform, Model model) {
        constrain(model, ruleform);
        return Proxy.newProxyInstance(stateInterface.getClassLoader(),
                                      new Class[] { stateInterface },
                                      new StateImpl(
                                                    ruleform,
                                                    model,
                                                    methods,
                                                    model.getWorkspaceModel().getScoped(model.getEntityManager().find(Product.class,
                                                                                                                      workspace))));

    }

    @SuppressWarnings("unchecked")
    /**
     * Constrain the ruleform to have the required facets.  
     * @param model
     * @param ruleform
     * @throws ClassCastException - if the ruleform is not classified as required by the facets of this state definition
     */
    private void constrain(Model model, RuleForm ruleform) {
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networked = model.getNetworkedModel(ruleform);
        WorkspaceScope scope = model.getWorkspaceModel().getScoped(workspace);
        List<Facet> failures = new ArrayList<>();
        for (Facet constraint : facets) {
            if (!networked.isAccessible((RuleForm) scope.lookup(constraint.classifier()),
                                        (Relationship) scope.lookup(constraint.classification()),
                                        ruleform)) {
                failures.add(constraint);
            }
        }
        if (failures.isEmpty()) {
            throw new ClassCastException(
                                         String.format("%s does not have required facets %s of state %s",
                                                       ruleform, failures,
                                                       stateInterface));
        }
    }

    /**
     * Construct the map of methods to functions that implement the state
     * defition behavior
     */
    private void construct() {
        State state = stateInterface.getAnnotation(State.class);
        for (Facet facet : state.facets()) {
            facets.add(facet);
        }
        for (Method method : stateInterface.getDeclaredMethods()) {
            if (!method.isDefault()) {
                process(method);
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
                    (StateImpl<RuleForm> state, Object[] arguments) -> state.getChild(value.namespace(),
                                                                                      value.name(),
                                                                                      (Class<Phantasm<? extends RuleForm>>) method.getReturnType()));
    }

    @SuppressWarnings("unchecked")
    private Class<RuleForm> getRuleformClass() {
        return (Class<RuleForm>) Model.getExistentialRuleform(stateInterface);
    }

    private void process(Edge annotation, Method method) {
        if (method.getName().startsWith("add")) {
            processAdd(annotation, method);
        } else if (method.getName().startsWith("remove")) {
            processRemove(annotation, method);
        } else if (method.getParameterTypes().length == 0
                   && List.class.isAssignableFrom(method.getReturnType())) {
            processGetList(annotation, method);
        } else if (method.getParameterTypes().length == 1
                   && List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            processSetList(annotation, method);
        } else {
            processSingular(annotation, method);
        }
    }

    private void process(Key annotation, Method method) {
        if (method.getName().startsWith(GET)) {
            processPrimitiveGetter(annotation.namespace(), annotation.name(),
                                   method);
        } else if (method.getName().startsWith(SET)) {
            processPrimitiveSetter(annotation.namespace(), annotation.name(),
                                   method);
        }
    }

    private void process(Method method) {
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
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.addChildren(annotation.value().namespace(),
                                                                                             annotation.value().name(),
                                                                                             (List<Phantasm<RuleForm>>) arguments[0]));
        } else if (Phantasm.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.addChild(annotation.value().namespace(),
                                                                                          annotation.value().name(),
                                                                                          ((Phantasm<RuleForm>) arguments[0]).getRuleform()));
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
                            (StateImpl<RuleForm> state, Object[] arguments) -> state.getChildren(annotation.value().namespace(),
                                                                                                 annotation.value().name(),
                                                                                                 phantasm));
            } else {
                methods.put(method,
                            (StateImpl<RuleForm> state, Object[] arguments) -> state.getImmediateChildren(annotation.value().namespace(),
                                                                                                          annotation.value().name(),
                                                                                                          phantasm));
            }
        } else {
            processGetAuthorizations(method, returnPhantasm,
                                     annotation.value(), ruleformClass);
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
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getLocationAuths(relationship.namespace(),
                                                                                                  relationship.name(),
                                                                                                  (Class<? extends Phantasm<Location>>) phantasmReturned));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getProductAuths(relationship.namespace(),
                                                                                                 relationship.name(),
                                                                                                 (Class<? extends Phantasm<Product>>) phantasmReturned));
        } else if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getAgencyAuths(relationship.namespace(),
                                                                                                relationship.name(),
                                                                                                (Class<? extends Phantasm<Agency>>) phantasmReturned));
        } else {
            throw new IllegalStateException(
                                            String.format("No such authorization from Product to %s",
                                                          ruleformClass.getSimpleName()));
        }
    }

    @SuppressWarnings("unchecked")
    private void processGetSingularAuthorization(Method method,
                                                 Class<? extends Phantasm<?>> phantasmReturned,
                                                 Key value,
                                                 Class<ExistentialRuleform<?, ?>> ruleformClass) {
        if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getSingularLocationAuth(value.namespace(),
                                                                                                         value.name(),
                                                                                                         (Class<? extends Phantasm<Location>>) phantasmReturned));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getSingularProductAuth(value.namespace(),
                                                                                                        value.name(),
                                                                                                        (Class<? extends Phantasm<Product>>) phantasmReturned));
        } else if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getSingularAgencyAuth(value.namespace(),
                                                                                                       value.name(),
                                                                                                       (Class<? extends Phantasm<Agency>>) phantasmReturned));
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
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getAttributeArray(namespace,
                                                                                                   name,
                                                                                                   method.getReturnType().getComponentType()));
        } else if (Map.class.isAssignableFrom(method.getReturnType())) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getAttributeMap(namespace,
                                                                                                 name,
                                                                                                 method.getReturnType()));
        } else if (List.class.isAssignableFrom(method.getReturnType())) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> Arrays.asList(state.getAttributeArray(namespace,
                                                                                                                 name,
                                                                                                                 method.getReturnType().getComponentType())));
        } else {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getAttributeValue(namespace,
                                                                                                   name));
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
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setAttributeArray(namespace,
                                                                                                   name,
                                                                                                   (Object[]) arguments[0]));
        } else if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setAttributeArray(namespace,
                                                                                                   name,
                                                                                                   ((List<?>) arguments[0]).toArray()));
        } else if (Map.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setAttributeMap(namespace,
                                                                                                 name,
                                                                                                 (Map<String, Object>) arguments[0]));
        } else {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setAttributeValue(namespace,
                                                                                                   name,
                                                                                                   arguments[0]));
        }
    }

    /**
     * @param annotation
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processRemove(Edge annotation, Method method) {
        if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.removeChildren(annotation.value().namespace(),
                                                                                                annotation.value().name(),
                                                                                                (List<Phantasm<RuleForm>>) arguments[0]));
        } else if (Phantasm.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.removeChild(annotation.value().namespace(),
                                                                                             annotation.value().name(),
                                                                                             ((Phantasm<RuleForm>) arguments[0]).getRuleform()));
        }
    }

    /**
     * @param annotation
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processSetList(Edge annotation, Method method) {
        methods.put(method,
                    (StateImpl<RuleForm> state, Object[] arguments) -> state.setImmediateChildren(annotation.value().namespace(),
                                                                                                  annotation.value().name(),
                                                                                                  (List<RuleForm>) arguments[0]));
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
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setImmediateChild(value.namespace(),
                                                                                                   value.name(),
                                                                                                   (Phantasm<RuleForm>) arguments[0]));
        } else {
            processSetSingularAuthorization(method, value, ruleformClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void processSetSingularAuthorization(Method method, Key value,
                                                 Class<?> ruleformClass) {
        if (ruleformClass.equals(Location.class)) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setSingularLocationAuth(value.namespace(),
                                                                                                         value.name(),
                                                                                                         (Phantasm<Location>) arguments[0]));
        } else if (ruleformClass.equals(Product.class)) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setSingularProductAuth(value.namespace(),
                                                                                                        value.name(),
                                                                                                        (Phantasm<Product>) arguments[0]));
        } else if (ruleformClass.equals(Agency.class)) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setSingularAgencyAuth(value.namespace(),
                                                                                                       value.name(),
                                                                                                       (Phantasm<Agency>) arguments[0]));
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
                            (StateImpl<RuleForm> state, Object[] arguments) -> state.getImmediateChild(value.namespace(),
                                                                                                       value.name(),
                                                                                                       (Class<Phantasm<? extends RuleForm>>) method.getReturnType()));
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
