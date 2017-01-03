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

package com.chiralbehaviors.CoRE.phantasm.java;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Edge;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.EdgeState;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Inferred;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.PrimitiveState;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria;

/**
 * @author hhildebrand
 *
 */
public class PhantasmDefinition extends Phantasmagoria {

    private final static Map<Class<Phantasm>, PhantasmDefinition> CACHE = new HashMap<>();
    private static final String                                   GET   = "get";

    private static final String                                   SET   = "set";

    public static Aspect facetFrom(Class<? extends Phantasm> phantasm,
                                   Model model) {
        Facet facet = phantasm.getAnnotation(Facet.class);
        if (facet == null) {
            throw new IllegalStateException(String.format("Not a facet: %s",
                                                          phantasm));
        }
        UUID uuid = WorkspaceAccessor.uuidOf(facet.workspace());
        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(uuid);
        if (scope == null) {
            throw new IllegalStateException(String.format("Workspace defining product: %s not found",
                                                          uuid));
        }
        Relationship classifier = (Relationship) scope.lookup(facet.classifier());
        if (classifier == null) {
            throw new IllegalStateException(String.format("%s not found in workspace %s | %s",
                                                          facet.classifier(),
                                                          uuid,
                                                          facet.workspace()));
        }
        ExistentialRuleform classification = (ExistentialRuleform) scope.lookup(facet.classification());
        if (classification == null) {
            throw new IllegalStateException(String.format("%s not found in workspace %s | %s",
                                                          facet.classification(),
                                                          uuid,
                                                          facet.workspace()));
        }
        return new Aspect(model.create(), model.getPhantasmModel()
                                               .getFacetDeclaration(classifier,
                                                                    classification));
    }

    public static String factString(Model model, FacetRecord aspect) {
        return String.format("%s:%s", model.records()
                                           .existentialName(aspect.getClassifier()),
                             model.records()
                                  .existentialName(aspect.getClassification()));
    }

    public static PhantasmDefinition getCached(Class<? extends Phantasm> returnPhantasm) {
        return CACHE.get(returnPhantasm);
    }

    protected final Map<Method, StateFunction> methods = new HashMap<>();
    private final Facet                        facetAnnotation;
    private final Class<? extends Phantasm>    phantasm;
    private final UUID                         workspace;

    public PhantasmDefinition(Class<? extends Phantasm> phantasm, Model model) {
        super(facetFrom(phantasm, model));
        traverse(model);
        this.phantasm = phantasm;
        facetAnnotation = phantasm.getAnnotation(Facet.class);
        workspace = WorkspaceAccessor.uuidOf(facetAnnotation.workspace());
        construct();
    }

    /**
     * Constrain the ruleform to have the required facets.
     *
     * @param model
     * @param ruleform
     * @throws ClassCastException
     *             - if the ruleform is not classified as required by the facets
     *             of this state definition
     */
    public void constrain(Model model, ExistentialRuleform ruleform) {
        if (ruleform == null) {
            throw new IllegalStateException("Ruleform cannot be null");
        }
        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(workspace);
        if (scope == null) {
            throw new IllegalStateException(String.format("Cannot obtain workspace for state interface %s",
                                                          phantasm));
        }
        if (!model.getPhantasmModel()
                  .isAccessible(ruleform.getId(), facet.getClassifier()
                                                       .getId(),
                                facet.getClassification()
                                     .getId())) {
            throw new ClassCastException(String.format("%s does not have required facet %s of state %s",
                                                       ruleform.getName(),
                                                       factString(model,
                                                                  facet.getFacet()),
                                                       phantasm));
        }
    }

    public Phantasm construct(ExistentialRuleform ruleform, Model model,
                              Agency updatedBy) {
        model.getPhantasmModel()
             .initialize(ruleform, facet.getFacet());
        return wrap(ruleform, model);
    }

    public Map<Method, StateFunction> getMethods() {
        return methods;
    }

    public Class<? extends Phantasm> getPhantasm() {
        return phantasm;
    }

    public UUID getWorkspace() {
        return workspace;
    }

    public Phantasm wrap(ExistentialRuleform ruleform, Model model) {
        constrain(model, ruleform);
        PhantasmTwo doppelgänger = new PhantasmTwo(ruleform, this, model);
        Phantasm proxy = (Phantasm) Proxy.newProxyInstance(phantasm.getClassLoader(),
                                                           new Class[] { phantasm },
                                                           doppelgänger);
        return proxy;

    }

    private void construct() {
        for (Method method : this.phantasm.getDeclaredMethods()) {
            if (!method.isDefault()) {
                process(method);
            }
        }
        for (Class<?> iFace : this.phantasm.getInterfaces()) {
            for (Method method : iFace.getDeclaredMethods()) {
                process(method);
            }
        }
    }

    private void getInferred(Class<Phantasm> phantasm, Method method,
                             String fieldName,
                             Class<ExistentialRuleform> rulformClass) {
        methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                             Object[] arguments) -> {
            NetworkAuthorization auth = singularAuthorizations.get(fieldName);
            if (auth == null) {
                throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                              fieldName,
                                                              phantasm.getSimpleName()));
            }
            return state.getChildren(facet, state.getRuleform(), auth)
                        .stream()
                        .map(r -> state.wrap(phantasm, r));
        });
    }

    private void process(Edge edge, Method method) {
        if (method.getName()
                  .startsWith("add")) {
            processAdd(edge, method);
        } else if (method.getName()
                         .startsWith("remove")) {
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

    private void process(EdgeState annotation, Method method) {
        if (method.getName()
                  .startsWith(GET)) {
            processPrimitiveGetter(annotation, method);
        } else if (method.getName()
                         .startsWith(SET)) {
            processPrimitiveSetter(annotation, method);
        } else {
            throw new IllegalStateException(String.format("The method is neither a primitive setter/getter: %s",
                                                          method.toGenericString()));
        }
    }

    private void process(Method method) {
        if (method.getName()
                  .equals("getScope")
            && method.getDeclaringClass()
                     .equals(ScopedPhantasm.class)) {
            methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                                 Object[] arguments) -> state.getScope(this));
            return;
        }
        if (method.getAnnotation(Edge.class) != null) {
            process(method.getAnnotation(Edge.class), method);
        } else if (method.getAnnotation(EdgeState.class) != null) {
            process(method.getAnnotation(EdgeState.class), method);
        } else if (method.getAnnotation(PrimitiveState.class) != null) {
            process(method.getAnnotation(PrimitiveState.class), method);
        }
    }

    private void process(PrimitiveState annotation, Method method) {
        if (method.getName()
                  .startsWith(GET)) {
            processPrimitiveGetter(annotation, method);
        } else if (method.getName()
                         .startsWith(SET)) {
            processPrimitiveSetter(annotation, method);
        } else {
            throw new IllegalStateException(String.format("The method is neither a primitive setter/getter: %s",
                                                          method.toGenericString()));
        }
    }

    @SuppressWarnings("unchecked")
    private void processAdd(Edge edge, Method method) {
        if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                                 Object[] arguments) -> {
                NetworkAuthorization auth = childAuthorizations.get(edge.fieldName());
                if (auth == null) {
                    throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                                  edge.fieldName(),
                                                                  phantasm.getSimpleName()));
                }
                List<ExistentialRuleform> children = new ArrayList<>();
                ((List<Phantasm>) arguments[0]).forEach(e -> children.add(e.getRuleform()));

                return state.addChildren(facet, state.getRuleform(), auth,
                                         children);
            });

        } else if (Phantasm.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                                 Object[] arguments) -> {
                NetworkAuthorization auth = childAuthorizations.get(edge.fieldName());
                if (auth == null) {
                    throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                                  edge.fieldName(),
                                                                  phantasm.getSimpleName()));
                }
                return state.addChild(facet, state.getRuleform(), auth,
                                      ((Phantasm) arguments[0]).getRuleform());
            });
        }
    }

    /**
     * @param edge
     * @param method
     * @return
     */
    @SuppressWarnings("unchecked")
    private void processGetList(Edge edge, Method method) {
        Class<? extends Phantasm> returnPhantasm = (Class<Phantasm>) edge.wrappedChildType();
        if (method.getAnnotation(Inferred.class) != null) {
            methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                                 Object[] arguments) -> {
                NetworkAuthorization auth = childAuthorizations.get(edge.fieldName());
                if (auth == null) {
                    throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                                  edge.fieldName(),
                                                                  phantasm.getSimpleName()));
                }
                return state.getChildren(facet, state.getRuleform(), auth)
                            .stream()
                            .map(ruleform -> state.wrap(returnPhantasm,
                                                        ruleform))
                            .collect(Collectors.toList());
            });
        } else {
            methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                                 Object[] arguments) -> {
                NetworkAuthorization auth = childAuthorizations.get(edge.fieldName());
                if (auth == null) {
                    throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                                  edge.fieldName(),
                                                                  phantasm.getSimpleName()));
                }
                return state.getImmediateChildren(facet, state.getRuleform(),
                                                  auth)
                            .stream()
                            .map(ruleform -> state.wrap(returnPhantasm,
                                                        ruleform))
                            .collect(Collectors.toList());
            });
        }
    }

    private void processPrimitiveGetter(EdgeState annotation, Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalStateException(String.format("getter method has > 1 argument %s",
                                                          method.toGenericString()));
        }
        methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                             Object[] arguments) -> {
            NetworkAttributeAuthorization auth = edgeAttributes.get(annotation.fieldName());
            if (auth == null) {
                throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                              annotation.fieldName(),
                                                              phantasm.getSimpleName()));
            }
            return state.getAttributeValue(facet, state.getRuleform(), auth,
                                           ((Phantasm) arguments[0]).getRuleform());
        });
    }

    private void processPrimitiveGetter(PrimitiveState annotation,
                                        Method method) {
        if (method.getParameterCount() != 0) {
            throw new IllegalStateException(String.format("getter method has arguments %s",
                                                          method.toGenericString()));
        }
        methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                             Object[] arguments) -> {
            AttributeAuthorization auth = attributes.get(annotation.fieldName());
            if (auth == null) {
                throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                              annotation.fieldName(),
                                                              phantasm.getSimpleName()));
            }
            return state.getAttributeValue(facet, state.getRuleform(), auth);
        });
    }

    @SuppressWarnings("unchecked")
    private void processPrimitiveSetter(EdgeState annotation, Method method) {
        if (method.getParameterCount() != 2) {
            throw new IllegalStateException(String.format("getter method does not have 2 arguments %s",
                                                          method.toGenericString()));
        }
        methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                             Object[] arguments) -> {
            NetworkAttributeAuthorization auth = edgeAttributes.get(annotation.fieldName());
            if (auth == null) {
                throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                              annotation.fieldName(),
                                                              phantasm.getSimpleName()));
            }
            if (arguments[1] instanceof List) {
                return state.setAttributeValue(facet, state.getRuleform(), auth,
                                               ((Phantasm) arguments[0]).getRuleform(),
                                               (List<Object>) arguments[1]);
            } else if (arguments[1] instanceof Object[]) {
                return state.setAttributeValue(facet, state.getRuleform(), auth,
                                               ((Phantasm) arguments[0]).getRuleform(),
                                               (Object[]) arguments[1]);
            } else if (arguments[1] instanceof Map) {
                return state.setAttributeValue(facet, state.getRuleform(), auth,
                                               ((Phantasm) arguments[0]).getRuleform(),
                                               (Map<String, Object>) arguments[1]);
            }
            return state.setAttributeObjectValue(facet, state.getRuleform(),
                                                 auth,
                                                 ((Phantasm) arguments[0]).getRuleform(),
                                                 arguments[1]);
        });
    }

    @SuppressWarnings("unchecked")
    private void processPrimitiveSetter(PrimitiveState annotation,
                                        Method method) {
        methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                             Object[] arguments) -> {
            AttributeAuthorization auth = attributes.get(annotation.fieldName());
            if (auth == null) {
                throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                              annotation.fieldName(),
                                                              phantasm.getSimpleName()));
            }
            if (arguments[0] instanceof List) {
                return state.setAttributeValue(facet, state.getRuleform(), auth,
                                               arguments[0]);
            } else if (arguments[0] instanceof Object[]) {
                return state.setAttributeValue(facet, state.getRuleform(), auth,
                                               (Object[]) arguments[0]);
            } else if (arguments[0] instanceof Map) {
                return state.setAttributeValue(facet, state.getRuleform(), auth,
                                               (Map<String, Object>) arguments[0]);
            }
            return state.setAttributeValue(facet, state.getRuleform(), auth,
                                           arguments[0]);
        });
    }

    /**
     * @param edge
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processRemove(Edge edge, Method method) {
        if (List.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                                 Object[] arguments) -> {
                List<ExistentialRuleform> children = new ArrayList<>();
                ((List<Phantasm>) arguments[0]).forEach(e -> children.add(e.getRuleform()));
                return state.removeChildren(facet,

                                            state.getRuleform(),
                                            childAuthorizations.get(edge.fieldName()),
                                            children);
            });

        } else if (Phantasm.class.isAssignableFrom(method.getParameterTypes()[0])) {
            methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                                 Object[] arguments) -> {
                NetworkAuthorization auth = childAuthorizations.get(edge.fieldName());
                if (auth == null) {
                    throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                                  edge.fieldName(),
                                                                  phantasm.getSimpleName()));
                }
                return state.removeChild(facet, state.getRuleform(), auth,
                                         ((Phantasm) arguments[0]).getRuleform());
            });
        }
    }

    /**
     * @param edge
     * @param method
     */
    @SuppressWarnings("unchecked")
    private void processSetList(Edge edge, Method method) {
        methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                             Object[] arguments) -> {
            NetworkAuthorization auth = childAuthorizations.get(edge.fieldName());
            if (auth == null) {
                throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                              edge.fieldName(),
                                                              phantasm.getSimpleName()));
            }
            List<ExistentialRuleform> children = new ArrayList<>();
            ((List<Phantasm>) arguments[0]).forEach(e -> children.add(e.getRuleform()));
            return state.setChildren(facet, state.getRuleform(), auth,
                                     children);
        });
    }

    private void processSetSingular(Method method, Edge edge) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException(String.format("Not a valid Relationship setter: %s",
                                                             method));
        }
        methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                             Object[] arguments) -> {
            NetworkAuthorization auth = singularAuthorizations.get(edge.fieldName());
            if (auth == null) {
                throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                              edge.fieldName(),
                                                              phantasm.getSimpleName()));
            }
            return state.setSingularChild(facet, state.getRuleform(), auth,
                                          ((Phantasm) arguments[0]).getRuleform());
        });
    }

    @SuppressWarnings("unchecked")
    private void processSingular(Edge edge, Method method) {
        if (method.getReturnType()
                  .equals(Void.TYPE)) {
            processSetSingular(method, edge);
            return;
        }

        Class<Phantasm> phantasmReturned = (Class<Phantasm>) edge.wrappedChildType();
        Class<ExistentialRuleform> ruleformClass = (Class<ExistentialRuleform>) phantasmReturned.getAnnotation(Facet.class)
                                                                                                .ruleformClass();
        if (method.getAnnotation(Inferred.class) != null) {
            getInferred(phantasmReturned, method, edge.fieldName(),
                        ruleformClass);
        } else {
            methods.put(method, (PhantasmTwo state, WorkspaceScope scope,
                                 Object[] arguments) -> {
                NetworkAuthorization auth = singularAuthorizations.get(edge.fieldName());
                if (auth == null) {
                    throw new IllegalStateException(String.format("field %s does not exist on %s",
                                                                  edge.fieldName(),
                                                                  phantasm.getSimpleName()));
                }
                return state.wrap(phantasmReturned,
                                  state.getSingularChild(facet,
                                                         state.getRuleform(),
                                                         auth));
            });
        }
    }
}
