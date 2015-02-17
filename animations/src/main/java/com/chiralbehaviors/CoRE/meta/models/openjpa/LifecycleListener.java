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
import org.apache.openjpa.event.LifecycleEvent;
import org.apache.openjpa.event.PersistListener;
import org.apache.openjpa.event.UpdateListener;

import com.chiralbehaviors.CoRE.Triggers;

/**
 * @author hhildebrand
 *
 */
public class LifecycleListener implements PersistListener,
        UpdateListener, DeleteListener {

    protected final Triggers triggers;

    public LifecycleListener(Triggers triggers) {
        this.triggers = triggers;
    }

    @Override
    public void afterDelete(LifecycleEvent event) {
    }

    @Override
    public void afterPersist(LifecycleEvent paramLifecycleEvent) {
    }

    @Override
    public void afterUpdatePerformed(LifecycleEvent paramLifecycleEvent) {
    }

    @Override
    public void beforeDelete(LifecycleEvent paramLifecycleEvent) {
    }

    @Override
    public void beforePersist(LifecycleEvent paramLifecycleEvent) {
    }

    @Override
    public void beforeUpdate(LifecycleEvent paramLifecycleEvent) {
    }
}