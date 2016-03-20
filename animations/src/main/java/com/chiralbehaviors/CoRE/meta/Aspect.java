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

import java.util.UUID;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;

/**
 * An Aspect is the classifier of an networked ruleform within a network. The
 * network relation is A relationship B, where "relationship" is the aspect's
 * classifier and "B" is the aspect's classification.
 *
 * @author hhildebrand
 *
 */
public class Aspect<RuleForm extends ExistentialRuleform> {
    private final UUID classification;
    private final UUID classifier;

    public Aspect(UUID classifier, UUID classification) {
        this.classification = classification;
        this.classifier = classifier;
    }

    public Aspect(Relationship classifier, ExistentialRuleform classification) {
        this(classifier.getId(), classification.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Aspect)) {
            return false;
        }
        Aspect<?> other = (Aspect<?>) obj;
        if (classification == null) {
            if (other.classification != null) {
                return false;
            }
        } else if (!classification.equals(other.classification)) {
            return false;
        }
        if (classifier == null) {
            if (other.classifier != null) {
                return false;
            }
        } else if (!classifier.equals(other.classifier)) {
            return false;
        }
        return true;
    }

    public UUID getClassification() {
        return classification;
    }

    public UUID getClassifier() {
        return classifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((classification == null) ? 0 : classification.hashCode());
        result = prime * result
                 + ((classifier == null) ? 0 : classifier.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Aspect [classifier=" + classifier + ", classification="
               + classification + "]";
    }
}
