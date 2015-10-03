/**
 * Generated Phantasm
 */
package com.chiralbehaviors.CoRE.kernel.phantasm.product;

import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.PrimitiveState;
import com.chiralbehaviors.CoRE.product.Product;

@Facet(classifier = @Key(name = "IsA") , classification = @Key(name = "Argument") , ruleformClass = Product.class, workspace = "urn:uuid:00000000-0000-0004-0000-000000000003")
public interface Argument extends ScopedPhantasm<Product> {

    @PrimitiveState(fieldName = "inputType", attribute = @Key(name = "InputType") )
    String getInputType();

    @PrimitiveState(fieldName = "inputType", attribute = @Key(name = "InputType") )
    void setInputType(String inputType);

}