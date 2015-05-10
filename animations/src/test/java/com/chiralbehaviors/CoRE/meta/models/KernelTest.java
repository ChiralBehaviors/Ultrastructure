/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.chiralbehaviors.CoRE.attribute.ValueType;

/**
 * @author hhildebrand
 *
 */
public class KernelTest extends AbstractModelTest {

    @Test
    public void testKernel() throws Exception {
        assertNotNull(kernel.getAnyAttribute());
        assertNotNull(kernel.getAnyProduct());
        assertNotNull(kernel.getAnyLocation());
        assertNotNull(kernel.getAnyRelationship());
        assertNotNull(kernel.getAnyAgency());
        assertNotNull(kernel.getContains());
        assertNotNull(kernel.getCore());
        assertNotNull(kernel.getCoreAnimationSoftware());
        assertNotNull(kernel.getDeveloped());
        assertNotNull(kernel.getDevelopedBy());
        assertNotNull(kernel.getEquals());
        assertNotNull(kernel.getFormerMemberOf());
        assertNotNull(kernel.getGreaterThan());
        assertNotNull(kernel.getGreaterThanOrEqual());
        assertNotNull(kernel.getHadMember());
        assertNotNull(kernel.getHasException());
        assertNotNull(kernel.getHasHead());
        assertNotNull(kernel.getHasMember());
        assertNotNull(kernel.getHasVersion());
        assertNotNull(kernel.getHeadOf());
        assertNotNull(kernel.getIncludes());
        assertNotNull(kernel.getIsA());
        assertNotNull(kernel.getIsContainedIn());
        assertNotNull(kernel.getIsExceptionTo());
        assertNotNull(kernel.getIsLocationOf());
        assertNotNull(kernel.getLessThan());
        assertNotNull(kernel.getLessThanOrEqual());
        assertNotNull(kernel.getMapsToLocation());
        assertNotNull(kernel.getMemberOf());
        assertNotNull(kernel.getPropagationSoftware());
        assertNotNull(kernel.getPrototype());
        assertNotNull(kernel.getPrototypeOf());
        assertNotNull(kernel.getSameAttribute());
        assertNotNull(kernel.getSameRelationship());
        assertNotNull(kernel.getSpecialSystemAgency());
        assertNotNull(kernel.getVersionOf());
        assertNotNull(kernel.getCoreModel());
        assertNotNull(kernel.getCoreUser());
        assertNotNull(kernel.getPasswordHash());
        assertNotNull(kernel.getLogin());
        assertNotNull(kernel.getUnset());
        assertNotNull(kernel.getInverseSoftware());

        assertEquals(ValueType.TEXT, kernel.getPasswordHash().getValueType());
        assertEquals(ValueType.TEXT, kernel.getLogin().getValueType());
    }
}
