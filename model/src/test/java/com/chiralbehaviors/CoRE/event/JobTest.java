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

package com.chiralbehaviors.CoRE.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.kernel.Bootstrap;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelImpl;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */
public class JobTest extends DatabaseTest {

    @Test
    public void testAttributes() throws Exception {
        Bootstrap bootstrap = new Bootstrap(connection);
        bootstrap.bootstrap();
        Kernel kernel = new KernelImpl(em);
        Agency core = kernel.getCore();
        Job job = new Job(core, core, kernel.getAnyProduct(),
                          kernel.getAnyProduct(), kernel.getAnyLocation(),
                          kernel.getAnyLocation(), core);
        em.persist(job);
        Attribute attr = new Attribute("foo", core);
        attr.setValueType(ValueType.TEXT);
        em.persist(attr);
        JobAttribute value = new JobAttribute(attr, core);
        value.setJob(job);
        em.persist(value);
        em.flush();
        em.clear();

        job = em.find(Job.class, job.getId());
        assertNotNull(job);
        assertNotNull(job.getAttributes());
        assertEquals(1, job.getAttributes().size());
    }
}
