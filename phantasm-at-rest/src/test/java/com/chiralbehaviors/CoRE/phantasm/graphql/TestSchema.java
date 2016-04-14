/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.graphql;

import org.junit.Test;

import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Job;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.JobChronology;

import graphql.annotations.GraphQLAnnotations;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;

/**
 * @author hhildebrand
 *
 */
public class TestSchema {

    @Test
    public void testIt() throws Exception {
        GraphQLInterfaceType existential = GraphQLAnnotations.iface(Existential.class);
        GraphQLObjectType objectType = GraphQLAnnotations.object(Existential.class);
        GraphQLObjectType jobType = GraphQLAnnotations.object(Job.class);
        GraphQLObjectType jobChronologyType = GraphQLAnnotations.object(JobChronology.class);

        System.out.println(existential);
        System.out.println(objectType);
        System.out.println(jobType);
        System.out.println(jobChronologyType);
    }

    @Test
    public void testY() {
        int index = 344;
        System.out.println("mutation m ($service: String!, $assignTo: String, $product: String, $deliverTo: String,           $deliverFrom: String, $requester: String) {   CreateJob(state: { setService: $service, setAssignTo: $assignTo, setProduct: $product,                      setDeliverTo: $deliverTo, setDeliverFrom: $deliverFrom, setRequester: $requester}) {       id, status {id, name} parent {id} product {name} service {name} requester {name} assignTo {name}       deliverFrom {name} deliverTo{name} quantity quantityUnit {name}       chronology {          id, job {id} status {id, name} product {name} service {name} requester {name} assignTo {name}           deliverFrom {name} deliverTo{name} quantity quantityUnit {name} updateDate sequenceNumber      }    } }".substring(index));
    }
}
