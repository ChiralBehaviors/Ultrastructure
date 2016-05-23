/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.ocular.diagram;

import static de.fxdiagram.core.extensions.CoreExtensions.getRoot;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.fxdiagram.mapping.IMappedElementDescriptor;
import de.fxdiagram.mapping.shapes.BaseDiagram;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceDiagram extends BaseDiagram<ObjectNode> {

    public WorkspaceDiagram() {
        super();
    }

    public WorkspaceDiagram(IMappedElementDescriptor<ObjectNode> domainObjectDescriptor) {
        super(domainObjectDescriptor);
    }

    @Override
    public void doActivate() {
        if (getNodes().isEmpty()) {
            setContentsInitializer(diagram -> {
                WorkspaceDomainObjectProvider provider = getRoot(diagram).getDomainObjectProvider(WorkspaceDomainObjectProvider.class);
                provider.getFacets()
                        .forEach(n -> {
                            getNodes().add(new FacetNode(provider.createDescriptor(n)));
                        });
            });
        }
        super.doActivate();
    }
}
