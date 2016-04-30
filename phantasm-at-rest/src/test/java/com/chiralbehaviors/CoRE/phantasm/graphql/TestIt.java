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

/**
 * @author hhildebrand
 *
 */
public class TestIt {

    @Test
    public void testIt() {
        String query = "mutation m ($service: String, $assignTo: String, $product: String, $deliverTo: String,           $deliverFrom: String, $requester: String) {   createJob(state: { service: $service, assignTo: $assignTo, product: $product,                      deliverTo: $deliverTo, deliverFrom: $deliverFrom, requester: $requester}) {       id, status {id, name} parent {id} product {name} service {name} requester {name} assignTo {name}       deliverFrom {name} deliverTo{name} quantity quantityUnit {name}       chronology {          id, job {id} status {id, name} product {name} service {name} requester {name} assignTo {name}           deliverFrom {name} deliverTo{name} quantity quantityUnit {name} updateDate sequenceNumber      }    } }";
        System.out.println(query.substring(153));
    }
}
