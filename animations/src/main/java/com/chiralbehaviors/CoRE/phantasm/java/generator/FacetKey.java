/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.java.generator;

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.FacetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.QualifiedNameContext;

/**
 * @author hhildebrand
 *
 */
public class FacetKey {
    private final ScopedName classification;
    private final ScopedName classifier;

    /**
     * @param facet
     */
    public FacetKey(FacetContext facet) {
        this(new ScopedName(facet.classifier),
             new ScopedName(facet.classification));
    }

    /**
     * @param authorizedRelationship
     * @param authorizedParent
     */
    public FacetKey(QualifiedNameContext authorizedRelationship,
                    QualifiedNameContext authorizedParent) {
        this(new ScopedName(authorizedRelationship),
             new ScopedName(authorizedParent));
    }

    public FacetKey(ScopedName classifier, ScopedName classification) {
        this.classification = classification;
        this.classifier = classifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FacetKey)) {
            return false;
        }
        FacetKey other = (FacetKey) obj;
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

    public ScopedName getClassification() {
        return classification;
    }

    public ScopedName getClassifier() {
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
        return String.format("%s : %s", classifier, classification);
    }
}
