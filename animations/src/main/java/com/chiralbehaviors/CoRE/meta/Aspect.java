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

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * An Aspect is the classifier of an networked ruleform within a network. The
 * network relation is A relationship B, where "relationship" is the aspect's
 * classifier and "B" is the aspect's classification.
 *
 * @author hhildebrand
 *
 */
public class Aspect<RuleForm extends ExistentialRuleform<RuleForm, ?>> {
    private final RuleForm     classification;
    private final Relationship classifier;

    /**
     * @param classifier
     * @param classification
     */
    public Aspect(Relationship classifier, RuleForm classification) {
        this.classification = classification;
        this.classifier = classifier;
    }

    public RuleForm getClassification() {
        return classification;
    }

    public Relationship getClassifier() {
        return classifier;
    }

    @Override
    public String toString() {
        return "Aspect [classifier=" + classifier + ", classification="
               + classification + "]";
    }
}
