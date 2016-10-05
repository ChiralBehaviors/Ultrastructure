package com.chiralbehaviors.graphql.layout.schema;

import org.glassfish.jersey.internal.util.Producer;

import com.chiralbehaviors.graphql.layout.NestedColumnView;

import javafx.scene.control.Control;

@FunctionalInterface
public interface NestingFunction {
    Control apply(Producer<Control> inner, NestedColumnView view,
                  double height);
}
