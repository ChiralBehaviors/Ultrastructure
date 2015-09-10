/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.workspace;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.product.Product;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.StringArgGenerator;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public interface WorkspaceAccessor {
    public static final String KERNEL_URN         = "urn:uuid:00000000-0000-0004-0000-000000000003";
    final StringArgGenerator   URL_UUID_GENERATOR = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_URL);
    public static final String URN_UUID           = "urn:uuid:";

    static UUID uuidOf(String url) {
        if (url.startsWith(URN_UUID)) {
            return UUID.fromString(url.substring(URN_UUID.length()));
        }
        return URL_UUID_GENERATOR.generate(url);
    }

    void flushCache();

    <T extends Ruleform> T get(String key);

    <T> T getAccessor(Class<T> accessorInterface);

    <T extends Ruleform> List<T> getCollection(Class<T> ruleformClass);

    Product getDefiningProduct();

    Map<String, Tuple<Product, Integer>> getImports();

    List<String> getKeys();

    WorkspaceScope getScope();

    WorkspaceSnapshot getSnapshot();

    void replaceFrom(EntityManager em);

    void retarget(EntityManager em);
}
