/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
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

import java.io.IOException;

import org.jooq.DSLContext;
import org.jooq.Table;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

/**
 * @author hhildebrand
 *
 */
public class RecordResolver extends ValueInstantiator {

    private final DSLContext create;
    private final Table<?>   table;

    public RecordResolver(DSLContext create, Table<?> table) {
        this.create = create;
        this.table = table;
    }

    @Override
    public boolean canCreateUsingDefault() {
        return true;
    }

    @Override
    public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
        return create.newRecord(table);
    }

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.deser.ValueInstantiator#getValueTypeDesc()
     */
    @Override
    public String getValueTypeDesc() {
        return String.format("Instantiator for: %s", table);
    }
}
