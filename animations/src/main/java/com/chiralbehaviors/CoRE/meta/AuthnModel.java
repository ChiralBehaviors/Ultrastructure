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

package com.chiralbehaviors.CoRE.meta;

import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.tables.records.TokenRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;

/**
 * @author halhildebrand
 *
 */
public interface AuthnModel {
    boolean authenticate(CoreUser user, char[] password);

    TokenRecord authenticate(CoreUser user, UUID token, String ip);

    boolean changePassword(CoreUser user, char[] oldPassword,
                           char[] newPassword);

    boolean create(CoreUser user, char[] password);

    TokenRecord mintToken(CoreUser user, String ip, int ttlSeconds, UUID nonce,
                          UUID[] roles);
}
