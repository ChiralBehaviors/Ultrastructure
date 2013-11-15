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

package com.hellblazer.CoRE.kernel;

import com.hellblazer.CoRE.attribute.ValueType;

/**
 * The repository of the ids of well known objects, as well as string contants
 * for naming them.
 * 
 * @author hhildebrand
 * 
 */
public interface WellKnownObject {

    public static enum WellKnownAttribute implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "A special Attribute that stands for any attribute";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ANY;
            }

        },
        ATTRIBUTE() {

            @Override
            public String description() {
                return "The abstract notion of an attribute. All existential entities defined in the Attribute ruleform are instances of 'Attribute'";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ATTRIBUTE;
            }

        },
        LOGIN() {

            @Override
            public String description() {
                return "The Attribute that contains the CoRE login name of the resource";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.LOGIN;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.TEXT;
            }

        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special Attribute that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        ORIGINAL() {

            @Override
            public String description() {
                return "A special Attribute that stands for the originally supplied attribute";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ORIGINAL;
            }

        },
        PASSWORD_HASH() {

            @Override
            public String description() {
                return "The Attribute that contains the password hash of the CoRE user";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.PASSWORD_HASH;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.TEXT;
            }

        };

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public Long id() {
            return Long.valueOf(ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.attribute";
        }

        public ValueType valueType() {
            return ValueType.BOOLEAN;
        }
    }

    public static enum WellKnownLocation implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "This is used in protocol rules to indicate that any Location will satisfy the conditions for that rule";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ANY;
            }

        },
        LOCATION() {

            @Override
            public String description() {
                return "The abstract notion of a location. All existential entities defined in the Location ruleform are instances of 'Location'";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.LOCATION;
            }

        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special Location that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        ORIGINAL() {

            @Override
            public String description() {
                return "A special Location that stands for the originally supplied location";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ORIGINAL;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "A special Location that stands for the same location";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.SAME;
            }

        };

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public Long id() {
            return Long.valueOf(ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.location";
        }
    }

    public static enum WellKnownProduct implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "A special Product that stands for any product";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ANY;
            }

        },
        ENTITY() {

            @Override
            public String description() {
                return "A special Product that stands for the originally supplied product";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ENTITY;
            }

        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "Special product that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        ORIGINAL() {

            @Override
            public String description() {
                return "A special Product that stands for the originally supplied product";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ORIGINAL;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "Special product that stands for the same product supplied";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.SAME;
            }

        };

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public Long id() {
            return Long.valueOf(ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.product";
        }
    }

    public static enum WellKnownRelationship implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "A special Relationship that stands for any relationship";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.ANY;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ANY;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        CONTAINS() {

            @Override
            public String description() {
                return "A contains B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.IS_CONTAINED_IN;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.CONTAINS;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        DEVELOPED() {

            @Override
            public String description() {
                return "A developed B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.DEVELOPED_BY;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.DEVELOPED;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        DEVELOPED_BY() {

            @Override
            public String description() {
                return "A is developed by B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.DEVELOPED;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.DEVELOPED_BY;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        EQUALS() {

            @Override
            public String description() {
                return "A equals B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.EQUALS;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.EQUALS;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        FORMER_MEMBER_OF() {

            @Override
            public String description() {
                return "A is a former member of B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.HAD_MEMBER;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.FORMER_MEMBER_OF;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        GREATER_THAN() {

            @Override
            public String description() {
                return "A > B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.LESS_THAN;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.GREATER_THAN;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        GREATER_THAN_OR_EQUAL() {

            @Override
            public String description() {
                return "A >= B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.LESS_THAN_OR_EQUAL;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.GREATER_THAN_OR_EQUALS;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        HAD_MEMBER() {

            @Override
            public String description() {
                return "A had member B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.FORMER_MEMBER_OF;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.HAD_MEMBER;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        HAS_EXCEPTION() {

            @Override
            public String description() {
                return "A has exception B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.IS_EXCEPTION_TO;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.HAS_EXCEPTION;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        HAS_HEAD() {

            @Override
            public String description() {
                return "The leader of A is B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.HEAD_OF;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.HAS_HEAD;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        HAS_MEMBER() {

            @Override
            public String description() {
                return "A has member B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.MEMBER_OF;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.HAS_MEMBER;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        HAS_VERSION() {

            @Override
            public String description() {
                return "A has version B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.VERSION_OF;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.HAS_VERSION;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        HEAD_OF() {

            @Override
            public String description() {
                return "A is the head of B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.HAS_HEAD;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.HEAD_OF;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        INCLUDES() {

            @Override
            public String description() {
                return "A includes B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.IS_A;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.INCLUDES;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        IS_A() {

            @Override
            public String description() {
                return "A is a B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.INCLUDES;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.IS_A;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        IS_CONTAINED_IN() {

            @Override
            public String description() {
                return "A is contained in B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.CONTAINS;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.IS_CONTAINED_IN;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        IS_EXCEPTION_TO() {

            @Override
            public String description() {
                return "A is exception to B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.HAS_EXCEPTION;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.IS_EXCEPTION_TO;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        IS_LOCATION_OF() {

            @Override
            public String description() {
                return "A is the location of B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.MAPS_TO_LOCATION;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.IS_LOCATION_OF;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        LESS_THAN() {

            @Override
            public String description() {
                return "A < B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.GREATER_THAN;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.LESS_THAN;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        LESS_THAN_OR_EQUAL() {

            @Override
            public String description() {
                return "A <= B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.GREATER_THAN_OR_EQUAL;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.LESS_THAN_OR_EQUALS;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        MAPS_TO_LOCATION() {

            @Override
            public String description() {
                return "A maps to location B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.IS_LOCATION_OF;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.MAPS_TO_LOCATION;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        MEMBER_OF() {

            @Override
            public String description() {
                return "A is a member of B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.HAS_MEMBER;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.MEMBER_OF;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        OWNS() {
            @Override
            public String description() {
                return "A owns B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.OWNED_BY;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.OWNS;
            }

            @Override
            boolean preferred() {
                return true;
            }
        },
        OWNED_BY() {
            @Override
            public String description() {
                return "A is owned by B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.OWNS;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.OWNED_BY;
            }

            @Override
            boolean preferred() {
                return false;
            }
        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A is not applicable to B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.NOT_APPLICABLE;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        PROTOTYPE() {

            @Override
            public String description() {
                return "A's prototype is B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.PROTOTYPE_OF;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.PROTOTYPE;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        PROTOTYPE_OF() {

            @Override
            public String description() {
                return "A is the prototype of B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.PROTOTYPE;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.PROTOTYPE_OF;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "Special relationship used in metarule tables to indicate that no network transformation should be performed";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.SAME;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.SAME;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        VERSION_OF() {

            @Override
            public String description() {
                return "A is a version of B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.HAS_VERSION;
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.VERSION_OF;
            }

            @Override
            boolean preferred() {
                return false;
            }

        };

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public Long id() {
            return Long.valueOf(ordinal() + 1);
        }

        abstract public WellKnownRelationship inverse();

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.relationship";
        }

        abstract boolean preferred();
    }

    public static enum WellKnownResource implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "A special Resource that stands for any resource";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ANY;
            }

        },
        CORE() {

            @Override
            public String description() {
                return "The CoRE Ultra-Structure system";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.CORE;
            }

        },
        CORE_ANIMATION_SOFTWARE() {

            @Override
            public String description() {
                return "General software component of the CoRE Ultra-Structure system";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.CORE_ANIMATION_SOFTWARE;
            }

        },
        CORE_MODEL() {

            @Override
            public String description() {
                return "The animation proceedure that implements the CoRE meta model behavior";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.CORE_MODEL;
            }

        },
        CORE_USER() {

            @Override
            public String description() {
                return "Users allowed to log into the CoRE system";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.CORE_USER;
            }

        },
        INVERSE_SOFTWARE() {

            @Override
            public String description() {
                return "The process that creates inverse network relationships";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.INVERSE_SOFTWARE;
            }

        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special Resource that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        ORIGINAL() {

            @Override
            public String description() {
                return "A special Resource that stands for the originally supplied resource";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.ORIGINAL;
            }

        },
        PROPAGATION_SOFTWARE() {

            @Override
            public String description() {
                return "Animation procedure that performs logical deduction on network ruleforms";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.PROPAGATION_SOFTWARE;
            }

        },
        RESOURCE() {

            @Override
            public String description() {
                return "The abstract notion of a resource. All existential entities defined in the Resource ruleform are instances of 'Resource'";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.RESOURCE;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "Special relationship used in metarule tables to indicate that no network transformation should be performed";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.SAME;
            }

        },
        SPECIAL_SYSTEM_RESOURCE() {

            @Override
            public String description() {
                return "Privileged Resources that have special meaning in the CoRE System";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.SPECIAL_SYSTEM_RESOURCE;
            }

        },
        SUPER_USER() {

            @Override
            public String description() {
                return "The god user that we can actually use to authenticate and log into the system";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.SUPER_USER;
            }

        };

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public Long id() {
            return Long.valueOf(ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.resource";
        }
    }

    public static enum WellKnownStatusCode implements WellKnownObject {
        UNSET() {

            @Override
            public String description() {
                return "The status code which indicates the status code is not set";
            }

            /* (non-Javadoc)
             * @see com.hellblazer.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String productName() {
                return WellKnownObject.UNSET;
            }

        };

        @Override
        public Long id() {
            return Long.valueOf(ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.hellblazer.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.status_code";
        }
    }

    String ANY                       = "(ANY)";
    String ANYTHING                  = "anything";
    String ATTRIBUTE                 = "Attribute";
    String CONTAINS                  = "contains";
    String CORE                      = "CoRE";
    String CORE_ANIMATION_SOFTWARE   = "CoRE Animation Software";
    String CORE_MODEL                = "CoRE Model";
    String CORE_USER                 = "CoRE User";
    String DEVELOPED                 = "developed";
    String DEVELOPED_BY              = "developed-by";
    String ENTITY                    = "Product";
    String EQUALS                    = "=";
    String EVENT                     = "Event";
    String FORMER_MEMBER_OF          = "former-member-of";
    String GREATER_THAN              = ">";
    String GREATER_THAN_OR_EQUALS    = ">=";
    String HAD_MEMBER                = "had-member";
    String HAS_EXCEPTION             = "has-exception";
    String HAS_HEAD                  = "has-head";
    String HAS_MEMBER                = "has-member";
    String HAS_VERSION               = "has-version";
    String HEAD_OF                   = "head-of";
    String INCLUDES                  = "includes";
    String INVERSE_SOFTWARE          = "Inverse Software";
    String IS_A                      = "is-a";
    String IS_CONTAINED_IN           = "is-contained-in";
    String IS_EXCEPTION_TO           = "is-exception-to";
    String IS_LOCATION_OF            = "is-location-of";
    String LESS_THAN                 = "<";
    String LESS_THAN_OR_EQUALS       = "<=";
    String LOCATION                  = "Location";
    String LOCATION_CONTEXT          = "LocationContext";
    String LOCATION_PROTOTYPE_COPIER = "Location Protototype Copier";
    String LOGIN                     = "login";
    String MAPS_TO_LOCATION          = "maps-to-location";
    String MEMBER                    = "member";
    String MEMBER_OF                 = "member-of";
    String NOT_APPLICABLE            = "(N/A)";
    String ORIGINAL                  = "(Original)";
    String OWNED_BY                  = "ownedBy";
    String OWNS                      = "owns";
    String PASSWORD_HASH             = "password-hash";
    String PROPAGATION_SOFTWARE      = "Propagation Software";
    String PROTOTYPE                 = "prototype";
    String PROTOTYPE_OF              = "prototype-of";
    String RELATIONSHIP              = "Relationship";
    String RESOURCE                  = "Resource";
    String SAME                      = "(SAME)";
    String SPECIAL_SYSTEM_EVENT      = "Special System Event";
    String SPECIAL_SYSTEM_RESOURCE   = "Special System Resource";
    String SUPER_USER                = "CoRE Super User";
    String UNSET                     = "(UNSET)";
    String VERSION_OF                = "version-of";

    /**
     * @return the descriptions of the wko
     */
    String description();

    /**
     * 
     * @return the id of the wko
     */
    Long id();

    /**
     * 
     * @return the name of the wko
     */
    String productName();

    /**
     * 
     * @return the table name of the class of wko
     */
    String tableName();
}
