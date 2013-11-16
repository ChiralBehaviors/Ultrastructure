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
package com.hellblazer.CoRE.coordinate;

import static com.hellblazer.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.hellblazer.CoRE.coordinate.CoordinateKind.LOWER_LEVEL_KIND;
import static com.hellblazer.CoRE.coordinate.CoordinateKind.TOP_LEVEL_KIND;

import java.util.List;
import java.util.Set;

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
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the coordinate_kind database table.
 * 
 */
@Entity
@Table(name = "coordinate_kind", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "coordinate_kind_id_seq", sequenceName = "coordinate_kind_id_seq")
@NamedQueries({
               @NamedQuery(name = TOP_LEVEL_KIND, query = "select distinct ck "
                                                          + "from CoordinateKind as ck "
                                                          + "join ck.definitionRules as ckd "
                                                          + "where ckd.subordinateCoordinateKind is not null "
                                                          + "order by ck.name"),
               @NamedQuery(name = LOWER_LEVEL_KIND, query = "select distinct ck "
                                                            + "from CoordinateKind as ck "
                                                            + "join ck.definitionRules as ckd "
                                                            + "where ckd.attribute is not null "
                                                            + "order by ck.name") })
// ?1 = :queryString, ? 2 = :numberOfMatches                                                            
@NamedNativeQueries({ @NamedNativeQuery(name = "coordinateKind"
                                               + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('coordinate_kind', ?1, ?2)", resultClass = NameSearchResult.class) })
public class CoordinateKind extends ExistentialRuleform {
    private static final long  serialVersionUID = 1L;
    public static final String TOP_LEVEL_KIND   = "coordinateKind.allTopLevelKinds";
    public static final String LOWER_LEVEL_KIND = "coordinateKind.allLowerLevelKinds";

    /**
     * Finds all CoordinateKinds that are <em>not</em> defined in terms of
     * subordinate CoordinateKinds. Whatever is not returned by {
     * {@link #getTopLevelKinds()} is returned by this.
     * 
     * @return all CoordinatesKinds that are not top level
     */
    public static List<CoordinateKind> getLowerLevelKinds(EntityManager em) {
        return em.createNamedQuery(LOWER_LEVEL_KIND, CoordinateKind.class).getResultList();
    }

    /**
     * Finds all CoordinateKinds that are defined (using
     * CoordinateKindDefinition rules) in terms of subordinate CoordinateKinds.
     * In other words, the CoordinateKinds that are built up from other kinds,
     * and are not defined in terms of attributes. As such, these are known as
     * "top level" kinds.
     * 
     * @return all the top level CoordinateKinds in the system
     * 
     * @see com.hellblazer.CoRE.ruleform.CoordinateKindDefinition
     */
    public static List<CoordinateKind> getTopLevelKinds(EntityManager em) {
        return em.createNamedQuery(TOP_LEVEL_KIND, CoordinateKind.class).getResultList();
    }

    //bi-directional many-to-one association to CoordinateKindDefinition
    @OneToMany(mappedBy = "kind")
    @JsonIgnore
    private Set<CoordinateKindDefinition> definitionRules;

    @Id
    @GeneratedValue(generator = "coordinate_kind_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                          id;

    private Boolean                       nestable;

    //bi-directional many-to-one association to CoordinateNesting
    @OneToMany(mappedBy = "kind")
    @JsonIgnore
    private Set<CoordinateNesting>        nestingRules;

    public CoordinateKind() {
    }

    /**
     * @param id
     */
    public CoordinateKind(Long id) {
        super(id);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param updatedBy
     */
    public CoordinateKind(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param name
     */
    public CoordinateKind(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public CoordinateKind(String name, Resource updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public CoordinateKind(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public CoordinateKind(String name, String description, Resource updatedBy) {
        super(name, description, updatedBy);
    }

    public Set<CoordinateKindDefinition> getDefinitionRules() {
        return definitionRules;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Boolean getNestable() {
        return nestable;
    }

    public Set<CoordinateNesting> getNestingRules() {
        return nestingRules;
    }

    public void setDefinitionRules(Set<CoordinateKindDefinition> coordinateKindDefinitions1) {
        definitionRules = coordinateKindDefinitions1;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setNestable(Boolean nestable) {
        this.nestable = nestable;
    }

    public void setNestingRules(Set<CoordinateNesting> coordinateNestings) {
        nestingRules = coordinateNestings;
    }

}