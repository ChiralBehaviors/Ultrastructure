/**
 * Copyright (C) 2014 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.access.resource.ruleform.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.access.resource.ruleform.ExistentialRuleformResource;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.hellblazer.CoRE.meta.NetworkedModel;
import com.hellblazer.CoRE.network.NetworkRuleform;

/**
 * @author hparry
 * 
 */
public abstract class AbstractRuleformResource<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuthorization extends ClassifiedAttributeAuthorization<RuleForm>, AttributeType extends AttributeValue<RuleForm>>
        implements
        ExistentialRuleformResource<RuleForm, Network, AttributeAuthorization, AttributeType> {

    protected final NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType> model;
    protected final EntityManager                                                            em;

    /**
     * @param emf
     */
    public AbstractRuleformResource(EntityManager em,
                                    NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType> model) {
        this.em = em;
        this.model = model;
    }


    @Override 
    public List<RuleForm> getAll() throws ClassNotFoundException {
        return model.findAll();
    }

    @Override
    public RuleForm getResource(long id)
                                                         throws ClassNotFoundException {
        return model.find(id);
    }

    @Override
    public final long insert(RuleForm rf) {
        em.getTransaction().begin();
        Map<Ruleform, Ruleform> map = new HashMap<Ruleform, Ruleform>();
        rf.manageEntity(em, map);
        em.getTransaction().commit();
        em.refresh(rf);
        return rf.getId();
    }

}