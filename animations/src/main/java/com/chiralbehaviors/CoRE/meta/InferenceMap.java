/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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