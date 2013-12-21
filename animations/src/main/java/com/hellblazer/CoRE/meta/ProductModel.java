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

package com.hellblazer.CoRE.meta;

import java.util.List;

import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductAttribute;
import com.hellblazer.CoRE.product.ProductAttributeAuthorization;

/**
 * @author hhildebrand
 * 
 */
public interface ProductModel
        extends
        NetworkedModel<Product, ProductAttributeAuthorization, ProductAttribute> {

    /**
     * 
     * @param parent
     * @return
     */
    List<StatusCode> findStatusCodes(Product parent);

    /**
     * 
     * @param parent
     * @param relationship
     * @return
     */
    List<StatusCodeSequencing> findStatusCodeSequences(Product parent,
                                                       Relationship relationship);

}
