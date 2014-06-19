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
package com.chiralbehaviors.CoRE.meta.models.debug;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.JobModelImpl;

/**
 * @author hparry
 *
 */
public class JobModelDebugger extends JobModelImpl {

    /**
     * @param model
     */
    public JobModelDebugger(Model model) {
        super(model);
    }
    
    /**
     * @param job
     * @param p
     * @return
     */
    private List<String> findGaps(Job job, Protocol p) {
        List<String> missingFields = new LinkedList<>();
        if (!job.getRequester().equals(p.getRequester())) {
            missingFields.add("Requester");
        }
        if (!job.getProduct().equals(p.getProduct())) {
            missingFields.add("Product");
        }
        if (!job.getDeliverTo().equals(p.getDeliverTo())) {
            missingFields.add("DeliverTo");
        }
        if (!job.getDeliverFrom().equals(p.getDeliverFrom())) {
            missingFields.add("DeliverFrom");
        }
        return missingFields;

    }
    
    /**
     * Returns a map of all protocols that match job.service and a list of field
     * names specifying which fields on the protocol prevent the protocol from
     * being matched. An empty list means the protocol would be matched if a job
     * were inserted.
     * 
     * This method does not take metaprotocols into account.
     * 
     * @param job
     * @return
     */
    public Map<Protocol, List<String>> findProtocolGaps(Job job) {

        TypedQuery<Protocol> query = em.createNamedQuery(Protocol.GET_FOR_SERVICE,
                                                         Protocol.class);
        query.setParameter("requestedService", job.getService());
        List<Protocol> protocols = query.getResultList();
        Map<Protocol, List<String>> gaps = new HashMap<>();
        for (Protocol p : protocols) {
            gaps.put(p, findGaps(job, p));
        }
        return gaps;
    }

}
