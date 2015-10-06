/**
 * Generated Phantasm
 */
package com.chiralbehaviors.CoRE.kernel.phantasm.product;

import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.PrimitiveState;
import com.chiralbehaviors.CoRE.product.Product;

@Facet(classifier = @Key(name = "IsA") , classification = @Key(name = "Constructor") , ruleformClass = Product.class, workspace = "urn:uuid:00000000-0000-0004-0000-000000000003")
public interface Constructor extends ScopedPhantasm<Product> {

    @PrimitiveState(fieldName = "implementationClass", attribute = @Key(name = "ImplementationClass") )
    String getImplementationClass();

    @PrimitiveState(fieldName = "implementationMethod", attribute = @Key(name = "ImplementationMethod") )
    String getImplementationMethod();

    @PrimitiveState(fieldName = "implementationClass", attribute = @Key(name = "ImplementationClass") )
    void setImplementationClass(String implementationClass);

    @PrimitiveState(fieldName = "implementationMethod", attribute = @Key(name = "ImplementationMethod") )
    void setImplementationMethod(String implementationMethod);

}