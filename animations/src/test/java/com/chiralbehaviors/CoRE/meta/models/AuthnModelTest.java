/**
 * Copyright (c) 2018 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;
import com.chiralbehaviors.CoRE.meta.AuthnModel;

/**
 * @author halhildebrand
 *
 */
public class AuthnModelTest extends AbstractModelTest {

    @Test
    public void testIt() throws Exception {
        model.create()
             .configuration()
             .connectionProvider()
             .acquire();
        Kernel kernel = model.getKernel();
        model.getPhantasmModel()
             .getFacetDeclaration(kernel.getIsA(), kernel.getCoreUser());
        CoreUser user = model.construct(CoreUser.class,
                                        ExistentialDomain.Agency, "foo", "bar");
        AuthnModel authnModel = model.getAuthnModel();
        char[] password = "barNone".toCharArray();
        assertTrue(authnModel.create(user, password));
        assertTrue(authnModel.authenticate(user, password));
        char[] newPassword = "barSome".toCharArray();
        assertTrue(authnModel.changePassword(user, password, newPassword));
        assertFalse(authnModel.authenticate(user, password));
        assertTrue(authnModel.authenticate(user, newPassword));
    }
}
