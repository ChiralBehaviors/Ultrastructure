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

@Facet(classifier = @Key(name = "IsA") , classification = @Key(name = "Plugin") , ruleformClass = Product.class, workspace = "urn:uuid:00000000-0000-0004-0000-000000000003")
public interface Plugin extends ScopedPhantasm<Product> {

    @PrimitiveState(fieldName = "facetName", attribute = @Key(name = "FacetName") )
    String getFacetName();

    @PrimitiveState(fieldName = "packageName", attribute = @Key(name = "PackageName") )
    String getPackageName();

    @PrimitiveState(fieldName = "facetName", attribute = @Key(name = "FacetName") )
    void setFacetName(String facetName);

    @PrimitiveState(fieldName = "packageName", attribute = @Key(name = "PackageName") )
    void setPackageName(String packageName);

    @Edge(fieldName = "codeSources", wrappedChildType = CodeSource.class)
    List<CodeSource> getCodeSources();

    @Edge(fieldName = "staticMethods", wrappedChildType = StaticMethod.class)
    List<StaticMethod> getStaticMethods();

    @Edge(fieldName = "constructor", wrappedChildType = Constructor.class)
    Constructor getConstructor();

    @Edge(fieldName = "instanceMethods", wrappedChildType = InstanceMethod.class)
    List<InstanceMethod> getInstanceMethods();

    @Edge(fieldName = "codeSources", wrappedChildType = CodeSource.class)
    void setCodeSources(List<CodeSource> codeSources);

    @Edge(fieldName = "codeSources", wrappedChildType = CodeSource.class)
    void addCodeSource(CodeSource codeSource);

    @Edge(fieldName = "codeSources", wrappedChildType = CodeSource.class)
    void removeCodeSource(CodeSource codeSource);

    @Edge(fieldName = "codeSources", wrappedChildType = CodeSource.class)
    void addCodeSources(List<CodeSource> codeSources);

    @Edge(fieldName = "codeSources", wrappedChildType = CodeSource.class)
    void removeCodeSources(List<CodeSource> codeSources);

    @Edge(fieldName = "staticMethods", wrappedChildType = StaticMethod.class)
    void setStaticMethods(List<StaticMethod> staticMethods);

    @Edge(fieldName = "staticMethods", wrappedChildType = StaticMethod.class)
    void addStaticMethod(StaticMethod staticMethod);

    @Edge(fieldName = "staticMethods", wrappedChildType = StaticMethod.class)
    void removeStaticMethod(StaticMethod staticMethod);

    @Edge(fieldName = "staticMethods", wrappedChildType = StaticMethod.class)
    void addStaticMethods(List<StaticMethod> staticMethods);

    @Edge(fieldName = "staticMethods", wrappedChildType = StaticMethod.class)
    void removeStaticMethods(List<StaticMethod> staticMethods);

    @Edge(fieldName = "constructor", wrappedChildType = Constructor.class)
    void setConstructor(Constructor constructor);

    @Edge(fieldName = "instanceMethods", wrappedChildType = InstanceMethod.class)
    void setInstanceMethods(List<InstanceMethod> instanceMethods);

    @Edge(fieldName = "instanceMethods", wrappedChildType = InstanceMethod.class)
    void addInstanceMethod(InstanceMethod instanceMethod);

    @Edge(fieldName = "instanceMethods", wrappedChildType = InstanceMethod.class)
    void removeInstanceMethod(InstanceMethod instanceMethod);

    @Edge(fieldName = "instanceMethods", wrappedChildType = InstanceMethod.class)
    void addInstanceMethods(List<InstanceMethod> instanceMethods);

    @Edge(fieldName = "instanceMethods", wrappedChildType = InstanceMethod.class)
    void removeInstanceMethods(List<InstanceMethod> instanceMethods);

}