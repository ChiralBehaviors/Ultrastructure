package com.chiralbehaviors.CoRE.phantasm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.test.MasterThing;
import com.chiralbehaviors.CoRE.phantasm.test.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.test.Thing1;
import com.chiralbehaviors.CoRE.phantasm.test.Thing2;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * 
 * @author halhildebrand
 *
 */
public class NetAttrTest extends AbstractModelTest {

    @Test
    public void testAttributes() throws Exception {
        WorkspaceImporter importer = WorkspaceImporter.manifest(getClass().getResourceAsStream("/thing.wsp"),
                                                                model);
        try (FileOutputStream os = new FileOutputStream(new File(TARGET_TEST_CLASSES,
                                                                 SOME_MORE_THINGS_WSP_JSON))) {
            new WorkspaceSnapshot(importer.getWorkspace()
                                          .getDefiningProduct(),
                                  model.create()).serializeTo(os);
        }
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "Freddy", "He always comes back");
        thing1.setName("Freddy");
        thing1.setDescription("He always comes back");
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "Neddy", "He never comes back");
        thing2.setName("Neddy");
        thing2.setDescription("He never comes back");

        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 ExistentialDomain.Location,
                                                 "maven", "art vandelay");
        artifact.setName("maven");
        artifact.setDescription("art vandelay");

        thing1.setDerivedFrom(artifact);

        thing1.setThing2(thing2);
        thing1.setAliasesOfThing2(thing2, new String[] { "foo" });

        String[] aliases = thing1.getAliasesOfThing2(thing2);
        assertNotNull(aliases);
        assertEquals("foo", aliases[0]);

        MasterThing master = model.construct(MasterThing.class,
                                             ExistentialDomain.Product,
                                             "Master", "blaster");
        master.setName("Master");
        master.setDescription("blaster");

        master.addThing1(thing1);
        master.setClassifierOfThing1(thing1, "Hello");

        String classifier = master.getClassifierOfThing1(thing1);
        assertEquals("Hello", classifier);

        thing2.addMasterThing(master);
        Map<String, String> properties = new HashMap<>();
        properties.put("hello", "world");
        thing2.setPropertiesOfMasterThing(master, properties);

        properties = thing2.getPropertiesOfMasterThing(master);
        assertNotNull(properties);
        assertEquals("world", properties.get("hello"));

        WorkspaceSnapshot snap = model.snapshot();
        try (OutputStream os = new FileOutputStream(new File(TARGET_TEST_CLASSES,
                                                             SOME_MORE_THINGS_JSON))) {
            snap.serializeTo(os);
        }
    }
}
