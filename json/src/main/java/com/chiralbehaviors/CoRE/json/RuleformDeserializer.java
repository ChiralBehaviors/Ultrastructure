package com.chiralbehaviors.CoRE.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.Ruleform;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class RuleformDeserializer extends StdDeserializer<Ruleform> implements
        ResolvableDeserializer {
    private static final long         serialVersionUID = 7923585097068641765L;

    private final JsonDeserializer<?> defaultDeserializer;

    private final EntityManager       em;

    public RuleformDeserializer(JsonDeserializer<?> defaultDeserializer,
                                EntityManager em) {
        super(Ruleform.class);
        this.defaultDeserializer = defaultDeserializer;
        this.em = em;
    }

    @Override
    public Ruleform deserialize(JsonParser jp, DeserializationContext ctxt)
                                                                           throws IOException,
                                                                           JsonProcessingException {
        Ruleform deserializedRuleform = (Ruleform) defaultDeserializer.deserialize(jp,
                                                                                   ctxt);

        Map<Ruleform, Ruleform> knownObjects = new HashMap<Ruleform, Ruleform>();
        deserializedRuleform.traverseForeignKeys(em, knownObjects);

        return deserializedRuleform;
    }

    // for some reason you have to implement ResolvableDeserializer when modifying BeanDeserializer
    // otherwise deserializing throws JsonMappingException??
    @Override
    public void resolve(DeserializationContext ctxt)
                                                    throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
    }

}