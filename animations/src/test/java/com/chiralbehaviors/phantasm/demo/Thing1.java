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

import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;
import com.chiralbehaviors.CoRE.phantasm.annotations.Aspect;
import com.chiralbehaviors.CoRE.phantasm.annotations.Attribute;
import com.chiralbehaviors.CoRE.phantasm.annotations.Relationship;
import com.chiralbehaviors.CoRE.phantasm.annotations.State;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
@State(facets = { @Aspect(classifier = "Thing1") }, workspace = "00000000-0000-0004-0000-000000000006")
public interface Thing1 extends PhantasmBase<Product> {

    // array attributes of the ruleform
    String[] getAliases();

    // Singular product-location authorization
    @Relationship(name = "derivedFrom", singular = true)
    MavenArtifact getArtifact();

    // product attribute that has a non defaulted workspace name, specifying the value type
    @Attribute(name = "discount", type = ValueType.NUMERIC)
    BigDecimal getPercentage();

    // mapped attributes of the ruleform
    Map<String, String> getProperties();

    // Singular child product
    @Relationship(name = "thing1Of", singular = true)
    Thing2 getThing2();

    // String attribute using the defaulted workspace name derived from getter
    String getURI();

    // Singular product-location authorization
    @Relationship(name = "derivedFrom", singular = true)
    void setArtifact(MavenArtifact artifact);

    // String attribute using the defaulted workspace name derived from getter
    void setURI(String uri);

    default void doSomething(String document) {
        System.out.println(getThing2());
    }
}
