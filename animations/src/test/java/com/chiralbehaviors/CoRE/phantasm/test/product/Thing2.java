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
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;

@Facet(classifier = @Key(name = "IsA"), classification = @Key(name = "Thing2"), ruleformClass = Product.class, workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm")
public interface Thing2 extends ScopedPhantasm<Product> {

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

    @Edge(fieldName = "thing1", wrappedChildType = Thing1.class)
    Thing1 getThing1();

    @Edge(fieldName = "derivedFroms", wrappedChildType = MavenArtifact.class)
    List<MavenArtifact> getDerivedFroms();

    @Edge(fieldName = "thing3s", wrappedChildType = Thing3.class)
    List<Thing3> getImmediateThing3s();

    @Inferred
    @Edge(fieldName = "thing3s", wrappedChildType = Thing3.class)
    List<Thing3> getThing3s();

    @Edge(fieldName = "thing1", wrappedChildType = Thing1.class)
    void setThing1(Thing1 thing1);

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

    @Edge(fieldName = "thing3s", wrappedChildType = Thing3.class)
    void setImmediateThing3s(List<Thing3> thing3s);

    @Edge(fieldName = "thing3s", wrappedChildType = Thing3.class)
    void addThing3(Thing3 thing3);

    @Edge(fieldName = "thing3s", wrappedChildType = Thing3.class)
    void removeThing3(Thing3 thing3);

    @Edge(fieldName = "thing3s", wrappedChildType = Thing3.class)
    void addThing3s(List<Thing3> thing3s);

    @Edge(fieldName = "thing3s", wrappedChildType = Thing3.class)
    void removeThing3s(List<Thing3> thing3s);

}