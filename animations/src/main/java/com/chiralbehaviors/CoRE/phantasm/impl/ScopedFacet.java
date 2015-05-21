/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.impl;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.annotations.Facet;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;

/**
 * @author hhildebrand
 *
 */
public interface ScopedFacet {
    static ScopedFacet from(Class<?> clazz) {
        return new ScopedFacet() {

            @Override
            public Ruleform resolveClassification(WorkspaceScope scope) {
                return scope.lookup("kernel", "IsA");
            }

            @Override
            public Ruleform resolveClassifier(WorkspaceScope scope) {
                return scope.lookup(clazz.getSimpleName());
            }

            @Override
            public String toClassificationString() {
                return clazz.getSimpleName();
            }

            @Override
            public String toClassifierString() {
                return "kernel:IsA";
            }
        };
    }

    static ScopedFacet from(Facet facet) {
        return new ScopedFacet() {

            @Override
            public Ruleform resolveClassification(WorkspaceScope scope) {
                return scope.lookup(facet.classifier());
            }

            @Override
            public Ruleform resolveClassifier(WorkspaceScope scope) {
                return scope.lookup(facet.classification());
            }

            @Override
            public String toClassificationString() {
                return String.format("%s:%s",
                                     facet.classifier().namespace(),
                                     facet.classifier().name());
            }

            @Override
            public String toClassifierString() {
                return String.format("%s:%s", facet.classification().namespace(),
                                     facet.classification().name());
            }
        };
    }

    Ruleform resolveClassification(WorkspaceScope scope);

    Ruleform resolveClassifier(WorkspaceScope scope);

    String toClassificationString();

    String toClassifierString();
}
