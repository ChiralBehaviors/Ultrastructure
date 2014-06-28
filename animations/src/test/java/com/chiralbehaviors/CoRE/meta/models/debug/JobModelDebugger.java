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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.openjpa.persistence.QueryImpl;

import com.chiralbehaviors.CoRE.event.AbstractProtocol_;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
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

    @SuppressWarnings("unused")
    public Map<Protocol, List<String>> findMetaProtocolGaps(Job job) {
        List<MetaProtocol> metaProtocols = getMetaprotocols(job);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Map<Protocol, List<String>> gaps = new HashMap<>();
        for (Protocol protocol : getProtocolsFor(job.getService())) {
            for (MetaProtocol metaProtocol : metaProtocols) {
                List<Predicate> masks = new ArrayList<>();
                CriteriaQuery<Protocol> query = cb.createQuery(Protocol.class);
                Root<Protocol> protocolRoot = query.from(Protocol.class);

                Predicate assignToMask = mask(job.getDeliverFrom(),
                                              metaProtocol.getDeliverFrom(),
                                              AbstractProtocol_.deliverFrom, cb,
                                              query, protocolRoot);
            Subquery<Boolean> sq = query.subquery(Boolean.class);
                Selection<Boolean> subquery = query.subquery(Boolean.class).select(cb.literal(true)).where().alias("deliverFrom");
                query.multiselect(subquery);
                Query tq = em.createQuery("select 1=1 from");
                System.out.println(tq.unwrap(QueryImpl.class).getQueryString());
            }
        }

        return gaps;
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
        Map<Protocol, List<String>> gaps = new HashMap<>();
        for (Protocol p : getProtocolsFor(job.getService())) {
            gaps.put(p, findGaps(job, p));
        }
        return gaps;
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
        if (!job.getProduct().equals(p.getRequestedProduct())) {
            missingFields.add("Product");
        }
        if (!job.getDeliverTo().equals(p.getDeliverTo())) {
            missingFields.add("DeliverTo");
        }
        if (!job.getDeliverFrom().equals(p.getDeliverFrom())) {
            missingFields.add("DeliverFrom");
        }
        if (!job.getRequesterAttribute().equals(p.getRequesterAttribute())) {
            missingFields.add("RequesterAttribute");
        }
        if (!job.getProductAttribute().equals(p.getProductAttribute())) {
            missingFields.add("ProductAttribute");
        }
        if (!job.getDeliverToAttribute().equals(p.getDeliverToAttribute())) {
            missingFields.add("DeliverToAttribute");
        }
        if (!job.getDeliverFromAttribute().equals(p.getDeliverFromAttribute())) {
            missingFields.add("DeliverFromAttribute");
        }
        return missingFields;

    }

}
