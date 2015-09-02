/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.proxy.HibernateProxy;
import org.reflections.Reflections;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.json.RuleformIdGenerator;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

/**
 * The superclass of all rule forms.
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIdentityInfo(generator = RuleformIdGenerator.class, property = "@id")
@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
@JsonInclude(Include.NON_NULL)
@Cacheable
abstract public class Ruleform implements Serializable, Cloneable {
    public static final Map<String, Class<? extends Ruleform>> CONCRETE_SUBCLASSES;
    public static final String                                 FIND_ALL_SUFFIX       = ".findAll";
    public static final String                                 FIND_BY_ID_SUFFIX     = ".findById";
    public static final String                                 FIND_BY_NAME_SUFFIX   = ".findByName";
    public static final NoArgGenerator                         GENERATOR             = Generators.timeBasedGenerator();
    public static final String                                 GET_UPDATED_BY_SUFFIX = ".getUpdatedBy";

    private static final long serialVersionUID = 1L;

    static {
        Map<String, Class<? extends Ruleform>> concrete = new HashMap<>();
        Reflections reflections = new Reflections(Ruleform.class.getPackage()
                                                                .getName());
        for (Class<? extends Ruleform> form : reflections.getSubTypesOf(Ruleform.class)) {
            if (!Modifier.isAbstract(form.getModifiers())) {
                concrete.put(form.getSimpleName(), form);
            }
        }
        CONCRETE_SUBCLASSES = Collections.unmodifiableMap(concrete);
    }

    @SuppressWarnings("unchecked")
    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            return null;
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                                                  .getImplementation();
        }
        return entity;
    }

    @Id
    @Type(type = "pg-uuid")
    private UUID id = GENERATOR.generate();

    @Basic(fetch = FetchType.LAZY)
    private String notes;

    @Version
    @Column(name = "version")
    private int version = 0;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    protected Agency updatedBy;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace")
    protected WorkspaceAuthorization workspace;

    public Ruleform() {
    }

    public Ruleform(Agency updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Ruleform(String notes) {
        this.notes = notes;
    }

    public Ruleform(String notes, Agency updatedBy) {
        this.notes = notes;
        this.updatedBy = updatedBy;
    }

    public Ruleform(UUID id) {
        this();
        setId(id);
    }

    public Ruleform(UUID id, Agency updatedBy) {
        this(id);
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
        return clone;
    }

    public void delete(Triggers triggers) {
        // default is to do nothing;
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
        if (!(obj instanceof Ruleform)) {
            return false;
        }
        Ruleform other = (Ruleform) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @JsonGetter
    public UUID getId() {
        return id;
    }

    /**
     * @return the notes
     */
    @JsonGetter
    public String getNotes() {
        return notes;
    }

    /**
     * @return the updatedBy
     */
    @JsonGetter
    public Agency getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @return the version
     */
    @JsonProperty
    public int getVersion() {
        return version;
    }

    @JsonGetter
    public WorkspaceAuthorization getWorkspace() {
        return workspace;
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

    public void persist(Triggers triggers) {
        // default is to do nothing
    }

    @JsonProperty
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @param notes
     *            the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @param updatedBy
     *            the updatedBy to set
     */
    public void setUpdatedBy(Agency updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setWorkspace(WorkspaceAuthorization workspace) {
        this.workspace = workspace;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass(), getId());
    }

    public void update(Triggers triggers) {
        // default is to do nothing;
    }
}
