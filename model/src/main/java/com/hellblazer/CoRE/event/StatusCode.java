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
package com.hellblazer.CoRE.event;

import static com.hellblazer.CoRE.event.StatusCode.FIND_BY_NAME;
import static com.hellblazer.CoRE.event.StatusCode.IS_TERMINAL_STATE;
import static com.hellblazer.CoRE.meta.Model.FIND_BY_NAME_SUFFIX;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.NameSearchResult;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the status_code database table.
 * 
 */

@javax.persistence.Entity
@Table(name = "status_code", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "status_code_id_seq", sequenceName = "status_code_id_seq")
@NamedQueries({ @NamedQuery(name = FIND_BY_NAME, query = "select sc from StatusCode sc where sc.name = :name"), })
// ?1 = :queryString, ?2 = :numberOfMatches
@NamedNativeQueries({
                     @NamedNativeQuery(name = "statusCode"
                                              + Model.NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('status_code', ?1, ?2)", resultClass = NameSearchResult.class),
                     @NamedNativeQuery(name = IS_TERMINAL_STATE, query = "SELECT EXISTS( "
                                                                         + "SELECT sc.id "
                                                                         + "FROM ruleform.status_code_sequencing AS seq "
                                                                         + "    JOIN ruleform.status_code AS sc ON seq.child_code = sc.id "
                                                                         + " WHERE "
                                                                         + "  NOT EXISTS ( "
                                                                         + "    SELECT parent_code FROM ruleform.status_code_sequencing "
                                                                         + "    WHERE service = seq.service "
                                                                         + "      AND parent_code = seq.child_code "
                                                                         + "  ) "
                                                                         + "  AND service = ? "
                                                                         + "  AND sc.id = ? "
                                                                         + " )") })
public class StatusCode extends ExistentialRuleform {
    private static final long  serialVersionUID  = 1L;
    public static final String FIND_BY_NAME      = "statusCode"
                                                   + FIND_BY_NAME_SUFFIX;
    public static final String IS_TERMINAL_STATE = "statusCode.isTerminalState";

    @Column(name = "fail_parent")
    private Boolean            failParent        = false;

    @Id
    @GeneratedValue(generator = "status_code_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    @Column(name = "propagate_children")
    private Boolean            propagateChildren = false;

    public StatusCode() {
    }

    public StatusCode(long l, String name) {
        super(name);
        id = l;
    }

    /**
     * @param id
     */
    public StatusCode(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public StatusCode(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param name
     */
    public StatusCode(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public StatusCode(String name, Resource updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public StatusCode(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public StatusCode(String name, String description, Resource updatedBy) {
        super(name, description, updatedBy);
    }

    public Boolean getFailParent() {
        return failParent;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Boolean getPropagateChildren() {
        return propagateChildren;
    }

    public void setFailParent(Boolean failParent) {
        this.failParent = failParent;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setPropagateChildren(Boolean propagateChildren) {
        this.propagateChildren = propagateChildren;
    }
}