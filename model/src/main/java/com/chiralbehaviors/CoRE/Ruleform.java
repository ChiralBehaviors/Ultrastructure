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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.Hibernate;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.proxy.HibernateProxy;
import org.reflections.Reflections;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.json.JsonMapType;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

/**
 * The superclass of all rule forms.
 *
 * @author hhildebrand
 *
 */
@TypeDefs({ @TypeDef(name = "jsonbType", typeClass = JsonMapType.class) })
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIdentityInfo(generator = PropertyGenerator.class, property = "id")
@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
@JsonInclude(Include.NON_NULL)
@Cacheable(true)
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

    public static <T extends Ruleform> T deserialize(InputStream is,
                                                     Class<T> clazz) throws JsonParseException,
                                                                     JsonMappingException,
                                                                     IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        mapper.registerModule(new Hibernate4Module());
        return mapper.readValue(is, clazz);
    }

    public static Ruleform find(EntityManager em, Ruleform ruleform) {
        return em.find(ruleform.getClass(), ruleform.getId());
    }

    public static List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getName()
                         .contains("$")
                    || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                fields.add(field);
            }
        }
        return fields;

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

    @SuppressWarnings("unchecked")
    public static <T extends Ruleform> T sanitize(T ruleform) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        serialize(ruleform, os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        return (T) deserialize(is, ruleform.getClass());
    }

    public static void serialize(Ruleform ruleform,
                                 OutputStream os) throws IOException {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new CoREModule());
        Hibernate4Module module = new Hibernate4Module();
        module.enable(Feature.FORCE_LAZY_LOADING);
        objMapper.registerModule(module);
        objMapper.writerWithDefaultPrettyPrinter()
                 .writeValue(os, ruleform);
    }

    public static Map<UUID, Ruleform> slice(Ruleform ruleform,
                                            Predicate<Ruleform> systemDefinition,
                                            Map<UUID, Ruleform> sliced,
                                            Set<UUID> traversed) {
        map(ruleform, systemDefinition, sliced, traversed);
        return sliced;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Ruleform> T smartMerge(EntityManager em,
                                                    T ruleform,
                                                    Map<UUID, Ruleform> mapped) {
        return (T) map(em, ruleform, mapped);
    }

    public static void traverse(Ruleform ruleform,
                                BiConsumer<Ruleform, Field> traverser) {
        for (Field field : getInheritedFields(ruleform.getClass())) {
            if (field.getAnnotation(JoinColumn.class) == null) {
                continue;
            }
            try {
                field.setAccessible(true);
                Ruleform value = (Ruleform) field.get(ruleform);
                if (value != null && !ruleform.id.equals(value.id)) {
                    traverser.accept(ruleform, field);
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(String.format("IllegalAccess access foreign key field: %s",
                                                              field),
                                                e);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(String.format("Illegal mapped value for field: %s",
                                                              field),
                                                e);
            }
        }
    }

    private static Ruleform asFrontier(Ruleform ruleform) {
        Ruleform mappedValue = ruleform.clone();
        for (Field field : getInheritedFields(ruleform.getClass())) {
            if (field.getAnnotation(JoinColumn.class) != null
                || field.getAnnotation(OneToMany.class) != null) {
                try {
                    field.setAccessible(true);
                    field.set(mappedValue, null);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(String.format("IllegalAccess access foreign key field: %s",
                                                                  field),
                                                    e);
                } catch (IllegalArgumentException e) {
                    throw new IllegalStateException(String.format("Illegal mapped value for field: %s",
                                                                  field),
                                                    e);
                }
            }
        }
        mappedValue.setId(ruleform.getId());
        mappedValue.setNotes("Mapped frontier stand in");
        return mappedValue;
    }

    private static Ruleform map(EntityManager em, Ruleform ruleform,
                                Map<UUID, Ruleform> mapped) {
        if (mapped.containsKey(ruleform.getId())) {
            Ruleform returned = mapped.get(ruleform.getId());
            return returned;
        }

        if (em.contains(ruleform)) {
            mapped.put(ruleform.getId(), ruleform);
            return ruleform;
        }

        BiConsumer<Ruleform, Field> slicer = (r, f) -> slice(r, f, em, mapped);
        Ruleform reference = find(em, ruleform);
        if (reference != null) {
            Ruleform merged = em.merge(ruleform);
            mapped.put(ruleform.getId(), merged);
            traverse(merged, slicer);
            return merged;
        } else {
            Ruleform selfReference = em.getReference(ruleform.getClass(),
                                                     ruleform.getId());
            mapped.put(ruleform.getId(), selfReference);
            traverse(ruleform, slicer);
            em.persist(ruleform);
            return selfReference;
        }
    }

    private static Ruleform map(Ruleform value,
                                Predicate<Ruleform> systemDefinition,
                                Map<UUID, Ruleform> sliced,
                                Set<UUID> traversed) {
        Ruleform mappedValue = sliced.get(value.getId());
        if (mappedValue != null) {
            // this value has already been determined to be part of another system
            return mappedValue;
        }
        value = Ruleform.initializeAndUnproxy(value);
        if (!traversed.add(value.getId())) {
            // We've already traversed this value
            return value;
        }
        if (systemDefinition.test(value)) {
            // This value is in the system, traverse it 
            traverse(value, (r, f) -> slice(r, f, systemDefinition, sliced,
                                            traversed));
            return value;
        }

        // This value is not in the system and has not been traversed, create a mapped value that stands for an exit from the system 
        mappedValue = asFrontier(value);

        // Mapped value has the same id, but null fields for everything else
        mappedValue.setId(value.getId());
        sliced.put(value.getId(), mappedValue);
        return mappedValue;
    }

    private static void slice(Ruleform ruleform, Field field, EntityManager em,
                              Map<UUID, Ruleform> mapped) {
        Ruleform value;
        try {
            value = (Ruleform) field.get(ruleform);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("IllegalAccess access foreign key field: %s",
                                                          field),
                                            e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(String.format("Illegal mapped value for field: %s",
                                                          field),
                                            e);
        }
        if (value != null && ruleform != value) {
            Ruleform mappedValue = map(em, value, mapped);
            if (mappedValue == null) {
                throw new IllegalStateException(String.format("%s mapped to null",
                                                              value));
            }
            if (mappedValue != value) {
                try {
                    field.set(ruleform, mappedValue);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(String.format("IllegalAccess access foreign key field: %s",
                                                                  field),
                                                    e);
                } catch (IllegalArgumentException e) {
                    throw new IllegalStateException(String.format("Illegal mapped value for field: %s",
                                                                  field),
                                                    e);
                }
            }
        }
    }

    private static void slice(Ruleform ruleform, Field field,
                              Predicate<Ruleform> systemDefinition,
                              Map<UUID, Ruleform> sliced, Set<UUID> traversed) {
        try {
            Ruleform value = (Ruleform) field.get(ruleform);
            if (value != null && ruleform != value) {
                field.set(ruleform,
                          map(value, systemDefinition, sliced, traversed));
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("IllegalAccess access foreign key field: %s",
                                                          field),
                                            e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(String.format("Illegal mapped value for field: %s",
                                                          field),
                                            e);
        }
    }

    @Id
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
