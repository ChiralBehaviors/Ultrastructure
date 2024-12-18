/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;
import com.chiralbehaviors.CoRE.kernel.phantasm.Role;
import com.chiralbehaviors.CoRE.kernel.phantasm.coreUserProperties.CoreUserProperties;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.schema.GraphQLSchema;

/**
 * @author hhildebrand
 *
 */
public class CurrentUserTest extends AbstractGraphQLTest {
    private GraphQLSchema schema;

    @Before
    public void load() throws Exception {
        schema = new WorkspaceSchema().build(model.getWorkspaceModel()
                                                  .getScoped(WorkspaceAccessor.uuidOf(WellKnownObject.KERNEL_IRI))
                                                  .getWorkspace(),
                                             model, Collections.emptySet());
    }

    @Test
    public void testCurrentUser() throws Exception {
        String username = "bob@slack.com";
        String password = "give me food or give me slack";
        CoreUser bob = model.construct(CoreUser.class, ExistentialDomain.Agency,
                                       "Bob", "Test Dummy");
        Role myRole = model.construct(Role.class, ExistentialDomain.Agency,
                                      "My Role", "As a test dummy");
        bob.addRole(model.wrap(Role.class, model.getKernel()
                                                .getLoginRole()));
        CoreUserProperties properties = new CoreUserProperties();
        properties.setLogin(username);
        bob.set_Properties(properties);

        model.getAuthnModel()
             .create(bob, password.toCharArray());
        AuthorizedPrincipal principal = new AuthorizedPrincipal((Agency) bob.getRuleform());
        Map<String, Object> variables = new HashMap<>();
        ObjectNode result = model.executeAs(principal,
                                            () -> execute(schema,
                                                          "{ currentUser { id login } }",
                                                          variables));
        String id = result.get("currentUser")
                          .get("id")
                          .asText();
        assertNotNull(id);
        assertEquals(UuidUtil.encode(bob.getRuleform()
                                        .getId()),
                     id);
        variables.put("id", id);
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($id: ID!) { coreUser(id: $id) {login } }",
                                               variables));
        assertEquals(username, result.get("coreUser")
                                     .get("login")
                                     .asText());

        variables.put("id", UuidUtil.encode(model.getKernel()
                                                 .getLoginRole()
                                                 .getId()));
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($id: ID!) { hasRole(role: $id) }",
                                               variables));
        assertTrue(result.get("hasRole")
                         .asBoolean());

        variables.put("id", UuidUtil.encode(myRole.getRuleform()
                                                  .getId()));
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($id: ID!) { hasRole(role: $id) }",
                                               variables));
        assertFalse(result.get("hasRole")
                          .asBoolean());

        variables.put("roles",
                      Collections.singletonList(UuidUtil.encode(myRole.getRuleform()
                                                                      .getId())));
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($roles: [ID]!) { hasRoles(roles: $roles) }",
                                               variables));
        assertFalse(result.get("hasRoles")
                          .asBoolean());

        bob.addRole(myRole);

        variables.put("id", UuidUtil.encode(myRole.getRuleform()
                                                  .getId()));
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($id: ID!) { hasRole(role: $id) }",
                                               variables));
        assertTrue(result.get("hasRole")
                         .asBoolean());

        variables.put("roles",
                      Collections.singletonList(UuidUtil.encode(myRole.getRuleform()
                                                                      .getId())));
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($roles: [ID]!) { hasRoles(roles: $roles) }",
                                               variables));
        assertTrue(result.get("hasRoles")
                         .asBoolean());

        variables.put("perm", UuidUtil.encode(model.getKernel()
                                                   .getLOGIN_TO()
                                                   .getId()));
        variables.put("e", UuidUtil.encode(model.getCoreInstance()
                                                .getRuleform()
                                                .getId()));
        variables.put("roles",
                      Collections.singletonList(UuidUtil.encode(model.getKernel()
                                                                     .getLoginRole()
                                                                     .getId())));

        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($e: ID! $perm: ID! $roles: [ID]!) { authorizedIfActive(permission: $perm entity: $e roles: $roles) }",
                                               variables));
        assertTrue(result.get("authorizedIfActive")
                         .asBoolean());

        principal = new AuthorizedPrincipal((Agency) bob.getRuleform(),
                                            Collections.singletonList(model.getKernel()
                                                                           .getLoginRole()));

        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($e: ID! $perm: ID!) { authorized(permission: $perm entity: $e) }",
                                               variables));
        assertTrue(result.get("authorized")
                         .asBoolean());

        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($roles: [ID]!) { inRoles(roles: $roles) }",
                                               variables));
        assertTrue(result.get("inRoles")
                         .asBoolean());

        variables.put("old", password);
        variables.put("new", password + " or kill me");
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "mutation m($old: String! $new: String!) { updatePassword(oldPassword: $old newPassword: $new)}",
                                               variables));
        assertTrue(result.get("updatePassword")
                         .asBoolean());

    }
}
