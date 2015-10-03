/**
 * Generated Phantasm
 */
package com.chiralbehaviors.CoRE.kernel.phantasm.product;

import java.util.List;

import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Edge;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.PrimitiveState;
import com.chiralbehaviors.CoRE.product.Product;

@Facet(classifier = @Key(name = "IsA") , classification = @Key(name = "Workspace") , ruleformClass = Product.class, workspace = "urn:uuid:00000000-0000-0004-0000-000000000003")
public interface Workspace extends ScopedPhantasm<Product> {

    @PrimitiveState(fieldName = "IRI", attribute = @Key(name = "IRI") )
    String getIRI();

    @PrimitiveState(fieldName = "IRI", attribute = @Key(name = "IRI") )
    void setIRI(String iRI);

    @Edge(fieldName = "plugins", wrappedChildType = Plugin.class)
    List<Plugin> getPlugins();

    @Edge(fieldName = "imports", wrappedChildType = Workspace.class)
    List<Workspace> getImports();

    @Edge(fieldName = "plugins", wrappedChildType = Plugin.class)
    void setPlugins(List<Plugin> plugins);

    @Edge(fieldName = "plugins", wrappedChildType = Plugin.class)
    void addPlugin(Plugin plugin);

    @Edge(fieldName = "plugins", wrappedChildType = Plugin.class)
    void removePlugin(Plugin plugin);

    @Edge(fieldName = "plugins", wrappedChildType = Plugin.class)
    void addPlugins(List<Plugin> plugins);

    @Edge(fieldName = "plugins", wrappedChildType = Plugin.class)
    void removePlugins(List<Plugin> plugins);

    @Edge(fieldName = "imports", wrappedChildType = Workspace.class)
    void setImports(List<Workspace> workspaces);

    @Edge(fieldName = "imports", wrappedChildType = Workspace.class)
    void addImport(Workspace workspace);

    @Edge(fieldName = "imports", wrappedChildType = Workspace.class)
    void removeImport(Workspace workspace);

    @Edge(fieldName = "imports", wrappedChildType = Workspace.class)
    void addImports(List<Workspace> workspaces);

    @Edge(fieldName = "imports", wrappedChildType = Workspace.class)
    void removeImports(List<Workspace> workspaces);

}