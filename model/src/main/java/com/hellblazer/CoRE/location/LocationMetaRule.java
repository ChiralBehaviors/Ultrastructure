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

import static com.hellblazer.CoRE.location.LocationMetaRule.CONTEXT_META_RULES;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the location_meta_rule database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "location_meta_rule", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_meta_rule_id_seq", sequenceName = "location_meta_rule_id_seq")
@NamedQueries({ @NamedQuery(name = CONTEXT_META_RULES, query = "select lmr from LocationMetaRule as lmr "
                                                               + "where lmr.locationContext = :context "
                                                               + "order by lmr.sequenceNumber") })
public class LocationMetaRule extends Ruleform {
    private static final long  serialVersionUID   = 1L;
    public static final String CONTEXT_META_RULES = "locationMetaRule.contextMetaRules";

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute_mask")
    private Attribute          attributeMask;

    @Id
    @GeneratedValue(generator = "location_meta_rule_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to LocationContext
    @ManyToOne
    @JoinColumn(name = "location_context")
    private LocationContext    locationContext;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber;

    public LocationMetaRule() {
    }

    /**
     * @param id
     */
    public LocationMetaRule(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public LocationMetaRule(Resource updatedBy) {
        super(updatedBy);
    }

    public Attribute getAttributeMask() {
        return attributeMask;
    }

    @Override
    public Long getId() {
        return id;
    }

    public LocationContext getLocationContext() {
        return locationContext;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setAttributeMask(Attribute attribute) {
        attributeMask = attribute;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLocationContext(LocationContext locationContext) {
        this.locationContext = locationContext;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		attributeMask.manageEntity(em, knownObjects);
		locationContext.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
}