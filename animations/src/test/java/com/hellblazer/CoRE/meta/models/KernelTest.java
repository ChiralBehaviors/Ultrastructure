/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

import com.hellblazer.CoRE.attribute.ValueType;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.WellKnownObject;
import com.hellblazer.CoRE.meta.BootstrapLoader;

/**
 * @author hhildebrand
 * 
 */
public class KernelTest {

    @Test
    public void testKernel() throws Exception {
        InputStream is = getClass().getResourceAsStream("/jpa.properties");
        Properties properties = new Properties();
        properties.load(is);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                                          properties);
        EntityManager em = emf.createEntityManager();
        BootstrapLoader loader = new BootstrapLoader(em);
        em.getTransaction().begin();
        loader.clear();
        em.getTransaction().commit();
        em.getTransaction().begin();
        loader.bootstrap();
        em.getTransaction().commit();

        Kernel kernel = new ModelImpl(em).getKernel();
        assertNotNull(kernel.getAnyAction());
        assertNotNull(kernel.getAnyAttribute());
        assertNotNull(kernel.getAnyProduct());
        assertNotNull(kernel.getAnyLocation());
        assertNotNull(kernel.getAnyRelationship());
        assertNotNull(kernel.getAnyResource());
        assertNotNull(kernel.getAnything());
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
        assertNotNull(kernel.getOriginalAction());
        assertNotNull(kernel.getOriginalAttribute());
        assertNotNull(kernel.getOriginalProduct());
        assertNotNull(kernel.getOriginalLocation());
        assertNotNull(kernel.getOriginalResource());
        assertNotNull(kernel.getPropagationSoftware());
        assertNotNull(kernel.getPrototype());
        assertNotNull(kernel.getPrototypeOf());
        assertNotNull(kernel.getResource());
        assertNotNull(kernel.getSameRelationship());
        assertNotNull(kernel.getSpecialSystemResource());
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
