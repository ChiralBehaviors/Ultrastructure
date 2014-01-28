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
package com.hellblazer.CoRE.access.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.openjpa.lib.util.Localizer;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.CoRE.access.formatting.PropertiesFormatter;
import com.hellblazer.CoRE.access.formatting.XMLFormatter;
import com.hellblazer.CoRE.security.AuthenticatedPrincipal;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.metrics.annotation.Timed;

/**
 * @author hhildebrand
 * 
 */
@Path("/v{version : \\d+}/services/data/meta/")
public class DomainResource {
    /**
     * Private class for serializing a ruleform into a JSON object. It's so
     * awesome that this works.
     * 
     * @author hparry
     * 
     */
    private class DomainObject {

        @JsonProperty("objectName")
        private String   name;

        @JsonProperty("objectFields")
        private String[] fields;

        DomainObject(String name, String[] fields) {
            this.name = name;
            this.fields = fields;
        }
    }

    private static final char                   DOT = '.';
    private static Localizer                    loc = Localizer.forPackage(CrudResource.class);
    protected final OpenJPAEntityManagerFactory emf;

    protected final String                      unitName;

    /**
     * @param unitName
     * @param emf
     */
    public DomainResource(String unitName, OpenJPAEntityManagerFactory emf) {
        super();
        this.unitName = unitName;
        this.emf = emf;
    }

    @GET
    @Path("/domain")
    @Produces(MediaType.APPLICATION_XML)
    @Timed
    public byte[] getDomain(@PathParam("version") int version,
                            @Context UriInfo uriInfo) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLFormatter formatter = new XMLFormatter();
        formatter.writeOut(getPersistenceContext().getMetamodel(),
                           loc.get("domain-title").toString(),
                           loc.get("domain-desc").toString(),
                           uriInfo.getRequestUri().getPath(), baos);
        return baos.toByteArray();
    }

    @GET
    @Path("/model")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public byte[] getModel(@PathParam("version") int version,
                           @Context UriInfo uriInfo,
                           @Auth AuthenticatedPrincipal user)
                                                             throws JsonGenerationException,
                                                             JsonMappingException,
                                                             IOException {

        Metamodel model = getPersistenceContext().getMetamodel();
        DomainObject objs[] = new DomainObject[model.getEntities().size()];

        Set<EntityType<?>> entities = model.getEntities();
        int i = 0;
        for (EntityType<?> e : entities) {

            String[] fields = new String[e.getAttributes().size()];
            int j = 0;
            for (Attribute<?, ?> a : e.getAttributes()) {
                fields[j] = a.getName();
                j++;
            }

            DomainObject obj = new DomainObject(e.getName(), fields);
            objs[i] = obj;
            i++;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(objs);

    }

    public OpenJPAEntityManager getPersistenceContext() {
        return emf.createEntityManager();
    }

    @GET
    @Path("/properties")
    @Produces({ MediaType.APPLICATION_XML })
    @Timed
    public byte[] getProperties(@PathParam("version") int version)
                                                                  throws IOException {
        Map<String, Object> properties = getPersistenceContext().getProperties();
        removeBadEntries(properties);
        PropertiesFormatter formatter = new PropertiesFormatter();
        String caption = loc.get("properties-caption", unitName).toString();
        Document xml = formatter.createXML(caption, "", "", properties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        formatter.write(xml, baos);
        return baos.toByteArray();
    }

    private void removeBadEntries(Map<String, Object> map) {
        Iterator<String> keys = map.keySet().iterator();
        for (; keys.hasNext();) {
            if (keys.next().indexOf(DOT) == -1) {
                keys.remove();
            }
        }
    }

}
