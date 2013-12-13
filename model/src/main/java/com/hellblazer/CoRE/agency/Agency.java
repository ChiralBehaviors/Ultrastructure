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
package com.hellblazer.CoRE.agency;

import static com.hellblazer.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.hellblazer.CoRE.agency.Agency.FIND_BY_NAME;
import static com.hellblazer.CoRE.agency.Agency.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.agency.Agency.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.agency.Agency.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.agency.Agency.GET_ALL_PARENT_RELATIONSHIPS;
import static com.hellblazer.CoRE.agency.Agency.GET_CHILD;
import static com.hellblazer.CoRE.agency.Agency.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.hellblazer.CoRE.agency.Agency.UNLINKED;
import static com.hellblazer.CoRE.agency.AgencyAttribute.GET_ATTRIBUTE;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.ProductNetwork;

/**
 * An agency that can provide information, produce products, or perform work.
 * 
 * Examples are people, lab groups, software, books, bank accounts, output files
 * of computational analyses, etc.
 * 
 * @author hhildebrand
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Agency e where e.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       AgencyAttribute attrValue, "
                                                                            + "       AgencyAttributeAuthorization auth, "
                                                                            + "       AgencyNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.agency = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from AgencyAttributeAuthorization ra "
                                                                                    + "WHERE ra.classification = :classification "
                                                                                    + "AND ra.classifier = :classifier"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from AgencyAttributeAuthorization ra "
                                                                                 + "WHERE ra.groupingAgency = :groupingAgency"),
               @NamedQuery(name = GET_CHILD, query = "SELECT n.child "
                                                     + "FROM AgencyNetwork n "
                                                     + "WHERE n.parent = :p "
                                                     + "AND n.relationship = :r"),
               @NamedQuery(name = GET_ALL_PARENT_RELATIONSHIPS, query = "SELECT n "
                                                                        + "FROM AgencyNetwork n "
                                                                        + "WHERE n.child = :c"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM AgencyNetwork n "
                                                                           + "WHERE n.parent = :agency "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
@NamedNativeQueries({
                     @NamedNativeQuery(name = UNLINKED, query = "SELECT unlinked.* "
                                                                + "FROM agency AS unlinked "
                                                                + "JOIN ("
                                                                + "     SELECT id "
                                                                + "     FROM agency "
                                                                + "     EXCEPT ("
                                                                + "             SELECT distinct(net.child) "
                                                                + "             FROM agency_network as net "
                                                                + "             WHERE net.parent = agency_id('Agency') "
                                                                + "             AND relationship = relationship_id('includes') "
                                                                + "     )"
                                                                + ") AS linked ON unlinked.id = linked.id "
                                                                + "WHERE unlinked.id != agency_id('Agency');", resultClass = Agency.class),
                     // ?1 = :queryString, ?2 = :numberOfMatches                                                                       
                     @NamedNativeQuery(name = "agency" + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('agency', :queryString, :numberOfMatches)", resultClass = NameSearchResult.class) })
@Entity
@Table(name = "agency", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "agency_id_seq", sequenceName = "agency_id_seq")
public class Agency extends ExistentialRuleform implements
        Networked<Agency, AgencyNetwork>, Attributable<AgencyAttribute> {
    public static final String   AUTHORIZED_AGENCY_ATTRIBUTES             = "agency.authorizedAttributes";

    public static final String   FIND_BY_NAME                             = "agency"
                                                                            + FIND_BY_NAME_SUFFIX;
    public static final String   FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "agency"
                                                                            + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String   FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "agency"
                                                                            + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String   FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS    = "agency"
                                                                            + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String   GET_CHILD                                = "agency"
                                                                            + GET_CHILD_SUFFIX;
    public static final String   GET_ALL_PARENT_RELATIONSHIPS             = "agency"
                                                                            + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String   IMMEDIATE_CHILDREN_NETWORK_RULES         = "agency.immediateChildrenNetworkRules";
    public static final String   QUALIFIED_ENTITY_NETWORK_RULES           = "agency.qualifiedEntityNetworkRules";
    public static final String   agency_ATTRIBUTES_BY_CLASSIFICATION      = "agency.AgencyAttributesByClassification";
    public static final String   UNLINKED                                 = "agency"
                                                                            + UNLINKED_SUFFIX;
    public static final String   GET_CHILD_RULES_BY_RELATIONSHIP          = "agency"
                                                                            + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    private static final long    serialVersionUID                         = 1L;

    //bi-directional many-to-one association to AgencyAttribute
    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<AgencyAttribute> attributes;

    @Id
    @GeneratedValue(generator = "agency_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                 id;

    //bi-directional many-to-one association to AgencyNetwork
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<AgencyNetwork>   networkByChild;

    //bi-directional many-to-one association to AgencyNetwork
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<AgencyNetwork>   networkByParent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<AgencyAccessAuthorization> accessAuthsByParent;
    

    public Agency() {
    }

    /**
     * @param updatedBy
     */
    public Agency(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public Agency(Long id) {
        super(id);
    }

    /**
     * @param name
     */
    public Agency(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Agency(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Agency(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Agency(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    public void addAttribute(AgencyAttribute attribute) {
        attribute.setAgency(this);
        attributes.add(attribute);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addChildRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(AgencyNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addParentRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(AgencyNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Agency clone() {
        Agency clone = (Agency) super.clone();
        clone.networkByChild = null;
        clone.networkByParent = null;
        clone.attributes = null;
        return clone;
    }

    /**
     * Retrieves the unique AgencyAttribute rule for the given Agency and
     * Attribute.
     * 
     * @param agency
     *            The Agency the Attribute applies to
     * @param attribute
     *            The Attribute of the Agency
     * @return the unique AgencyAttribute rule, or <code>null</code> if no such
     *         rule exists
     */
    public AgencyAttribute getAttribute(EntityManager em, Attribute attribute) {
        return em.createNamedQuery(GET_ATTRIBUTE, AgencyAttribute.class).setParameter("agency",
                                                                                      this).setParameter("attribute",
                                                                                                         attribute).getSingleResult();
    }

    @Override
    public Set<AgencyAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<AgencyAttribute> getAttributeType() {
        return AgencyAttribute.class;
    }

    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getImmediateChildren()
     */
    @Override
    public List<AgencyNetwork> getImmediateChildren(EntityManager em) {
        return em.createNamedQuery(IMMEDIATE_CHILDREN_NETWORK_RULES,
                                   AgencyNetwork.class).setParameter("agency",
                                                                     this).getResultList();
    }

    @Override
    public Set<AgencyNetwork> getNetworkByChild() {
        return networkByChild;
    }

    @Override
    public Set<AgencyNetwork> getNetworkByParent() {
        return networkByParent;
    }

    public List<ProductNetwork> getQualifiedEntityNetworkRules(EntityManager em) {
        return em.createNamedQuery(QUALIFIED_ENTITY_NETWORK_RULES,
                                   ProductNetwork.class).setParameter("agency",
                                                                      this).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#link(com.hellblazer.CoRE.network.Relationship, com.hellblazer.CoRE.network.Networked, com.hellblazer.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Agency child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        AgencyNetwork link = new AgencyNetwork(this, r, child, updatedBy);
        em.persist(link);
        AgencyNetwork inverse = new AgencyNetwork(child, r.getInverse(), this,
                                                  inverseSoftware);
        em.persist(inverse);
    }

    @Override
    public void setAttributes(Set<AgencyAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setNetworkByChild(Set<AgencyNetwork> AgencyNetworks3) {
        networkByChild = AgencyNetworks3;
    }

    @Override
    public void setNetworkByParent(Set<AgencyNetwork> AgencyNetworks2) {
        networkByParent = AgencyNetworks2;
    }

}