/**
 * Generated Phantasm
 */
package com.chiralbehaviors.CoRE.phantasm.test.product;

import com.chiralbehaviors.CoRE.phantasm.*;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.*;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;
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


@SuppressWarnings("unused")
@Facet(classifier = @Key(name="IsA"), classification = @Key(name="Thing2"),
        ruleformClass=Product.class,
        workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm")
public interface Thing2 extends ScopedPhantasm<Product> {

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


    @Edge(fieldName="Thing1",
           wrappedChildType = Thing1.class)
    Thing1 getThing1();

    @Edge(fieldName="DerivedFrom",
           wrappedChildType = MavenArtifact.class)
    List<MavenArtifact> getDerivedFroms();

    @Edge(fieldName="Thing3",
           wrappedChildType = Thing3.class)
    List<Thing3> getImmediateThing3s();


    @Inferred
    @Edge(fieldName="Thing3",
           wrappedChildType = Thing3.class)
    List<Thing3> getThing3s();


    @Edge(fieldName="Thing1",
           wrappedChildType = Thing1.class)
    void setThing1(Thing1 thing1);

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

    @Edge(fieldName="Thing3",
           wrappedChildType = Thing3.class)
    void setImmediateThing3s(List<Thing3> thing3s);

    @Edge(fieldName="Thing3",
           wrappedChildType = Thing3.class)
    void addThing3(Thing3 thing3);

    @Edge(fieldName="Thing3",
           wrappedChildType = Thing3.class)
    void removeThing3(Thing3 thing3);

    @Edge(fieldName="Thing3",
           wrappedChildType = Thing3.class)
    void addThing3s(List<Thing3> thing3s);

    @Edge(fieldName="Thing3",
           wrappedChildType = Thing3.class)
    void removeThing3s(List<Thing3> thing3s);


}