/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import com.hellblazer.CoRE.resource.Resource;

/**
 * A ruleform that declares existence.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class ExistentialRuleform extends Ruleform {
    private static final long serialVersionUID = 1L;

    private String            description;
    private String            name;
    private Boolean           pinned           = Boolean.FALSE;

    public ExistentialRuleform() {
    }

    public ExistentialRuleform(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ExistentialRuleform(Resource updatedBy) {
        super(updatedBy);
    }

    public ExistentialRuleform(String name) {
        this.name = name;
    }
    
    public ExistentialRuleform(Long id, Resource updatedBy) {
        super(id, updatedBy);
    }

    public ExistentialRuleform(String name, Resource updatedBy) {
        super(updatedBy);
        this.name = name;
    }

    public ExistentialRuleform(String name, String description) {
        this(name);
        this.description = description;
    }

    public ExistentialRuleform(String name, String description,
                               Resource updatedBy) {
        this(name, updatedBy);
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the pinned
     */
    public Boolean getPinned() {
        return pinned;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param pinned
     *            the pinned to set
     */
    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    @Override
    public String toString() {
        return String.format("%s [name=%s, id=%s]", getClass().getSimpleName(),
                             name, getId());
    }
}
