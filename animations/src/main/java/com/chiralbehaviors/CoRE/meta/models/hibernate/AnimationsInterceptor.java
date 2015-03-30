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

package com.chiralbehaviors.CoRE.meta.models.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hibernate.type.Type;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.meta.models.Animations;

/**
 * @author hhildebrand
 *
 */
public class AnimationsInterceptor extends EmptyInterceptor {

    private static final long  serialVersionUID = 1L;

    protected final Animations animations;

    public AnimationsInterceptor(Session session, Animations animations) {
        this.animations = animations;
        try {
            Field interceptorField = SessionImpl.class.getDeclaredField("interceptor");
            interceptorField.setAccessible(true);
            interceptorField.set(session, this);
        } catch (NoSuchFieldException | IllegalArgumentException
                | IllegalAccessException e) {
            throw new IllegalStateException("Unable to set interceptor", e);
        }

    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state,
                         String[] propertyNames, Type[] types) {
        ((Ruleform) entity).delete(animations);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id,
                                Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types) {
        ((Ruleform) entity).update(animations);
        return false;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
                          String[] propertyNames, Type[] types) {
        ((Ruleform) entity).persist(animations);
        return false;
    }
}
