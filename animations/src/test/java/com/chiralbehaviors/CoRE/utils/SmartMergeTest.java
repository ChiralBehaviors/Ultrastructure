/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.utils;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.Test;

import com.chiralbehaviors.CoRE.job.Job;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;

/**
 * @author hhildebrand
 *
 */
public class SmartMergeTest extends AbstractModelTest {

    @Test
    public void testCircularity() {
        Job job = model.getJobModel()
                       .newInitializedJob(kernel.getAnyProduct(),
                                          kernel.getCore());
        job.setAssignTo(kernel.getCore());
        job.setProduct(kernel.getAnyProduct());
        job.setDeliverTo(kernel.getAnyLocation());
        job.setDeliverFrom(kernel.getAnyLocation());
        job.setRequester(kernel.getAnyAgency());
        Job merged = Util.smartMerge(em, job, new HashMap<>());
        assertNotNull(merged);
    }
}
