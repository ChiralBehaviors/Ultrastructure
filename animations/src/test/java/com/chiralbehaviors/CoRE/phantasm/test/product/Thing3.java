/**
 * Generated Phantasm
 */
package com.chiralbehaviors.CoRE.phantasm.test.product;

import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Edge;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Inferred;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.PrimitiveState;
import com.chiralbehaviors.CoRE.phantasm.java.any.AnyProduct;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;

@Facet(classifier = @Key(name = "IsA"), classification = @Key(name = "Thing3"), ruleformClass = Product.class, workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm")
public interface Thing3 extends ScopedPhantasm<Product> {

    @PrimitiveState(fieldName = "aliases", attribute = @Key(name = "aliases"))
    String[] getAliases();

    @PrimitiveState(fieldName = "URI", attribute = @Key(name = "URI"))
    String getURI();

    @PrimitiveState(fieldName = "properties", attribute = @Key(name = "properties"))
    Map<String, String> getProperties();

    @PrimitiveState(fieldName = "aliases", attribute = @Key(name = "aliases"))
    void setAliases(String[] aliases);

    @PrimitiveState(fieldName = "URI", attribute = @Key(name = "URI"))
    void setURI(String uRI);

    @PrimitiveState(fieldName = "properties", attribute = @Key(name = "properties"))
    void setProperties(Map<String, String> properties);

    @Edge(fieldName = "superFly", wrappedChildType = AnyProduct.class)
    AnyProduct getSuperFly();

    @Edge(fieldName = "derivedFroms", wrappedChildType = MavenArtifact.class)
    List<MavenArtifact> getDerivedFroms();

    @Edge(fieldName = "thing2s", wrappedChildType = Thing2.class)
    List<Thing2> getImmediateThing2s();

    @Inferred
    @Edge(fieldName = "thing2s", wrappedChildType = Thing2.class)
    List<Thing2> getThing2s();

    @Edge(fieldName = "superFly", wrappedChildType = AnyProduct.class)
    void setSuperFly(AnyProduct anyProduct);

    @Edge(fieldName = "derivedFroms", wrappedChildType = MavenArtifact.class)
    void setDerivedFroms(List<MavenArtifact> mavenArtifacts);

    @Edge(fieldName = "derivedFroms", wrappedChildType = MavenArtifact.class)
    void addDerivedFrom(MavenArtifact mavenArtifact);

    @Edge(fieldName = "derivedFroms", wrappedChildType = MavenArtifact.class)
    void removeDerivedFrom(MavenArtifact mavenArtifact);

    @Edge(fieldName = "derivedFroms", wrappedChildType = MavenArtifact.class)
    void addDerivedFroms(List<MavenArtifact> mavenArtifacts);

    @Edge(fieldName = "derivedFroms", wrappedChildType = MavenArtifact.class)
    void removeDerivedFroms(List<MavenArtifact> mavenArtifacts);

    @Edge(fieldName = "thing2s", wrappedChildType = Thing2.class)
    void setImmediateThing2s(List<Thing2> thing2s);

    @Edge(fieldName = "thing2s", wrappedChildType = Thing2.class)
    void addThing2(Thing2 thing2);

    @Edge(fieldName = "thing2s", wrappedChildType = Thing2.class)
    void removeThing2(Thing2 thing2);

    @Edge(fieldName = "thing2s", wrappedChildType = Thing2.class)
    void addThing2s(List<Thing2> thing2s);

    @Edge(fieldName = "thing2s", wrappedChildType = Thing2.class)
    void removeThing2s(List<Thing2> thing2s);

}