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

package com.hellblazer.CoRE.capability;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * Authorization ruleform to indicated allowed actions by Resources on other
 * entities
 * 
 * @author hhildebrand
 * 
 */
@SequenceGenerator(schema = "ruleform", name = "capability_id_seq", sequenceName = "capability_id_seq")
@Table(name = "capability", schema = "ruleform")
@javax.persistence.Entity
public class Capability extends Ruleform {
    public enum Target {
        Attribute, Product, Location, Resource;
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "capability_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    @ManyToOne
    @JoinColumn(name = "subject")
    private Resource          subject;

    @ManyToOne
    @JoinColumn(name = "target_attribute")
    private Attribute         targetAttribute;

    @ManyToOne
    @JoinColumn(name = "target_product")
    private Product           targetProduct;

    @ManyToOne
    @JoinColumn(name = "target_location")
    private Location          targetLocation;

    @ManyToOne
    @JoinColumn(name = "target_resource")
    private Resource          targetResource;

    @Column(name = "target_type")
    private Target            targetType;

    @ManyToOne
    @JoinColumn(name = "verb")
    private Action            verb;

    public Capability() {
        super();
    }

    /**
     * @param id
     */
    public Capability(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public Capability(Resource updatedBy) {
        super(updatedBy);
    }

    public Capability(Resource subject, Action verb, Attribute target,
                      String notes, Resource updatedBy) {
        this(notes, updatedBy);
        this.subject = subject;
        this.verb = verb;
        setTargetAttribute(target);
    }

    public Capability(Resource subject, Action verb, Location target,
                      String notes, Resource updatedBy) {
        this(notes, updatedBy);
        this.subject = subject;
        this.verb = verb;
        setTargetLocation(target);
    }

    /**
     * @param subject
     * @param notes
     * @param updatedBy
     */
    public Capability(Resource subject, Action verb, Product target,
                      String notes, Resource updatedBy) {
        this(notes, updatedBy);
        this.subject = subject;
        this.verb = verb;
        setTargetProduct(target);
    }

    public Capability(Resource subject, Action verb, Resource target,
                      String notes, Resource updatedBy) {
        this(notes, updatedBy);
        this.subject = subject;
        this.verb = verb;
        setTargetResource(target);
    }

    /**
     * @param subject
     * @param notes
     * @param updatedBy
     */
    public Capability(Resource subject, Action verb, String notes,
                      Resource updatedBy) {
        this(notes, updatedBy);
        this.subject = subject;
        this.verb = verb;
    }

    /**
     * @param notes
     */
    public Capability(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public Capability(String notes, Resource updatedBy) {
        super(notes, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Resource getSubject() {
        return subject;
    }

    public Attribute getTargetAttribute() {
        return targetAttribute;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public Product getTargetProduct() {
        return targetProduct;
    }

    public Resource getTargetResource() {
        return targetResource;
    }

    public Target getTargetType() {
        return targetType;
    }

    public Action getVerb() {
        return verb;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setSubject(Resource subject) {
        this.subject = subject;
    }

    public void setTargetAttribute(Attribute targetAttribute) {
        this.targetAttribute = targetAttribute;
        targetType = Target.Attribute;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
        targetType = Target.Location;
    }

    public void setTargetProduct(Product targetProduct) {
        this.targetProduct = targetProduct;
        targetType = Target.Product;
    }

    public void setTargetResource(Resource targetResource) {
        this.targetResource = targetResource;
        targetType = Target.Resource;
    }

    public void setTargetType(Target targetType) {
        this.targetType = targetType;
    }

    public void setVerb(Action verb) {
        this.verb = verb;
    }
}
