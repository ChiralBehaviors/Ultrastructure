/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.phantasm.demo;

import java.math.BigDecimal;
import java.util.Map;

import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.annotations.Aspect;
import com.chiralbehaviors.annotations.Attribute;
import com.chiralbehaviors.annotations.Key;
import com.chiralbehaviors.annotations.Relationship;
import com.chiralbehaviors.annotations.Singular;
import com.chiralbehaviors.annotations.State;

/**
 * @author hhildebrand
 *
 */
@State(facets = { @Aspect(classification = @Key(name = "IsA"), classifier = @Key(name = "Thing1")) }, workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1")
public interface Thing1 extends PhantasmBase<Product> {

    // Default methods are used to add functional behavior
    default void doSomething(String document) {
        System.out.println(getThing2());
    }

    // Even "this" works because Java 8 default method magic
    default Thing1 doSomethingElse() {
        return this;
    }

    // array attributes of the ruleform
    String[] getAliases();

    // Singular product-location authorization
    @Singular
    @Relationship(@Key(name = "derivedFrom"))
    MavenArtifact getArtifact();

    // product attribute that has a non defaulted workspace name
    @Attribute(@Key(name = "discount"))
    BigDecimal getPercentage();

    // mapped attributes of the ruleform
    Map<String, String> getProperties();

    // Singular child product
    @Singular
    @Relationship(@Key(name = "thing1Of"))
    Thing2 getThing2();

    // String attribute using the defaulted workspace name derived from getter
    String getURI();

    // Singular product-location authorization
    @Singular
    @Relationship(@Key(name = "derivedFrom"))
    void setArtifact(MavenArtifact artifact);

    // String attribute using the defaulted workspace name derived from getter
    void setURI(String uri);
}
