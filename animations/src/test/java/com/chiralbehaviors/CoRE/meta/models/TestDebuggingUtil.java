/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.meta.models;

import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;

/**
 * A class full of utility methods to aid in debugging.
 *
 * @author hparry
 *
 */
public class TestDebuggingUtil {

    public static void printJobs(List<JobRecord> jobs) {
        for (JobRecord j : jobs) {
            System.out.println(String.format("%s: Status: %s, Parent: %s",
                                             j.getService(), j.getStatus(),
                                             j.getParent() != null ? j.getParent()
                                                                   : "null"));
        }
    }

    /**
     * @param findMetaProtocolGaps
     */
    public static void printMetaProtocolGaps(Map<ProtocolRecord, Map<MetaProtocolRecord, List<String>>> gaps) {
        for (Map.Entry<ProtocolRecord, Map<MetaProtocolRecord, List<String>>> e : gaps.entrySet()) {
            System.out.println(String.format("requested service: %s, service: %s",
                                             e.getKey()
                                              .getService(),
                                             e.getKey()
                                              .getService()));

            for (Map.Entry<MetaProtocolRecord, List<String>> mpe : e.getValue()
                                                                    .entrySet()) {
                System.out.println(String.format("MetaProtocol: %s",
                                                 mpe.getKey()
                                                    .getId()));
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
    public static void printProtocolGaps(Map<ProtocolRecord, List<String>> gaps) {
        for (Map.Entry<ProtocolRecord, List<String>> e : gaps.entrySet()) {
            System.out.println(String.format("childService: %s, service: %s",
                                             e.getKey()
                                              .getChildService(),
                                             e.getKey()
                                              .getService()));
            System.out.println("Unmatched fields: ");
            for (String f : e.getValue()) {
                System.out.println(f);
            }
        }

    }

    public static void printProtocols(List<ProtocolRecord> protocols) {
        for (ProtocolRecord p : protocols) {
            System.out.println(String.format("Requested Service: %s, Service: %s",
                                             p.getChildService(),
                                             p.getService()));
        }
    }

    public static void printSequencings(List<StatusCodeSequencingRecord> seqs) {
        for (StatusCodeSequencingRecord s : seqs) {
            System.out.println(String.format("%s: %s -> %s", s.getService(),
                                             s.getParent(), s.getChild()));
        }
    }

}
