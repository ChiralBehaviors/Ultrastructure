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

package com.chiralbehaviors.CoRE.phantasm.resources;

import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing2;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

/**
 * @author hhildebrand
 *
 */
public class ResourcesTest extends ThingWorkspaceTest {
    protected final static PhantasmApplication application = new PhantasmApplication();

    @BeforeClass
    public static void initialize() throws Exception {
        application.run("server", "target/test-classes/test.yml");
    }

    @AfterClass
    public static void shutdown() {
        application.stop();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFacetNode() throws Exception {
        URL url;
        Map<String, Object> jsonObject;
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "test", "testy");
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "tester", "testier");
        thing1.setAliases(new String[] { "smith", "jones" });
        thing1.setURI("http://example.com");
        thing1.setThing2(thing2);
        JsonLdOptions options = new JsonLdOptions();
        url = new URL(String.format("http://localhost:%s/json-ld/facet/Product/%s/%s/%s",
                                    application.getPort(),
                                    ((ExistentialRecord) scope.lookup("kernel",
                                                                      "IsA")).getId()
                                                                             .toString(),
                                    ((ExistentialRecord) scope.lookup("Thing1")).getId()
                                                                                .toString(),
                                    thing1.getRuleform()
                                          .getId()));
        jsonObject = (Map<String, Object>) JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        URL contextUrl = new URL((String) jsonObject.get(Constants.CONTEXT));
        Map<String, Object> context = (Map<String, Object>) JsonUtils.fromInputStream(contextUrl.openStream());
        JsonLdProcessor.normalize(jsonObject, options);
        JsonLdProcessor.compact(jsonObject, context, options);
        JsonLdProcessor.flatten(jsonObject, context, options);
        JsonLdProcessor.expand(jsonObject, options);
    }

    @Test
    public void testLookupWorkspace() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/workspace/%s",
                                        application.getPort(),
                                        scope.getWorkspace()
                                             .getDefiningProduct()
                                             .getId()
                                             .toString()));
        Map<?, ?> jsonObject = (Map<?, ?>) JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject.get("ruleforms"));
        assertNotNull(jsonObject.get("frontier"));
    }

    @Test
    public void testRuleformContext() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/ruleform/Attribute/context",
                                        application.getPort()));
        JsonUtils.fromInputStream(url.openStream());
    }

    @Test
    public void testRuleformNode() throws Exception {
        URL url;
        Object jsonObject;
        url = new URL(String.format("http://localhost:%s/json-ld/ruleform/Attribute/%s",
                                    application.getPort(),
                                    ((ExistentialRecord) scope.lookup("URI")).getId()
                                                                             .toString()));
        jsonObject = JsonUtils.fromInputStream(url.openStream());
        assertNotNull(jsonObject);
        JsonLdOptions options = new JsonLdOptions(String.format("http://localhost:%s/",
                                                                application.getPort()));
        JsonLdProcessor.normalize(jsonObject, options);
        JsonLdProcessor.compact(jsonObject, new HashMap<>(), options);
        JsonLdProcessor.flatten(jsonObject, new HashMap<>(), options);
        JsonLdProcessor.expand(jsonObject, options);
    }

    @Test
    public void testRuleforms() throws Exception {
        URL url = new URL(String.format("http://localhost:%s/json-ld/ruleform",
                                        application.getPort()));
        JsonUtils.fromInputStream(url.openStream());
    }
}
