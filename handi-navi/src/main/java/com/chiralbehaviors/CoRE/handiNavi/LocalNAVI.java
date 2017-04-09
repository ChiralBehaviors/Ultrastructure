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

package com.chiralbehaviors.CoRE.handiNavi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.chiralbehaviors.CoRE.phantasm.service.NAVI;
import com.hellblazer.utils.Utils;

import io.dropwizard.setup.Bootstrap;

/**
 * Ultrastructure as an Application
 * 
 * @author hhildebrand
 * 
 */
public class LocalNAVI extends NAVI<EmbeddedConfiguration> {

    private static final String DEFAULT_YML         = "/default.yml";
    private static final String DEFAULT_YML_RUNTIME = ".default.yml";

    public static void main(String[] argv) throws Exception {
        runLocal(argv);
    }

    public static LocalNAVI runLocal(String[] argv) throws IOException,
                                                    FileNotFoundException,
                                                    Exception {
        if (argv.length == 0) {
            try (InputStream is = LocalNAVI.class.getResourceAsStream(DEFAULT_YML);
                    OutputStream os = new FileOutputStream(DEFAULT_YML_RUNTIME)) {
                Utils.copy(is, os);
            }
            argv = new String[] { "server", DEFAULT_YML_RUNTIME };
        }
        LocalNAVI local = new LocalNAVI();
        local.run(argv);
        return local;
    }

    @Override
    public void initialize(Bootstrap<EmbeddedConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

}
