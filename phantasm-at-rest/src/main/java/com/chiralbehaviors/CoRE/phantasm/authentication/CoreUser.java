/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.annotations.Facet;
import com.chiralbehaviors.CoRE.annotations.Key;
import com.chiralbehaviors.CoRE.annotations.State;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.bcrypt.BCrypt;

/**
 * @author hhildebrand
 *
 */
@State(facets = { @Facet(classifier = @Key(name = "IsA") , classification = @Key(name = "CoreUser") ) }, workspace = "urn:uuid:00000000-0000-0004-0000-000000000003")
public interface CoreUser extends Phantasm<Agency> {

    default boolean authenticate(String password) {
        return BCrypt.checkpw(password, getPasswordHash());
    }

    @Key(name = "PasswordHash")
    String getPasswordHash();

    @Key(name = "PasswordRounds")
    int getPasswordRounds();

    @Key(name = "LoginA")
    String getUserName();

    default void resetPassword(String newPassword) {
        setPasswordHash(BCrypt.hashpw(newPassword,
                                      BCrypt.gensalt(getPasswordRounds())));
    }

    @Key(name = "PasswordHash")
    void setPasswordHash(String hash);

    @Key(name = "PasswordRounds")
    void setPasswordRounds(int rounds);

    @Key(name = "Login")
    void setUserName(String username);

    default void updatePassword(String newPassword, String oldPassword) {
        if (BCrypt.checkpw(oldPassword, getPasswordHash())) {
            setPasswordHash(BCrypt.hashpw(newPassword,
                                          BCrypt.gensalt(getPasswordRounds())));
        }
    }
}
