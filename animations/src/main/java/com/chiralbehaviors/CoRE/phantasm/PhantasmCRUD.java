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

import java.lang.reflect.Array;
import java.math.BigDecimal;
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
 * unwieldy, because of the type signatures required for erasure. But this class
 * provides a centralized implementation of Phantasm CRUD and the security model
 * for such.
 * 
 * @author hhildebrand
 *
 */
public class PhantasmCRUD {
    private final Model model;

    public PhantasmCRUD(Model model) {
        this.model = model;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addChild(RuleForm instance,
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
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addChildren(RuleForm instance,
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
    }

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
     * @param child
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<?> getChildren(RuleForm instance,
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
     * Remove a child from the instance
     * 
     * @param instance
     * @param auth
     * @param child
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void removeChild(RuleForm instance,
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
    }

    /**
     * Remove the immediate child links from the instance
     * 
     * @param instance
     * @param auth
     * @param children
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void removeImmediateChildren(RuleForm instance,
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
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setAttributeValue(RuleForm instance,
                                                                                                                                       AttributeAuthorization<RuleForm, Network> stateAuth,
                                                                                                                                       List<Object> value) {

    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setAttributeValue(RuleForm instance,
                                                                                                                                       AttributeAuthorization<RuleForm, Network> stateAuth,
                                                                                                                                       Map<String, Object> value) {

    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setAttributeValue(RuleForm instance,
                                                                                                                                       AttributeAuthorization<RuleForm, Network> stateAuth,
                                                                                                                                       Object value) {

    }

    /**
     * Set the xd children of the instance.
     * 
     * @param instance
     * @param facet
     * @param auth
     * @param child
     * @param children
     */
    @SuppressWarnings("unchecked")
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setChildren(RuleForm instance,
                                                                                                                                 NetworkAuthorization<RuleForm> facet,
                                                                                                                                 XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                 NetworkAuthorization<?> child,
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
    }

    /**
     * Set the immediate children of the instance to be the list of supplied
     * children. No inferred links will be explicitly added or deleted.
     * 
     * @param instance
     * @param auth
     * @param children
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setChildren(RuleForm instance,
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
    }

    /**
     * Set the singular child of the instance.
     * 
     * @param instance
     * @param auth
     * @param child
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setSingularChild(RuleForm instance,
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
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void setSingularChild(RuleForm instance,
                                                                                                                                      NetworkAuthorization<RuleForm> facet,
                                                                                                                                      XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                      NetworkAuthorization<?> child,
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
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object setAttributeArray(RuleForm instance,
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
        return null;
    }

    @SuppressWarnings("unused")
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object setAttributeMap(RuleForm instance,
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
        return null;
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
