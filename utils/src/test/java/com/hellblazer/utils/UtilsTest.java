/** (C) Copyright 2014 Hal Hildebrand, All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.hellblazer.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * @author hhildebrand
 * 
 */
public class UtilsTest {
    @Test
    public void testRelativeFile() {
        File parent = new File("/this/is/an/absolute/path/to/the/location");
        File child = new File("/this/is/an/absolute/path/to/the/location/of/my/file");
        File relative = Utils.relativize(parent, child);
        assertEquals("of/my/file", relative.getPath());
    }
}
