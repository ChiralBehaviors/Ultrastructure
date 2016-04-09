/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.workspace.dsl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;

/**
 * @author hhildebrand
 *
 */
public class TestParse {
    @Test
    public void testParse() throws Exception {
        WorkspaceLexer l = new WorkspaceLexer(new ANTLRInputStream(getClass().getResourceAsStream("/thing.wsp")));
        WorkspaceParser p = new WorkspaceParser(new CommonTokenStream(l));
        p.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                    Object offendingSymbol, int line,
                                    int charPositionInLine, String msg,
                                    RecognitionException e) {
                throw new IllegalStateException("failed to parse at line "
                                                + line + " due to " + msg, e);
            }
        });
        p.workspace();
    }

    @Test
    public void testPresentation() throws Exception {
        WorkspacePresentation presentation = new WorkspacePresentation(getClass().getResourceAsStream("/thing.wsp"));
        presentation.getAgencies();
        presentation.getAgencyFacets();
        presentation.getAgencyNetworks();
        presentation.getAttributes();
        presentation.getAttributeFacets();
        presentation.getAttributeNetworks();
        presentation.getChildSequencings();
        presentation.getImports();
        presentation.getInferences();
        presentation.getIntervalFacets();
        presentation.getIntervalNetworks();
        presentation.getIntervals();
        presentation.getLocationFacets();
        presentation.getLocationNetworks();
        presentation.getLocations();
        presentation.getMetaProtocols();
        presentation.getParentSequencings();
        presentation.getProductFacets();
        presentation.getProductNetworks();
        presentation.getProducts();
        presentation.getProtocols();
        presentation.getRelationshipFacets();
        presentation.getRelationshipNetworks();
        presentation.getRelationships();
        presentation.getSelfSequencings();
        presentation.getSiblingSequencings();
        presentation.getStatusCodeFacets();
        presentation.getStatusCodeNetworks();
        presentation.getStatusCodes();
        presentation.getStatusCodeSequencings();
        presentation.getUnitFacets();
        presentation.getUnitNetworks();
        presentation.getUnits();
        presentation.getWorkspaceDefinition();

    }

}
