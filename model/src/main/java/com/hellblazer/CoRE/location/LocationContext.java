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
package com.hellblazer.CoRE.location;

import static com.hellblazer.CoRE.Ruleform.FIND_BY_NAME_SUFFIX;
import static com.hellblazer.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.hellblazer.CoRE.location.LocationRelationship.AVAILABLE_CONTEXTS;
import static com.hellblazer.CoRE.location.LocationRelationship.AVAILABLE_RELATIONSHIPS;

import java.util.List;
import java.util.Set;

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
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The location space; the framework in which a Location can sensibly be
 * interpreted. 18th draft human genome coordinate, 17th draft human genome
 * coordinate, Mailboxes, Email Addresses, Earth latitude and longitude
 * 
 */
@javax.persistence.Entity
@Table(name = "location_context", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_context_id_seq", sequenceName = "location_context_id_seq")
@NamedQueries({ @NamedQuery(name = "locationContext"
                                   +  FIND_BY_NAME_SUFFIX, query = "select e from LocationContext e where e.name = :name"), })
// ?1 = :queryString, ?2 = :numberOfMatches
@NamedNativeQueries({ @NamedNativeQuery(name = "locationContext"
                                               +  NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('location_context', ?1, ?2)", resultClass = NameSearchResult.class) })
public class LocationContext extends ExistentialRuleform implements
        Attributable<ContextAttribute> {
    private static final long  serialVersionUID = 1L;

    public static final String ANY_CONTEXT      = "(ANY)";

    /**
     * Returns a list of all LocationContexts that have LocationRelationship
     * rules relating them to some other context(s). This will be a subset of
     * all possible LocationContexts, because some LocationContexts may not have
     * any relationship rules defined.
     * 
     * @return
     */
    public static List<LocationContext> getAvailableContexts(EntityManager em) {
        return em.createNamedQuery(AVAILABLE_CONTEXTS, LocationContext.class).getResultList();
    }

    //bi-directional many-to-one association to ContextAttribute
    @OneToMany(mappedBy = "locationContext")
    @JsonIgnore
    private Set<ContextAttribute> attributes;

    @Id
    @GeneratedValue(generator = "location_context_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                  id;

    public LocationContext() {
    }

    /**
     * @param id
     */
    public LocationContext(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public LocationContext(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param name
     */
    public LocationContext(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public LocationContext(String name, Resource updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public LocationContext(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public LocationContext(String name, String description, Resource updatedBy) {
        super(name, description, updatedBy);
    }

    @Override
    public Set<ContextAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<ContextAttribute> getAttributeType() {
        return ContextAttribute.class;
    }

    public List<Relationship> getAvailableTransformationRelationships(EntityManager em,
                                                                      LocationContext target) {
        return em.createNamedQuery(AVAILABLE_RELATIONSHIPS, Relationship.class).setParameter("context",
                                                                                             this).setParameter("target",
                                                                                                                target).getResultList();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setAttributes(Set<ContextAttribute> contextAttributes) {
        attributes = contextAttributes;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}