/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ValueType;

/**
 * @author hhildebrand
 *
 */
public class KernelTest extends AbstractModelTest {

    @Test
    public void testKernel() throws Exception {
        assertNotNull(model.getKernel()
                           .getAnyAttribute());
        assertNotNull(model.getKernel()
                           .getAnyProduct());
        assertNotNull(model.getKernel()
                           .getAnyLocation());
        assertNotNull(model.getKernel()
                           .getAnyRelationship());
        assertNotNull(model.getKernel()
                           .getAnyAgency());
        assertNotNull(model.getKernel()
                           .getContains());
        assertNotNull(model.getKernel()
                           .getCore());
        assertNotNull(model.getKernel()
                           .getCoreAnimationSoftware());
        assertNotNull(model.getKernel()
                           .getDeveloped());
        assertNotNull(model.getKernel()
                           .getDevelopedBy());
        assertNotNull(model.getKernel()
                           .getEquals());
        assertNotNull(model.getKernel()
                           .getFormerMemberOf());
        assertNotNull(model.getKernel()
                           .getGreaterThan());
        assertNotNull(model.getKernel()
                           .getGreaterThanOrEqual());
        assertNotNull(model.getKernel()
                           .getHadMember());
        assertNotNull(model.getKernel()
                           .getHasException());
        assertNotNull(model.getKernel()
                           .getHasHead());
        assertNotNull(model.getKernel()
                           .getHasMember());
        assertNotNull(model.getKernel()
                           .getHasVersion());
        assertNotNull(model.getKernel()
                           .getHeadOf());
        assertNotNull(model.getKernel()
                           .getIncludes());
        assertNotNull(model.getKernel()
                           .getIsA());
        assertNotNull(model.getKernel()
                           .getIsContainedIn());
        assertNotNull(model.getKernel()
                           .getIsExceptionTo());
        assertNotNull(model.getKernel()
                           .getIsLocationOf());
        assertNotNull(model.getKernel()
                           .getLessThan());
        assertNotNull(model.getKernel()
                           .getLessThanOrEqual());
        assertNotNull(model.getKernel()
                           .getMapsToLocation());
        assertNotNull(model.getKernel()
                           .getMemberOf());
        assertNotNull(model.getKernel()
                           .getPropagationSoftware());
        assertNotNull(model.getKernel()
                           .getPrototype());
        assertNotNull(model.getKernel()
                           .getPrototypeOf());
        assertNotNull(model.getKernel()
                           .getSameAttribute());
        assertNotNull(model.getKernel()
                           .getSameRelationship());
        assertNotNull(model.getKernel()
                           .getSpecialSystemAgency());
        assertNotNull(model.getKernel()
                           .getVersionOf());
        assertNotNull(model.getKernel()
                           .getCoreModel());
        assertNotNull(model.getKernel()
                           .getCoreUser());
        assertNotNull(model.getKernel()
                           .getPasswordHash());
        assertNotNull(model.getKernel()
                           .getLogin());
        assertNotNull(model.getKernel()
                           .getUnset());
        assertNotNull(model.getKernel()
                           .getInverseSoftware());

        assertEquals(ValueType.Text, model.getKernel()
                                          .getPasswordHash()
                                          .getValueType());
        assertEquals(ValueType.Text, model.getKernel()
                                          .getLogin()
                                          .getValueType());
    }
}
