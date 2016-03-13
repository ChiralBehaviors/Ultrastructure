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

package com.chiralbehaviors.CoRE.phantasm.jsonld;

import static com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext.getIri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AttributeModelImpl;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria;
import com.chiralbehaviors.CoRE.phantasm.resources.FacetResource;
import com.chiralbehaviors.CoRE.phantasm.resources.RuleformResource;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;

/**
 * @author hhildebrand
 *
 */
public class FacetContext<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        extends Phantasmagoria<RuleForm, Network> {

    private static final ConcurrentMap<Aspect<?>, FacetContext<?, ?>> cachedContexts = new ConcurrentHashMap<>();

    public static URI getAllInstancesIri(NetworkAuthorization<?> facet,
                                         UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getAllInstances",
                                                  AuthorizedPrincipal.class,
                                                  String.class, UUID.class,
                                                  UUID.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to get all instances method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           Ruleform.initializeAndUnproxy(facet.getClassification())
                                   .getClass()
                                   .getSimpleName());
        ub.resolveTemplate("classifier", facet.getClassifier()
                                              .getId()
                                              .toString());
        ub.resolveTemplate("classification", facet.getClassification()
                                                  .getId()
                                                  .toString());
        return ub.build();
    }

    public static URI getContextIri(NetworkAuthorization<?> facet,
                                    UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getContext",
                                                  AuthorizedPrincipal.class,
                                                  String.class, UUID.class,
                                                  UUID.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("error getting getFacetContext method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           Ruleform.initializeAndUnproxy(facet.getClassification())
                                   .getClass()
                                   .getSimpleName());
        ub.resolveTemplate("classifier", facet.getClassifier()
                                              .getId()
                                              .toString());
        ub.resolveTemplate("classification", facet.getClassification()
                                                  .getId()
                                                  .toString());
        return ub.build();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static FacetContext getFacet(Aspect aspect, Model model,
                                        UriInfo uriInfo) {
        return cachedContexts.computeIfAbsent(aspect,
                                              a -> new FacetContext(model.getNetworkedModel(aspect.getClassification())
                                                                         .getFacetDeclaration(a),
                                                                    model,
                                                                    uriInfo));
    }

    public static String getFacetIri(NetworkAuthorization<?> facet) {
        return String.format("%s/%s/%s",
                             Ruleform.initializeAndUnproxy(facet.getClassification())
                                     .getClass()
                                     .getSimpleName(),
                             facet.getClassifier()
                                  .getId()
                                  .toString(),
                             facet.getClassification()
                                  .getId()
                                  .toString());
    }

    public static URI getFacetsIri(Class<?> ruleform, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getFacets",
                                                  AuthorizedPrincipal.class,
                                                  String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to getFacets", e);
        }
        ub.resolveTemplate("ruleform-type", ruleform.getSimpleName());
        return ub.build();
    }

    public static URI getFullFacetIri(NetworkAuthorization<?> facet,
                                      UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getFacet",
                                                  AuthorizedPrincipal.class,
                                                  String.class, UUID.class,
                                                  UUID.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot retrieve getFacet method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           Ruleform.initializeAndUnproxy(facet.getClassification())
                                   .getClass()
                                   .getSimpleName());
        ub.resolveTemplate("classifier", facet.getClassifier()
                                              .getId()
                                              .toString());
        ub.resolveTemplate("classification", facet.getClassification()
                                                  .getId()
                                                  .toString());
        return ub.build();
    }

    /**
     * @param aspect
     * @param term
     * @param uriInfo
     * @return
     */
    public static URI getFullTermIri(Aspect<?> aspect, String term,
                                     UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getTerm",
                                                  AuthorizedPrincipal.class,
                                                  String.class, UUID.class,
                                                  UUID.class, String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot retrieve getTerm method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           Ruleform.initializeAndUnproxy(aspect.getClassification())
                                   .getClass()
                                   .getSimpleName());
        ub.resolveTemplate("classifier", aspect.getClassifier()
                                               .getId()
                                               .toString());
        ub.resolveTemplate("classification", aspect.getClassification()
                                                   .getId()
                                                   .toString());
        ub.resolveTemplate("term", term);
        return ub.build();
    }

    public static URI getInstanceIri(Aspect<?> aspect,
                                     ExistentialRuleform<?, ?> child,
                                     UriInfo uriInfo) {
        return getInstanceIri(aspect, child.getId(), uriInfo);
    }

    public static URI getInstanceIri(Aspect<?> aspect, String instance,
                                     UriInfo uriInfo, List<String> selection) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getInstance",
                                                  AuthorizedPrincipal.class,
                                                  String.class, UUID.class,
                                                  UUID.class, UUID.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot retrieve getInstance method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           Ruleform.initializeAndUnproxy(aspect.getClassification())
                                   .getClass()
                                   .getSimpleName());
        ub.resolveTemplate("classifier", aspect.getClassifier()
                                               .getId()
                                               .toString());
        ub.resolveTemplate("classification", aspect.getClassification()
                                                   .getId()
                                                   .toString());
        ub.resolveTemplate("instance", instance);
        if (selection != null && !selection.isEmpty()) {
            String[] elements = new String[selection.size()];
            int i = 0;
            for (String element : selection) {
                try {
                    elements[i++] = URLEncoder.encode(element, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException(e);
                }
            }
            ub.queryParam("select", (Object[]) elements);
        }
        return ub.build();
    }

    public static URI getInstanceIri(Aspect<?> aspect, UUID instance,
                                     UriInfo uriInfo) {
        return getInstanceIri(aspect, instance.toString(), uriInfo, null);
    }

    public static String getTermIri(String term) {
        return String.format("%s:term/%s", Constants.FACET, term);
    }

    public static String getTypeName(Aspect<?> aspect) {
        return String.format("%s:%s", aspect.getClassifier()
                                            .getName(),
                             aspect.getClassification()
                                   .getName());
    }

    private final Map<String, NetworkAuthorization<?>> children = new HashMap<>();
    private final Map<String, Object>                  context;
    private final String                               contextContext;
    private final String                               facetIri;
    private final Map<String, Typed>                   terms    = new HashMap<>();
    private final String                               typeName;

    /**
     * @param facet
     * @param traverser
     */
    public FacetContext(NetworkAuthorization<RuleForm> facet, Model model,
                        UriInfo uriInfo) {
        super(facet);
        traverse(facet, new PhantasmTraversal<>(model));
        collectRuleformAttributeTerms();
        typeName = String.format("%s:%s", facet.getClassifier()
                                               .getName(),
                                 facet.getClassification()
                                      .getName());
        contextContext = getContextIri(facet, uriInfo).toASCIIString();
        context = buildContext(uriInfo);
        facetIri = getFacetIri(facet);
    }

    public NetworkAuthorization<?> getChild(String term) {
        return children.get(term);
    }

    public String getId(RuleForm ruleform) {
        return String.format("%s:%s", Constants.FACET, ruleform.getId());
    }

    public Map<String, Object> getMicro(RuleForm instance) {
        Map<String, Object> shorty = getShort();
        shorty.remove(Constants.TYPE);
        shorty.remove(Constants.CONTEXT);
        shorty.put(Constants.ID, getId(instance));
        return shorty;
    }

    public Map<String, Object> getPropertyReference(String property) {
        return terms.get(property)
                    .toMap();
    }

    public Typed getRuleformTerm(String term) {
        if ("name".equals(term) || "description".equals(term)
            || "notes".equals(term) || "updatedBy".equals(term)) {
            return terms.get(term);
        }
        return null;
    }

    public Map<String, Object> getShort() {
        Map<String, Object> shorty = new TreeMap<>();
        shorty.put(Constants.CONTEXT, contextContext);
        shorty.put(Constants.TYPE, getTypeId());
        shorty.put(Constants.TYPENAME, getTypeName());
        return shorty;
    }

    public Map<String, Object> getShort(RuleForm instance) {
        Map<String, Object> shorty = getShort();
        shorty.put(Constants.ID, getId(instance));
        return shorty;
    }

    public String getTypeId() {
        return Constants.FACET;
    }

    public String getTypeName() {
        return typeName;
    }

    public Map<String, Object> toCompactInstance(RuleForm instance, Model model,
                                                 UriInfo uriInfo) {
        Map<String, Object> node = new TreeMap<>();
        node.put(Constants.ID, getId(instance));
        PhantasmCRUD<RuleForm, Network> crud = new PhantasmCRUD<>(model);
        addRuleformAttributes(instance, node, uriInfo);
        addAttributes(instance, crud, node, uriInfo);
        addNetworkAuths(instance, crud, node, uriInfo);
        addXdAuths(instance, crud, node, uriInfo);
        return node;
    }

    public Map<String, Object> toContext() {
        return context;
    }

    public Map<String, Object> toContext(UriInfo uriInfo) {
        Map<String, Object> object = new TreeMap<>();
        Map<String, Object> context = new TreeMap<>();
        object.put(Constants.CONTEXT, context);
        context.put(Constants.VOCAB, uriInfo.getBaseUriBuilder()
                                            .path(FacetResource.class)
                                            .build()
                                            .toASCIIString()
                                     + "/");
        context.put(Constants.FACET, facetIri);
        context.put(Constants.RULEFORM,
                    RuleformResource.getRuleformIri(uriInfo));
        for (Map.Entry<String, Typed> term : terms.entrySet()) {
            context.put(term.getKey(), term.getValue()
                                           .toMap());
        }
        return object;
    }

    @Override
    public void visit(NetworkAuthorization<RuleForm> facet,
                      AttributeAuthorization<RuleForm, Network> auth,
                      String fieldName) {
        super.visit(facet, auth, fieldName);
        terms.put(fieldName, new Typed(getTermIri(auth.getAuthorizedAttribute()
                                                      .getName()),
                                       getIri(auth.getAuthorizedAttribute())));
    }

    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> facet,
                              NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child,
                              String singularFieldName) {
        super.visitChildren(facet, auth, fieldName, child, singularFieldName);
        terms.put(fieldName, new Typed(getTermIri(fieldName), Constants.ID));
        children.put(fieldName, Ruleform.initializeAndUnproxy(child));
    }

    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> facet,
                              XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child,
                              String singularFieldName) {
        super.visitChildren(facet, auth, fieldName, child, singularFieldName);
        terms.put(fieldName, new Typed(getTermIri(fieldName), Constants.ID));
        children.put(fieldName, Ruleform.initializeAndUnproxy(child));
    }

    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> facet,
                              NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child) {
        super.visitSingular(facet, auth, fieldName, child);
        terms.put(fieldName, new Typed(getTermIri(fieldName), Constants.ID));
        children.put(fieldName, Ruleform.initializeAndUnproxy(child));
    }

    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> facet,
                              XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child) {
        super.visitSingular(facet, auth, fieldName, child);
        terms.put(fieldName, new Typed(getTermIri(fieldName), Constants.ID));
        children.put(fieldName, Ruleform.initializeAndUnproxy(child));
    }

    /**
     * @param instance
     * @param crud
     * @param node
     * @param uriInfo
     */
    private void addAttributes(RuleForm instance,
                               PhantasmCRUD<RuleForm, Network> crud,
                               Map<String, Object> node, UriInfo uriInfo) {
        for (Map.Entry<String, AttributeAuthorization<RuleForm, Network>> entry : attributes.entrySet()) {
            node.put(entry.getKey(),
                     crud.getAttributeValue(facet, instance, entry.getValue()));
        }
    }

    /**
     * @param instance
     * @param crud
     * @param node
     * @param uriInfo
     */
    private void addNetworkAuths(RuleForm instance,
                                 PhantasmCRUD<RuleForm, Network> crud,
                                 Map<String, Object> node, UriInfo uriInfo) {
        for (Map.Entry<String, NetworkAuthorization<RuleForm>> entry : childAuthorizations.entrySet()) {
            switch (entry.getValue()
                         .getCardinality()) {
                case N:
                    node.put(entry.getKey(),
                             crud.getChildren(facet, instance, entry.getValue())
                                 .stream()
                                 .map(inst -> getReference(facet, inst,
                                                           crud.getModel(),
                                                           uriInfo)));
                    break;
                case ONE:
                    node.put(entry.getKey(),
                             getReference(facet,
                                          crud.getSingularChild(facet, instance,
                                                                entry.getValue()),
                                          crud.getModel(), uriInfo));
                default:
                    break;
            }
        }
    }

    private void addRuleformAttributes(RuleForm instance,
                                       Map<String, Object> node,
                                       UriInfo uriInfo) {
        if (instance.getName() != null) {
            node.put("name", instance.getName());
        }
        if (instance.getDescription() != null) {
            node.put("description", instance.getDescription());
        }
        if (instance.getNotes() != null) {
            node.put("notes", instance.getNotes());
        }
        Agency updatedBy = instance.getUpdatedBy();
        node.put("updated-by",
                 new RuleformContext(Agency.class, uriInfo).getShort(updatedBy,
                                                                     uriInfo));
    }

    /**
     * @param instance
     * @param crud
     * @param node
     * @param uriInfo
     */
    private void addXdAuths(RuleForm instance,
                            PhantasmCRUD<RuleForm, Network> crud,
                            Map<String, Object> node, UriInfo uriInfo) {
        for (Entry<String, XDomainNetworkAuthorization<?, ?>> entry : xdChildAuthorizations.entrySet()) {
            switch (entry.getValue()
                         .getCardinality()) {
                case N:
                    node.put(entry.getKey(),
                             crud.getChildren(facet, instance, entry.getValue())
                                 .stream()
                                 .map(inst -> getReference(facet, inst,
                                                           crud.getModel(),
                                                           uriInfo)));
                    break;
                case ONE:
                    node.put(entry.getKey(),
                             getReference(facet,
                                          crud.getSingularChild(facet, instance,
                                                                entry.getValue()),
                                          crud.getModel(), uriInfo));
                default:
                    break;
            }
        }

    }

    private Map<String, Object> buildContext(UriInfo uriInfo) {
        Map<String, Object> object = new TreeMap<>();
        Map<String, Object> context = new TreeMap<>();
        object.put(Constants.CONTEXT, context);
        context.put(Constants.VOCAB, uriInfo.getBaseUriBuilder()
                                            .path(FacetResource.class)
                                            .build()
                                            .toASCIIString()
                                     + "/");
        context.put(Constants.FACET, facetIri);
        context.put(Constants.RULEFORM,
                    RuleformResource.getRuleformIri(uriInfo));
        for (Map.Entry<String, Typed> term : terms.entrySet()) {
            context.put(term.getKey(), term.getValue()
                                           .toMap());
        }
        return object;
    }

    private void collectRuleformAttributeTerms() {
        String textType;
        try {
            textType = new URI(AttributeModelImpl.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_TEXT).toASCIIString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("Cannot create URI: %s",
                                                          AttributeModelImpl.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_TEXT));
        }
        terms.put("name", new Typed(getTermIri("name"), textType));
        terms.put("description",
                  new Typed(getTermIri("description"), textType));
        terms.put("notes", new Typed(getTermIri("notes"), textType));
        terms.put("updatedBy",
                  new Typed(getTermIri("updatedBy"),
                            String.format("%s:%s", Constants.RULEFORM,
                                          Agency.class.getSimpleName())));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map<String, String> getReference(NetworkAuthorization<?> facet,
                                             ExistentialRuleform child,
                                             Model model, UriInfo uriInfo) {
        if (child == null) {
            return null;
        }
        return getFacet(new Aspect(facet.getClassifier(),
                                   facet.getClassification()),
                        model, uriInfo).getShort(child);
    }

}
