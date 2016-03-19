/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.meta;

import java.util.Collection;
import java.util.List;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;

/**
 * @author hhildebrand
 *
 */
public interface StatusCodeModel extends ExistentialModel<StatusCode> {

    /**
     * Answer the unique status codes associated with a service
     *
     * @param service
     * @return the unique status codes associated with a service
     */
    Collection<StatusCode> getStatusCodes(Product service);

    /**
     * Answer the list of {@link #StatusCodeSequencingRecord} that refer to a
     * service
     *
     * @param service
     * @return the list of {@link #StatusCodeSequencingRecord} that refer to a
     *         service
     */
    List<StatusCodeSequencingRecord> getStatusCodeSequencing(Product service);

    /**
     * Answer the list of {@link #StatusCodeSequencingRecord} that refer to the
     * service with the child {@link #ExistentialRecord}
     *
     * @param service
     * @param child
     * @return
     */
    List<StatusCodeSequencingRecord> getStatusCodeSequencingChild(Product service,
                                                                  StatusCode child);

    /**
     * Answer the list of {@link #StatusCodeSequencingRecord} that refer to the
     * child {@link #ExistentialRecord}
     *
     * @param child
     * @return
     */
    Collection<StatusCodeSequencingRecord> getStatusCodeSequencingChild(StatusCode child);

    /**
     * Answer the list of {@link #StatusCodeSequencingRecord} that refer to the
     * service with the parent {@link #ExistentialRecord}
     *
     * @param service
     * @param parent
     * @return the list of {@link #StatusCodeSequencingRecord} that refer to the
     *         service with the parent {@link #ExistentialRecord}
     */
    List<StatusCodeSequencingRecord> getStatusCodeSequencingParent(Product service,
                                                                   StatusCode parent);

    /**
     * Answer the list of {@link #StatusCodeSequencingRecord} that refer to the
     * parent {@link #ExistentialRecord}
     *
     * @param service
     * @return the list of {@link #StatusCodeSequencingRecord} that refer to the
     *         service with the parent {@link #ExistentialRecord}
     */
    List<StatusCodeSequencingRecord> getStatusCodeSequencingParent(StatusCode parent);

}
