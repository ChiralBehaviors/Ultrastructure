/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.resource;

import static com.hellblazer.CoRE.meta.Model.FIND_BY_NAME_SUFFIX;
import static com.hellblazer.CoRE.meta.NetworkedModel.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
import static com.hellblazer.CoRE.meta.NetworkedModel.FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
import static com.hellblazer.CoRE.meta.NetworkedModel.FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
import static com.hellblazer.CoRE.meta.NetworkedModel.UNLINKED_SUFFIX;
import static com.hellblazer.CoRE.resource.Resource.FIND_BY_NAME;
import static com.hellblazer.CoRE.resource.Resource.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.resource.Resource.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.resource.Resource.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.resource.Resource.UNLINKED;
import static com.hellblazer.CoRE.resource.ResourceAttribute.GET_ATTRIBUTE;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.NameSearchResult;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.entity.EntityNetwork;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;

/**
 * An entity that can provide information, produce products, or perform work.
 * 
 * Examples are people, lab groups, software, books, bank accounts, output files
 * of computational analyses, etc.
 * 
 * "SELECT " + "  value " + "FROM " + "       ResourceAttribute value " +
 * "JOIN ResourceAttribute auth on auth.authorizedAttribute = value.attribute "
 * +
 * "JOIN ResourceNetwork network on network.relationship = auth.classification AND network.child = auth.classifier "
 * + "WHERE "+ "        value.resource = :resource AND " +
 * "        auth.classification = :classificatoin AND " +
 * "        auth.classifier = :classifier "
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Resource e where e.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       ResourceAttribute attrValue, "
                                                                            + "       ResourceAttributeAuthorization auth, "
                                                                            + "       ResourceNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.resource = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from ResourceAttributeAuthorization ra "
                                                                                    + "WHERE ra.classification = :classification "
                                                                                    + "AND ra.classifier = :classifier"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from ResourceAttributeAuthorization ra "
                                                                                 + "WHERE ra.groupingResource = :groupingResource") })
@NamedNativeQueries({
                     @NamedNativeQuery(name = UNLINKED, query = "SELECT unlinked.* "
                                                                + "FROM resource AS unlinked "
                                                                + "JOIN ("
                                                                + "     SELECT id "
                                                                + "     FROM resource "
                                                                + "     EXCEPT ("
                                                                + "             SELECT distinct(net.child) "
                                                                + "             FROM resource_network as net "
                                                                + "             WHERE net.parent = resource_id('Resource') "
                                                                + "             AND relationship = relationship_id('includes') "
                                                                + "     )"
                                                                + ") AS linked ON unlinked.id = linked.id "
                                                                + "WHERE unlinked.id != resource_id('Resource');", resultClass = Resource.class),
                     // ?1 = :queryString, ?2 = :numberOfMatches                                                                       
                     @NamedNativeQuery(name = "resource"
                                              + Model.NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('resource', :queryString, :numberOfMatches)", resultClass = NameSearchResult.class) })
@javax.persistence.Entity
@Table(name = "resource", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "resource_id_seq", sequenceName = "resource_id_seq")
public class Resource extends ExistentialRuleform implements
        Networked<Resource, ResourceNetwork>, Attributable<ResourceAttribute> {
    private static final long      serialVersionUID                         = 1L;

    public static final String     AUTHORIZED_RESOURCE_ATTRIBUTES           = "resource.authorizedAttributes";
    public static final String     FIND_BY_NAME                             = "resource"
                                                                              + FIND_BY_NAME_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "resource"
                                                                              + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "resource"
                                                                              + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String     FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS    = "resource"
                                                                              + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String     IMMEDIATE_CHILDREN_NETWORK_RULES         = "resource.immediateChildrenNetworkRules";
    public static final String     QUALIFIED_ENTITY_NETWORK_RULES           = "resource.qualifiedEntityNetworkRules";
    public static final String     RESOURCE_ATTRIBUTES_BY_CLASSIFICATION    = "resource.resourceAttributesByClassification";
    public static final String     UNLINKED                                 = "resource"
                                                                              + UNLINKED_SUFFIX;

    //bi-directional many-to-one association to ResourceAttribute
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ResourceAttribute> attributes;

    @Id
    @GeneratedValue(generator = "resource_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                   id;

    //bi-directional many-to-one association to ResourceLocation
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ResourceLocation>  locations;

    //bi-directional many-to-one association to ResourceNetwork
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ResourceNetwork>   networkByChild;

    //bi-directional many-to-one association to ResourceNetwork
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ResourceNetwork>   networkByParent;

    public Resource() {
    }

    /**
     * @param id
     */
    public Resource(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public Resource(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param name
     */
    public Resource(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Resource(String name, Resource updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Resource(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Resource(String name, String description, Resource updatedBy) {
        super(name, description, updatedBy);
    }

    public void addAttribute(ResourceAttribute attribute) {
        attribute.setResource(this);
        attributes.add(attribute);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addChildRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(ResourceNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addParentRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(ResourceNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Resource clone() {
        Resource clone = (Resource) super.clone();
        clone.networkByChild = null;
        clone.networkByParent = null;
        clone.attributes = null;
        return clone;
    }

    /**
     * Retrieves the unique ResourceAttribute rule for the given Resource and
     * Attribute.
     * 
     * @param resource
     *            The Resource the Attribute applies to
     * @param attribute
     *            The Attribute of the Resource
     * @return the unique ResourceAttribute rule, or <code>null</code> if no
     *         such rule exists
     */
    public ResourceAttribute getAttribute(EntityManager em, Attribute attribute) {
        return em.createNamedQuery(GET_ATTRIBUTE, ResourceAttribute.class).setParameter("resource",
                                                                                        this).setParameter("attribute",
                                                                                                           attribute).getSingleResult();
    }

    @Override
    public Set<ResourceAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<ResourceAttribute> getAttributeType() {
        return ResourceAttribute.class;
    }

    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getImmediateChildren()
     */
    @Override
    public List<ResourceNetwork> getImmediateChildren(EntityManager em) {
        return em.createNamedQuery(IMMEDIATE_CHILDREN_NETWORK_RULES,
                                   ResourceNetwork.class).setParameter("resource",
                                                                       this).getResultList();
    }

    public Set<ResourceLocation> getLocations() {
        return locations;
    }

    @Override
    public Set<ResourceNetwork> getNetworkByChild() {
        return networkByChild;
    }

    @Override
    public Set<ResourceNetwork> getNetworkByParent() {
        return networkByParent;
    }

    public List<EntityNetwork> getQualifiedEntityNetworkRules(EntityManager em) {
        return em.createNamedQuery(QUALIFIED_ENTITY_NETWORK_RULES,
                                   EntityNetwork.class).setParameter("resource",
                                                                     this).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#link(com.hellblazer.CoRE.network.Relationship, com.hellblazer.CoRE.network.Networked, com.hellblazer.CoRE.resource.Resource, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Resource child, Resource updatedBy,
                     Resource inverseSoftware, EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        ResourceNetwork link = new ResourceNetwork(this, r, child, updatedBy);
        em.persist(link);
        ResourceNetwork inverse = new ResourceNetwork(child, r.getInverse(),
                                                      this, inverseSoftware);
        em.persist(inverse);
    }

    @Override
    public void setAttributes(Set<ResourceAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLocations(Set<ResourceLocation> resourceLocations1) {
        locations = resourceLocations1;
    }

    @Override
    public void setNetworkByChild(Set<ResourceNetwork> resourceNetworks3) {
        networkByChild = resourceNetworks3;
    }

    @Override
    public void setNetworkByParent(Set<ResourceNetwork> resourceNetworks2) {
        networkByParent = resourceNetworks2;
    }
}