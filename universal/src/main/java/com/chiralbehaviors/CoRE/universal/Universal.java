/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
 *

 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.universal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.layout.graphql.GraphQlUtil;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryException;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryRequest;
import com.chiralbehaviors.layout.schema.Relation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.StringArgGenerator;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.hellblazer.utils.Utils;

/**
 *
 * @author hhildebrand
 *
 */
public class Universal {
    public static final String              GET_APPLICATION_QUERY;
    public static final String              GET_APPLICATION_QUERY_RESOURCE           = "getApplication.query";
    public static final String              GET_APPLICATIONS_QUERY_RESOURCE          = "getApplications.query";
    public static final String              SINGLE_PAGE_APPLICATION                  = "singlePageApplication";
    public static final String              SINGLE_PAGE_APPLICATIONS                 = "singlePageApplications";
    public static final String              SINGLE_PAGE_URI                          = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/uaas/single-page";
    public static final String              SINGLE_PAGE_UUID;
    public static final String              SPA_WSP;
    public static final String              UNIVERSAL_APP_ID                         = "universal.app.id";
    public static final String              UNIVERSAL_ENDPOINT                       = "universal.endpoint";

    private static final String             ALLOW_RESTRICTED_HEADERS_SYSTEM_PROPERTY = "sun.net.http.allowRestrictedHeaders";
    private static final Logger             log                                      = LoggerFactory.getLogger(Universal.class);
    private static final StringArgGenerator URL_UUID_GENERATOR;
    private static final String             URN_UUID                                 = "urn:uuid:";

    static {
        URL_UUID_GENERATOR = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_URL);
        System.setProperty(ALLOW_RESTRICTED_HEADERS_SYSTEM_PROPERTY, "true");
        try {
            GET_APPLICATION_QUERY = Utils.getDocument(Universal.class.getResourceAsStream(GET_APPLICATION_QUERY_RESOURCE));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        SINGLE_PAGE_UUID = UuidUtil.encode(uuidOf(SINGLE_PAGE_URI));
        try {
            SPA_WSP = URLEncoder.encode(SINGLE_PAGE_UUID.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String textOrNull(JsonNode node) {
        return node == null ? null : node.isNull() ? null : node.asText();
    }

    public static UUID uuidOf(String url) {
        if (url.startsWith(URN_UUID)) {
            return UUID.fromString(url.substring(URN_UUID.length()));
        }
        return URL_UUID_GENERATOR.generate(url);
    }

    private final Spa                     application;
    private final Stack<Context>          back    = new Stack<>();
    private BiConsumer<Context, JsonNode> display;
    private final WebTarget               endpoint;
    private final Stack<Context>          forward = new Stack<>();
    private final String                  frame;
    private Consumer<Universal>           launcher;

    public Universal(String frame, Spa application, WebTarget endpoint) {
        this.frame = frame;
        this.application = application;
        this.endpoint = endpoint;
    }

    public Universal(String frame, String intialId, URI uri) {
        this(frame, intialId, ClientBuilder.newClient()
                                           .target(uri));
    }

    public Universal(String frame, String intialId, WebTarget endpoint) {
        this.frame = frame;
        this.endpoint = endpoint;
        if (intialId == null) {
            log.info("No application id defined, using default app launcher");
            application = resolve(appLauncherId());
        } else {
            application = resolve(intialId);
        }
    }

    public Universal(String frame, WebTarget endpoint) {
        this(frame, (String) null, endpoint);
    }

    public Universal applicationFrom(JsonNode item, Launch launch) {
        String frame = back.peek()
                           .getFrame();
        if (launch.getFrame() != null) {
            frame = launch.getFrame();
        } else if (launch.getFrameBy() != null) {
            JsonNode launchFrame = apply(item, launch.getFrameBy());
            if (launchFrame == null) {
                log.warn("Invalid routing frame by {} : {}",
                         launch.getFrameBy(), item);
            } else {
                frame = launchFrame.asText();
            }
        }
        String application = launch.getImmediate();
        if (launch.getLaunchBy() != null) {
            JsonNode applicationNode = apply(item, launch.getLaunchBy());
            if (applicationNode != null) {
                application = applicationNode.asText();
            }
        }
        return new Universal(frame, resolve(application), endpoint);
    }

    public void back() {
        forward.push(back.pop());
        display();
    }

    public boolean backwardContexts() {
        return !back.isEmpty();
    }

    public void display() {
        try {
            display.accept(current(), evaluate());
        } catch (QueryException e) {
            log.error("Unable to query", e);
        }
    }

    public void forward() {
        back.push(forward.pop());
        display();
    }

    public boolean forwardContexts() {
        return !forward.isEmpty();
    }

    public Spa getApplication() {
        return application;
    }

    public BiConsumer<Context, JsonNode> getDisplay() {
        return display;
    }

    public String getFrame() {
        return frame;
    }

    public Consumer<Universal> getLauncher() {
        return launcher;
    }

    public void navigate(JsonNode node, Relation relation) {
        Context current = current();

        // favor routes over launch
        Route route = current.getNavigation(relation);
        if (route != null) {
            try {
                push(extract(current.getFrame(), route, node));
                display();
            } catch (QueryException e) {
                log.error("Unable to push page: %s", route.getPath(), e);
            }
            return;
        }

        // try a launch if no routes
        Launch launch = current.getLaunch(relation);
        if (launch == null) {
            return;
        }

        try {
            launcher.accept(applicationFrom(node, launch));
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Cannot launch %s",
                                                          relation.getField()),
                                            e);
        }
    }

    public void places() throws QueryException {
        push(new Context(frame, application.getRoot()));
    }

    public void setDisplay(BiConsumer<Context, JsonNode> display) {
        this.display = display;
    }

    public void setLauncher(Consumer<Universal> launcher) {
        this.launcher = launcher;
    }

    JsonNode evaluate() throws QueryException {
        return current().evaluate(endpoint);
    }

    private String appLauncherId() {
        WebTarget webTarget = endpoint.path(SPA_WSP)
                                      .path("meta");
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "AppLauncher");
        try {
            return GraphQlUtil.evaluate(webTarget,
                                        new QueryRequest("query q($name: String!) { lookup(name: $name) }",
                                                         variables))
                              .get("lookup")
                              .asText();
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
    }

    private JsonNode apply(JsonNode node, String path) {
        StringTokenizer tokens = new StringTokenizer(path, "/");
        JsonNode current = node;
        while (tokens.hasMoreTokens()) {
            node = current.get(tokens.nextToken());
            current = node;
        }
        return node;
    }

    private Context current() {
        return back.peek();
    }

    private Context extract(String workspace, Route route, JsonNode item) {
        Map<String, Object> variables = new HashMap<>();
        route.getExtract()
             .fields()
             .forEachRemaining(entry -> {
                 variables.put(entry.getKey(), apply(item, entry.getValue()
                                                                .asText()));
             });

        Page target = application.route(route.getPath());
        String frame = workspace;
        if (target.getFrame() != null) {
            frame = target.getFrame();
        } else if (route.getFrameBy() != null) {
            JsonNode routedFrame = apply(item, route.getFrameBy());
            if (routedFrame == null) {
                log.warn("Invalid routing frame by {} route: {}",
                         route.getFrameBy(), route.getPath());
            }
            frame = routedFrame.asText();
        }
        return new Context(frame, target, variables);
    }

    private void push(Context pageContext) throws QueryException {
        back.push(pageContext);
        forward.clear();
    }

    private Spa resolve(String application) {
        WebTarget webTarget = endpoint.path(SPA_WSP);
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", application);
        ObjectNode app;
        try {
            app = (ObjectNode) GraphQlUtil.evaluate(webTarget,
                                                    new QueryRequest(GET_APPLICATION_QUERY,
                                                                     variables))
                                          .get(SINGLE_PAGE_APPLICATION);
        } catch (QueryException e) {
            String msg = String.format("cannot resolve application: %s",
                                       application);
            log.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
        return new Spa(app);
    }
}
