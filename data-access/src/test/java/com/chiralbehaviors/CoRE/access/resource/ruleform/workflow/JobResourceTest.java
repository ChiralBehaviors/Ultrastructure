/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.access.resource.ruleform.workflow;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobAttribute;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;

/**
 * @author hparry
 * 
 */
public class JobResourceTest extends AbstractModelTest {

    private JobResource resource;

    @Before
    public void initialize() throws Exception {
        resource = new JobResource(model);
    }

    @Test
    public void testInsertAttributableJob() {
        em.getTransaction().begin();
        Attribute attr = new Attribute("attribute", null, kernel.getCore());
        attr.setValueType(ValueType.TEXT);
        em.persist(attr);
        em.getTransaction().commit();
        em.refresh(attr);
        Job job = new Job(kernel.getCore(), kernel.getCore(),
                          kernel.getAnyProduct(), kernel.getAnyProduct(),
                          kernel.getAnyLocation(), kernel.getAnyLocation(),
                          kernel.getCore());
        JobAttribute jobAttr = new JobAttribute(attr, "foo", kernel.getCore());
        jobAttr.setJob(job);
        Map<String, JobAttribute> map = new HashMap<>();
        map.put(jobAttr.getAttribute().getName(), jobAttr);
        AttributedJob attributedJob = new AttributedJob(job, map);
        job = resource.insertJob(attributedJob);
        assertNotNull(job.getId());
    }

}
