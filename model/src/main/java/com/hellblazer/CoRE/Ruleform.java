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
package com.hellblazer.CoRE;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.hellblazer.CoRE.json.RuleformIdGenerator;
import com.hellblazer.CoRE.resource.Resource;

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
    public static final String FIND_BY_NAME_SUFFIX   = ".findByName";
    public static final String NAME_SEARCH_SUFFIX    = ".namesearch";
    public static final String GET_UPDATED_BY_SUFFIX = ".getUpdatedBy";
    public static final String FIND_BY_ID_SUFFIX     = ".findById";
    public static final String FIND_FLAGGED_SUFFIX   = ".findFlagged";

    private static final long  serialVersionUID      = 1L;

    private String             notes;

    @ManyToOne
    @JoinColumn(name = "research")
    private Research           research;

    @Column(name = "update_date")
    private Timestamp          updateDate;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Resource           updatedBy;

    public Ruleform() {
    }

    public Ruleform(Long id) {
        this();
        setId(id);
    }

    public Ruleform(Long id, Resource updatedBy) {
        this(id);
        this.updatedBy = updatedBy;
    }

    public Ruleform(Resource updatedBy) {
        this();
        this.updatedBy = updatedBy;
    }

    public Ruleform(String notes) {
        this.notes = notes;
    }

    public Ruleform(String notes, Resource updatedBy) {
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
     * @return the research
     */
    public Research getResearch() {
        return research;
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
    public Resource getUpdatedBy() {
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

    abstract public void setId(Long id);

    /**
     * @param notes
     *            the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @param research
     *            the research to set
     */
    public void setResearch(Research research) {
        this.research = research;
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
    public void setUpdatedBy(Resource updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass(), getId());
    }
}
