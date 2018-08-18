package com.chiralbehaviors.CoRE.phantasm;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.JsonImporter;
import com.chiralbehaviors.CoRE.phantasm.test.MasterThing;
import com.chiralbehaviors.CoRE.phantasm.test.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.test.Thing1;
import com.chiralbehaviors.CoRE.phantasm.test.Thing2;
import com.chiralbehaviors.CoRE.phantasm.test.masterThingEdgeProperties.derivedFrom_.DerivedFromProperties;
import com.chiralbehaviors.CoRE.phantasm.test.masterThingProperties.MasterThingProperties;
import com.chiralbehaviors.CoRE.phantasm.test.mavenArtifactProperties.MavenArtifactProperties;
import com.chiralbehaviors.CoRE.phantasm.test.thing1EdgeProperties.thing2_.Thing2Properties;
import com.chiralbehaviors.CoRE.phantasm.test.thing1Properties.Thing1Properties;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * 
 * @author halhildebrand
 *
 */
public class NetAttrTest extends AbstractModelTest {

    @SuppressWarnings("serial")
    @Test
    public void testAttributes() throws Exception {
        JsonImporter importer = JsonImporter.manifest(getClass().getResourceAsStream("/thing.json"),
                                                      model);
        try (FileOutputStream os = new FileOutputStream(new File(TARGET_TEST_CLASSES,
                                                                 SOME_MORE_THINGS_WSP_JSON))) {
            new WorkspaceSnapshot(importer.getWorkspace()
                                          .getDefiningProduct(),
                                  model.create()).serializeTo(os);
        }
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "Freddy", "He always comes back");
        thing1.set_Properties(new Thing1Properties());
        thing1.get_Properties()
              .setName("Freddy");
        thing1.get_Properties()
              .setDescription("He always comes back");
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "Neddy", "He never comes back");
        thing2.set_Properties(new com.chiralbehaviors.CoRE.phantasm.test.thing2Properties.Thing2Properties());
        thing2.get_Properties()
              .setName("Neddy");
        thing2.get_Properties()
              .setDescription("He never comes back");

        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 ExistentialDomain.Location,
                                                 "maven", "art vandelay");
        artifact.set_Properties(new MavenArtifactProperties());
        MavenArtifactProperties artProps = new MavenArtifactProperties();
        artProps.setName("maven");
        artProps.setDescription("art vandelay");
        artifact.set_Properties(artProps);

        thing1.setDerivedFrom(artifact);

        thing1.setThing2(thing2);

        Thing2Properties props = new Thing2Properties();
        props.setAliases(new ArrayList<String>() {
            {
                add("foo");
            }
        });
        thing1.set_PropertiesOfThing2(thing2, props);

        MasterThing master = model.construct(MasterThing.class,
                                             ExistentialDomain.Product,
                                             "Master", "blaster");
        MasterThingProperties masterProps = new MasterThingProperties();
        masterProps.setName("Master");
        masterProps.setDescription("blaster");
        master.set_Properties(masterProps);

        master.addDerivedFrom(thing1);
        master.set_PropertiesOfDerivedFrom(thing1, new DerivedFromProperties());
        DerivedFromProperties properties = master.get_PropertiesOfDerivedFrom(thing1);
        properties.setClassifier("Hello");
        master.set_PropertiesOfDerivedFrom(thing1, properties);

        String classifier = master.get_PropertiesOfDerivedFrom(thing1)
                                  .getClassifier();
        assertEquals("Hello", classifier);

        thing2.addMasterThing(master);

        WorkspaceSnapshot snap = model.snapshot();
        try (OutputStream os = new FileOutputStream(new File(TARGET_TEST_CLASSES,
                                                             SOME_MORE_THINGS_JSON))) {
            snap.serializeTo(os);
        }
    }
}
