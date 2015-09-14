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

package com.chiralbehaviors.CoRE.navi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * @author hhildebrand
 *
 *         <ul>
 *         <li><b>allowedOrigins</b>, a list of origins that are allowed to
 *         access the resources. Default value is <b>*</b>, meaning all origins.
 *         <br />
 *         If an allowed origin contains one or more * characters (for example
 *         http://*.domain.com), then "*" characters are converted to ".*", "."
 *         characters are escaped to "\." and the resulting allowed origin
 *         interpreted as a regular expression.<br />
 *         Allowed origins can therefore be more complex expressions such as
 *         https?://*.domain.[a-z]{3} that matches http or https, multiple
 *         subdomains and any 3 letter top-level domain (.com, .net, .org,
 *         etc.).</li>
 *         <li><b>allowedMethods</b>, a list of HTTP methods that are allowed to
 *         be used when accessing the resources. Default value is
 *         <b>GET,POST,HEAD</b></li>
 *         <li><b>allowedHeaders</b>, a list of HTTP headers that are allowed to
 *         be specified when accessing the resources. Default value is
 *         <b>X-Requested-With,Content-Type,Accept,Origin</b>. If the value is a
 *         single "*", this means that any headers will be accepted.</li>
 *         <li><b>preflightMaxAge</b>, the number of seconds that preflight
 *         requests can be cached by the client. Default value is <b>1800</b>
 *         seconds, or 30 minutes</li>
 *         <li><b>allowCredentials</b>, a boolean indicating if the resource
 *         allows requests with credentials. Default value is <b>false</b></li>
 *         <li><b>exposedHeaders</b>, a list of HTTP headers that are allowed to
 *         be exposed on the client. Default value is the <b>empty list</b></li>
 *         <li><b>chainPreflight</b>, if true preflight requests are chained to
 *         their target resource for normal handling (as an OPTION request).
 *         Otherwise the filter will response to the preflight. Default is true.
 *         </li>
 *         </ul>
 *         </p>
 */
public class CorsConfiguration {
    public boolean      allowCredentials = false;
    @NotNull
    public List<String> allowedMethods   = Arrays.asList("GET", "POST", "HEAD");
    @NotNull
    public List<String> allowedHeaders   = Arrays.asList("X-Requested-With",
                                                         "Content-Type",
                                                         "Accept,Origin");
    @NotNull
    public List<String> allowedOrigins   = Collections.singletonList("*");
    public boolean      chainPreflight   = true;
    @NotNull
    public List<String> exposedHeaders   = Collections.emptyList();
    public int          preflightMaxAge  = 1800;

    @Override
    public String toString() {
        return String.format("CorsConfiguration [allowedOrigins=%s, allowedMethods=%s, allowedHeaders=%s, preflightMaxAge=%s, allowCredentials=%s, exposedHeaders=%s, chainPreflight=%s]",
                             allowedOrigins, allowedMethods, allowedHeaders,
                             preflightMaxAge, allowCredentials, exposedHeaders,
                             chainPreflight);
    }
}
