/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

/**
 * @author hhildebrand
 *
 */
public class UuidGeneratorTest {

    @Test
    public void testBase64Conversion() {
        UUID id = UUID.randomUUID();
        String converted = UuidGenerator.toBase64(id);
        assertEquals(id, UuidGenerator.fromBase64(converted));
    }

    @Test
    public void testBase64Ordering() {
        UUID id = new UUID(0, 1);
        String converted = "AAAAAAAAAAAAAAAAAAAAAQ";
        assertEquals(id, UuidGenerator.fromBase64(converted));
    }
}
