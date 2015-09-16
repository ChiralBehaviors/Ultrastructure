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
import java.math.BigDecimal;
import java.util.Map;

import com.chiralbehaviors.CoRE.phantasm.java.any.AnyProduct;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;


@SuppressWarnings("unused")
@Facet(classifier = @Key(name="IsA"), classification = @Key(name="Thing1"),
        ruleformClass=Product.class,
        workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm")
public interface Thing1 extends ScopedPhantasm<Product> {

    @PrimitiveState(fieldName="Aliases", attribute=@Key(name="aliases"))
    String[] getAliases();

    @PrimitiveState(fieldName="URI", attribute=@Key(name="URI"))
    String getURI();

    @PrimitiveState(fieldName="Percentage", attribute=@Key(name="discount"))
    BigDecimal getPercentage();

    @PrimitiveState(fieldName="Properties", attribute=@Key(name="properties"))
    Map<String, String> getProperties();


    @PrimitiveState(fieldName="Aliases", attribute=@Key(name="aliases"))
    void setAliases(String[] aliases);

    @PrimitiveState(fieldName="URI", attribute=@Key(name="URI"))
    void setURI(String uRI);

    @PrimitiveState(fieldName="Percentage", attribute=@Key(name="discount"))
    void setPercentage(BigDecimal percentage);

    @PrimitiveState(fieldName="Properties", attribute=@Key(name="properties"))
    void setProperties(Map<String, String> properties);


    @Edge(fieldName="Thing2",
           wrappedChildType = Thing2.class)
    Thing2 getThing2();

    @Edge(fieldName="DerivedFrom",
           wrappedChildType = MavenArtifact.class)
    MavenArtifact getDerivedFrom();

    @Edge(fieldName="HasMember",
           wrappedChildType = AnyProduct.class)
    AnyProduct getHasMember();


    @Edge(fieldName="Thing2",
           wrappedChildType = Thing2.class)
    void setThing2(Thing2 thing2);

    @Edge(fieldName="DerivedFrom",
           wrappedChildType = MavenArtifact.class)
    void setDerivedFrom(MavenArtifact mavenArtifact);

    @Edge(fieldName="HasMember",
           wrappedChildType = AnyProduct.class)
    void setHasMember(AnyProduct anyProduct);


}