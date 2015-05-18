/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.attribute.unit;

import static com.chiralbehaviors.CoRE.attribute.unit.Unit.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.GET_CHILD;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.GET_CHILD_RULES_BY_RELATIONSHIP;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownUnit;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The attribute unit.
 *
 * @author hhildebrand
 *
 */
@NamedQueries({
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       UnitAttribute attrValue, "
                                                                            + "       UnitAttributeAuthorization auth, "
                                                                            + "       UnitNetworkAuthorization na, "
                                                                            + "       UnitNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.networkAuthorization = na "
                                                                            + "    AND auth.authorizedAttribute = attrValue.attribute "
                                                                            + "    AND network.relationship = na.classifier "
                                                                            + "    AND network.child = na.classification"
                                                                            + "    AND attrValue.unitRf = :ruleform "
                                                                            + "    AND na.classifier = :classifier "
                                                                            + "    AND na.classification= :classification "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "SELECT "
                                                                                                  + "  auth "
                                                                                                  + "FROM "
                                                                                                  + "       IntervalAttributeAuthorization auth, "
                                                                                                  + "       IntervalNetworkAuthorization na, "
                                                                                                  + "       IntervalNetwork network "
                                                                                                  + "WHERE "
                                                                                                  + "        auth.networkAuthorization = na "
                                                                                                  + "    AND auth.authorizedAttribute = :attribute "
                                                                                                  + "    AND network.relationship = na.classifier "
                                                                                                  + "    AND network.child = na.classification"
                                                                                                  + "    AND na.classifier = :classifier "
                                                                                                  + "    AND na.classification= :classification "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select auth from UnitAttributeAuthorization auth "
                                                                                    + "WHERE auth.networkAuthorization.classifier = :classifier "
                                                                                    + "AND auth.networkAuthorization.classification = :classification "
                                                                                    + "AND auth.authorizedAttribute IS NOT NULL"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Attribute e where e.name = :name"),
               @NamedQuery(name = GET_CHILD, query = "SELECT rn.child "
                                                     + "FROM UnitNetwork rn "
                                                     + "WHERE rn.parent = :parent "
                                                     + "AND rn.relationship = :relationship"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM UnitNetwork n "
                                                                           + "WHERE n.parent = :attribute "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
@Entity
@Table(name = "unit", schema = "ruleform")
public class Unit extends ExistentialRuleform<Unit, UnitNetwork> {
    public static final String FIND_BY_NAME                                           = "unit.findByName";
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "unit"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "unit"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_VALUES                       = "unit"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String GET_CHILD                                              = "unit"
                                                                                        + GET_CHILDREN_SUFFIX;
    public static final String GET_CHILD_RULES_BY_RELATIONSHIP                        = "unit"
                                                                                        + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    private static final long  serialVersionUID                                       = 1L;

    private String             abbreviation;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "unitRf")
    @JsonIgnore
    private Set<UnitAttribute> attributes;

    private String             datatype;

    private Integer            enumerated                                             = FALSE;

    private BigDecimal         max;

    private BigDecimal         min;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child")
    @JsonIgnore
    private Set<UnitNetwork>   networkByChild;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonIgnore
    private Set<UnitNetwork>   networkByParent;

    public Unit() {
    }

    public Unit(Agency updatedBy) {
        super(updatedBy);
    }

    public Unit(String name) {
        setName(name);
    }

    public Unit(String name, Agency updatedBy) {
        super(updatedBy);
        setName(name);
    }

    public Unit(String name, String description, Agency updatedBy) {
        super(updatedBy);
        setName(name);
        setDescription(description);
    }

    public Unit(String name, String description, String notes, Agency updatedBy) {
        super(notes, updatedBy);
        setName(name);
        setDescription(description);
    }

    public Unit(UUID id) {
        super(id);
    }

    public Unit(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#addChildRelationship(com
     * .chiralbehaviors .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(UnitNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#addParentRelationship(com
     * .chiralbehaviors .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(UnitNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Unit clone() {
        Unit clone = (Unit) super.clone();
        clone.networkByChild = null;
        clone.networkByParent = null;
        clone.attributes = null;
        return clone;
    }

    @Override
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getAnyId()
     */
    @Override
    public UUID getAnyId() {
        return WellKnownUnit.ANY.id();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<UnitAttribute> getAttributes() {
        return attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<UnitAttribute> getAttributeValueClass() {
        return UnitAttribute.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getCopyId()
     */
    @Override
    public UUID getCopyId() {
        return WellKnownUnit.COPY.id();
    }

    public String getDatatype() {
        return datatype;
    }

    public Boolean getEnumerated() {
        return toBoolean(enumerated);
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkByChild()
     */
    @Override
    public Set<UnitNetwork> getNetworkByChild() {
        if (networkByChild == null) {
            return Collections.emptySet();
        }
        return networkByChild;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkByParent()
     */
    @Override
    public Set<UnitNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkChildAttribute()
     */
    @Override
    public SingularAttribute<UnitNetwork, Unit> getNetworkChildAttribute() {
        return UnitNetwork_.child;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkClass()
     */
    @Override
    public Class<UnitNetwork> getNetworkClass() {
        return UnitNetwork.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkParentAttribute()
     */
    @Override
    public SingularAttribute<UnitNetwork, Unit> getNetworkParentAttribute() {
        return UnitNetwork_.parent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkWorkspaceAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, UnitNetwork> getNetworkWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.unitNetwork;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNotApplicableId()
     */
    @Override
    public UUID getNotApplicableId() {
        return WellKnownUnit.NOT_APPLICABLE.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getSameId()
     */
    @Override
    public UUID getSameId() {
        return WellKnownUnit.SAME.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, Unit> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.unit;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownUnit.ANY.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAnyOrSame()
     */
    @Override
    public boolean isAnyOrSame() {
        return WellKnownUnit.ANY.id().equals(getId())
               || WellKnownUnit.SAME.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownUnit.COPY.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownUnit.NOT_APPLICABLE.id().equals(getId());
    }

    @Override
    public boolean isSame() {
        return WellKnownUnit.SAME.id().equals(getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#link(com.chiralbehaviors
     * .CoRE.network .Relationship,
     * com.chiralbehaviors.CoRE.ExistentialRuleform,
     * com.chiralbehaviors.CoRE.agency.Agency,
     * com.chiralbehaviors.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public UnitNetwork link(Relationship r, Unit child, Agency updatedBy,
                            Agency inverseSoftware, EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        UnitNetwork link = new UnitNetwork(this, r, child, updatedBy);
        em.persist(link);
        UnitNetwork inverse = new UnitNetwork(child, r.getInverse(), this,
                                              inverseSoftware);
        em.persist(inverse);
        return link;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends AttributeValue<Unit>> void setAttributes(Set<A> attributes) {
        this.attributes = (Set<UnitAttribute>) attributes;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public void setEnumerated(Boolean enumerated) {
        this.enumerated = toInteger(enumerated);
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#setNetworkByChild(java.util
     * .Set)
     */
    @Override
    public void setNetworkByChild(Set<UnitNetwork> theNetworkByChild) {
        networkByChild = theNetworkByChild;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#setNetworkByParent(java.
     * util.Set)
     */
    @Override
    public void setNetworkByParent(Set<UnitNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }
}