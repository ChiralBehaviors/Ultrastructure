package com.chiralbehaviors.CoRE.phantasm.resources.test;

import java.util.Map;

import javax.persistence.Persistence;

import io.dropwizard.setup.Environment;

/**
 * A dropwizard application that exposes the Ultrastructure REST APIs. Use this
 * for developing UIs without starting your own app server.
 * @author hparry
 *
 */
public class RestServiceApplication extends TestApplication {

    public static void main(String[] argv) throws Exception {
        new RestServiceApplication().run(argv);
    }
    
    @Override
    public void run(TestServiceConfiguration configuration,
                    Environment environment) throws Exception {
        JpaConfiguration jpaConfig = configuration.getCrudServiceConfiguration();

        String unit = jpaConfig.getPersistenceUnit();
        Map<String, String> properties = jpaConfig.getProperties();
        setEmf(Persistence.createEntityManagerFactory(unit, properties));
        super.run(configuration, environment);
    }
}
