/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.phantasm.demo;

import java.math.BigDecimal;
import java.util.Map;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.annotations.Edge;
import com.chiralbehaviors.CoRE.annotations.Key;
import com.chiralbehaviors.CoRE.annotations.State;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
@State(workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1")
public interface Thing1 extends ScopedPhantasm<Product> {

    // Default methods are used to add functional behavior
    default void doSomething(String document) {
        System.out.println(getThing2());
    }

    // Even "this" works because Java 8 default method magic
    default Thing1 doSomethingElse() {
        return this;
    }

    // array attributes of the ruleform
    @Key(name = "aliases")
    String[] getAliases();

    // Singular product-location authorization
    @Edge(@Key(name = "derivedFrom"))
    MavenArtifact getArtifact();

    // product attribute that has a non defaulted workspace name
    @Key(name = "discount")
    BigDecimal getPercentage();

    // mapped attributes of the ruleform
    @Key(name = "properties")
    Map<String, String> getProperties();

    // Singular child product
    @Edge(@Key(name = "thing1Of"))
    Thing2 getThing2();

    // String attribute using the defaulted workspace name derived from getter
    String getURI();

    default Thing1 scopedAccess() {
        Ruleform lookup = getScope().lookup("kernel", "IsA");
        System.out.println(lookup);
        Model model = getModel();
        return model.wrap(Thing1.class, getRuleform());
    }

    // array attributes of the ruleform
    @Key(name = "aliases")
    void setAliases(String[] aliases);

    // Singular product-location authorization
    @Edge(@Key(name = "derivedFrom"))
    void setArtifact(MavenArtifact artifact);

    @Key(name = "discount")
    void setPercentage(BigDecimal discount);

    // mapped attributes of the ruleform
    @Key(name = "properties")
    void setProperties(Map<String, String> props);

    @Edge(@Key(name = "thing1Of"))
    void setThing2(Thing2 thing2);

    // String attribute using the defaulted workspace name derived from getter
    void setURI(String uri);
}
