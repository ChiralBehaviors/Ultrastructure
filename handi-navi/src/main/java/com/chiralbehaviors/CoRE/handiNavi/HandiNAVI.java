package com.chiralbehaviors.CoRE.handiNavi;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;
import com.hellblazer.utils.Utils;

import io.dropwizard.setup.Bootstrap;

/**
 * Ultrastructure as an Application
 * 
 * @author hhildebrand
 * 
 */
public class HandiNAVI extends PhantasmApplication<EmbeddedConfiguration> {

    private static final String DEFAULT_YML         = "/default.yml";
    private static final String DEFAULT_YML_RUNTIME = ".default.yml";

    public static void main(String[] argv) throws Exception {
        if (argv.length == 0) {
            try (InputStream is = HandiNAVI.class.getResourceAsStream(DEFAULT_YML);
                    OutputStream os = new FileOutputStream(DEFAULT_YML_RUNTIME)) {
                Utils.copy(is, os);
            }
            argv = new String[] { "server", DEFAULT_YML_RUNTIME };
        }
        new HandiNAVI().run(argv);
    }

    @Override
    public void initialize(Bootstrap<EmbeddedConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

}
