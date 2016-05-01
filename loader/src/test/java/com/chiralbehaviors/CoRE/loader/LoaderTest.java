/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.loader;

import java.sql.Connection;

import org.junit.Test;

import com.chiralbehaviors.CoRE.utils.DbaConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author hhildebrand
 *
 */
public class LoaderTest {

    @Test
    public void testLoader() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        DbaConfiguration config = mapper.readValue(getClass().getResourceAsStream("/loader.yml"),
                                                   DbaConfiguration.class);
        try (Connection dbaConnection = config.getDbaConnection()) {
            dbaConnection.setAutoCommit(true);
            dbaConnection.prepareStatement("DROP DATABASE IF EXISTS testme")
                         .execute();
            dbaConnection.prepareStatement("DROP ROLE IF EXISTS scott")
                         .execute();
        }
        config.dropDatabase = true;
        Loader loader = new Loader(config);
        try {
            loader.execute();
            loader.clear();
        } finally {
            loader.dropDatabase();
        }
    }
}
