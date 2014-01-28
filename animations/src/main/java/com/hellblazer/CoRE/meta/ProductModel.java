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

package com.hellblazer.CoRE.meta;

import java.util.List;

import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductAttribute;
import com.hellblazer.CoRE.product.ProductAttributeAuthorization;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductAttributeAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductLocationAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductRelationshipAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductStatusCodeAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductUnitAccessAuthorization;

/**
 * @author hhildebrand
 * 
 */
public interface ProductModel
        extends
        NetworkedModel<Product, ProductNetwork, ProductAttributeAuthorization, ProductAttribute> {

    List<ProductAgencyAccessAuthorization> getAgencyAccessAuths(Product parent,
                                                                Relationship relationship);

    List<ProductAttributeAccessAuthorization> getAttributeAccessAuths(Product parent,
                                                                      Relationship relationship);

    List<ProductLocationAccessAuthorization> getLocationAccessAuths(Product parent,
                                                                    Relationship relationship);

    List<ProductRelationshipAccessAuthorization> getRelationshipAccessAuths(Product parent,
                                                                            Relationship relationship);

    List<ProductStatusCodeAccessAuthorization> getStatusCodeAccessAuths(Product parent,
                                                                        Relationship relationship);

    List<ProductUnitAccessAuthorization> getUnitAccessAuths(Product parent,
                                                            Relationship relationship);

}
