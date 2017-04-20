/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.universal;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import com.chiralbehaviors.CoRE.universal.spa.SpaLexer;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser;
import com.chiralbehaviors.CoRE.universal.spa.SpaParser.SpaContext;
import static org.junit.Assert.*;

/**
 * @author halhildebrand
 *
 */
public class TestSpaDsl {
    @Test
    public void testParse() throws Exception {
        SpaLexer l = new SpaLexer(CharStreams.fromStream(getClass().getResourceAsStream("/smoke.app")));
        SpaParser p = new SpaParser(new CommonTokenStream(l));
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
        SpaContext spa = p.spa();
        SpaImporter importer = new SpaImporter();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(importer, spa);
        Spa constructedSpa = importer.getSpa();
        assertNotNull(constructedSpa.route("launch"));
    }
}
