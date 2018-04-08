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

package com.chiralbehaviors.CoRE.phantasm.authentication;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author hhildebrand
 *
 */
public class Credential {
    /**
     * Pointers to agency network auths that represent the asserted roles of the
     * principal
     */
    public List<UUID> roles = new ArrayList<>();

    /**
     * The remote ip address that is authorized for this credential
     */
    public String     ip;

    /**
     * The time to live for this credential, in milliseconds
     */
    public long       ttl   = TimeUnit.MINUTES.toMillis(30);

    /**
     *
     * @return true if the credential is valid at the current time, given the
     *         last updated timestamp of the credential
     */
    public boolean isValid(OffsetDateTime lastUpdated, OffsetDateTime currentTime) {
        return lastUpdated.toInstant()
                          .plusMillis(ttl)
                          .isAfter(currentTime.toInstant());
    }
}