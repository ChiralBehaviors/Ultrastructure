/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
