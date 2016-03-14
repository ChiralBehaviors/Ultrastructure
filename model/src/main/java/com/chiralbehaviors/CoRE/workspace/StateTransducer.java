/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.Transducer;
import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyExistentialGroupingRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceAuthorizationRecord;

/**
 * A Transducer that slices state according to workspace membership
 * 
 * @author hhildebrand
 *
 */
abstract public class StateTransducer implements Transducer {
    private final Map<String, List<Record>> closure = new HashMap<>();

    @Override
    public UUID traverse(AgencyExistentialGroupingRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ChildSequencingAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ExistentialAttributeAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ExistentialAttributeRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ExistentialNetworkAttributeAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ExistentialNetworkAttributeRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ExistentialNetworkAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ExistentialNetworkRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ExistentialRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(JobChronologyRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(JobRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(MetaProtocolRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(NetworkInferenceRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ParentSequencingAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(ProtocolRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(SelfSequencingAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(SiblingSequencingAuthorizationRecord record,
                         DSLContext create, Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(StatusCodeSequencingRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        UUID exit = slice(record, record.getId(), record.getWorkspace(), create,
                          traversed, replacements);
        if (exit != null) {
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    @Override
    public UUID traverse(WorkspaceAuthorizationRecord record, DSLContext create,
                         Collection<UUID> traversed,
                         Map<UUID, UUID> replacements) {
        if (!sameWorkspace(record.getId(), create)) {
            UUID exit = Ruleform.GENERATOR.generate();
            replacements.put(record.getId(), exit);
            return exit;
        }
        return Transducer.super.traverse(record, create, traversed,
                                         replacements);
    }

    protected void record(Record record) {
        closure.computeIfAbsent(record.getClass()
                                      .getSimpleName(),
                                name -> new ArrayList<Record>())
               .add(record);
    }

    abstract protected boolean sameWorkspace(UUID workspace, DSLContext create);

    abstract protected UUID slice(Record record, UUID id, UUID workspace,
                                  DSLContext create, Collection<UUID> traversed,
                                  Map<UUID, UUID> replacements);
}
