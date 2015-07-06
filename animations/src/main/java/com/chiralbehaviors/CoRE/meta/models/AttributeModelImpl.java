/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.models;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.AttributeNetworkAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.AttributeModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class AttributeModelImpl extends
        AbstractNetworkedModel<Attribute, AttributeNetwork, AttributeMetaAttributeAuthorization, AttributeMetaAttribute>
        implements AttributeModel {

    public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_BINARY    = "http://www.w3.org/2001/XMLSchema#binary";
    public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_BOOLEAN   = "http://www.w3.org/2001/XMLSchema#boolean";
    public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME = "http://www.w3.org/2001/XMLSchema#dateTime";
    public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_INT       = "http://www.w3.org/2001/XMLSchema#int";
    public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_NUMERIC   = "http://www.w3.org/2001/XMLSchema#numeric";
    public static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA_TEXT      = "http://www.w3.org/2001/XMLSchema#text";

    /**
     * @param em
     */
    public AttributeModelImpl(Model model) {
        super(model);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE
     * .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Attribute> aspect, Attribute... attributes) {
        AttributeNetworkAuthorization auth = new AttributeNetworkAuthorization(model.getCurrentPrincipal().getPrincipal());
        auth.setClassifier(aspect.getClassifier());
        auth.setClassification(aspect.getClassification());
        em.persist(auth);
        for (Attribute attribute : attributes) {
            AttributeMetaAttributeAuthorization authorization = new AttributeMetaAttributeAuthorization(attribute,
                                                                                                        model.getCurrentPrincipal().getPrincipal());
            authorization.setNetworkAuthorization(auth);
            em.persist(authorization);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
     */
    @Override
    public Attribute create(Attribute prototype) {
        Attribute copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(model.getCurrentPrincipal().getPrincipal());
        for (AttributeNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     model.getCurrentPrincipal().getPrincipal(),
                                     model.getCurrentPrincipal().getPrincipal(),
                                     em);
        }
        for (AttributeMetaAttribute attribute : prototype.getAttributes()) {
            AttributeMetaAttribute clone = (AttributeMetaAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setAttribute(copy);
            clone.setUpdatedBy(model.getCurrentPrincipal().getPrincipal());
        }
        return copy;
    }

    @Override
    public AttributeMetaAttribute create(Attribute ruleform,
                                         Attribute attribute,
                                         Agency updatedBy) {
        return new AttributeMetaAttribute(attribute, ruleform, updatedBy);
    }

    @Override
    public final Attribute create(String name, String description) {
        Attribute attribute = new Attribute(name, description,
                                            model.getCurrentPrincipal().getPrincipal());
        em.persist(attribute);
        return attribute;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#create(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization)
     */

    @SafeVarargs
    @Override
    public final Attribute create(String name, String description,
                                  Aspect<Attribute> aspect, Agency updatedBy,
                                  Aspect<Attribute>... aspects) {
        Attribute attribute = new Attribute(name, description,
                                            model.getCurrentPrincipal().getPrincipal());
        em.persist(attribute);
        initialize(attribute, aspect);
        if (aspects != null) {
            for (Aspect<Attribute> a : aspects) {
                initialize(attribute, a);
            }
        }
        return attribute;
    }

    @Override
    public List<AttributeNetwork> getInterconnections(Collection<Attribute> parents,
                                                      Collection<Relationship> relationships,
                                                      Collection<Attribute> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<AttributeNetwork> query = em.createNamedQuery(AttributeNetwork.GET_NETWORKS,
                                                                 AttributeNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.AttributeModel#getJsonLdType(com.chiralbehaviors.CoRE.attribute.Attribute)
     */
    @Override
    public String getJsonLdType(Attribute attribute) {
        AttributeValue<Attribute> type = getAttributeValue(attribute,
                                                           model.getKernel().getJsonldType());
        if (type != null) {
            return type.getTextValue();
        }
        switch (attribute.getValueType()) {
            case BINARY:
                return HTTP_WWW_W3_ORG_2001_XML_SCHEMA_BINARY;
            case BOOLEAN:
                return HTTP_WWW_W3_ORG_2001_XML_SCHEMA_BOOLEAN;
            case INTEGER:
                return HTTP_WWW_W3_ORG_2001_XML_SCHEMA_INT;
            case NUMERIC:
                return HTTP_WWW_W3_ORG_2001_XML_SCHEMA_NUMERIC;
            case TEXT:
                return HTTP_WWW_W3_ORG_2001_XML_SCHEMA_TEXT;
            case TIMESTAMP:
                return HTTP_WWW_W3_ORG_2001_XML_SCHEMA_DATE_TIME;
            default:
                throw new IllegalStateException(String.format("invalid value type: %s",
                                                              attribute.getValueType()));
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getNetworkAuthClass()
     */
    @Override
    protected Class<?> getNetworkAuthClass() {
        return AttributeNetworkAuthorization.class;
    }
}
