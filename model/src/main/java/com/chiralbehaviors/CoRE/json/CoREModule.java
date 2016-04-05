/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.json;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;

import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A jackson module for registering serializers and deserializers.
 *
 * @author hparry
 *
 */
public class CoREModule extends SimpleModule {

    private static final long serialVersionUID = 1L;
    private final DSLContext  create;

    public CoREModule() {
        this(null);
    }

    public CoREModule(DSLContext create) {
        super("CoREModule");
        this.create = create;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(Record.class, RecordMixin.class);
        ObjectMapper objectMapper = (ObjectMapper) context.getOwner();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enableDefaultTyping();
        addSerializer(new RecordSerializer());
        List<Class<?>> subTypes = new ArrayList<>();
        Ruleform.RULEFORM.getTables()
                         .forEach(table -> {
                             subTypes.add(table.getRecordType());
                             if (create != null) {
                                 addValueInstantiator(table.getRecordType(),
                                                      new RecordResolver(create,
                                                                         table));
                             }
                         });
        registerSubtypes(subTypes.toArray(new Class<?>[subTypes.size()]));

        super.setupModule(context);
    }
}
