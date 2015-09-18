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

package com.chiralbehaviors.CoRE.phantasm.jsonld;

import static com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext.getIri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.meta.models.AttributeModelImpl;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmTraversal;
import com.chiralbehaviors.CoRE.phantasm.model.Phantasmagoria;

/**
 * @author hhildebrand
 *
 */
public class FacetContext<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>>
        extends Phantasmagoria<RuleForm, NetworkRuleform<RuleForm>> {
    public static String getTermIri(String term, UriInfo uriInfo) {
        return String.format("%s:term/%s", Constants.FACET, term);
    }

    private final Map<String, Typed> terms = new TreeMap<>();

    private final UriInfo uriInfo;

    /**
     * @param facet
     * @param traverser
     */
    public FacetContext(NetworkAuthorization<RuleForm> facet,
                        PhantasmTraversal<RuleForm, NetworkRuleform<RuleForm>> traverser,
                        UriInfo uriInfo) {
        super(facet);
        this.uriInfo = uriInfo;
        traverse(facet, traverser);
        collectRuleformAttributeTerms();
    }

    @Override
    public void visit(NetworkAuthorization<RuleForm> facet,
                      AttributeAuthorization<RuleForm, NetworkRuleform<RuleForm>> auth,
                      String fieldName) {
        super.visit(facet, auth, fieldName);
        terms.put(fieldName, new Typed(getTermIri(auth.getAuthorizedAttribute()
                                                      .getName(),
                                                  uriInfo),
                                       getIri(auth.getAuthorizedAttribute())));
    }

    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> facet,
                              NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child,
                              String singularFieldName) {
        super.visitChildren(facet, auth, fieldName, child, singularFieldName);
        terms.put(fieldName,
                  new Typed(getTermIri(fieldName, uriInfo), Constants.ID));
    }

    @Override
    public void visitChildren(NetworkAuthorization<RuleForm> facet,
                              XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child,
                              String singularFieldName) {
        super.visitChildren(facet, auth, fieldName, child, singularFieldName);
        terms.put(fieldName,
                  new Typed(getTermIri(fieldName, uriInfo), Constants.ID));
    }

    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> facet,
                              NetworkAuthorization<RuleForm> auth,
                              String fieldName,
                              NetworkAuthorization<RuleForm> child) {
        super.visitSingular(facet, auth, fieldName, child);
        terms.put(fieldName,
                  new Typed(getTermIri(fieldName, uriInfo), Constants.ID));
    }

    @Override
    public void visitSingular(NetworkAuthorization<RuleForm> facet,
                              XDomainNetworkAuthorization<?, ?> auth,
                              String fieldName, NetworkAuthorization<?> child) {
        super.visitSingular(facet, auth, fieldName, child);
        terms.put(fieldName,
                  new Typed(getTermIri(fieldName, uriInfo), Constants.ID));
    }

    private void collectRuleformAttributeTerms() {
        String textType;
        try {
            textType = new URI(AttributeModelImpl.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_TEXT).toASCIIString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("Cannot create URI: %s",
                                                          AttributeModelImpl.HTTP_WWW_W3_ORG_2001_XML_SCHEMA_TEXT));
        }
        terms.put("name", new Typed(getTermIri("name", uriInfo), textType));
        terms.put("description",
                  new Typed(getTermIri("description", uriInfo), textType));
        terms.put("notes", new Typed(getTermIri("notes", uriInfo), textType));
        terms.put("updatedBy",
                  new Typed(getTermIri("updatedBy", uriInfo),
                            String.format("%s:%s", Constants.RULEFORM,
                                          Agency.class.getSimpleName())));
    }

}
