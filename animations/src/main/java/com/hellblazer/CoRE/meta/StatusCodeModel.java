/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

import java.util.Collection;
import java.util.List;

import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeAttribute;
import com.hellblazer.CoRE.event.status.StatusCodeAttributeAuthorization;
import com.hellblazer.CoRE.event.status.StatusCodeNetwork;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
import com.hellblazer.CoRE.product.Product;

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
