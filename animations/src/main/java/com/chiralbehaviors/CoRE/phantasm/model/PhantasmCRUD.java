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

import static com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.resolveFrom;
import static com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.resolveTo;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * CRUD for Phantasms. This class is the animation procedure that maintains and
 * mediates the Phantasm/Facet constructs in Ultrastructure. It's a bit
 * unwieldy, because of the type signatures required for erasure. Provides a
 * centralized implementation of Phantasm CRUD and the security model for such.
 * 
 * @author hhildebrand
 *
 */
public class PhantasmCRUD<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> {
    private final Model model;

    public PhantasmCRUD(Model model) {
        this.model = model;
    }

    /**
     * Add the child to the list of children of the instance
     * 
     * @param instance
     * @param auth
     * @param child
     */
    public RuleForm addChild(RuleForm instance,
                             NetworkAuthorization<RuleForm> auth,
                             RuleForm child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        checkUPDATE(auth, networkedModel);

        networkedModel.link(instance, auth.getChildRelationship(), child,
                            model.getCurrentPrincipal()
                                 .getPrincipal());
        return instance;
    }

    public RuleForm addChild(RuleForm instance,
                             XDomainNetworkAuthorization<?, ?> auth,
                             @SuppressWarnings("rawtypes") ExistentialRuleform child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkUPDATE(auth, networkedModel);
        NetworkAuthorization<?> childAuth = auth.isForward() ? resolveTo(auth,
                                                                         model)
                                                             : resolveFrom(auth,
                                                                           model);
        if (childAuth.getClassification() instanceof Agency) {
            networkedModel.authorize(instance, auth.getConnection(),
                                     (Agency) child);
        } else if (childAuth.getClassification() instanceof Location) {
            networkedModel.authorize(instance, auth.getConnection(),
                                     (Location) child);
        } else if (childAuth.getClassification() instanceof Product) {
            networkedModel.authorize(instance, auth.getConnection(),
                                     (Product) child);
        } else if (childAuth.getClassification() instanceof Relationship) {
            networkedModel.authorize(instance, auth.getConnection(),
                                     (Relationship) child);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuth.getClassification()
                                                                      .getClass()
                                                                      .getSimpleName()));
        }
        return instance;
    }

    /**
     * Add the list of children to the instance
     * 
     * @param instance
     * @param auth
     * @param children
     */
    public RuleForm addChildren(RuleForm instance,
                                NetworkAuthorization<RuleForm> auth,
                                List<RuleForm> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        checkUPDATE(auth, networkedModel);
        for (RuleForm child : children) {
            checkREAD(child, networkedModel);
            networkedModel.link(instance, auth.getAuthorizedRelationship(),
                                child, model.getCurrentPrincipal()
                                            .getPrincipal());
        }
        return instance;
    }

    public RuleForm addChildren(RuleForm instance,
                                XDomainNetworkAuthorization<?, ?> auth,
                                @SuppressWarnings("rawtypes") List<ExistentialRuleform> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkUPDATE(auth, networkedModel);
        NetworkAuthorization<?> childAuth = auth.isForward() ? resolveTo(auth,
                                                                         model)
                                                             : resolveFrom(auth,
                                                                           model);
        for (@SuppressWarnings("rawtypes")
        ExistentialRuleform child : children) {
            if (childAuth.getClassification() instanceof Agency) {
                networkedModel.authorize(instance, auth.getConnection(),
                                         (Agency) child);
            } else if (childAuth.getClassification() instanceof Location) {
                networkedModel.authorize(instance, auth.getConnection(),
                                         (Location) child);
            } else if (childAuth.getClassification() instanceof Product) {
                networkedModel.authorize(instance, auth.getConnection(),
                                         (Product) child);
            } else if (childAuth.getClassification() instanceof Relationship) {
                networkedModel.authorize(instance, auth.getConnection(),
                                         (Relationship) child);
            } else {
                throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                                 instance.getClass()
                                                                         .getSimpleName(),
                                                                 childAuth.getClassification()
                                                                          .getClass()
                                                                          .getSimpleName()));
            }
        }
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
    public RuleForm apply(NetworkAuthorization<RuleForm> facet,
                          RuleForm instance) throws SecurityException {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        checkAPPLY(facet, networkedModel);
        networkedModel.initialize(instance, facet, model.getCurrentPrincipal()
                                                        .getPrincipal());
        return instance;
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
    @SuppressWarnings("unchecked")
    public RuleForm createInstance(NetworkAuthorization<RuleForm> facet,
                                   String name,
                                   String description) throws SecurityException {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        checkFacetCREATE(facet, networkedModel);
        RuleForm instance;
        try {
            instance = (RuleForm) facet.getClassification()
                                       .getClass()
                                       .getConstructor(String.class,
                                                       String.class,
                                                       Agency.class)
                                       .newInstance(name, description,
                                                    model.getCurrentPrincipal()
                                                         .getPrincipal());
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException
                | NoSuchMethodException e) {
            throw new IllegalStateException(String.format("Cannot construct instance of existential ruleform for %s",
                                                          new Aspect<RuleForm>(facet.getClassifier(),
                                                                               facet.getClassification())),
                                            e);
        }
        networkedModel.initialize(instance, facet, model.getCurrentPrincipal()
                                                        .getPrincipal());
        return instance;
    }

    /**
     * Answer the attribute value of the instance
     * 
     * @param instance
     * @param stateAuth
     * @return
     */
    public Object getAttributeValue(RuleForm instance,
                                    AttributeAuthorization<RuleForm, Network> stateAuth) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(stateAuth.getNetworkAuthorization()
                                                                                            .getClassification());
        checkREAD(stateAuth, networkedModel);
        Attribute authorizedAttribute = stateAuth.getAuthorizedAttribute();
        if (authorizedAttribute.getIndexed()) {
            return getIndexedAttributeValue(instance, authorizedAttribute,
                                            networkedModel);
        } else if (authorizedAttribute.getKeyed()) {
            return getMappedAttributeValue(instance, authorizedAttribute,
                                           networkedModel);
        }
        Object value = networkedModel.getAttributeValue(instance,
                                                        authorizedAttribute)
                                     .getValue();
        if (value instanceof BigDecimal) {
            value = ((BigDecimal) value).floatValue();
        }
        return value;
    }

    /**
     * Answer the inferred and immediate network children of the instance
     * 
     * @param instance
     * @param auth
     * @return
     */
    public List<RuleForm> getChildren(RuleForm instance,
                                      NetworkAuthorization<RuleForm> auth) {
        if (instance == null) {
            return Collections.emptyList();
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        checkREAD(auth, networkedModel);
        return networkedModel.getChildren(instance, auth.getChildRelationship())
                             .stream()
                             .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                                  .getPrincipal(),
                                                                             child,
                                                                             model.getKernel()
                                                                                  .getREAD()))
                             .collect(Collectors.toList());

    }

    /***
     * Answer the xd children of the instance
     * 
     * @param instance
     * @param auth
     * @return
     */
    public List<?> getChildren(RuleForm instance,
                               XDomainNetworkAuthorization<?, ?> auth) {
        if (instance == null) {
            return Collections.emptyList();
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkREAD(auth, networkedModel);
        NetworkAuthorization<?> childAuth = auth.isForward() ? resolveTo(auth,
                                                                         model)
                                                             : resolveFrom(auth,
                                                                           model);
        @SuppressWarnings("rawtypes")
        List<? extends ExistentialRuleform> result;
        if (childAuth.getClassification() instanceof Agency) {
            result = networkedModel.getAuthorizedAgencies(instance,
                                                          auth.getConnection());
        } else if (childAuth.getClassification() instanceof Location) {
            result = networkedModel.getAuthorizedLocations(instance,
                                                           auth.getConnection());
        } else if (childAuth.getClassification() instanceof Product) {
            result = networkedModel.getAuthorizedProducts(instance,
                                                          auth.getConnection());
        } else if (childAuth.getClassification() instanceof Relationship) {
            result = networkedModel.getAuthorizedRelationships(instance,
                                                               auth.getConnection());
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuth.getClassification()
                                                                      .getClass()
                                                                      .getSimpleName()));
        }
        NetworkedModel<?, ?, ?, ?> childNetworkModel = model.getUnknownNetworkedModel(childAuth.getClassification());
        return result.stream()
                     .filter(child -> childNetworkModel.checkCapability(model.getCurrentPrincipal()
                                                                             .getPrincipal(),
                                                                        child,
                                                                        model.getKernel()
                                                                             .getREAD()))
                     .collect(Collectors.toList());
    }

    /**
     * Answer the immediate, non inferred children of the instance
     * 
     * @param instance
     * @param auth
     * @return
     */
    public List<RuleForm> getImmediateChildren(RuleForm instance,
                                               NetworkAuthorization<RuleForm> auth) {
        if (instance == null) {
            return Collections.emptyList();
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        checkREAD(auth, networkedModel);
        return networkedModel.getImmediateChildren(instance,
                                                   auth.getChildRelationship())
                             .stream()
                             .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                                  .getPrincipal(),
                                                                             child,
                                                                             model.getKernel()
                                                                                  .getREAD()))
                             .collect(Collectors.toList());
    }

    /**
     * Answer the list of instances of this facet.
     * 
     * @param facet
     * @return
     */
    public List<RuleForm> getInstances(NetworkAuthorization<RuleForm> facet) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        checkFacetREAD(facet, networkedModel);
        return networkedModel.getChildren(facet.getClassification(),
                                          facet.getClassifier()
                                               .getInverse())
                             .stream()
                             .filter(instance -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                                     .getPrincipal(),
                                                                                instance,
                                                                                model.getKernel()
                                                                                     .getREAD()))
                             .collect(Collectors.toList());
    }

    /**
     * Answer the singular network child of the instance
     * 
     * @param instance
     * @param auth
     * @return
     */
    public RuleForm getSingularChild(RuleForm instance,
                                     NetworkAuthorization<RuleForm> auth) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        checkREAD(auth, networkedModel);
        RuleForm child = networkedModel.getImmediateChild(instance,
                                                          auth.getChildRelationship());
        checkREAD(child, networkedModel);
        return child;
    }

    /**
     * Answer the singular xd child of the instance
     * 
     * @param instance
     * @param auth
     * @return
     */
    public Object getSingularChild(RuleForm instance,
                                   XDomainNetworkAuthorization<?, ?> auth) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkREAD(auth, networkedModel);
        NetworkAuthorization<?> childAuth = auth.isForward() ? resolveTo(auth,
                                                                         model)
                                                             : resolveFrom(auth,
                                                                           model);
        @SuppressWarnings("rawtypes")
        ExistentialRuleform child;
        if (childAuth.getClassification() instanceof Agency) {
            child = networkedModel.getAuthorizedAgency(instance,
                                                       auth.getConnection());
        } else if (childAuth.getClassification() instanceof Location) {
            child = networkedModel.getAuthorizedLocation(instance,
                                                         auth.getConnection());
        } else if (childAuth.getClassification() instanceof Product) {
            child = networkedModel.getAuthorizedProduct(instance,
                                                        auth.getConnection());
        } else if (childAuth.getClassification() instanceof Relationship) {
            child = networkedModel.getAuthorizedRelationship(instance,
                                                             auth.getConnection());
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuth.getClassification()
                                                                      .getClass()
                                                                      .getSimpleName()));
        }
        checkREAD(child,
                  model.getUnknownNetworkedModel(childAuth.getClassification()));
        return child;
    }

    @SuppressWarnings("rawtypes")
    public List<ExistentialRuleform> lookup(NetworkAuthorization auth,
                                            List<String> ids) {
        NetworkedModel<?, ?, ?, ?> networkedModel = model.getUnknownNetworkedModel(auth.getClassification());
        return ids.stream()
                  .map(id -> networkedModel.find(UUID.fromString(id)))
                  .filter(rf -> rf != null)
                  .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                       .getPrincipal(),
                                                                  child,
                                                                  model.getKernel()
                                                                       .getREAD()))
                  .collect(Collectors.toList());
    }

    public List<RuleForm> lookupRuleForm(NetworkAuthorization<RuleForm> auth,
                                         List<String> ids) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        return ids.stream()
                  .map(id -> networkedModel.find(UUID.fromString(id)))
                  .filter(rf -> rf != null)
                  .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                       .getPrincipal(),
                                                                  child,
                                                                  model.getKernel()
                                                                       .getREAD()))
                  .collect(Collectors.toList());
    }

    @SuppressWarnings("rawtypes")
    public ExistentialRuleform lookup(NetworkAuthorization auth, String id) {
        NetworkedModel<?, ?, ?, ?> networkedModel = model.getUnknownNetworkedModel(auth.getClassification());
        return Optional.of(networkedModel.find(UUID.fromString(id)))
                       .filter(rf -> rf != null)
                       .filter(child -> networkedModel.checkCapability(model.getCurrentPrincipal()
                                                                            .getPrincipal(),
                                                                       child,
                                                                       model.getKernel()
                                                                            .getREAD()))
                       .get();
    }

    /**
     * Remove the facet from the instance
     * 
     * @param facet
     * @param instance
     * @return
     * @throws SecurityException
     */
    public RuleForm remove(NetworkAuthorization<RuleForm> facet,
                           RuleForm instance,
                           boolean deleteAttributes) throws SecurityException {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        checkREMOVE(facet, networkedModel);
        networkedModel.initialize(instance, facet, model.getCurrentPrincipal()
                                                        .getPrincipal());
        return instance;
    }

    /**
     * Remove a child from the instance
     * 
     * @param instance
     * @param auth
     * @param child
     */
    public RuleForm removeChild(RuleForm instance,
                                NetworkAuthorization<RuleForm> auth,
                                RuleForm child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        checkUPDATE(auth, networkedModel);
        NetworkRuleform<RuleForm> link = networkedModel.getImmediateLink(instance,
                                                                         auth.getChildRelationship(),
                                                                         child.getRuleform());
        if (link != null) {
            model.getEntityManager()
                 .remove(link);
        }
        return instance;
    }

    public RuleForm removeChild(RuleForm instance,
                                XDomainNetworkAuthorization<?, ?> auth,
                                @SuppressWarnings("rawtypes") ExistentialRuleform child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkUPDATE(auth, networkedModel);
        NetworkAuthorization<?> childAuth = auth.isForward() ? resolveTo(auth,
                                                                         model)
                                                             : resolveFrom(auth,
                                                                           model);
        if (childAuth.getClassification() instanceof Agency) {
            networkedModel.deauthorize(instance, auth.getConnection(),
                                       (Agency) child);
        } else if (childAuth.getClassification() instanceof Location) {
            networkedModel.deauthorize(instance, auth.getConnection(),
                                       (Location) child);
        } else if (childAuth.getClassification() instanceof Product) {
            networkedModel.deauthorize(instance, auth.getConnection(),
                                       (Product) child);
        } else if (childAuth.getClassification() instanceof Relationship) {
            networkedModel.deauthorize(instance, auth.getConnection(),
                                       (Relationship) child);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuth.getClassification()
                                                                      .getClass()
                                                                      .getSimpleName()));
        }
        return instance;
    }

    /**
     * Remove the immediate child links from the instance
     * 
     * @param instance
     * @param auth
     * @param children
     */
    public RuleForm removeChildren(RuleForm instance,
                                   NetworkAuthorization<RuleForm> auth,
                                   List<RuleForm> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        checkUPDATE(auth, networkedModel);
        for (RuleForm child : children) {
            NetworkRuleform<RuleForm> link = networkedModel.getImmediateLink(instance,
                                                                             auth.getChildRelationship(),
                                                                             child.getRuleform());
            if (link != null) {
                model.getEntityManager()
                     .remove(link);
            }
        }
        return instance;
    }

    public RuleForm removeChildren(RuleForm instance,
                                   XDomainNetworkAuthorization<?, ?> auth,
                                   @SuppressWarnings("rawtypes") List<ExistentialRuleform> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkUPDATE(auth, networkedModel);
        NetworkAuthorization<?> childAuth = auth.isForward() ? resolveTo(auth,
                                                                         model)
                                                             : resolveFrom(auth,
                                                                           model);
        for (ExistentialRuleform<?, ?> child : children) {
            if (childAuth.getClassification() instanceof Agency) {
                networkedModel.deauthorize(instance, auth.getConnection(),
                                           (Agency) child);
            } else if (childAuth.getClassification() instanceof Location) {
                networkedModel.deauthorize(instance, auth.getConnection(),
                                           (Location) child);
            } else if (childAuth.getClassification() instanceof Product) {
                networkedModel.deauthorize(instance, auth.getConnection(),
                                           (Product) child);
            } else if (childAuth.getClassification() instanceof Relationship) {
                networkedModel.deauthorize(instance, auth.getConnection(),
                                           (Relationship) child);
            } else {
                throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                                 instance.getClass()
                                                                         .getSimpleName(),
                                                                 childAuth.getClassification()
                                                                          .getClass()
                                                                          .getSimpleName()));
            }
        }
        return instance;
    }

    public RuleForm setAttributeValue(RuleForm instance,
                                      AttributeAuthorization<RuleForm, Network> stateAuth,
                                      List<Object> value) {
        return instance;
    }

    public RuleForm setAttributeValue(RuleForm instance,
                                      AttributeAuthorization<RuleForm, Network> stateAuth,
                                      Map<String, Object> value) {
        return instance;
    }

    public RuleForm setAttributeValue(RuleForm instance,
                                      AttributeAuthorization<RuleForm, Network> stateAuth,
                                      Object value) {
        return instance;
    }

    /**
     * Set the immediate children of the instance to be the list of supplied
     * children. No inferred links will be explicitly added or deleted.
     * 
     * @param instance
     * @param auth
     * @param children
     */
    public RuleForm setChildren(RuleForm instance,
                                NetworkAuthorization<RuleForm> auth,
                                List<RuleForm> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        checkUPDATE(auth, networkedModel);

        for (NetworkRuleform<RuleForm> childLink : networkedModel.getImmediateChildrenLinks(instance,
                                                                                            auth.getChildRelationship())) {
            model.getEntityManager()
                 .remove(childLink);
        }
        for (RuleForm child : children) {
            checkREAD(child, networkedModel);
            networkedModel.link(instance, auth.getChildRelationship(), child,
                                model.getCurrentPrincipal()
                                     .getPrincipal());
        }
        return instance;
    }

    /**
     * Set the xd children of the instance.
     * 
     * @param instance
     * @param auth
     * @param children
     */
    @SuppressWarnings({ "unchecked" })
    public RuleForm setChildren(RuleForm instance,
                                XDomainNetworkAuthorization<?, ?> auth,
                                List<?> children) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkUPDATE(auth, networkedModel);
        checkREAD(instance, networkedModel);
        NetworkAuthorization<?> childAuth = auth.isForward() ? resolveTo(auth,
                                                                         model)
                                                             : resolveFrom(auth,
                                                                           model);
        if (childAuth.getClassification() instanceof Agency) {
            networkedModel.setAuthorizedAgencies(instance, auth.getConnection(),
                                                 (List<Agency>) children);
        } else if (childAuth.getClassification() instanceof Location) {
            networkedModel.setAuthorizedLocations(instance,
                                                  auth.getConnection(),
                                                  (List<Location>) children);
        } else if (childAuth.getClassification() instanceof Product) {
            networkedModel.setAuthorizedProducts(instance, auth.getConnection(),
                                                 (List<Product>) children);
        } else if (childAuth.getClassification() instanceof Relationship) {
            networkedModel.setAuthorizedRelationships(instance,
                                                      auth.getConnection(),
                                                      (List<Relationship>) children);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuth.getClassification()
                                                                      .getClass()
                                                                      .getSimpleName()));
        }
        return instance;
    }

    /**
     * @param description
     * @param id
     * @return
     */
    public RuleForm setDescription(RuleForm instance, String description) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkUPDATE(instance, networkedModel);
        instance.setDescription(description);
        return instance;
    }

    /**
     * @param name
     * @param id
     * @return
     */
    public RuleForm setName(RuleForm instance, String name) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkUPDATE(instance, networkedModel);
        instance.setName(name);
        return instance;
    }

    /**
     * Set the singular child of the instance.
     * 
     * @param instance
     * @param auth
     * @param child
     */
    public RuleForm setSingularChild(RuleForm instance,
                                     NetworkAuthorization<RuleForm> auth,
                                     RuleForm child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        checkUPDATE(auth, networkedModel);
        checkREAD(child, networkedModel);
        networkedModel.setImmediateChild(instance,
                                         auth.getAuthorizedRelationship(),
                                         child, model.getCurrentPrincipal()
                                                     .getPrincipal());
        return instance;
    }

    /**
     * Set the singular xd child of the instance
     * 
     * @param instance
     * @param auth
     * @param child
     * @return
     */
    public RuleForm setSingularChild(RuleForm instance,
                                     XDomainNetworkAuthorization<?, ?> auth,
                                     @SuppressWarnings("rawtypes") ExistentialRuleform child) {
        if (instance == null) {
            return null;
        }
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(instance);
        checkUPDATE(auth, networkedModel);
        checkREAD(child, model.getUnknownNetworkedModel(child));
        NetworkAuthorization<?> childAuth = auth.isForward() ? resolveTo(auth,
                                                                         model)
                                                             : resolveFrom(auth,
                                                                           model);
        if (childAuth.getClassification() instanceof Agency) {
            networkedModel.authorizeSingular(instance, auth.getConnection(),
                                             (Agency) child);
        } else if (childAuth.getClassification() instanceof Location) {
            networkedModel.authorizeSingular(instance, auth.getConnection(),
                                             (Location) child);
        } else if (childAuth.getClassification() instanceof Product) {
            networkedModel.authorizeSingular(instance, auth.getConnection(),
                                             (Product) child);
        } else if (childAuth.getClassification() instanceof Relationship) {
            networkedModel.authorizeSingular(instance, auth.getConnection(),
                                             (Relationship) child);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             instance.getClass()
                                                                     .getSimpleName(),
                                                             childAuth.getClassification()
                                                                      .getClass()
                                                                      .getSimpleName()));
        }
        return instance;
    }

    private void checkAPPLY(NetworkAuthorization<RuleForm> facet,
                            NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, model.getKernel()
                                                             .getAPPLY())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getAPPLY()));
        }
    }

    private void checkFacetCREATE(NetworkAuthorization<RuleForm> facet,
                                  NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, model.getKernel()
                                                             .getCREATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getCREATE()));
        }
    }

    private void checkFacetREAD(NetworkAuthorization<RuleForm> facet,
                                NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, model.getKernel()
                                                             .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
    }

    private void checkREAD(AttributeAuthorization<RuleForm, Network> stateAuth,
                           NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            stateAuth, model.getKernel()
                                                            .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
    }

    private void checkREAD(@SuppressWarnings("rawtypes") ExistentialRuleform child,
                           NetworkedModel<?, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            child, model.getKernel()
                                                        .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
    }

    private void checkREAD(NetworkAuthorization<RuleForm> auth,
                           NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 auth, model.getKernel()
                                                            .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
    }

    private void checkREAD(XDomainNetworkAuthorization<?, ?> auth,
                           NetworkedModel<?, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
    }

    private void checkREMOVE(NetworkAuthorization<RuleForm> facet,
                             NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, model.getKernel()
                                                             .getREMOVE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREMOVE()));
        }
    }

    private void checkUPDATE(@SuppressWarnings("rawtypes") ExistentialRuleform child,
                             NetworkedModel<?, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            child, model.getKernel()
                                                        .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }
    }

    private void checkUPDATE(NetworkAuthorization<RuleForm> auth,
                             NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }
    }

    private void checkUPDATE(XDomainNetworkAuthorization<?, ?> auth,
                             NetworkedModel<?, ?, ?, ?> networkedModel) {
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }
    }

    private Object[] getIndexedAttributeValue(RuleForm instance,
                                              Attribute authorizedAttribute,
                                              NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {

        AttributeValue<RuleForm>[] attributeValues = getValueArray(instance,
                                                                   authorizedAttribute,
                                                                   networkedModel);

        Object[] values = (Object[]) Array.newInstance(Object.class,
                                                       attributeValues.length);
        for (AttributeValue<RuleForm> value : attributeValues) {
            values[value.getSequenceNumber()] = value.getValue();
        }
        return values;
    }

    private Map<String, Object> getMappedAttributeValue(RuleForm instance,
                                                        Attribute authorizedAttribute,
                                                        NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, AttributeValue<RuleForm>> entry : getValueMap(instance,
                                                                             authorizedAttribute,
                                                                             networkedModel).entrySet()) {
            map.put(entry.getKey(), entry.getValue()
                                         .getValue());
        }
        return map;
    }

    private AttributeValue<RuleForm>[] getValueArray(RuleForm instance,
                                                     Attribute attribute,
                                                     NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        List<? extends AttributeValue<RuleForm>> values = networkedModel.getAttributeValues(instance,
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

    private Map<String, AttributeValue<RuleForm>> getValueMap(RuleForm instance,
                                                              Attribute attribute,
                                                              NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        Map<String, AttributeValue<RuleForm>> map = new HashMap<>();
        for (AttributeValue<RuleForm> value : networkedModel.getAttributeValues(instance,
                                                                                attribute)) {
            map.put(value.getKey(), value);
        }
        return map;
    }

    private AttributeValue<RuleForm> newAttributeValue(RuleForm instance,
                                                       Attribute attribute,
                                                       int i,
                                                       NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        AttributeValue<RuleForm> value = networkedModel.create(instance,
                                                               attribute,
                                                               model.getCurrentPrincipal()
                                                                    .getPrincipal());
        value.setSequenceNumber(i);
        return value;
    }

    @SuppressWarnings("unused")
    private void setAttributeArray(RuleForm instance,
                                   Attribute authorizedAttribute,
                                   Object[] values,
                                   NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        AttributeValue<RuleForm>[] old = getValueArray(instance,
                                                       authorizedAttribute,
                                                       networkedModel);
        if (values == null) {
            if (old != null) {
                for (AttributeValue<RuleForm> value : old) {
                    model.getEntityManager()
                         .remove(value);
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
                model.getEntityManager()
                     .remove(old[i]);
            }
        }
    }

    @SuppressWarnings("unused")
    private void setAttributeMap(RuleForm instance,
                                 Attribute authorizedAttribute,
                                 Map<String, Object> values,
                                 NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        Map<String, AttributeValue<RuleForm>> valueMap = getValueMap(instance,
                                                                     authorizedAttribute,
                                                                     networkedModel);
        values.keySet()
              .stream()
              .filter(keyName -> !valueMap.containsKey(keyName))
              .forEach(keyName -> valueMap.remove(keyName));
        int maxSeq = 0;
        for (AttributeValue<RuleForm> value : valueMap.values()) {
            maxSeq = Math.max(maxSeq, value.getSequenceNumber());
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            AttributeValue<RuleForm> value = valueMap.get(entry.getKey());
            if (value == null) {
                value = newAttributeValue(instance, authorizedAttribute,
                                          ++maxSeq, networkedModel);
                model.getEntityManager()
                     .persist(value);
                value.setKey(entry.getKey());
            }
            value.setValue(entry.getValue());
        }
    }

    private void setValue(RuleForm instance, Attribute attribute, int i,
                          AttributeValue<RuleForm> existing, Object newValue) {
        if (existing == null) {
            existing = newAttributeValue(null, attribute, i, null);
            model.getEntityManager()
                 .persist(existing);
        }
        existing.setValue(newValue);
    }
}
