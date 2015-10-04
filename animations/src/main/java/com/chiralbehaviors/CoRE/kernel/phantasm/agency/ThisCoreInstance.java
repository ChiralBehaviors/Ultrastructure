/**
 * Generated Phantasm
 */
package com.chiralbehaviors.CoRE.kernel.phantasm.agency;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;

@Facet(classifier = @Key(name = "SingletonOf") , classification = @Key(name = "Core") , ruleformClass = Agency.class, workspace = "urn:uuid:00000000-0000-0004-0000-000000000003")
public interface ThisCoreInstance extends ScopedPhantasm<Agency> {

}