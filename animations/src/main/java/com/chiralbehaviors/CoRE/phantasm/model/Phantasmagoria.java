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

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor;

/**
 * Bare bones metadata representation of a Phantasm facet in Java
 * 
 * @author hhildebrand
 *
 */
public class Phantasmagoria<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        implements PhantasmVisitor<RuleForm, Network> {

    public final Map<String, AttributeAuthorization<RuleForm, Network>> attributes            = new HashMap<>();
    public final Map<String, NetworkAuthorization<RuleForm>>            childAuthorizations   = new HashMap<>();
    public final NetworkAuthorization<RuleForm>                         facet;
    public final Map<String, XDomainNetworkAuthorization<?, ?>>         xdChildAuthorizations = new HashMap<>();

    public Phantasmagoria(NetworkAuthorization<RuleForm> facet,
                          PhantasmTraversal<RuleForm, Network> traverser) {
        this.facet = Ruleform.initializeAndUnproxy(facet);
        traverser.traverse(facet, this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor#visit(com.chiralbehaviors.CoRE.network.NetworkAuthorization, com.chiralbehaviors.CoRE.attribute.AttributeAuthorization, java.lang.String)
     */
    @Override
    public void visit(NetworkAuthorization<RuleForm> facet,
                      AttributeAuthorization<RuleForm, Network> auth,
                      String fieldName) {
        attributes.put(fieldName, auth);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor#visitChildren(com.chiralbehaviors.CoRE.network.NetworkAuthorization, com.chiralbehaviors.CoRE.network.NetworkAuthorization, java.lang.String, com.chiralbehaviors.CoRE.network.NetworkAuthorization, java.lang.String)
     */
    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> facet,
                              NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child,
                              String singularFieldName) {
        childAuthorizations.put(fieldName, auth);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor#visitChildren(com.chiralbehaviors.CoRE.network.NetworkAuthorization, com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization, java.lang.String, com.chiralbehaviors.CoRE.network.NetworkAuthorization, java.lang.String)
     */
    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> facet,
                              XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child,
                              String singularFieldName) {
        xdChildAuthorizations.put(fieldName, auth);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor#visitSingular(com.chiralbehaviors.CoRE.network.NetworkAuthorization, com.chiralbehaviors.CoRE.network.NetworkAuthorization, java.lang.String, com.chiralbehaviors.CoRE.network.NetworkAuthorization)
     */
    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> facet,
                              NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child) {
        childAuthorizations.put(fieldName, auth);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal.PhantasmVisitor#visitSingular(com.chiralbehaviors.CoRE.network.NetworkAuthorization, com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization, java.lang.String, com.chiralbehaviors.CoRE.network.NetworkAuthorization)
     */
    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> facet,
                              XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child) {
        xdChildAuthorizations.put(fieldName, auth);
    }

}
