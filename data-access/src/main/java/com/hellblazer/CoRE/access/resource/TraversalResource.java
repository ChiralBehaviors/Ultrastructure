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
package com.hellblazer.CoRE.access.resource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.kernel.OpenJPAStateManager;
import org.reflections.Reflections;

import com.hellblazer.CoRE.Ruleform;
import com.yammer.metrics.annotation.Timed;

/**
 * @author hhildebrand
 * 
 */
@Path("/v{version : \\d+}/services/data/traversal/{entity}")
public class TraversalResource {

    private final EntityManagerFactory                   emf;
    private final Map<String, Class<? extends Ruleform>> entityMap = new HashMap<String, Class<? extends Ruleform>>();

    public TraversalResource(EntityManagerFactory emf) {
        Reflections reflections = new Reflections(
                                                  Ruleform.class.getPackage().getName());
        for (Class<? extends Ruleform> form : reflections.getSubTypesOf(Ruleform.class)) {
            if (!Modifier.isAbstract(form.getModifiers())) {
                Class<?> prev = entityMap.put(form.getSimpleName(), form);
                assert prev == null : String.format("Found previous mapping %s of: %s",
                                                    prev, form);
            }
        }
        this.emf = emf;
    }

    @GET
    @Timed
    @Produces({ "text/json" })
    public Object getEntity(@PathParam("version") int version,
                            @PathParam("entity") PathSegment entity,
                            @MatrixParam("fields") List<String> fields,
                            @MatrixParam("format") String format)
                                                                 throws IOException,
                                                                 JAXBException {
        return traverse(entity, Collections.<PathSegment> emptyList(), fields,
                        format);
    }

    @GET
    @Path("{traversal : .+}")
    @Timed
    @Produces({ "text/json" })
    public Object traverseEntity(@PathParam("version") int version,
                                 @PathParam("entity") PathSegment entity,
                                 @PathParam("traversal") List<PathSegment> traversal,
                                 @MatrixParam("fields") List<String> fields,
                                 @MatrixParam("format") String format)
                                                                      throws IOException,
                                                                      JAXBException {
        return traverse(entity, traversal, fields, format);
    }

    /**
     * @param instance
     * @param fields
     * @param em
     * @param mediaType
     * @param formatter
     * @return
     */
    protected Collection<?> fieldsOf(Object instance, List<String> fields) {
        if (fields.isEmpty()) {

        }
        return Collections.singleton(instance);
    }

    protected Field getField(String fieldName, Class<?> clazz)
                                                              throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            Field field;
            try {
                field = current.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    protected List<OpenJPAStateManager> toStateManager(Collection<?> objects) {
        List<OpenJPAStateManager> sms = new ArrayList<OpenJPAStateManager>();
        for (Object o : objects) {
            OpenJPAStateManager sm = toStateManager(o);
            if (sm != null) {
                sms.add(sm);
            }
        }
        return sms;
    }

    protected OpenJPAStateManager toStateManager(Object obj) {
        if (obj instanceof OpenJPAStateManager) {
            return (OpenJPAStateManager) obj;
        }
        if (obj instanceof PersistenceCapable) {
            return (OpenJPAStateManager) ((PersistenceCapable) obj).pcGetStateManager();
        }
        return null;
    }

    protected Object traverse(PathSegment entity, List<PathSegment> traversal,
                              List<String> fields, String format)
                                                                 throws IOException,
                                                                 JAXBException {

        if (entity == null) {
            return Response.status(Status.NOT_FOUND).entity("No root entity specified").build();
        }

        EntityManager em = emf.createEntityManager();
        Class<? extends Ruleform> rootClass = entityMap.get(entity.getPath());

        if (rootClass == null) {
            return Response.status(Status.NOT_FOUND).entity(String.format("Not a valid root entity %s",
                                                                          entity.getPath())).build();
        }

        String id = entity.getMatrixParameters().getFirst("id");

        if (id == null) {
            return Response.status(Status.NOT_FOUND).entity("No id specified for entity").build();
        }

        long primaryKey;
        try {
            primaryKey = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Response.status(Status.BAD_REQUEST).entity(String.format("Supplied root entity id was not a long: %s",
                                                                            id)).build();
        }

        Ruleform rootInstance = em.find(rootClass, primaryKey);

        if (rootInstance == null) {
            return Response.status(Status.NOT_FOUND).entity(String.format("Could not find an instance of % with id %s",
                                                                          entity,
                                                                          primaryKey)).build();
        }

        if (traversal.isEmpty()) {
            if (fields.isEmpty()) {
                return Collections.singleton(rootInstance);
            } else {
                return fieldsOf(rootInstance, fields);
            }
        } else {
            return traverse(rootInstance, traversal, fields, em);
        }
    }

    /**
     * @param traversal
     * @param rootInstance
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    protected Object traverse(Ruleform root, List<PathSegment> traversal,
                              List<String> fields, EntityManager em)
                                                                    throws IOException,
                                                                    JAXBException {
        Object current = root;
        for (PathSegment segment : traversal) {
            String fieldName = segment.getPath();
            Field field;
            try {
                field = getField(fieldName, current.getClass());
            } catch (SecurityException e) {
                return Response.status(Status.FORBIDDEN).build();
            } catch (NoSuchFieldException e) {
                return Response.status(Status.NOT_FOUND).entity(String.format("Field %s not found on entity %s",
                                                                              fieldName,
                                                                              current.getClass().getSimpleName())).build();
            }
            field.setAccessible(true);
            try {
                current = field.get(current);
            } catch (IllegalArgumentException e) {
                return Response.status(Status.NOT_FOUND).build();
            } catch (IllegalAccessException e) {
                return Response.status(Status.FORBIDDEN).build();
            }
        }
        return fieldsOf(current, fields);
    }
}
