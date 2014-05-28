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
package com.chiralbehaviors.CoRE.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.chiralbehaviors.CoRE.attribute.Attribute;

/**
 * @author hparry
 * 
 */
public class AttributedJob {

    private Map<Attribute, JobAttribute> attributes;
    private Job                          job;

    public AttributedJob() {
    }

    public AttributedJob(Job job) {
        this.job = job;
        attributes = new HashMap<>();
        Set<JobAttribute> attrs = job.getAttributes();
        if (attrs != null) {
            for (JobAttribute value : attrs) {
                attributes.put(value.getAttribute(), value);
            }
        }
    }

    /**
     * @return the attributes
     */
    public Map<Attribute, JobAttribute> getAttributes() {
        return attributes;
    }

    /**
     * @return the job
     */
    public Job getJob() {
        return job;
    }

    public JobAttribute getValue(Attribute attribute) {
        return attributes.get(attribute);
    }

}
