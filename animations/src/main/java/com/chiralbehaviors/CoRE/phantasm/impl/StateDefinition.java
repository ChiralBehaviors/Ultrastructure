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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;
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
    private final Class<PhantasmBase<RuleForm>>          accessorInterface;
    private final List<Aspect>                           aspects = new ArrayList<Aspect>();
    private final UUID                                   workspace;
    protected final Map<Method, StateFunction<RuleForm>> methods = new HashMap<>();

    public StateDefinition(Class<PhantasmBase<RuleForm>> accessorInterface) {
        this.accessorInterface = accessorInterface;
        State state = accessorInterface.getAnnotation(State.class);
        workspace = UUID.fromString(state.workspace());
        construct();
    }

    @SuppressWarnings("unchecked")
    public void constrain(Model model, RuleForm ruleform) {
        NetworkedModel<RuleForm, NetworkRuleform<RuleForm>, ?, ?> networked = model.getNetworkedModel(ruleform);
        WorkspaceScope scope = model.getWorkspaceModel().getScoped(workspace);
        List<Aspect> failures = new ArrayList<>();
        for (Aspect constraint : aspects) {
            if (!networked.isAccessible((RuleForm) scope.lookup(constraint.classifier()),
                                        (com.chiralbehaviors.CoRE.network.Relationship) scope.lookup(constraint.classification()),
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
        return Proxy.newProxyInstance(accessorInterface.getClassLoader(),
                                      new Class[] { accessorInterface },
                                      new StateImpl(
                                                    ruleform,
                                                    model,
                                                    methods,
                                                    model.getWorkspaceModel().getScoped(model.getEntityManager().find(Product.class,
                                                                                                                      workspace))));

    }

    private void construct() {
        State state = accessorInterface.getAnnotation(State.class);
        for (Aspect aspect : state.facets()) {
            aspects.add(aspect);
        }
        for (Class<?> iFace : accessorInterface.getInterfaces()) {
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
        if (method.getAnnotation(Relationship.class) != null) {
            process(method.getAnnotation(Relationship.class), method);
        } else if (method.getAnnotation(Attribute.class) != null) {
            process(method.getAnnotation(Attribute.class), method);
        } else {
            processUnknown(method);
        }
    }

    private void process(Relationship annotation, Method method) {
        // TODO Auto-generated method stub
    }

    private void processGetter(Attribute attribute, Method method) {
        if (method.getParameterCount() != 0) {
            throw new IllegalStateException(
                                            String.format("getter method has arguments %s",
                                                          method.toGenericString()));
        }
        Key value = attribute.value();
        if (List.class.isAssignableFrom(method.getReturnType())) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getAttributeValues(value.namespace(),
                                                                                                    value.name()));
        } else {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getAttributeValue(value.namespace(),
                                                                                                   value.name()));
        }
    }

    private void processGetter(Method method) {
        if (method.getParameterCount() != 0) {
            throw new IllegalStateException(
                                            String.format("getter method has arguments %s",
                                                          method.toGenericString()));
        }
        String key = method.getName().substring(GET.length(),
                                                method.getName().length());
        if (method.getReturnType().isArray()) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getAttributeValues(null,
                                                                                                    key));
        } else {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.getAttributeValue(null,
                                                                                                   key));
        }
    }

    private void processSetter(Attribute attribute, Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalStateException(
                                            String.format("setter method does not have a singular argument %s",
                                                          method.toGenericString()));
        }
        Key value = attribute.value();
        if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setAttributeValues(value.namespace(),
                                                                                                    value.name(),
                                                                                                    (List<?>) arguments[0]));
        } else {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setAttributeValue(value.namespace(),
                                                                                                   value.name(),
                                                                                                   arguments[0]));
        }
    }

    private void processSetter(Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalStateException(
                                            String.format("setter method does not have a singular argument %s",
                                                          method.toGenericString()));
        }
        String key = method.getName().substring(SET.length(),
                                                method.getName().length());
        if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setAttributeValues(null,
                                                                                                    key,
                                                                                                    (List<?>) arguments[0]));
        } else {
            methods.put(method,
                        (StateImpl<RuleForm> state, Object[] arguments) -> state.setAttributeValue(null,
                                                                                                   key,
                                                                                                   arguments[0]));
        }
    }

    private void processUnknown(Method method) {
        if (method.getName().startsWith(GET)) {
            processGetter(method);
        } else if (method.getName().startsWith(SET)) {
            processSetter(method);
        }
    }

    /**
     * @param model
     *            TODO
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<com.chiralbehaviors.CoRE.network.Aspect<RuleForm>> getAspects(Model model) {
        WorkspaceScope scope = model.getWorkspaceModel().getScoped(workspace);
        List<com.chiralbehaviors.CoRE.network.Aspect<RuleForm>> specs = new ArrayList<>();
        for (Aspect aspect : aspects) {
            specs.add(new com.chiralbehaviors.CoRE.network.Aspect<RuleForm>(
                                                                            (com.chiralbehaviors.CoRE.network.Relationship) scope.lookup(aspect.classification()),
                                                                            (RuleForm) scope.lookup(aspect.classifier())));
        }
        return specs;
    }
}
