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

import java.util.Collection;
import java.util.List;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttribute;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttributeAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public interface StatusCodeModel
        extends
        NetworkedModel<StatusCode, StatusCodeNetwork, StatusCodeAttributeAuthorization, StatusCodeAttribute> {

    /**
     * Answer the unique status codes associated with a service
     * 
     * @param service
     * @return the unique status codes associated with a service
     */
    Collection<StatusCode> getStatusCodes(Product service);

    /**
     * Answer the list of {@link #StatusCodeSequencing} that refer to a service
     * 
     * @param service
     * @return the list of {@link #StatusCodeSequencing} that refer to a service
     */
    List<StatusCodeSequencing> getStatusCodeSequencing(Product service);

    /**
     * Answer the list of {@link #StatusCodeSequencing} that refer to the
     * service with the child {@link #StatusCode}
     * 
     * @param service
     * @param child
     * @return
     */
    List<StatusCodeSequencing> getStatusCodeSequencingChild(Product service,
                                                            StatusCode child);

    /**
     * Answer the list of {@link #StatusCodeSequencing} that refer to the child
     * {@link #StatusCode}
     * 
     * @param child
     * @return
     */
    Collection<StatusCodeSequencing> getStatusCodeSequencingChild(StatusCode child);

    /**
     * Answer the list of {@link #StatusCodeSequencing} that refer to the
     * service with the parent {@link #StatusCode}
     * 
     * @param service
     * @param parent
     * @return the list of {@link #StatusCodeSequencing} that refer to the
     *         service with the parent {@link #StatusCode}
     */
    List<StatusCodeSequencing> getStatusCodeSequencingParent(Product service,
                                                             StatusCode parent);

    /**
     * Answer the list of {@link #StatusCodeSequencing} that refer to the parent
     * {@link #StatusCode}
     * 
     * @param service
     * @return the list of {@link #StatusCodeSequencing} that refer to the
     *         service with the parent {@link #StatusCode}
     */
    List<StatusCodeSequencing> getStatusCodeSequencingParent(StatusCode parent);

}
