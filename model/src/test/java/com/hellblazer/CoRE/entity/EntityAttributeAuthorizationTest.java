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
package com.hellblazer.CoRE.entity;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ValueType;
import com.hellblazer.CoRE.meta.Aspect;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public class EntityAttributeAuthorizationTest {
    private EntityManager        em;
    private EntityManagerFactory emf;

    @After
    public void closeEntityManager() {
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

        Resource resource = new Resource();
        resource.setName("Primordial Resource");
        resource.setDescription("Just ye olde time resource");
        resource.setUpdatedBy(resource);
        em.persist(resource);

        Relationship classification = new Relationship("My classification",
                                                       "A classification",
                                                       resource, true);
        em.persist(classification);
        Relationship inverse = new Relationship("inverse classification",
                                                "The inverse classification",
                                                resource, classification);
        em.persist(inverse);

        Entity classificationEntity = new Entity();
        classificationEntity.setName("Classification Entity");
        classificationEntity.setUpdatedBy(resource);
        em.persist(classificationEntity);

        Attribute authorizedAttribute = new Attribute();
        authorizedAttribute.setName("My classification");
        authorizedAttribute.setUpdatedBy(resource);
        authorizedAttribute.setValueType(ValueType.NUMERIC);
        em.persist(authorizedAttribute);

        model.getEntityModel().getAllowedValues(authorizedAttribute,
                                                new Aspect<Entity>(
                                                                   classification,
                                                                   classificationEntity));
        em.getTransaction().rollback();
    }
}
