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
    public final boolean assignTo, assignToAttribute, deliverFrom,
            deliverFromAttribute, deliverTo, deliverToAttribute, product,
            productAttribute, requester, requesterAttribute, serviceAttribute,
            quantityUnit;

    public InferenceMap(boolean assignTo, boolean assignToAttribute,
                        boolean deliverFrom, boolean deliverFromAttribute,
                        boolean deliverTo, boolean deliverToAttribute,
                        boolean product, boolean productAttribute,
                        boolean requester, boolean requesterAttribute,
                        boolean serviceAttribute, boolean quantityUnit) {
        this.assignTo = assignTo;
        this.assignToAttribute = assignToAttribute;
        this.deliverFrom = deliverFrom;
        this.deliverFromAttribute = deliverFromAttribute;
        this.deliverTo = deliverTo;
        this.deliverToAttribute = deliverToAttribute;
        this.product = product;
        this.productAttribute = productAttribute;
        this.requester = requester;
        this.requesterAttribute = requesterAttribute;
        this.serviceAttribute = serviceAttribute;
        this.quantityUnit = quantityUnit;
    }

    @Override
    public String toString() {
        return String.format("TransformationMap [assignTo=%s, assignToAttribute=%s, deliverFrom=%s, deliverFromAttribute=%s, deliverTo=%s, deliverToAttribute=%s, product=%s, productAttribute=%s, requester=%s, requesterAttribute=%s, serviceAttribute=%s, quantityUnit=%s]",
                             assignTo, assignToAttribute, deliverFrom,
                             deliverFromAttribute, deliverTo,
                             deliverToAttribute, product, productAttribute,
                             requester, requesterAttribute, serviceAttribute,
                             quantityUnit);
    }
}