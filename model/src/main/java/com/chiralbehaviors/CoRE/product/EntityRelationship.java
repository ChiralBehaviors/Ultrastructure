package com.chiralbehaviors.CoRE.product;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.relationship.Relationship;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class EntityRelationship extends Ruleform {

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "agency")
    private Agency       agency;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "relationship")
    private Relationship relationship;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "child")
    private Relationship child;

    public EntityRelationship() {
        
    }
    /**
     * @param id
     */
    public EntityRelationship(UUID id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public EntityRelationship(Agency updatedBy) {
        super(updatedBy);
    }

    public Agency getAgency() {
        return agency;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Relationship getChild() {
        return child;
    }

    public void setAgency(Agency agency2) {
        agency = agency2;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public void setChild(Relationship child) {
        this.child = child;
    }

}
