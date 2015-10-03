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

@Facet(classifier = @Key(name = "IsA") , classification = @Key(name = "InstanceMethod") , ruleformClass = Product.class, workspace = "urn:uuid:00000000-0000-0004-0000-000000000003")
public interface InstanceMethod extends ScopedPhantasm<Product> {

    @PrimitiveState(fieldName = "returnType", attribute = @Key(name = "ReturnType") )
    String getReturnType();

    @PrimitiveState(fieldName = "implementationClass", attribute = @Key(name = "ImplementationClass") )
    String getImplementationClass();

    @PrimitiveState(fieldName = "implementationMethod", attribute = @Key(name = "ImplementationMethod") )
    String getImplementationMethod();

    @PrimitiveState(fieldName = "returnType", attribute = @Key(name = "ReturnType") )
    void setReturnType(String returnType);

    @PrimitiveState(fieldName = "implementationClass", attribute = @Key(name = "ImplementationClass") )
    void setImplementationClass(String implementationClass);

    @PrimitiveState(fieldName = "implementationMethod", attribute = @Key(name = "ImplementationMethod") )
    void setImplementationMethod(String implementationMethod);

    @Edge(fieldName = "arguments", wrappedChildType = Argument.class)
    List<Argument> getArguments();

    @Edge(fieldName = "arguments", wrappedChildType = Argument.class)
    void setArguments(List<Argument> arguments);

    @Edge(fieldName = "arguments", wrappedChildType = Argument.class)
    void addArgument(Argument argument);

    @Edge(fieldName = "arguments", wrappedChildType = Argument.class)
    void removeArgument(Argument argument);

    @Edge(fieldName = "arguments", wrappedChildType = Argument.class)
    void addArguments(List<Argument> arguments);

    @Edge(fieldName = "arguments", wrappedChildType = Argument.class)
    void removeArguments(List<Argument> arguments);

}