package com.chiralbehaviors.CoRE.phantasm.test.product;

import com.chiralbehaviors.CoRE.phantasm.*;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.*;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Interval;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.domain.Unit;
import java.util.List;


@SuppressWarnings("unused")
@Facet(classifier = @Key(name="IsA"), classification = @Key(name="OtherThing"),
        ruleformClass=Product.class,
        workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm")
public interface OtherThing extends ScopedPhantasm {

    @Edge(fieldName="otherThings",
           wrappedChildType = OtherThing.class)
    List<OtherThing> getOtherThings();


    @Edge(fieldName="otherThings",
           wrappedChildType = OtherThing.class)
    void setOtherThings(List<OtherThing> otherThings);

    @Edge(fieldName="otherThings",
           wrappedChildType = OtherThing.class)
    void addOtherThing(OtherThing otherThing);

    @Edge(fieldName="otherThings",
           wrappedChildType = OtherThing.class)
    void removeOtherThing(OtherThing otherThing);

    @Edge(fieldName="otherThings",
           wrappedChildType = OtherThing.class)
    void addOtherThings(List<OtherThing> otherThings);

    @Edge(fieldName="otherThings",
           wrappedChildType = OtherThing.class)
    void removeOtherThings(List<OtherThing> otherThings);


}