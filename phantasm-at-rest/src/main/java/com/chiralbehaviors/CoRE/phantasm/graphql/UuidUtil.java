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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * @author halhildebrand
 *
 */
public class UuidUtil {
    public static UUID decode(String encoded) {
        ByteBuffer bb = ByteBuffer.wrap(Base64.getUrlDecoder()
                                              .decode(encoded));
        return new UUID(bb.getLong(), bb.getLong());
    }

    public static String encode(UUID uuid) {
        byte[] bytes = ByteBuffer.allocate(16)
                                 .putLong(uuid.getMostSignificantBits())
                                 .putLong(uuid.getLeastSignificantBits())
                                 .array();
        return Base64.getUrlEncoder()
                     .withoutPadding()
                     .encodeToString(bytes);
    }
}