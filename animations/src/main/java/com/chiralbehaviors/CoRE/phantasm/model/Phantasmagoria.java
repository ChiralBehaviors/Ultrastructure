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

package com.chiralbehaviors.CoRE.phantasm.model;

import java.util.HashMap;
import java.util.Map;

import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.Aspect;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.NetworkAttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.NetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor;

/**
 * Bare bones metadata representation of a Phantasm facet in Java
 *
 * @author hhildebrand
 *
 */
public class Phantasmagoria implements PhantasmVisitor {

    public final Map<String, AttributeAuthorization>        attributes          = new HashMap<>();
    public final Map<String, NetworkAuthorization>          childAuthorizations = new HashMap<>();
    public final Map<String, NetworkAttributeAuthorization> edgeAuthorizations  = new HashMap<>();
    public final Aspect                                     facet;

    public Phantasmagoria(Aspect facet) {
        this.facet = facet;
    }

    public void traverse(PhantasmTraversal traverser) {
        traverser.traverse(facet, this);
    }

    @Override
    public void visit(Aspect facet, AttributeAuthorization auth,
                      String fieldName) {
        attributes.put(fieldName, auth);
    }

    @Override
    public void visit(Aspect facet, NetworkAttributeAuthorization auth,
                      String fieldName) {
        edgeAuthorizations.put(fieldName, auth);
    }

    @Override
    public void visitChildren(Aspect facet, NetworkAuthorization auth,
                              String fieldName, Aspect child,
                              String singularFieldName) {
        childAuthorizations.put(fieldName, auth);
    }

    @Override
    public void visitSingular(Aspect facet, NetworkAuthorization auth,
                              String fieldName, Aspect child) {
        childAuthorizations.put(fieldName, auth);
    }
}
