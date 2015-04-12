/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.workspace;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public interface Workspace {

    <T extends Ruleform> T get(String key);

    <T> T getAccessor(Class<T> accessorInterface);

    <T extends Ruleform> List<T> getCollection(Class<T> ruleformClass);

    Product getDefiningProduct();

    Map<String, Product> getImports();

    WorkspaceScope getScope();

    WorkspaceSnapshot getSnapshot();

    void replaceFrom(EntityManager em);

    void retarget(EntityManager em);
}
