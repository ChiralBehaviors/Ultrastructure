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

package com.chiralbehaviors.CoRE.universal.wsp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.hellblazer.utils.Utils;

/**
 * @author halhildebrand
 *
 */
public class ResourceStringController {
    private Map<String, Object> injection;

    public Map<String, Object> injection() {
        Map<String, Object> params = new HashMap<>();
        params.put("resources", injection);
        return params;
    }

    public void setProperties(Map<String, String> properties) {
        injection = properties.entrySet()
                              .stream()
                              .collect(Collectors.toMap(k -> k.getKey(), v -> {
                                  try {
                                      InputStream resource = getClass().getResourceAsStream(v.getValue());
                                      if (resource == null) {
                                          throw new IllegalArgumentException(String.format("Resource does not exist: %s",
                                                                                           v.getValue()));
                                      }
                                      return Utils.getDocument(resource);
                                  } catch (IOException e) {
                                      throw new IllegalStateException(String.format("Error processing %s",
                                                                                    v),
                                                                      e);
                                  }
                              }));
    }
}
