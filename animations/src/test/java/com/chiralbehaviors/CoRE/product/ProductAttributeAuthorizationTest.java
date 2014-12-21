/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.product;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class ProductAttributeAuthorizationTest {
    private EntityManager        em;
    private EntityManagerFactory emf;

    @After
    public void closeEntityManager() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
        emf.close();
    }

    @Before
    public void initEntityManager() throws Exception {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/jpa.properties"));
        emf = Persistence.createEntityManagerFactory("CoRE", properties);
        em = emf.createEntityManager();
    }

    @Test
    public void testAllowedNumericValues() throws Exception {
        Model model = new ModelImpl(em, null);

        em.getTransaction().begin();

        Agency agency = new Agency();
        agency.setName("Primordial Agency");
        agency.setDescription("Just ye olde time agency");
        agency.setUpdatedBy(agency);
        em.persist(agency);

        Relationship classification = new Relationship("My classification",
                                                       "A classification",
                                                       agency, true);
        em.persist(classification);
        Relationship inverse = new Relationship("inverse classification",
                                                "The inverse classification",
                                                agency, classification);
        em.persist(inverse);

        Product classificationProduct = new Product();
        classificationProduct.setName("Classification Product");
        classificationProduct.setUpdatedBy(agency);
        em.persist(classificationProduct);

        Attribute authorizedAttribute = new Attribute();
        authorizedAttribute.setName("My classification");
        authorizedAttribute.setUpdatedBy(agency);
        authorizedAttribute.setValueType(ValueType.NUMERIC);
        em.persist(authorizedAttribute);

        model.getProductModel().getAllowedValues(authorizedAttribute,
                                                 new Aspect<Product>(
                                                         classification,
                                                         classificationProduct));
        em.getTransaction().rollback();
    }
}
