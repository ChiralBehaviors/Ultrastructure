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
package com.chiralbehaviors.CoRE.meta.models;

import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;

/**
 * A class full of utility methods to aid in debugging.
 *
 * @author hparry
 *
 */
public class TestDebuggingUtil {

    public static void printJobs(List<Job> jobs) {
        for (Job j : jobs) {
            System.out.println(String.format("%s: Status: %s, Parent: %s",
                                             j.getService().getName(),
                                             j.getStatus().getName(),
                                             j.getParent() != null ? j.getParent().getService().getName()
                                                                  : "null"));
        }
    }

    /**
     * @param findProtocolGaps
     */
    public static void printProtocolGaps(Map<Protocol, List<String>> gaps) {
        for (Map.Entry<Protocol, List<String>> e : gaps.entrySet()) {
            System.out.println(String.format("requestedService: %s, service: %s",
                                             e.getKey().getRequestedService().getName(),
                                             e.getKey().getService().getName()));
            System.out.println("Unmatched fields: ");
            for (String f : e.getValue()) {
                System.out.println(f);
            }
        }

    }

    public static void printProtocols(List<Protocol> protocols) {
        for (Protocol p : protocols) {
            System.out.println(String.format("Requested Service: %s, Service: %s",
                                             p.getRequestedService().getName(),
                                             p.getService()));
        }
    }

    public static void printSequencings(List<StatusCodeSequencing> seqs) {
        for (StatusCodeSequencing s : seqs) {
            System.out.println(String.format("%s: %s -> %s",
                                             s.getService().getName(),
                                             s.getParentCode().getName(),
                                             s.getChildCode().getName()));
        }
    }

}
