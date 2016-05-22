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

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.fxdiagram.core.XDomainObjectOwner;
import de.fxdiagram.mapping.AbstractDiagramConfig;
import de.fxdiagram.mapping.IMappedElementDescriptorProvider;
import de.fxdiagram.mapping.MappingAcceptor;
import de.fxdiagram.mapping.NodeMapping;

/**
 * @author hhildebrand
 *
 */
public class PhantasmDiagramConfig extends AbstractDiagramConfig {

    NodeMapping<ObjectNode> facetNode = new NodeMapping<ObjectNode>(this,
                                                                    "stateNode",
                                                                    "State") {
        @Override
        protected void calls() {
            // TODO Auto-generated method stub
            super.calls();
        }

    };

    @Override
    public void initialize(XDomainObjectOwner shape) {
        // TODO Auto-generated method stub

    }

    @Override
    protected IMappedElementDescriptorProvider createDomainObjectProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected <ARG> void entryCalls(ARG domainArgument,
                                    MappingAcceptor<ARG> acceptor) {
        // TODO Auto-generated method stub

    }

}
