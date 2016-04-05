/**
 * Hand crafted, artisinal phantasm
 */
package com.chiralbehaviors.CoRE.phantasm.test.location;

import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.PrimitiveState;

@Facet(classifier = @Key(name = "IsA"), classification = @Key(name = "MavenArtifact"), ruleformClass = Location.class, workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm")
public interface MavenArtifact extends ScopedPhantasm {

    @PrimitiveState(fieldName = "artifactID", attribute = @Key(name = "artifactId"))
    String getArtifactID();

    @PrimitiveState(fieldName = "classifier", attribute = @Key(name = "classifier"))
    String getClassifier();

    @PrimitiveState(fieldName = "groupID", attribute = @Key(name = "groupId"))
    String getGroupID();

    @PrimitiveState(fieldName = "type", attribute = @Key(name = "type"))
    String getType();

    @PrimitiveState(fieldName = "version", attribute = @Key(name = "version"))
    String getVersion();

    @PrimitiveState(fieldName = "artifactID", attribute = @Key(name = "artifactId"))
    void setArtifactID(String artifactID);

    @PrimitiveState(fieldName = "classifier", attribute = @Key(name = "classifier"))
    void setClassifier(String classifier);

    @PrimitiveState(fieldName = "groupID", attribute = @Key(name = "groupId"))
    void setGroupID(String groupID);

    @PrimitiveState(fieldName = "type", attribute = @Key(name = "type"))
    void setType(String type);

    @PrimitiveState(fieldName = "version", attribute = @Key(name = "version"))
    void setVersion(String version);

}