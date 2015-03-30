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

import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Test;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * @author hhildebrand
 *
 */
public class KernelTest {

    private EntityManager        em;
    private EntityManagerFactory emf;

    @After
    public void cleanup() {
        if (em != null) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    public void testKernel() throws Exception {
        InputStream is = getClass().getResourceAsStream("/jpa.properties");
        Properties properties = new Properties();
        properties.load(is);
        emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                     properties);
        EntityManager loadEm = emf.createEntityManager();
        KernelUtil.clearAndLoadKernel(loadEm);
        loadEm.close();
        Model model = new ModelImpl(emf);
        Kernel kernel = model.getKernel();
        assertNotNull(kernel.getAnyAttribute());
        assertNotNull(kernel.getAnyProduct());
        assertNotNull(kernel.getAnyLocation());
        assertNotNull(kernel.getAnyRelationship());
        assertNotNull(kernel.getAnyAgency());
        assertNotNull(kernel.getAttribute());
        assertNotNull(kernel.getContains());
        assertNotNull(kernel.getCore());
        assertNotNull(kernel.getCoreAnimationSoftware());
        assertNotNull(kernel.getDeveloped());
        assertNotNull(kernel.getDevelopedBy());
        assertNotNull(kernel.getProduct());
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
        assertNotNull(kernel.getLocation());
        assertNotNull(kernel.getMapsToLocation());
        assertNotNull(kernel.getMemberOf());
        assertNotNull(kernel.getPropagationSoftware());
        assertNotNull(kernel.getPrototype());
        assertNotNull(kernel.getPrototypeOf());
        assertNotNull(kernel.getAgency());
        assertNotNull(kernel.getSameAttribute());
        assertNotNull(kernel.getSameRelationship());
        assertNotNull(kernel.getSpecialSystemAgency());
        assertNotNull(kernel.getVersionOf());
        assertNotNull(kernel.getCoreModel());
        assertNotNull(kernel.getCoreUser());
        assertNotNull(kernel.getPasswordHashAttribute());
        assertNotNull(kernel.getLoginAttribute());
        assertNotNull(kernel.getUnset());
        assertNotNull(kernel.getInverseSoftware());

        assertEquals(ValueType.TEXT,
                     kernel.getPasswordHashAttribute().getValueType());
        assertEquals(ValueType.TEXT, kernel.getLoginAttribute().getValueType());
    }
}
