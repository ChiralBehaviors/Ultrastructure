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
package com.hellblazer.CoRE.attribute;

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
    },
    BINARY {
        @Override
        public Class<?> valueClass() {
            return byte[].class;
        }
    };

    abstract public Class<?> valueClass();
}
