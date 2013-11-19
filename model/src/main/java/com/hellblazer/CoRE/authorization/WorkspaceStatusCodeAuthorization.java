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

package com.hellblazer.CoRE.authorization;

import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.event.StatusCode;

/**
 * @author hhildebrand
 * 
 */
@Entity
@DiscriminatorValue(WorkspaceAuthorization.PRODUCT_STATUS_CODE)
public class WorkspaceStatusCodeAuthorization extends WorkspaceAuthorization {

    private static final long serialVersionUID = 1L;

    {
        setAuthorizationType(WorkspaceAuthorization.PRODUCT_STATUS_CODE);
    }

    @ManyToOne
    @JoinColumn(name = "status_code")
    protected StatusCode      statusCode;

    public WorkspaceStatusCodeAuthorization() {
        super();
    }

    public WorkspaceStatusCodeAuthorization(Long id) {
        super(id);
    }

    public WorkspaceStatusCodeAuthorization(Long id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public WorkspaceStatusCodeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public WorkspaceStatusCodeAuthorization(String notes) {
        super(notes);
    }

    public WorkspaceStatusCodeAuthorization(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {

        if (statusCode != null) {
            statusCode = (StatusCode) statusCode.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }

}
