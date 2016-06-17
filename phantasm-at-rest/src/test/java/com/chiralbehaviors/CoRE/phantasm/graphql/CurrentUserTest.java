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
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.Role;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
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
                                             model,
                                             getClass().getClassLoader());
    }

    @Test
    public void testCurrentUser() throws Exception {
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        CoreUser bob = model.construct(CoreUser.class, ExistentialDomain.Agency,
                                       "Bob", "Test Dummy");
        Role myRole = model.construct(Role.class, ExistentialDomain.Agency,
                                      "My Role", "As a test dummy");
        bob.addRole(model.wrap(Role.class, model.getKernel()
                                                .getLoginRole()));
        bob.setLogin(username);
        bob.setPasswordRounds(10);
        AgencyBasicAuthenticator.resetPassword(bob, password);
        AuthorizedPrincipal principal = new AuthorizedPrincipal((Agency) bob.getRuleform());
        Map<String, Object> variables = new HashMap<>();
        ObjectNode result = model.executeAs(principal,
                                            () -> execute(schema,
                                                          "{ currentUser { id name } }",
                                                          variables));
        String id = result.get("currentUser")
                          .get("id")
                          .asText();
        assertNotNull(id);
        assertEquals(bob.getRuleform()
                        .getId()
                        .toString(),
                     id);
        variables.put("id", id);
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($id: String!) { coREUser(id: $id) {name } }",
                                               variables));
        assertEquals(principal.getPrincipal()
                              .getName(),
                     result.get("coREUser")
                           .get("name")
                           .asText());

        variables.put("id", model.getKernel()
                                 .getLoginRole()
                                 .getId());
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($id: String!) { hasRole(role: $id) }",
                                               variables));
        assertTrue(result.get("hasRole")
                         .asBoolean());

        variables.put("id", myRole.getRuleform()
                                  .getId());
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($id: String!) { hasRole(role: $id) }",
                                               variables));
        assertFalse(result.get("hasRole")
                          .asBoolean());

        variables.put("roles", Collections.singletonList(myRole.getRuleform()
                                                               .getId()));
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($roles: [String]!) { hasRoles(roles: $roles) }",
                                               variables));
        assertFalse(result.get("hasRoles")
                          .asBoolean());

        bob.addRole(myRole);

        variables.put("id", myRole.getRuleform()
                                  .getId());
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($id: String!) { hasRole(role: $id) }",
                                               variables));
        assertTrue(result.get("hasRole")
                         .asBoolean());

        variables.put("roles", Collections.singletonList(myRole.getRuleform()
                                                               .getId()));
        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($roles: [String]!) { hasRoles(roles: $roles) }",
                                               variables));
        assertTrue(result.get("hasRoles")
                         .asBoolean());

        variables.put("perm", model.getKernel()
                                   .getLOGIN_TO()
                                   .getId()
                                   .toString());
        variables.put("e", model.getCoreInstance()
                                .getRuleform()
                                .getId()
                                .toString());
        variables.put("roles", Collections.singletonList(model.getKernel()
                                                              .getLoginRole()
                                                              .getId()));

        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($e: String! $perm: String! $roles: [String]!) { authorizedIfActive(permission: $perm entity: $e roles: $roles) }",
                                               variables));
        assertTrue(result.get("authorizedIfActive")
                         .asBoolean());

        principal = new AuthorizedPrincipal((Agency) bob.getRuleform(),
                                            Collections.singletonList(model.getKernel()
                                                                           .getLoginRole()));

        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($e: String! $perm: String!) { authorized(permission: $perm entity: $e) }",
                                               variables));
        assertTrue(result.get("authorized")
                         .asBoolean());

        result = model.executeAs(principal,
                                 () -> execute(schema,
                                               "query m($roles: [String]!) { inRoles(roles: $roles) }",
                                               variables));
        assertTrue(result.get("inRoles")
                         .asBoolean());

    }
}
