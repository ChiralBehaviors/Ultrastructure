package com.chiralbehaviors.CoRE.handiNavi;

import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;

import io.dropwizard.setup.Bootstrap;

/**
 * Ultrastructure as an Application
 * 
 * @author hhildebrand
 * 
 */
public class HandiNAVI extends PhantasmApplication<EmbeddedConfiguration> {

    public static void main(String[] argv) throws Exception {
        new HandiNAVI().run(argv);
    }

    @Override
    public void initialize(Bootstrap<EmbeddedConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

}
