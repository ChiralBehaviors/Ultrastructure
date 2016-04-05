/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
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

import java.util.Map;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;

/**
 * @author hhildebrand
 *
 */
public class AnyFacet implements Facet {

    private final Class<?> ruleform;

    public AnyFacet(Class<?> ruleform) {
        this.ruleform = ruleform;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.java.generator.Facet#getClassification()
     */
    @Override
    public ScopedName getClassification() {
        return new ScopedName(null, "ANY");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.java.generator.Facet#getClassifier()
     */
    @Override
    public ScopedName getClassifier() {
        return new ScopedName(null, "ANY");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.generator.Facet#getClassName()
     */
    @Override
    public String getClassName() {
        return String.format("Any%s", ruleform.getSimpleName());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.generator.Facet#getImport()
     */
    @Override
    public String getImport() {
        return String.format("com.chiralbehaviors.CoRE.phantasm.java.any.Any%s",
                             ruleform.getSimpleName());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.generator.Facet#getPackageName()
     */
    @Override
    public String getPackageName() {
        return ruleform.getPackage()
                       .getName();
    }

    @Override
    public String getParameterName() {
        return String.format("any%s", ruleform.getSimpleName());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.java.generator.Facet#getRuleformClass()
     */
    @Override
    public String getRuleformType() {
        return ruleform.getSimpleName();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.java.generator.Facet#getWorkspace()
     */
    @Override
    public String getUri() {
        return WellKnownObject.KERNEL_IRI;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.generator.Facet#resolve(java.util.Map, com.chiralbehaviors.CoRE.workspace.dsl.WorkspacePresentation, java.util.Map)
     */
    @Override
    public void resolve(Map<FacetKey, Facet> facets,
                        WorkspacePresentation presentation,
                        Map<ScopedName, MappedAttribute> mapped) {
        throw new UnsupportedOperationException("Should never be called");
    }
}
