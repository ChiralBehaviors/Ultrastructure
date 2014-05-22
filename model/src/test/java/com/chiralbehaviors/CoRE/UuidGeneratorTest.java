/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE;

import java.util.UUID;

import org.junit.Test;
import static junit.framework.Assert.*;

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