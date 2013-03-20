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
package com.hellblazer.CoRE.access.health;

import javax.persistence.EntityManagerFactory;

import com.yammer.metrics.core.HealthCheck;

/**
 * @author hhildebrand
 * 
 */
public class JpaHealthCheck extends HealthCheck {
    private final EntityManagerFactory emf;

    public JpaHealthCheck(EntityManagerFactory emf) {
        super("CRUD");
        this.emf = emf;
    }

    /* (non-Javadoc)
     * @see com.yammer.metrics.core.HealthCheck#check()
     */
    @Override
    protected Result check() throws Exception {
        return emf.isOpen() ? Result.healthy()
                           : Result.unhealthy("Entity Manager Factory offline");
    }

}
