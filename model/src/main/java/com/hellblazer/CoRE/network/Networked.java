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
package com.hellblazer.CoRE.network;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public interface Networked<E extends Networked<E, N>, N extends NetworkRuleform<E>> {

    void addChildRelationship(N relationship);

    void addParentRelationship(N relationship);

    List<N> getImmediateChildren(EntityManager em);

    String getName();

    Set<N> getNetworkByChild();

    Set<N> getNetworkByParent();

    void link(Relationship r, E child, Resource updatedBy,
              Resource inverseSoftware, EntityManager em);

    void setNetworkByChild(Set<N> theNetworkByChild);

    void setNetworkByParent(Set<N> theNetworkByParent);

}