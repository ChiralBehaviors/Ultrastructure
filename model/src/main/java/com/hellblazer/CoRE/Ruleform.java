/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.hellblazer.CoRE;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.hellblazer.CoRE.agency.Agency;

/**
 * The superclass of all rule forms.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIdentityInfo(generator = RuleformIdGenerator.class, property = "@id")
@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY)
abstract public class Ruleform implements Serializable, Cloneable {
    public static final String  FIND_BY_ID_SUFFIX     = ".findById";
    public static final String  FIND_BY_NAME_SUFFIX   = ".findByName";
    public static final String  FIND_ALL_SUFFIX       = ".findAll";
    public static final String  FIND_FLAGGED_SUFFIX   = ".findFlagged";
    public static final String  GET_UPDATED_BY_SUFFIX = ".getUpdatedBy";
    public static final String  NAME_SEARCH_SUFFIX    = ".namesearch";

    private static final long   serialVersionUID      = 1L;

    private String              notes;

    @Column(name = "update_date")
    private Timestamp           updateDate;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    protected Agency            updatedBy;
    public static final Integer TRUE                  = Integer.valueOf((byte) 1);
    public static final Integer FALSE                 = Integer.valueOf((byte) 0);

    public static Boolean toBoolean(Integer value) {
        if (value == null) {
            return null;
        }
        return value.equals(Integer.valueOf(0)) ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Integer toInteger(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? TRUE : FALSE;
    }

    public Ruleform() {
    }

    public Ruleform(Agency updatedBy) {
        this();
        this.updatedBy = updatedBy;
    }

    public Ruleform(Long id) {
        this();
        setId(id);
    }

    public Ruleform(Long id, Agency updatedBy) {
        this(id);
        this.updatedBy = updatedBy;
    }

    public Ruleform(String notes) {
        this.notes = notes;
    }

    public Ruleform(String notes, Agency updatedBy) {
        this.notes = notes;
        this.updatedBy = updatedBy;
    }

    @Override
    public Ruleform clone() {
        Ruleform clone;
        try {
            clone = (Ruleform) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Unable to clone");
        }
        clone.setId(null);
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Ruleform other = (Ruleform) obj;
        Long id = getId();
        if (id == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!id.equals(other.getId())) {
            return false;
        }
        return true;
    }

    abstract public Long getId();

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @return the updateDate
     */
    public Timestamp getUpdateDate() {
        return updateDate;
    }

    /**
     * @return the updatedBy
     */
    public Agency getUpdatedBy() {
        return updatedBy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (getId() == null) {
            return 31;
        }
        return getId().hashCode();
    }

    public Ruleform manageEntity(EntityManager em,
                                 Map<Ruleform, Ruleform> knownObjects) {
        if (knownObjects.containsKey(this)) {
            return knownObjects.get(this);
        }

        //need to traverse leaf nodes first, before persisting this entity.
        knownObjects.put(this, this);
        traverseForeignKeys(em, knownObjects);

        if (getId() != null
            && em.getReference(this.getClass(), getId()) != null) {
            em.detach(this);
            knownObjects.put(this, em.merge(this));
        } else {
            em.persist(this);
            em.refresh(this);
            knownObjects.put(this, this);
        }

        return knownObjects.get(this);
    }

    abstract public void setId(Long id);

    /**
     * @param notes
     *            the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @param updateDate
     *            the updateDate to set
     */
    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @param updatedBy
     *            the updatedBy to set
     */
    public void setUpdatedBy(Agency updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass(), getId());
    }

    //am I traversing the merged entity or the non-merged uploaded state?
    //might as well make it merged
    /**
     * Calls manageEntity on each foreign key and replaces non-managed foreign
     * key objects with managed objects
     * 
     * @param em
     * @param knownObjects
     */
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (updatedBy != null) {
            updatedBy = (Agency) updatedBy.manageEntity(em, knownObjects);
        }

    }
}
