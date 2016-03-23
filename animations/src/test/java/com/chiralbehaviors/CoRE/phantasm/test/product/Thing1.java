/**
 *  Hand crafted, artisinal phantasm
 */
package com.chiralbehaviors.CoRE.phantasm.test.product;

import java.math.BigDecimal;
import java.util.Map;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Edge;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.PrimitiveState;
import com.chiralbehaviors.CoRE.phantasm.java.any.AnyProduct;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;

@Facet(classifier = @Key(name = "IsA"), classification = @Key(name = "Thing1"), ruleformClass = Product.class, workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm")
public interface Thing1 extends ScopedPhantasm<Product> {

    @PrimitiveState(fieldName = "aliases", attribute = @Key(name = "aliases"))
    String[] getAliases();

    @PrimitiveState(fieldName = "URI", attribute = @Key(name = "URI"))
    String getURI();

    @PrimitiveState(fieldName = "percentage", attribute = @Key(name = "discount"))
    BigDecimal getPercentage();

    @PrimitiveState(fieldName = "properties", attribute = @Key(name = "properties"))
    Map<String, String> getProperties();

    @PrimitiveState(fieldName = "aliases", attribute = @Key(name = "aliases"))
    void setAliases(String[] aliases);

    @PrimitiveState(fieldName = "URI", attribute = @Key(name = "URI"))
    void setURI(String uRI);

    @PrimitiveState(fieldName = "percentage", attribute = @Key(name = "discount"))
    void setPercentage(BigDecimal percentage);

    @PrimitiveState(fieldName = "properties", attribute = @Key(name = "properties"))
    void setProperties(Map<String, String> properties);

    @Edge(fieldName = "thing2", wrappedChildType = Thing2.class)
    Thing2 getThing2();

    @Edge(fieldName = "derivedFrom", wrappedChildType = MavenArtifact.class)
    MavenArtifact getDerivedFrom();

    @Edge(fieldName = "hasMember", wrappedChildType = AnyProduct.class)
    AnyProduct getHasMember();

    @Edge(fieldName = "thing2", wrappedChildType = Thing2.class)
    void setThing2(Thing2 thing2);

    @Edge(fieldName = "derivedFrom", wrappedChildType = MavenArtifact.class)
    void setDerivedFrom(MavenArtifact mavenArtifact);

    @Edge(fieldName = "hasMember", wrappedChildType = AnyProduct.class)
    void setHasMember(AnyProduct anyProduct);

}