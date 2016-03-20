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

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.AttributeAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.NetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor;

/**
 * Bare bones metadata representation of a Phantasm facet in Java
 * 
 * @author hhildebrand
 *
 */
public class Phantasmagoria<RuleForm extends ExistentialRuleform>
        implements PhantasmVisitor<RuleForm> {

    public final Map<String, AttributeAuthorization> attributes          = new HashMap<>();
    public final Map<String, NetworkAuthorization>   childAuthorizations = new HashMap<>();
    public final NetworkAuthorization                facet;

    public Phantasmagoria(ExistentialNetworkAuthorizationRecord facet) {
        this.facet = new NetworkAuthorization(null, facet);
    }

    public void traverse(PhantasmTraversal<RuleForm> traverser) {
        traverser.traverse(facet, this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor#visit(com.chiralbehaviors.CoRE.network.NetworkAuthorization, com.chiralbehaviors.CoRE.attribute.AttributeAuthorization, java.lang.String)
     */
    @Override
    public void visit(NetworkAuthorization facet, AttributeAuthorization auth,
                      String fieldName) {
        attributes.put(fieldName, auth);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor#visitChildren(com.chiralbehaviors.CoRE.network.NetworkAuthorization, com.chiralbehaviors.CoRE.network.NetworkAuthorization, java.lang.String, com.chiralbehaviors.CoRE.network.NetworkAuthorization, java.lang.String)
     */
    @Override
    public void visitChildren(NetworkAuthorization facet,
                              NetworkAuthorization auth, String fieldName,
                              NetworkAuthorization child,
                              String singularFieldName) {
        childAuthorizations.put(fieldName, child);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor#visitSingular(com.chiralbehaviors.CoRE.network.NetworkAuthorization, com.chiralbehaviors.CoRE.network.NetworkAuthorization, java.lang.String, com.chiralbehaviors.CoRE.network.NetworkAuthorization)
     */
    @Override
    public void visitSingular(NetworkAuthorization facet,
                              NetworkAuthorization auth, String fieldName,
                              NetworkAuthorization child) {
        childAuthorizations.put(fieldName, child);
    }
}
