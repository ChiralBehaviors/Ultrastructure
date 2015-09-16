/**
 * Generated Phantasm
 */
package com.chiralbehaviors.CoRE.phantasm.test.product;

import com.chiralbehaviors.CoRE.phantasm.*;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.*;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.phantasm.java.any.AnyProduct;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;


@SuppressWarnings("unused")
@Facet(classifier = @Key(name="IsA"), classification = @Key(name="Thing3"),
        ruleformClass=Product.class,
        workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm")
public interface Thing3 extends ScopedPhantasm<Product> {

    @PrimitiveState(fieldName="Aliases", attribute=@Key(name="aliases"))
    String[] getAliases();

    @PrimitiveState(fieldName="URI", attribute=@Key(name="URI"))
    String getURI();

    @PrimitiveState(fieldName="Properties", attribute=@Key(name="properties"))
    Map<String, String> getProperties();


    @PrimitiveState(fieldName="Aliases", attribute=@Key(name="aliases"))
    void setAliases(String[] aliases);

    @PrimitiveState(fieldName="URI", attribute=@Key(name="URI"))
    void setURI(String uRI);

    @PrimitiveState(fieldName="Properties", attribute=@Key(name="properties"))
    void setProperties(Map<String, String> properties);


    @Edge(fieldName="SuperFly",
           wrappedChildType = AnyProduct.class)
    AnyProduct getSuperFly();

    @Edge(fieldName="DerivedFrom",
           wrappedChildType = MavenArtifact.class)
    List<MavenArtifact> getDerivedFroms();

    @Edge(fieldName="Thing2",
           wrappedChildType = Thing2.class)
    List<Thing2> getImmediateThing2s();


    @Inferred
    @Edge(fieldName="Thing2",
           wrappedChildType = Thing2.class)
    List<Thing2> getThing2s();


    @Edge(fieldName="SuperFly",
           wrappedChildType = AnyProduct.class)
    void setSuperFly(AnyProduct anyProduct);

    @Edge(fieldName="DerivedFrom",
           wrappedChildType = MavenArtifact.class)
    void setDerivedFroms(List<MavenArtifact> mavenArtifacts);

    @Edge(fieldName="DerivedFrom",
           wrappedChildType = MavenArtifact.class)
    void addDerivedFrom(MavenArtifact mavenArtifact);

    @Edge(fieldName="DerivedFrom",
           wrappedChildType = MavenArtifact.class)
    void removeDerivedFrom(MavenArtifact mavenArtifact);

    @Edge(fieldName="DerivedFrom",
           wrappedChildType = MavenArtifact.class)
    void addDerivedFroms(List<MavenArtifact> mavenArtifacts);

    @Edge(fieldName="DerivedFrom",
           wrappedChildType = MavenArtifact.class)
    void removeDerivedFroms(List<MavenArtifact> mavenArtifacts);

    @Edge(fieldName="Thing2",
           wrappedChildType = Thing2.class)
    void setImmediateThing2s(List<Thing2> thing2s);

    @Edge(fieldName="Thing2",
           wrappedChildType = Thing2.class)
    void addThing2(Thing2 thing2);

    @Edge(fieldName="Thing2",
           wrappedChildType = Thing2.class)
    void removeThing2(Thing2 thing2);

    @Edge(fieldName="Thing2",
           wrappedChildType = Thing2.class)
    void addThing2s(List<Thing2> thing2s);

    @Edge(fieldName="Thing2",
           wrappedChildType = Thing2.class)
    void removeThing2s(List<Thing2> thing2s);


}