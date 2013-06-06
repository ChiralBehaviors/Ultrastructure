/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.event;

import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.attribute.AttributeAuthorization;

/**
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "protocol_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_attribute_authorization_id_seq", sequenceName = "protocol_attribute_authorization_id_seq")
public class ProtocolAttributeAuthorization extends AttributeAuthorization {

    private static final long serialVersionUID = 1L;
    
    private Long id;

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

}
