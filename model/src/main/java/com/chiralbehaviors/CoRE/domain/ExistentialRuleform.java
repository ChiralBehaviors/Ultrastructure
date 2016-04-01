/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.domain;

import java.util.UUID;

import org.jooq.Record;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;

/**
 * @author hhildebrand
 *
 */
public interface ExistentialRuleform extends Phantasm {
    public int delete();

    public ExistentialDomain getDomain();

    public int insert();

    public void refresh();

    public int update();

    /**
     * Yes, this is really intentional
     * 
     * @param record
     * @return
     */
    default boolean equals(ExistentialRecord record) {
        return record == null ? false : getId().equals(record.getId());
    }

    /**
     * Yes, this is really intentional
     * 
     * @param record
     * @return
     */
    default boolean equals(Record record) {
        if (record instanceof ExistentialRecord) {
            return equals((ExistentialRecord) record);
        }
        return false;
    }

    @Override
    String getDescription();

    UUID getId();

    @Override
    String getName();

    @Override
    String getNotes();

    @Override
    default ExistentialRuleform getRuleform() {
        return this;
    }

    UUID getUpdatedBy();

    void setDescription(String description);

    void setName(String name);

    void setNotes(String notes);

    void setUpdatedBy(UUID updatedBy);
}
