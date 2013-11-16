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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
@Entity
public class WorkspaceStatusCodeAuthorization extends WorkspaceAuthorization {

    private static final long serialVersionUID = 1L;

    {
        setAuthorizationType(WorkspaceAuthorization.PRODUCT_RESOURCE);
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

    public WorkspaceStatusCodeAuthorization(Long id, Resource updatedBy) {
        super(id, updatedBy);
    }

    public WorkspaceStatusCodeAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    public WorkspaceStatusCodeAuthorization(String notes) {
        super(notes);
    }

    public WorkspaceStatusCodeAuthorization(String notes, Resource updatedBy) {
        super(notes, updatedBy);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

}
