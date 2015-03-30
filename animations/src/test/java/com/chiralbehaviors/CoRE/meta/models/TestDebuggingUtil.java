/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.meta.models;

import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
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
     * @param findMetaProtocolGaps
     */
    public static void printMetaProtocolGaps(Map<Protocol, Map<MetaProtocol, List<String>>> gaps) {
        for (Map.Entry<Protocol, Map<MetaProtocol, List<String>>> e : gaps.entrySet()) {
            System.out.println(String.format("requested service: %s, service: %s",
                                             e.getKey().getService().getName(),
                                             e.getKey().getService().getName()));

            for (Map.Entry<MetaProtocol, List<String>> mpe : e.getValue().entrySet()) {
                System.out.println(String.format("MetaProtocol: %s",
                                                 mpe.getKey().getId()));
                System.out.println("Unmatched fields: ");
                for (String f : mpe.getValue()) {
                    System.out.println(f);
                }
            }
        }

    }

    /**
     * @param findProtocolGaps
     */
    public static void printProtocolGaps(Map<Protocol, List<String>> gaps) {
        for (Map.Entry<Protocol, List<String>> e : gaps.entrySet()) {
            System.out.println(String.format("childService: %s, service: %s",
                                             e.getKey().getChildService().getName(),
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
                                             p.getChildService().getName(),
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
