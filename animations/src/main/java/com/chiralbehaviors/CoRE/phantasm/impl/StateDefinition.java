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
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.annotations.Aspect;
import com.chiralbehaviors.annotations.Attribute;
import com.chiralbehaviors.annotations.Key;
import com.chiralbehaviors.annotations.Relationship;
import com.chiralbehaviors.annotations.State;

/**
 * @author hhildebrand
 *
 */
public class StateDefinition<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>> {

    private static final String                          GET     = "get";
    private static final String                          SET     = "set";
    private final List<Aspect>                           aspects = new ArrayList<Aspect>();
    private final Class<Phantasm<RuleForm>>              stateInterface;
    private final UUID                                   workspace;
    protected final Map<Method, StateFunction<RuleForm>> methods = new HashMap<>();

    public StateDefinition(Class<Phantasm<RuleForm>> stateInterface) {
        this.stateInterface = stateInterface;
        State state = stateInterface.getAnnotation(State.class);
        workspace = Workspace.uuidOf(state.workspace());
        construct();
    }

    @SuppressWarnings("unchecked")
    public void constrain(Model model, RuleForm ruleform) {
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networked = model.getNetworkedModel(ruleform);
        WorkspaceScope scope = model.getWorkspaceModel().getScoped(workspace);
        List<Aspect> failures = new ArrayList<>();
        for (Aspect constraint : aspects) {
            if (!networked.isAccessible((RuleForm) scope.lookup(constraint.classifier()),
                                        (com.chiralbehaviors.CoRE.relationship.Relationship) scope.lookup(constraint.classification()),
                                        ruleform)) {
                failures.add(constraint);
            }
        }
        if (failures.isEmpty()) {
            throw new RuntimeException(
                                       String.format("%s does not have required aspects: ",
                                                     failures));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object construct(RuleForm ruleform, Model model) {
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

    /**
     * @param model
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<com.chiralbehaviors.CoRE.network.Aspect<RuleForm>> getAspects(Model model) {
        WorkspaceScope scope = model.getWorkspaceModel().getScoped(workspace);
        List<com.chiralbehaviors.CoRE.network.Aspect<RuleForm>> specs = new ArrayList<>();
        for (Aspect aspect : aspects) {
            specs.add(new com.chiralbehaviors.CoRE.network.Aspect<RuleForm>(
                                                                            (com.chiralbehaviors.CoRE.relationship.Relationship) scope.lookup(aspect.classification()),
                                                                            (RuleForm) scope.lookup(aspect.classifier())));
        }
        return specs;
    }

    public Class<Phantasm<RuleForm>> getStateInterface() {
        return stateInterface;
    }

    private void construct() {
        State state = stateInterface.getAnnotation(State.class);
        for (Aspect aspect : state.facets()) {
            aspects.add(aspect);
        }
        process(stateInterface);
        for (Class<?> iFace : stateInterface.getInterfaces()) {
            process(iFace);
        }
    }

    private void process(Attribute annotation, Method method) {
        if (method.getName().startsWith(GET)) {
            processGetter(annotation, method);
        } else if (method.getName().startsWith(SET)) {
            processSetter(annotation, method);
        }
    }

    private void process(Class<?> iFace) {
        for (Method method : iFace.getDeclaredMethods()) {
            if (!method.isDefault()) {
                process(method);
            }
        }
    }

    private void process(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(Phantasm.class)
            || declaringClass.equals(ScopedPhantasm.class)) {
            return;
        }
        if (method.getAnnotation(Relationship.class) != null) {
            process(method.getAnnotation(Relationship.class), method);
        } else if (method.getAnnotation(Attribute.class) != null) {
            process(method.getAnnotation(Attribute.class), method);
        } else {
            processUnknown(method);
        }
    }

    private void process(Relationship annotation, Method method) {
        if (List.class.isAssignableFrom(method.getReturnType())) {
            processGetList(annotation, method);
        } else {
            processSingular(annotation, method);
        }
    }

    /**
     * @param annotation
     * @param method
     * @return
     */
    private void processGetList(Relationship annotation, Method method) {
    }

    private void processGetter(Attribute attribute, Method method) {
        processGetter(attribute.value().namespace(), attribute.value().name(),
                      method);
    }

    private void processGetter(Method method) {
        processGetter(null,
                      method.getName().substring(GET.length(),
                                                 method.getName().length()),
                      method);
    }

    private void processGetter(String namespace, String name, Method method) {
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

    private void processSetter(Attribute attribute, Method method) {
        processSetter(attribute.value().namespace(), attribute.value().name(),
                      method);
    }

    private void processSetter(Method method) {
        processSetter(null,
                      method.getName().substring(SET.length(),
                                                 method.getName().length()),
                      method);
    }

    private void processSetter(String namespace, String name, Method method) {
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
        } else {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setAttributeValue(namespace,
                                                                                                   name,
                                                                                                   arguments[0]));
        }
    }

    @SuppressWarnings("unchecked")
    private void processSingular(Relationship annotation, Method method) {
        Key value = annotation.value();
        if (method.getReturnType().equals(Void.TYPE)) {
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException(
                                                   String.format("Not a valid Relationship setter: %s",
                                                                 method));
            }
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setImmediateChild(value.namespace(),
                                                                                                   value.name(),
                                                                                                   arguments[0]));
        } else {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getImmediateChild(value.namespace(),
                                                                                                   value.name(),
                                                                                                   (Class<Phantasm<? extends RuleForm>>) method.getReturnType()));
        }
    }

    private void processUnknown(Method method) {
        if (method.getName().startsWith(GET)) {
            processGetter(method);
        } else if (method.getName().startsWith(SET)) {
            processSetter(method);
        }
    }
}
