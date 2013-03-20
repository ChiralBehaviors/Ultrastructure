/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 * @author hhildebrand
 * 
 */

public class UtilTest {

    @Test
    public void testFilHashes() throws Exception {
        for (String[] pair : new String[][] {
                { "UtilTestFile.txt", "54870d01088953335250a7e2dbc87d5a" },
                { "EmptyFile.txt", "d41d8cd98f00b204e9800998ecf8427e" } }) {
            hash(getClass().getResourceAsStream(pair[0]), pair[1]);
        }
    }

    /**
     * Returns strings and their corresponding MD5 hashes.
     */
    @Test
    public void testStringHashes() throws Exception {
        for (String[] pair : new String[][] {
                { "SooperSeekritP@$$W0rD", "fb3cdfe07656952d6520b4a138e3fe6d" },
                { "test", "098f6bcd4621d373cade4e832627b4f6" },
                { "a", "0cc175b9c0f1b6a831c399e269772661" },
                { "aa", "4124bc0a9335c27f086f24ba207a4912" },
                { "aaa", "47bce5c74f589f4867dbd57e9ca9f808" },
                { "aaaa", "74b87337454200d4d33f80c4663dc5e5" } }) {
            hash(pair[0], pair[1]);
        }
    }

    protected void hash(InputStream is, String expectedHash) throws IOException {
        String actualHash = Util.md5Hash(is);
        assertEquals(expectedHash, actualHash);
    }

    protected void hash(String password, String expectedHash) throws Exception {

        // Get the hash...
        String actualHash = Util.md5Hash(password);

        // Sanity checks on the hashes
        if (expectedHash != null) {
            assertEquals("The expected hash needs to be 32 characters long",
                         expectedHash.length(), 32);
        }
        if (actualHash != null) {
            assertEquals("Improper length", actualHash.length(), 32);
        }

        assertEquals(expectedHash, actualHash);

    }
}
