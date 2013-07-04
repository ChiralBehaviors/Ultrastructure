/**
 * Copyright (C) 2011 Hal Hildebrand. All rights reserved.
 * 
 * This file is part of the Thoth Interest Management and Load Balancing
 * Framework.
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

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.network.NetworkAuthorization;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "location_network_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_network_authorization_id_seq", sequenceName = "location_network_authorization_id_seq")
public class LocationNetworkAuthorization extends
        NetworkAuthorization<Location> {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Event
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "authorized_parent")
    private Location          authorizedParent;

    //bi-directional many-to-one association to Event
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "classifier")
    private Location          classifier;

    @Id
    @GeneratedValue(generator = "location_network_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    /**
     * 
     */
    public LocationNetworkAuthorization() {
        super();
    }

    /**
     * @param id
     */
    public LocationNetworkAuthorization(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public LocationNetworkAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkAuthorization#getAuthorizedParent()
     */
    @Override
    public Location getAuthorizedParent() {
        return authorizedParent;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkAuthorization#getClassifier()
     */
    @Override
    public Location getClassifier() {
        return classifier;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkAuthorization#setAuthorizedParent(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public void setAuthorizedParent(Location parent) {
        authorizedParent = parent;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkAuthorization#setClassifier(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Location classifier) {
        this.classifier = classifier;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
