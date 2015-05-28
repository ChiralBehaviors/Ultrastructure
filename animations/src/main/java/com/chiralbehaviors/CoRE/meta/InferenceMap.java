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

package com.chiralbehaviors.CoRE.meta;

/**
 * A map used in job transformations to determine which fields need to be
 * transformed.
 *
 * Service has INTENTIONALLY been omitted because it is never transformed.
 *
 * @author hhildebrand
 *
 */
public class InferenceMap {
    public final boolean assignTo, deliverFrom, deliverTo, product, requester,
            quantityUnit;

    public InferenceMap(boolean assignTo, boolean deliverFrom,
                        boolean deliverTo, boolean product, boolean requester,
                        boolean quantityUnit) {
        this.assignTo = assignTo;
        this.deliverFrom = deliverFrom;
        this.deliverTo = deliverTo;
        this.product = product;
        this.requester = requester;
        this.quantityUnit = quantityUnit;
    }

    @Override
    public String toString() {
        return String.format("TransformationMap [assignTo=%s, deliverFrom=%s, deliverTo=%s, product=%s, requester=%s, quantityUnit=%s]",
                             assignTo, deliverFrom, deliverTo, product,
                             requester, quantityUnit);
    }
}