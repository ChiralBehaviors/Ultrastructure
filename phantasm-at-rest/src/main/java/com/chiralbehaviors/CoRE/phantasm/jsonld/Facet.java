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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProductAuthorization;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.models.AttributeModelImpl;
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.FacetResource;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductRelationshipAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.utils.English;

/**
 * @author hhildebrand
 *
 */
public class Facet<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        extends Aspect<RuleForm> {
    public static String getAllInstancesIri(@SuppressWarnings("rawtypes") Aspect aspect,
                                            UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getAllInstances",
                                                  String.class, UUID.class,
                                                  UUID.class));
            ub.resolveTemplate("ruleform-type",
                               aspect.getClassification().getClass().getSimpleName());
            ub.resolveTemplate("classifier",
                               aspect.getClassifier().getId().toString());
            ub.resolveTemplate("classification",
                               aspect.getClassification().getId().toString());
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to get all instances method",
                                            e);
        }
        return ub.build().toASCIIString();
    }

    public static String getContextIri(Aspect<?> aspect, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getContext", String.class,
                                                  UUID.class, UUID.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("error getting getFacetContext method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.fragment(String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
        return ub.build().toASCIIString();
    }

    public static String getNodeIri(Aspect<?> aspect,
                                    ExistentialRuleform<?, ?> child,
                                    UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getInstance", String.class,
                                                  UUID.class, UUID.class,
                                                  UUID.class, String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot retrieve getInstance method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.resolveTemplate("instance", child.getId().toString());
        ub.fragment(String.format("%s:%s:%s", child.getName(),
                                  aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
        return ub.build().toASCIIString();
    }

    public static String getNodeIri(Aspect<?> aspect, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getInstance", String.class,
                                                  UUID.class, UUID.class,
                                                  UUID.class, String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot retrieve getInstance method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.resolveTemplate("instance", "");
        return ub.build().toASCIIString();
    }

    public static String getTermIri(Aspect<?> aspect, String term,
                                    UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getTerm", String.class,
                                                  UUID.class, UUID.class,
                                                  String.class, String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("error getting getTerm method", e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.resolveTemplate("term", term);
        return ub.build().toASCIIString();
    }

    public static String getTermIri(Aspect<?> aspect, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getTerm", String.class,
                                                  UUID.class, UUID.class,
                                                  String.class, String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("error getting getTerm method", e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.resolveTemplate("term", "");
        return ub.build().toASCIIString();
    }

    public static String getTypeIri(Aspect<?> aspect, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getType", String.class,
                                                  UUID.class, UUID.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot get getFacetType method",
                                            e);
        }
        ub.resolveTemplate("ruleform-type",
                           aspect.getClassification().getClass().getSimpleName());
        ub.resolveTemplate("classifier",
                           aspect.getClassifier().getId().toString());
        ub.resolveTemplate("classification",
                           aspect.getClassification().getId().toString());
        ub.fragment(String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
        return ub.build().toASCIIString();
    }

    private final Map<String, XDomainNetworkAuthorization<Agency, Location>>      agencyLocationAuths      = new TreeMap<>();
    private final Map<String, XDomainNetworkAuthorization<Agency, Product>>       agencyProductAuths       = new TreeMap<>();
    private final Map<String, Attribute>                                          attributes               = new TreeMap<>();
    private final String                                                          context;
    private final Map<String, NetworkAuthorization<RuleForm>>                     networkAuths             = new TreeMap<>();
    private final Map<String, XDomainNetworkAuthorization<Product, Location>>     productLocationAuths     = new TreeMap<>();
    private final Map<String, XDomainNetworkAuthorization<Product, Relationship>> productRelationshipAuths = new TreeMap<>();
    private final String                                                          termIri;
    private final Map<String, Typed>                                              terms                    = new TreeMap<>();

    private final String type;

    public Facet(Aspect<RuleForm> aspect, Model model, UriInfo uriInfo) {
        super(aspect.getClassifier(), aspect.getClassification());
        context = getContextIri(this, uriInfo);
        type = getTypeIri(this, uriInfo);
        collectTerms(model, uriInfo);
        termIri = getTermIri(this, uriInfo);
    }

    public Attribute getAttribute(String term) {
        Attribute attribute = attributes.get(term);
        return attribute;
    }

    public Map<String, Object> getPropertyReference(String property) {
        return terms.get(property).toMap();
    }

    public Typed getRuleformTerm(String term) {
        if ("name".equals(term) || "description".equals(term)
            || "notes".equals(term) || "updatedBy".equals(term)) {
            return terms.get(term);
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Aspect<?> getTerm(String term) {
        for (Entry<String, NetworkAuthorization<RuleForm>> entry : networkAuths.entrySet()) {
            if (entry.getKey().equals(term)) {
                return new Aspect(entry.getValue().getAuthorizedRelationship(),
                                  entry.getValue().getAuthorizedParent());
            }
        }
        for (Entry<String, XDomainNetworkAuthorization<Agency, Location>> entry : agencyLocationAuths.entrySet()) {
            if (entry.getKey().equals(term)) {
                XDomainNetworkAuthorization<?, ?> auth = entry.getValue();
                if (auth.isForward()) {
                    return new Aspect(auth.getToRelationship(),
                                      auth.getToParent());
                } else {
                    return new Aspect(auth.getFromRelationship(),
                                      auth.getFromParent());
                }
            }
        }
        for (Entry<String, XDomainNetworkAuthorization<Agency, Product>> entry : agencyProductAuths.entrySet()) {
            if (entry.getKey().equals(term)) {
                XDomainNetworkAuthorization<?, ?> auth = entry.getValue();
                if (auth.isForward()) {
                    return new Aspect(auth.getToRelationship(),
                                      auth.getToParent());
                } else {
                    return new Aspect(auth.getFromRelationship(),
                                      auth.getFromParent());
                }
            }
        }
        for (Entry<String, XDomainNetworkAuthorization<Product, Location>> entry : productLocationAuths.entrySet()) {
            if (entry.getKey().equals(term)) {
                XDomainNetworkAuthorization<?, ?> auth = entry.getValue();
                if (auth.isForward()) {
                    return new Aspect(auth.getToRelationship(),
                                      auth.getToParent());
                } else {
                    return new Aspect(auth.getFromRelationship(),
                                      auth.getFromParent());
                }
            }
        }
        for (Entry<String, XDomainNetworkAuthorization<Product, Relationship>> entry : productRelationshipAuths.entrySet()) {
            if (entry.getKey().equals(term)) {
                XDomainNetworkAuthorization<?, ?> auth = entry.getValue();
                if (auth.isForward()) {
                    return new Aspect(auth.getToRelationship(),
                                      auth.getToParent());
                } else {
                    return new Aspect(auth.getFromRelationship(),
                                      auth.getFromParent());
                }
            }
        }
        return null;
    }

    public Map<String, Object> toContext() {
        Map<String, Object> object = new TreeMap<>();
        Map<String, Object> context = new TreeMap<>();
        object.put(Constants.CONTEXT, context);
        context.put(Constants.TERM, termIri);
        for (Map.Entry<String, Typed> term : terms.entrySet()) {
            context.put(term.getKey(), term.getValue().toMap());
        }
        return object;
    }

    public Map<String, Object> toInstance(RuleForm instance, Model model,
                                          UriInfo uriInfo) {
        Map<String, Object> node = new TreeMap<>();
        fillIn(instance, node, model, uriInfo);
        return node;
    }

    private void addAgencyAuths(RuleForm instance, Map<String, Object> node,
                                Model model, UriInfo uriInfo) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        for (Entry<String, XDomainNetworkAuthorization<Agency, Product>> entry : agencyProductAuths.entrySet()) {
            Aspect<Product> targetAspect = new Aspect<>(entry.getValue().getToRelationship(),
                                                        entry.getValue().getToParent());
            if (entry.getValue().getCardinality() == Cardinality.N) {
                List<Map<String, String>> array = new ArrayList<>();
                networkedModel.getAuthorizedProducts(instance,
                                                     entry.getValue().getConnection()).forEach(child -> array.add(getReference(targetAspect,
                                                                                                                               child,
                                                                                                                               uriInfo)));
                node.put(entry.getKey(), array);
            } else if (entry.getValue().getCardinality() == Cardinality.ONE) {
                networkedModel.getAuthorizedProducts(instance,
                                                     entry.getValue().getConnection()).forEach(child -> node.put(entry.getKey(),
                                                                                                                 getReference(targetAspect,
                                                                                                                              child,
                                                                                                                              uriInfo)));
            }
        }
        for (Entry<String, XDomainNetworkAuthorization<Agency, Location>> entry : agencyLocationAuths.entrySet()) {
            Aspect<Location> targetAspect = new Aspect<>(entry.getValue().getToRelationship(),
                                                         entry.getValue().getToParent());
            if (entry.getValue().getCardinality() == Cardinality.N) {
                List<Map<String, String>> array = new ArrayList<>();
                networkedModel.getAuthorizedLocations(instance,
                                                      entry.getValue().getConnection()).forEach(child -> array.add(getReference(targetAspect,
                                                                                                                                child,
                                                                                                                                uriInfo)));
                node.put(entry.getKey(), array);
            } else if (entry.getValue().getCardinality() == Cardinality.ONE) {
                networkedModel.getAuthorizedLocations(instance,
                                                      entry.getValue().getConnection()).forEach(child -> node.put(entry.getKey(),
                                                                                                                  getReference(targetAspect,
                                                                                                                               child,
                                                                                                                               uriInfo)));
            }
        }
    }

    private void addAttributes(RuleForm instance,
                               NetworkedModel<RuleForm, Network, ?, ?> networkedModel,
                               Map<String, Object> node, UriInfo uriInfo) {
        for (Entry<String, Attribute> entry : attributes.entrySet()) {
            @SuppressWarnings("unchecked")
            List<AttributeValue<RuleForm>> values = (List<AttributeValue<RuleForm>>) networkedModel.getAttributeValues(instance,
                                                                                                                       entry.getValue());
            if (values.size() == 1) {
                node.put(entry.getKey(), values.get(0).getValue());
            } else if (!values.isEmpty()) {
                List<String> array = new ArrayList<>();
                node.put(entry.getKey(), array);
                for (AttributeValue<RuleForm> attr : values) {
                    Object value = attr.getValue();
                    array.add(value.toString());
                }
            }
        }
    }

    /**
     * @param node
     */
    private void addLocationAuths(RuleForm instance, Map<String, Object> node,
                                  Model model, UriInfo uriInfo) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        for (Entry<String, XDomainNetworkAuthorization<Agency, Location>> entry : agencyLocationAuths.entrySet()) {
            Aspect<Agency> targetAspect = new Aspect<>(entry.getValue().getFromRelationship(),
                                                       entry.getValue().getFromParent());
            if (entry.getValue().getCardinality() == Cardinality.N) {
                List<Map<String, String>> array = new ArrayList<>();
                networkedModel.getAuthorizedAgencies(instance,
                                                     entry.getValue().getConnection()).forEach(child -> array.add(getReference(targetAspect,
                                                                                                                               child,
                                                                                                                               uriInfo)));
                node.put(entry.getKey(), array);
            } else if (entry.getValue().getCardinality() == Cardinality.ONE) {
                networkedModel.getAuthorizedAgencies(instance,
                                                     entry.getValue().getConnection()).forEach(child -> node.put(entry.getKey(),
                                                                                                                 getReference(targetAspect,
                                                                                                                              child,
                                                                                                                              uriInfo)));
            }
        }
        for (Entry<String, XDomainNetworkAuthorization<Product, Location>> entry : productLocationAuths.entrySet()) {
            Aspect<Product> targetAspect = new Aspect<>(entry.getValue().getFromRelationship(),
                                                        entry.getValue().getFromParent());
            if (entry.getValue().getCardinality() == Cardinality.N) {
                List<Map<String, String>> array = new ArrayList<>();
                networkedModel.getAuthorizedProducts(instance,
                                                     entry.getValue().getConnection()).forEach(child -> array.add(getReference(targetAspect,
                                                                                                                               child,
                                                                                                                               uriInfo)));
                node.put(entry.getKey(), array);
            } else if (entry.getValue().getCardinality() == Cardinality.ONE) {
                networkedModel.getAuthorizedProducts(instance,
                                                     entry.getValue().getConnection()).forEach(child -> node.put(entry.getKey(),
                                                                                                                 getReference(targetAspect,
                                                                                                                              child,
                                                                                                                              uriInfo)));
            }
        }
    }

    /**
     * @param networkedModel
     * @param node
     * @param uriInfo
     */
    private void addNetworkAuths(RuleForm instance,
                                 NetworkedModel<RuleForm, Network, ?, ?> networkedModel,
                                 Map<String, Object> node, UriInfo uriInfo) {
        for (Entry<String, NetworkAuthorization<RuleForm>> entry : networkAuths.entrySet()) {
            NetworkAuthorization<RuleForm> auth = entry.getValue();
            Aspect<RuleForm> aspect = new Aspect<RuleForm>(auth.getAuthorizedRelationship(),
                                                           auth.getAuthorizedParent());
            if (auth.getCardinality() == Cardinality.N) {
                List<Map<String, String>> array = new ArrayList<>();
                networkedModel.getChildren(instance,
                                           auth.getChildRelationship()).forEach(child -> array.add(getReference(aspect,
                                                                                                                child,
                                                                                                                uriInfo)));
                node.put(entry.getKey(), array);

            } else if (auth.getCardinality() == Cardinality.ONE) {
                networkedModel.getImmediateChildren(instance,
                                                    auth.getChildRelationship()).forEach(child -> node.put(auth.getName(),
                                                                                                           getReference(aspect,
                                                                                                                        child,
                                                                                                                        uriInfo)));
            }
        }
    }

    private void addProductAuths(RuleForm instance, Map<String, Object> node,
                                 Model model, UriInfo uriInfo) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        for (Entry<String, XDomainNetworkAuthorization<Agency, Product>> entry : agencyProductAuths.entrySet()) {
            Aspect<Agency> targetAspect = new Aspect<>(entry.getValue().getFromRelationship(),
                                                       entry.getValue().getFromParent());
            if (entry.getValue().getCardinality() == Cardinality.N) {
                List<Map<String, String>> array = new ArrayList<>();
                networkedModel.getAuthorizedAgencies(instance,
                                                     entry.getValue().getConnection()).forEach(child -> array.add(getReference(targetAspect,
                                                                                                                               child,
                                                                                                                               uriInfo)));
                node.put(entry.getKey(), array);
            } else if (entry.getValue().getCardinality() == Cardinality.ONE) {
                networkedModel.getAuthorizedAgencies(instance,
                                                     entry.getValue().getConnection()).forEach(child -> node.put(entry.getKey(),
                                                                                                                 getReference(targetAspect,
                                                                                                                              child,
                                                                                                                              uriInfo)));
            }
        }
        for (Entry<String, XDomainNetworkAuthorization<Product, Location>> entry : productLocationAuths.entrySet()) {
            Aspect<Location> targetAspect = new Aspect<>(entry.getValue().getToRelationship(),
                                                         entry.getValue().getToParent());
            if (entry.getValue().getCardinality() == Cardinality.N) {
                List<Map<String, String>> array = new ArrayList<>();
                networkedModel.getAuthorizedLocations(instance,
                                                      entry.getValue().getConnection()).forEach(child -> array.add(getReference(targetAspect,
                                                                                                                                child,
                                                                                                                                uriInfo)));
                node.put(entry.getKey(), array);
            } else if (entry.getValue().getCardinality() == Cardinality.ONE) {
                networkedModel.getAuthorizedLocations(instance,
                                                      entry.getValue().getConnection()).forEach(child -> node.put(entry.getKey(),
                                                                                                                  getReference(targetAspect,
                                                                                                                               child,
                                                                                                                               uriInfo)));
            }
        }
        for (Entry<String, XDomainNetworkAuthorization<Product, Relationship>> entry : productRelationshipAuths.entrySet()) {
            Aspect<Relationship> targetAspect = new Aspect<>(entry.getValue().getToRelationship(),
                                                             entry.getValue().getToParent());
            if (entry.getValue().getCardinality() == Cardinality.N) {
                List<Map<String, String>> array = new ArrayList<>();
                networkedModel.getAuthorizedRelationships(instance,
                                                          entry.getValue().getConnection()).forEach(child -> array.add(getReference(targetAspect,
                                                                                                                                    child,
                                                                                                                                    uriInfo)));
                node.put(entry.getKey(), array);
            } else if (entry.getValue().getCardinality() == Cardinality.ONE) {
                networkedModel.getAuthorizedRelationships(instance,
                                                          entry.getValue().getConnection()).forEach(child -> node.put(entry.getKey(),
                                                                                                                      getReference(targetAspect,
                                                                                                                                   child,
                                                                                                                                   uriInfo)));
            }
        }
    }

    private void addRelationshipAuths(RuleForm instance,
                                      Map<String, Object> node, Model model,
                                      UriInfo uriInfo) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        for (Entry<String, XDomainNetworkAuthorization<Product, Relationship>> entry : productRelationshipAuths.entrySet()) {
            Aspect<Product> targetAspect = new Aspect<>(entry.getValue().getFromRelationship(),
                                                        entry.getValue().getFromParent());
            if (entry.getValue().getCardinality() == Cardinality.N) {
                List<Map<String, String>> array = new ArrayList<>();
                networkedModel.getAuthorizedProducts(instance,
                                                     entry.getValue().getConnection()).forEach(child -> array.add(getReference(targetAspect,
                                                                                                                               child,
                                                                                                                               uriInfo)));
                node.put(entry.getKey(), array);
            } else if (entry.getValue().getCardinality() == Cardinality.ONE) {
                networkedModel.getAuthorizedProducts(instance,
                                                     entry.getValue().getConnection()).forEach(child -> node.put(entry.getKey(),
                                                                                                                 getReference(targetAspect,
                                                                                                                              child,
                                                                                                                              uriInfo)));
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
        Map<String, String> ref = new TreeMap<>();
        Agency updatedBy = instance.getUpdatedBy();
        ref.put(Constants.ID, getIri(updatedBy, uriInfo));
        ref.put(Constants.TYPE,
                RuleformContext.getTypeIri(updatedBy.getClass(), uriInfo));
        node.put("updated-by", ref);
    }

    private void addXdAuths(RuleForm instance, Model model,
                            Map<String, Object> node, UriInfo uriInfo) {
        if (getClassification() instanceof Agency) {
            addAgencyAuths(instance, node, model, uriInfo);
        } else if (getClassification() instanceof Product) {
            addProductAuths(instance, node, model, uriInfo);
        } else if (getClassification() instanceof Location) {
            addLocationAuths(instance, node, model, uriInfo);
        } else if (getClassification() instanceof Relationship) {
            addRelationshipAuths(instance, node, model, uriInfo);
        }
    }

    private void collectAgencyAuthTerms(Model model, UriInfo uriInfo) {
        AgencyModel agencyModel = model.getAgencyModel();
        @SuppressWarnings("unchecked")
        Aspect<Agency> aspect = (Aspect<Agency>) this;
        for (AgencyLocationAuthorization auth : agencyModel.getAgencyLocationAuths(aspect)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            collectTerm(term, model, uriInfo);
            agencyLocationAuths.put(term, auth);
        }
        for (AgencyProductAuthorization auth : agencyModel.getAgencyProductAuths(aspect)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            collectTerm(term, model, uriInfo);
            agencyProductAuths.put(term, auth);
        }
    }

    private void collectAttributes(Model model, UriInfo uriInfo) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(getClassification());
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(this)) {
            String term = auth.getAuthorizedAttribute().getName();
            attributes.put(term, auth.getAuthorizedAttribute());
            String encodedTerm = encode(term);
            terms.put(encodedTerm,
                      new Typed(encodedTerm,
                                getIri(auth.getAuthorizedAttribute(),
                                       uriInfo)));
        }
    }

    private void collectChildren(Model model, UriInfo uriInfo) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(getClassification());
        collectNetworkAuths(model, networkedModel, uriInfo);
        collectXdAuths(model, networkedModel, uriInfo);
    }

    private void collectLocationAuthTerms(Model model, UriInfo uriInfo) {
        @SuppressWarnings("unchecked")
        Aspect<Location> aspect = (Aspect<Location>) this;
        for (AgencyLocationAuthorization auth : model.getLocationModel().getLocationAgencyAuths(aspect)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            collectTerm(term, model, uriInfo);
            agencyLocationAuths.put(term, auth);
        }
        for (ProductLocationAuthorization auth : model.getLocationModel().getLocationProductAuths(aspect)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            collectTerm(term, model, uriInfo);
            productLocationAuths.put(term, auth);
        }
    }

    private void collectNetworkAuths(Model model,
                                     NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                     UriInfo uriInfo) {
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(this)) {
            String term = auth.getName();
            if (term == null) {
                continue;
            }
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            networkAuths.put(term, auth);
            collectTerm(term, model, uriInfo);
        }
    }

    private void collectProductAuthTerms(Model model, UriInfo uriInfo) {
        @SuppressWarnings("unchecked")
        Aspect<Product> aspect = (Aspect<Product>) this;
        for (AgencyProductAuthorization auth : model.getProductModel().getProductAgencyAuths(aspect)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            collectTerm(term, model, uriInfo);
            agencyProductAuths.put(term, auth);
        }
        for (ProductLocationAuthorization auth : model.getProductModel().getProductLocationAuths(aspect)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            collectTerm(term, model, uriInfo);
            productLocationAuths.put(term, auth);
        }
        for (ProductRelationshipAuthorization auth : model.getProductModel().getProductRelationshipAuths(aspect)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            collectTerm(term, model, uriInfo);
            productRelationshipAuths.put(term, auth);
        }
    }

    private void collectRelationshipAuthTerms(Model model, UriInfo uriInfo) {
        @SuppressWarnings("unchecked")
        Aspect<Relationship> aspect = (Aspect<Relationship>) this;
        for (ProductRelationshipAuthorization auth : model.getRelationshipModel().getRelationshipProductAuths(aspect)) {
            String term = auth.getName();
            if (auth.getCardinality() == Cardinality.N) {
                term = English.plural(term);
            }
            collectTerm(term, model, uriInfo);
            productRelationshipAuths.put(term, auth);
        }
    }

    private void collectRuleformAttributes(Model model, UriInfo uriInfo) {
        String textType;
        try {
            textType = new URI(AttributeModelImpl.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_TEXT).toASCIIString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("Cannot create URI: %s",
                                                          AttributeModelImpl.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_TEXT));
        }
        terms.put("name",
                  new Typed(String.format("%s:%s", Constants.TERM, "name"),
                            textType));
        terms.put("description",
                  new Typed(String.format("%s:%s", Constants.TERM,
                                          "description"),
                            textType));
        terms.put("notes",
                  new Typed(String.format("%s:%s", Constants.TERM, "notes"),
                            textType));
        terms.put("updatedBy",
                  new Typed(String.format("%s:%s", Constants.TERM, "updatedBy"),
                            textType));
    }

    private void collectTerm(String term, Model model, UriInfo uriInfo) {
        if (term == null) {
            return;
        }
        String encodedTerm = encode(term);
        terms.put(encodedTerm,
                  new Typed(String.format("%s:%s", Constants.TERM, encodedTerm),
                            Constants.ID));
    }

    private void collectTerms(Model model, UriInfo uriInfo) {
        collectRuleformAttributes(model, uriInfo);
        collectAttributes(model, uriInfo);
        collectChildren(model, uriInfo);
    }

    private void collectXdAuths(Model model,
                                NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                UriInfo uriInfo) {
        if (getClassification() instanceof Agency) {
            collectAgencyAuthTerms(model, uriInfo);
        } else if (getClassification() instanceof Product) {
            collectProductAuthTerms(model, uriInfo);
        } else if (getClassification() instanceof Location) {
            collectLocationAuthTerms(model, uriInfo);
        } else if (getClassification() instanceof Relationship) {
            collectRelationshipAuthTerms(model, uriInfo);
        }
    }

    private String encode(String term) {
        try {
            return URLEncoder.encode(term, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not known?", e);
        }
    }

    private void fillIn(RuleForm instance, Map<String, Object> node,
                        Model model, UriInfo uriInfo) {
        node.put(Constants.CONTEXT, context);
        node.put(Constants.TYPE, type);
        node.put(Constants.ID, getIri(instance, uriInfo));
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        addRuleformAttributes(instance, node, uriInfo);
        addAttributes(instance, networkedModel, node, uriInfo);
        addNetworkAuths(instance, networkedModel, node, uriInfo);
        addXdAuths(instance, model, node, uriInfo);
    }

    private Map<String, String> getReference(Aspect<?> aspect,
                                             @SuppressWarnings("rawtypes") ExistentialRuleform child,
                                             UriInfo uriInfo) {
        Map<String, String> node = new TreeMap<>();
        node.put(Constants.ID, getNodeIri(aspect, child, uriInfo));
        node.put(Constants.TYPE, getTypeIri(aspect, uriInfo));
        return node;
    }
}
