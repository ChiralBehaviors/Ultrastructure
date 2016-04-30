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

package com.chiralbehaviors.CoRE.phantasm.authentication;

import java.util.Collections;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

/**
 * @author hhildebrand
 *
 */
public class NullAuthenticator
        implements Authenticator<String, AuthorizedPrincipal> {
    private Model model;

    @Override
    public Optional<AuthorizedPrincipal> authenticate(String credentials) throws AuthenticationException {
        return Optional.of(model.principalFrom(model.getKernel()
                                                    .getUnauthenticatedAgency(),
                                               Collections.emptyList()));
    }

    public void setModel(Model model) {
        this.model = model;
    }

}
