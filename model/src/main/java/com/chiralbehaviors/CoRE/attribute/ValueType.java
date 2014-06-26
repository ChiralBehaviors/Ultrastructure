/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.attribute;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * NOTE: DO NOT REORDER THE ENUMERATIONS IN THIS CLASS. Unfortunately, the
 * orderings are hard coded until we can find a better way of doing this.
 *
 * @author hhildebrand
 *
 */
public enum ValueType {
    BINARY {
        @Override
        public Class<?> valueClass() {
            return byte[].class;
        }
    },
    BOOLEAN {
        @Override
        public Class<?> valueClass() {
            return Boolean.class;
        }
    },
    INTEGER {
        @Override
        public Class<?> valueClass() {
            return Integer.class;
        }
    },
    NUMERIC {
        @Override
        public Class<?> valueClass() {
            return BigDecimal.class;
        }
    },
    TEXT {
        @Override
        public Class<?> valueClass() {
            return String.class;
        }
    },
    TIMESTAMP {
        @Override
        public Class<?> valueClass() {
            return Timestamp.class;
        }
    };

    abstract public Class<?> valueClass();
}
