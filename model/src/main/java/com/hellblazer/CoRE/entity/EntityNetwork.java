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
package com.hellblazer.CoRE.entity;

import static com.hellblazer.CoRE.entity.Entity.IMMEDIATE_CHILDREN_NETWORK_RULES;
import static com.hellblazer.CoRE.entity.EntityNetwork.GET_CHILD;
import static com.hellblazer.CoRE.entity.EntityNetwork.GET_USED_RELATIONSHIPS;
import static com.hellblazer.CoRE.meta.NetworkedModel.USED_RELATIONSHIPS_SUFFIX;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the entity_network database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "entity_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "entity_network_id_seq", sequenceName = "entity_network_id_seq", allocationSize = 1)
@NamedQueries({
               @NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "select n from EntityNetwork n "
                                                                            + "where n.parent = :entity "
                                                                            + "and n.distance = 1 "
                                                                            + "and n.relationship.preferred = FALSE "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from EntityNetwork n"),
               @NamedQuery(name = GET_CHILD, query = "SELECT rn.child "
                                                     + "FROM EntityNetwork rn "
                                                     + "WHERE rn.parent = :parent "
                                                     + "AND rn.relationship = :relationship") })
public class EntityNetwork extends NetworkRuleform<Entity> implements
        Attributable<EntityNetworkAttribute> {
    private static final long  serialVersionUID       = 1L;
    public static final String GET_CHILD              = "entityNetwork.getChild";
    public static final String GET_USED_RELATIONSHIPS = "entityNetwork"
                                                        + USED_RELATIONSHIPS_SUFFIX;

    public static List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    //bi-directional many-to-one association to EntityNetworkAttribute
    @OneToMany(mappedBy = "entityNetwork", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<EntityNetworkAttribute> attributes;

    //bi-directional many-to-one association to Entity
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "child")
    private Entity                      child;

    @Id
    @GeneratedValue(generator = "entity_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                        id;

    //bi-directional many-to-one association to Entity
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    private Entity                      parent;

    public EntityNetwork() {
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public EntityNetwork(Entity parent, Relationship relationship,
                         Entity child, Resource updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param id
     */
    public EntityNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public EntityNetwork(Relationship relationship, Resource updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public EntityNetwork(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Set<EntityNetworkAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<EntityNetworkAttribute> getAttributeType() {
        return EntityNetworkAttribute.class;
    }

    @Override
    public Entity getChild() {
        return child;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Entity getParent() {
        return parent;
    }

    @Override
    public void setAttributes(Set<EntityNetworkAttribute> entityNetworkAttributes) {
        attributes = entityNetworkAttributes;
    }

    @Override
    public void setChild(Entity child) {
        this.child = child;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setParent(Entity parent) {
        this.parent = parent;
    }
}