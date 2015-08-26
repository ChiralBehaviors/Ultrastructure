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

package com.chiralbehaviors.CoRE.phantasm;

import static com.chiralbehaviors.CoRE.phantasm.PhantasmTraversal.resolveFrom;
import static com.chiralbehaviors.CoRE.phantasm.PhantasmTraversal.resolveTo;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class PhantasmCRUD {
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm addChild(RuleForm instance,
                                                                                                                                  NetworkAuthorization<RuleForm> auth,
                                                                                                                                  RuleForm child) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 auth, model.getKernel()
                                                            .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }

        networkedModel.link(instance, auth.getAuthorizedRelationship(), child,
                            model.getCurrentPrincipal()
                                 .getPrincipal());
        return instance;
    }

    /**
     * Add the list of children to the instance
     * 
     * @param instance
     * @param auth
     * @param children
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm addChildren(RuleForm instance,
                                                                                                                                     NetworkAuthorization<RuleForm> auth,
                                                                                                                                     List<RuleForm> children) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 auth, model.getKernel()
                                                            .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }
        for (RuleForm child : children) {
            networkedModel.link(instance, auth.getAuthorizedRelationship(),
                                child, model.getCurrentPrincipal()
                                            .getPrincipal());
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm apply(NetworkAuthorization<RuleForm> facet,
                                                                                                                               RuleForm instance) throws SecurityException {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, model.getKernel()
                                                             .getAPPLY())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getAPPLY()));
        }
        networkedModel.initialize(instance, facet, model.getCurrentPrincipal()
                                                        .getPrincipal());
        return instance;
    }

    /**
     * Apply the facet to the instance
     * 
     * @param facet
     * @param id
     * 
     * @return
     * @throws SecurityException
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm apply(NetworkAuthorization<RuleForm> facet,
                                                                                                                               String id) throws SecurityException {
        UUID uuid = UUID.fromString(id);
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());

        RuleForm instance = networkedModel.find(uuid);
        if (uuid == null) {
            return null;
        }
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm createInstance(NetworkAuthorization<RuleForm> facet,
                                                                                                                                        String name,
                                                                                                                                        String description) throws SecurityException {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, model.getKernel()
                                                             .getCREATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getCREATE()));
        }
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object getAttributeValue(RuleForm instance,
                                                                                                                                         AttributeAuthorization<RuleForm, Network> stateAuth) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(stateAuth.getNetworkAuthorization()
                                                                                            .getClassification());
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            stateAuth, model.getKernel()
                                                            .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getChildren(RuleForm instance,
                                                                                                                                           NetworkAuthorization<RuleForm> auth) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
        return networkedModel.getChildren(instance,
                                          auth.getChildRelationship());
    }

    /***
     * Answer the xd children of the instance
     * 
     * @param instance
     * @param facet
     * @param auth
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<?> getChildren(RuleForm instance,
                                                                                                                                    NetworkAuthorization<RuleForm> facet,
                                                                                                                                    XDomainNetworkAuthorization<?, ?> auth) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
        NetworkAuthorization<?> child = auth.isForward() ? resolveTo(auth,
                                                                     model)
                                                         : resolveFrom(auth,
                                                                       model);
        if (child.getClassification() instanceof Agency) {
            return networkedModel.getAuthorizedAgencies(instance,
                                                        auth.getConnection());
        } else if (child.getClassification() instanceof Location) {
            return networkedModel.getAuthorizedLocations(instance,
                                                         auth.getConnection());
        } else if (child.getClassification() instanceof Product) {
            return networkedModel.getAuthorizedProducts(instance,
                                                        auth.getConnection());
        } else if (child.getClassification() instanceof Relationship) {
            return networkedModel.getAuthorizedRelationships(instance,
                                                             auth.getConnection());
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             facet.getClassification(),
                                                             child.getClassification()));
        }
    }

    /**
     * Answer the immediate, non inferred children of the instance
     * 
     * @param instance
     * @param auth
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getImmediateChildren(RuleForm instance,
                                                                                                                                                    NetworkAuthorization<RuleForm> auth) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
        return networkedModel.getImmediateChildren(instance,
                                                   auth.getChildRelationship());
    }

    /**
     * Answer the instance associated with the string uuid
     * 
     * @param id
     * @param facet
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm getInstance(String id,
                                                                                                                                     NetworkAuthorization<RuleForm> facet) {

        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, model.getKernel()
                                                             .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
        RuleForm instance = networkedModel.find(UUID.fromString(id));
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            instance, model.getKernel()
                                                           .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
        return instance;

    }

    /**
     * Answer the list of instances of this facet.
     * 
     * @param facet
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getInstances(NetworkAuthorization<RuleForm> facet) {

        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, model.getKernel()
                                                             .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm getSingularChild(RuleForm instance,
                                                                                                                                          NetworkAuthorization<RuleForm> auth) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 auth, model.getKernel()
                                                            .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
        return networkedModel.getImmediateChild(instance,
                                                auth.getChildRelationship());
    }

    /**
     * Answer the singular xd child of the instance
     * 
     * @param instance
     * @param facet
     * @param auth
     * @param child
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object getSingularChild(RuleForm instance,
                                                                                                                                        NetworkAuthorization<RuleForm> facet,
                                                                                                                                        XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                        NetworkAuthorization<?> child) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
        if (child.getClassification() instanceof Agency) {
            return networkedModel.getAuthorizedAgency(instance,
                                                      auth.getConnection());
        } else if (child.getClassification() instanceof Location) {
            return networkedModel.getAuthorizedLocation(instance,
                                                        auth.getConnection());
        } else if (child.getClassification() instanceof Product) {
            return networkedModel.getAuthorizedProduct(instance,
                                                       auth.getConnection());
        } else if (child.getClassification() instanceof Relationship) {
            return networkedModel.getAuthorizedRelationship(instance,
                                                            auth.getConnection());
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             facet.getClassification(),
                                                             child.getClassification()));
        }
    }

    /**
     * Remove the facet from the instance
     * 
     * @param facet
     * @param instance
     * @return
     * @throws SecurityException
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm remove(NetworkAuthorization<RuleForm> facet,
                                                                                                                                RuleForm instance,
                                                                                                                                boolean deleteAttributes) throws SecurityException {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 facet, model.getKernel()
                                                             .getAPPLY())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getAPPLY()));
        }
        networkedModel.initialize(instance, facet, model.getCurrentPrincipal()
                                                        .getPrincipal());
        return instance;
    }

    /**
     * Remove the facet from the instance
     * 
     * @param facet
     * @param instance
     * @return
     * @throws SecurityException
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm remove(NetworkAuthorization<RuleForm> facet,
                                                                                                                                String id,
                                                                                                                                boolean deleteAttributes) throws SecurityException {
        UUID uuid = UUID.fromString(id);
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());

        RuleForm instance = networkedModel.find(uuid);
        if (uuid == null) {
            return null;
        }
        return remove(facet, instance, deleteAttributes);
    }

    /**
     * Remove a child from the instance
     * 
     * @param instance
     * @param auth
     * @param child
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm removeChild(RuleForm instance,
                                                                                                                                     NetworkAuthorization<RuleForm> auth,
                                                                                                                                     RuleForm child) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 auth, model.getKernel()
                                                            .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }
        NetworkRuleform<RuleForm> link = networkedModel.getImmediateLink(instance,
                                                                         auth.getChildRelationship(),
                                                                         child.getRuleform());
        if (link != null) {
            model.getEntityManager()
                 .remove(link);
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm removeImmediateChildren(RuleForm instance,
                                                                                                                                                 NetworkAuthorization<RuleForm> auth,
                                                                                                                                                 List<RuleForm> children) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 auth, model.getKernel()
                                                            .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }
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

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm setAttributeValue(RuleForm instance,
                                                                                                                                           AttributeAuthorization<RuleForm, Network> stateAuth,
                                                                                                                                           List<Object> value) {
        return instance;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm setAttributeValue(RuleForm instance,
                                                                                                                                           AttributeAuthorization<RuleForm, Network> stateAuth,
                                                                                                                                           Map<String, Object> value) {
        return instance;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm setAttributeValue(RuleForm instance,
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
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm setChildren(RuleForm instance,
                                                                                                                                     NetworkAuthorization<RuleForm> auth,
                                                                                                                                     List<RuleForm> children) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }

        for (NetworkRuleform<RuleForm> childLink : networkedModel.getImmediateChildrenLinks(instance,
                                                                                            auth.getChildRelationship())) {
            model.getEntityManager()
                 .remove(childLink);
        }
        for (RuleForm child : children) {
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
     * @param facet
     * @param auth
     * @param children
     */
    @SuppressWarnings("unchecked")
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm setChildren(RuleForm instance,
                                                                                                                                     NetworkAuthorization<RuleForm> facet,
                                                                                                                                     XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                     List<?> children) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
        NetworkAuthorization<?> child = auth.isForward() ? resolveTo(auth,
                                                                     model)
                                                         : resolveFrom(auth,
                                                                       model);
        if (child.getClassification() instanceof Agency) {
            networkedModel.setAuthorizedAgencies(instance, auth.getConnection(),
                                                 (List<Agency>) children);
        } else if (child.getClassification() instanceof Location) {
            networkedModel.setAuthorizedLocations(instance,
                                                  auth.getConnection(),
                                                  (List<Location>) children);
        } else if (child.getClassification() instanceof Product) {
            networkedModel.setAuthorizedProducts(instance, auth.getConnection(),
                                                 (List<Product>) children);
        } else if (child.getClassification() instanceof Relationship) {
            networkedModel.setAuthorizedRelationships(instance,
                                                      auth.getConnection(),
                                                      (List<Relationship>) children);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             facet.getClassification(),
                                                             child.getClassification()));
        }
        return instance;
    }

    /**
     * Set the immediate children of the instance to be the list of supplied
     * children. No inferred links will be explicitly added or deleted.
     * 
     * @param id
     * @param auth
     * @param childrenIds
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm setChildren(String id,
                                                                                                                                     NetworkAuthorization<RuleForm> auth,
                                                                                                                                     List<String> childrenIds) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        RuleForm instance = networkedModel.find(UUID.fromString(id));
        List<RuleForm> children = new ArrayList<>();
        for (String childId : childrenIds) {
            RuleForm child = networkedModel.find(UUID.fromString(childId));
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
        return setChildren(instance, auth, children);
    }

    /**
     * Set the xd children of the instance.
     * 
     * @param id
     * @param auth
     * @param childrenIds
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm setChildren(String id,
                                                                                                                                     NetworkAuthorization<RuleForm> facet,
                                                                                                                                     XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                     List<String> childrenIds) {
        NetworkedModel<RuleForm, Network, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        RuleForm instance = networkedModel.find(UUID.fromString(id));
        List<RuleForm> children = new ArrayList<>();
        // READ capability on the child facet
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getREAD())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getREAD()));
        }
        NetworkAuthorization<?> child = auth.isForward() ? resolveTo(auth,
                                                                     model)
                                                         : resolveFrom(auth,
                                                                       model);
        // READ capability on all the children
        for (String childId : childrenIds) {
            RuleForm inst = lookup(child, childId);
            if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                     .getPrincipal(),
                                                inst, model.getKernel()
                                                           .getREAD())) {
                throw new SecurityException(String.format("%s does not have %s capability",
                                                          model.getCurrentPrincipal(),
                                                          model.getKernel()
                                                               .getREAD()));
            }
        }
        return setChildren(instance, facet, auth, children);
    }

    /**
     * Set the singular child of the instance.
     * 
     * @param instance
     * @param auth
     * @param child
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm setSingularChild(RuleForm instance,
                                                                                                                                          NetworkAuthorization<RuleForm> auth,
                                                                                                                                          RuleForm child) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(auth.getClassification());
        if (!networkedModel.checkFacetCapability(model.getCurrentPrincipal()
                                                      .getPrincipal(),
                                                 auth, model.getKernel()
                                                            .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }
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
     * @param facet
     * @param auth
     * @param childInstance
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm setSingularChild(RuleForm instance,
                                                                                                                                          NetworkAuthorization<RuleForm> facet,
                                                                                                                                          XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                          @SuppressWarnings("rawtypes") ExistentialRuleform childInstance) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.getNetworkedModel(facet.getClassification());
        if (!networkedModel.checkCapability(model.getCurrentPrincipal()
                                                 .getPrincipal(),
                                            auth, model.getKernel()
                                                       .getUPDATE())) {
            throw new SecurityException(String.format("%s does not have %s capability",
                                                      model.getCurrentPrincipal(),
                                                      model.getKernel()
                                                           .getUPDATE()));
        }
        NetworkAuthorization<?> child = auth.isForward() ? resolveTo(auth,
                                                                     model)
                                                         : resolveFrom(auth,
                                                                       model);
        if (child.getClassification() instanceof Agency) {
            networkedModel.authorizeSingular(instance, auth.getConnection(),
                                             (Agency) childInstance);
        } else if (child.getClassification() instanceof Location) {
            networkedModel.authorizeSingular(instance, auth.getConnection(),
                                             (Location) childInstance);
        } else if (child.getClassification() instanceof Product) {
            networkedModel.authorizeSingular(instance, auth.getConnection(),
                                             (Product) childInstance);
        } else if (child.getClassification() instanceof Relationship) {
            networkedModel.authorizeSingular(instance, auth.getConnection(),
                                             (Relationship) childInstance);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             facet.getClassification(),
                                                             child.getClassification()));
        }
        return instance;
    }

    /**
     * Update the facet state of the instance
     * 
     * @param facet
     * @param state
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm update(NetworkAuthorization<RuleForm> facet,
                                                                                                                                Map<String, Object> state) {
        // TODO The idea is to iterate over all the update state, reflecting this in the facet state of 
        // the instance identified by the ID of the update state
        return null;
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object[] getIndexedAttributeValue(RuleForm instance,
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

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getMappedAttributeValue(RuleForm instance,
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

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> AttributeValue<RuleForm>[] getValueArray(RuleForm instance,
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

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, AttributeValue<RuleForm>> getValueMap(RuleForm instance,
                                                                                                                                                                   Attribute attribute,
                                                                                                                                                                   NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        Map<String, AttributeValue<RuleForm>> map = new HashMap<>();
        for (AttributeValue<RuleForm> value : networkedModel.getAttributeValues(instance,
                                                                                attribute)) {
            map.put(value.getKey(), value);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm lookup(@SuppressWarnings("rawtypes") NetworkAuthorization auth,
                                                                                                                                 String id) {
        UUID uuid = UUID.fromString(id);
        if (auth.getClassification() instanceof Agency) {
            return (RuleForm) model.getAgencyModel()
                                   .find(uuid);
        } else if (auth.getClassification() instanceof Location) {
            return (RuleForm) model.getLocationModel()
                                   .find(uuid);
        } else if (auth.getClassification() instanceof Product) {
            return (RuleForm) model.getProductModel()
                                   .find(uuid);
        } else if (auth.getClassification() instanceof Relationship) {
            return (RuleForm) model.getRelationshipModel()
                                   .find(uuid);
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth target: %s",
                                                             auth.getClassification()));
        }
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> AttributeValue<RuleForm> newAttributeValue(RuleForm instance,
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
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setAttributeArray(RuleForm instance,
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
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setAttributeMap(RuleForm instance,
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

    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setValue(RuleForm instance,
                                                                                                                               Attribute attribute,
                                                                                                                               int i,
                                                                                                                               AttributeValue<RuleForm> existing,
                                                                                                                               Object newValue) {
        if (existing == null) {
            existing = newAttributeValue(null, attribute, i, null);
            model.getEntityManager()
                 .persist(existing);
        }
        existing.setValue(newValue);
    }
}
