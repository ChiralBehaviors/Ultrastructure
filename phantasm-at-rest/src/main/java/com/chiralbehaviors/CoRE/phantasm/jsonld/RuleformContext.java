/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.jsonld;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.RuleformResource;

/**
 * @author hhildebrand
 *
 */
public class RuleformContext {
    private static final Map<Class<?>, URI> TYPES = new HashMap<>();

    static {
        // initialize primitive types
        try {
            TYPES.put(String.class,
                      new URI("http://www.w3.org/2001/XMLSchema#text"));
            TYPES.put(Integer.class,
                      new URI("http://www.w3.org/2001/XMLSchema#int"));
            TYPES.put(Integer.TYPE,
                      new URI("http://www.w3.org/2001/XMLSchema#int"));
            TYPES.put(BigDecimal.class,
                      new URI("http://www.w3.org/2001/XMLSchema#number"));
            TYPES.put(Boolean.class,
                      new URI("http://www.w3.org/2001/XMLSchema#boolean"));
            TYPES.put(Boolean.TYPE,
                      new URI("http://www.w3.org/2001/XMLSchema#boolean"));
            TYPES.put(Timestamp.class,
                      new URI("http://www.w3.org/2001/XMLSchema#date-dateTime"));
            TYPES.put(UUID.class,
                      new URI("http://www.w3.org/2001/XMLSchema#uuid"));
            TYPES.put(ValueType.class,
                      new URI("http://www.w3.org/2001/XMLSchema#text"));
            TYPES.put(Cardinality.class,
                      new URI("http://www.w3.org/2001/XMLSchema#text"));
            TYPES.put(new byte[0].getClass(),
                      new URI("http://www.w3.org/2001/XMLSchema#byteArray"));
        } catch (URISyntaxException e) {
            throw new IllegalStateException();
        }
    }

    public static URI getContextIri(Class<? extends Ruleform> ruleformClass,
                                    UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(RuleformResource.class);
        try {
            ub.path(RuleformResource.class.getMethod("getContext",
                                                     String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot get getContext method", e);
        }
        ub.resolveTemplate("ruleform", ruleformClass.getSimpleName());
        return ub.build();
    }

    public static List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getName().contains("$")
                    || Modifier.isStatic(field.getModifiers())
                    || field.getAnnotation(OneToMany.class) != null) {
                    continue;
                }
                fields.add(field);
            }
        }
        return fields;
    }

    public static String getIri(Ruleform ruleform) {
        return String.format("%s:%s", Constants.RULEFORM, ruleform.getId());
    }

    public static String getTermIri(Class<? extends Ruleform> ruleformClass,
                                    String term) {
        return String.format("%s:term/%s", Constants.RULEFORM, term);
    }

    public static String getTypeIri(Class<? extends Ruleform> ruleformClass) {
        return String.format("%s:%s", Constants.RULEFORM,
                             ruleformClass.getSimpleName());
    }

    private final Class<? extends Ruleform> ruleformClass;

    private final Map<String, Typed> terms = new TreeMap<>();

    public RuleformContext(Class<? extends Ruleform> ruleformClass,
                           UriInfo uriInfo) {
        this.ruleformClass = ruleformClass;
        gatherTerms(uriInfo);
    }

    public Map<String, Object> toContext(UriInfo uriInfo) {
        Map<String, Object> context = new TreeMap<>();
        Map<String, Object> t = new TreeMap<>();
        context.put(Constants.CONTEXT, t);
        t.put(Constants.VOCAB,
              uriInfo.getBaseUriBuilder().path(RuleformResource.class).build().toASCIIString()
                               + "/");
        t.put(Constants.RULEFORM,
              String.format("%s/", ruleformClass.getSimpleName()));
        for (Entry<String, Typed> entry : terms.entrySet()) {
            t.put(entry.getKey(), entry.getValue().toMap());
        }
        return context;
    }

    public Map<String, Object> toNode(Ruleform instance, UriInfo uriInfo) {
        Map<String, Object> object = getShort(instance, uriInfo);
        for (Field field : RuleformContext.getInheritedFields(instance.getClass())) {
            field.setAccessible(true);
            if (field.getAnnotation(JoinColumn.class) == null) {
                field.setAccessible(true);
                Object value;
                try {
                    value = field.get(instance);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
                if (value != null) {
                    object.put(field.getName(), value);
                }
            } else {
                Ruleform fk;
                try {
                    fk = (Ruleform) field.get(instance);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
                if (fk != null) {
                    RuleformContext fkContext = new RuleformContext(fk.getClass(),
                                                                    uriInfo);
                    object.put(field.getName(),
                               fkContext.getShort(fk, uriInfo));
                }
            }
        }
        return object;
    }

    public Map<String, Object> getShort(Ruleform instance, UriInfo uriInfo) {
        Map<String, Object> object = new TreeMap<>();
        object.put(Constants.CONTEXT, getContextIri(ruleformClass, uriInfo));
        object.put(Constants.TYPENAME, instance.getClass().getSimpleName());
        object.put(Constants.TYPE, Constants.RULEFORM);
        object.put(Constants.ID, getIri(instance));
        return object;
    }

    private void gatherTerms(UriInfo uriInfo) {
        for (Field field : getInheritedFields(ruleformClass)) {
            if (field.getAnnotation(JoinColumn.class) == null) {
                terms.put(field.getName(),
                          new Typed(getTermIri(ruleformClass, field.getName()),
                                    TYPES.get(field.getType())));
            } else {
                terms.put(field.getName(),
                          new Typed(getTermIri(ruleformClass, field.getName()),
                                    Constants.ID));
            }
        }
    }
}
