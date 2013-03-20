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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.openjpa.lib.util.Localizer;

/**
 * @author hhildebrand
 * 
 */
public class Parse {
    public static final Collection<String> EMPTY_LIST = Collections.emptySet();
    public static final char               EQUAL      = '=';
    protected static Localizer             loc        = Localizer.forPackage(Parse.class);

    private final Map<String, String>      args       = new HashMap<String, String>();
    private final Map<String, String>      margs      = new HashMap<String, String>();
    private final Map<String, String>      qualifiers = new HashMap<String, String>();

    /**
     * @param uriInfo
     * @param validQualifiers
     */
    public Parse(String qualifiersPath, UriInfo uriInfo,
                 Collection<String> mandatoryArgs,
                 List<String> validQualifiers, int minArguments,
                 int maxArguments) {
        for (String path : qualifiersPath.split("/")) {
            int idx = path.indexOf(EQUAL);
            if (idx == -1) {
                qualifiers.put(path, null);
            } else {
                qualifiers.put(path.substring(0, idx), path.substring(idx + 1));
            }
        }

        for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            if (entry.getKey().startsWith("dojo.")) {
                continue;
            }
            Map<String, String> map = mandatoryArgs.contains(entry.getKey()) ? margs
                                                                            : args;
            map.put(entry.getKey(), entry.getValue().get(0));
        }

        validate(uriInfo, validQualifiers, mandatoryArgs, minArguments,
                 maxArguments);
    }

    public String getArgument(String key) {
        return args.get(key);
    }

    public Map<String, String> getArguments() {
        return args;
    }

    public String getMandatoryArgument(String key) {
        return margs.get(key);
    }

    public String getQualifier(String key) {
        return qualifiers.get(key);
    }

    public Map<String, String> getQualifiers() {
        return qualifiers;
    }

    public boolean hasArgument(String key) {
        return args.containsKey(key);
    }

    public boolean hasQualifier(String key) {
        return qualifiers.containsKey(key);
    }

    private void validate(UriInfo uriInfo, Collection<String> validQualifiers,
                          Collection<String> mandatoryArgs, int minArguments,
                          int maxArguments) {
        for (String key : qualifiers.keySet()) {
            if (!validQualifiers.contains(key)) {
                throw new WebApplicationException(
                                                  Response.status(Status.BAD_REQUEST).entity(loc.get("parse-invalid-qualifier",
                                                                                                     this,
                                                                                                     key,
                                                                                                     validQualifiers).getMessage()).build());
            }
        }
        for (String key : mandatoryArgs) {
            if (margs.get(key) == null) {
                throw new WebApplicationException(
                                                  Response.status(Status.BAD_REQUEST).entity(loc.get("parse-missing-mandatory-argument",
                                                                                                     this,
                                                                                                     key,
                                                                                                     uriInfo.getPathParameters().keySet()).getMessage()).build());
            }
        }
        if (margs.size() + args.size() < minArguments) {
            throw new WebApplicationException(
                                              Response.status(Status.BAD_REQUEST).entity(loc.get("parse-less-argument",
                                                                                                 this,
                                                                                                 args.keySet()).getMessage()).build());
        }
        if (margs.size() + args.size() > maxArguments) {
            throw new WebApplicationException(
                                              Response.status(Status.BAD_REQUEST).entity(loc.get("parse-less-argument",
                                                                                                 this,
                                                                                                 args.keySet()).getMessage()).build());
        }
    }

    protected boolean isBooleanQualifier(String key) {
        if (hasQualifier(key)) {
            Object value = getQualifier(key);
            return value == null || "true".equalsIgnoreCase(value.toString());
        }
        return false;
    }
}
