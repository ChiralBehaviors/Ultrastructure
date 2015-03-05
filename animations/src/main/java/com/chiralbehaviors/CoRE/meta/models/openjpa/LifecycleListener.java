/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.models.openjpa;

import org.apache.openjpa.event.DeleteListener;
import org.apache.openjpa.event.DirtyListener;
import org.apache.openjpa.event.LifecycleEvent;
import org.apache.openjpa.event.PersistListener;
import org.apache.openjpa.event.UpdateListener;
import org.apache.openjpa.persistence.OpenJPAEntityManagerSPI;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.meta.models.Animations;

/**
 * @author hhildebrand
 *
 */
public class LifecycleListener implements PersistListener, UpdateListener,
        DeleteListener, DirtyListener {

    protected final Animations animations;

    public LifecycleListener(Animations animations) {
        this.animations = animations;
        OpenJPAEntityManagerSPI openJpaEm = animations.getEm().unwrap(OpenJPAEntityManagerSPI.class);
        openJpaEm.addLifecycleListener(this, (Class[]) null);
    }

    @Override
    public void afterDelete(LifecycleEvent event) {
        ((Ruleform) event.getSource()).delete(animations);
    }

    @Override
    public void afterPersist(LifecycleEvent event) {
    }

    @Override
    public void afterUpdatePerformed(LifecycleEvent event) {
    }

    @Override
    public void beforeDelete(LifecycleEvent event) {
    }

    @Override
    public void beforePersist(LifecycleEvent event) {
        ((Ruleform) event.getSource()).persist(animations);
    }

    @Override
    public void beforeUpdate(LifecycleEvent event) {
        ((Ruleform) event.getSource()).update(animations);
    }

    @Override
    public void afterDirty(LifecycleEvent event) {
        ((Ruleform) event.getSource()).update(animations);
    }

    @Override
    public void afterDirtyFlushed(LifecycleEvent event) {
    }

    @Override
    public void beforeDirty(LifecycleEvent event) {
    }

    @Override
    public void beforeDirtyFlushed(LifecycleEvent event) {
    }
}