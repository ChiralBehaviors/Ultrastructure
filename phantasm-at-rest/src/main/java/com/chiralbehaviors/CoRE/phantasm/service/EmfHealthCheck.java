/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.phantasm.service;

import javax.persistence.EntityManagerFactory;

import com.codahale.metrics.health.HealthCheck;

/**
 * @author hhildebrand
 *
 */
public class EmfHealthCheck extends HealthCheck {
    public EmfHealthCheck(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private final EntityManagerFactory emf;

    /* (non-Javadoc)
     * @see com.codahale.metrics.health.HealthCheck#check()
     */
    @Override
    protected Result check() throws Exception {
        return emf.isOpen() ? Result.healthy()
                           : Result.unhealthy("Entity Manager Factory is closed");
    }

}
