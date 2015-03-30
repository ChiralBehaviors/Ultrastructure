/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models.openjpa;

import javax.persistence.EntityManager;

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

    private final Animations    animations;
    private final EntityManager em;

    public LifecycleListener(Animations animations, EntityManager em) {
        this.animations = animations;
        this.em = em;
        OpenJPAEntityManagerSPI openJpaEm = this.em.unwrap(OpenJPAEntityManagerSPI.class);
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