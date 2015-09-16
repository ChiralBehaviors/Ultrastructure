/**
 * Generated Phantasm
 */
package com.chiralbehaviors.CoRE.phantasm.test.location;

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

@SuppressWarnings("unused")
@Facet(classifier = @Key(name="IsA"), classification = @Key(name="MavenArtifact"),
        ruleformClass=Location.class,
        workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm")
public interface MavenArtifact extends ScopedPhantasm<Location> {

    @PrimitiveState(fieldName="ArtifactID", attribute=@Key(name="artifactId"))
    String getArtifactID();

    @PrimitiveState(fieldName="Classifier", attribute=@Key(name="classifier"))
    String getClassifier();

    @PrimitiveState(fieldName="GroupID", attribute=@Key(name="groupId"))
    String getGroupID();

    @PrimitiveState(fieldName="Type", attribute=@Key(name="type"))
    String getType();

    @PrimitiveState(fieldName="Version", attribute=@Key(name="version"))
    String getVersion();


    @PrimitiveState(fieldName="ArtifactID", attribute=@Key(name="artifactId"))
    void setArtifactID(String artifactID);

    @PrimitiveState(fieldName="Classifier", attribute=@Key(name="classifier"))
    void setClassifier(String classifier);

    @PrimitiveState(fieldName="GroupID", attribute=@Key(name="groupId"))
    void setGroupID(String groupID);

    @PrimitiveState(fieldName="Type", attribute=@Key(name="type"))
    void setType(String type);

    @PrimitiveState(fieldName="Version", attribute=@Key(name="version"))
    void setVersion(String version);


}