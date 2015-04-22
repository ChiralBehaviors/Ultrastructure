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

package com.chiralbehaviors.CoRE;

import java.util.UUID;

import com.chiralbehaviors.CoRE.attribute.ValueType;

/**
 * The repository of the ids of well known objects, as well as string constants
 * for naming them.
 *
 * @author hhildebrand
 *
 */
public interface WellKnownObject {

    public static enum WellKnownAgency implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "A special Agency that stands for any agency";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ANY;
            }

        },
        COPY() {

            @Override
            public String description() {
                return "A special Agency that stands for copy agency";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
            }

        },
        CORE() {

            @Override
            public String description() {
                return "The CoRE Ultra-Structure system";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.CORE;
            }

        },
        CORE_ANIMATION_SOFTWARE() {

            @Override
            public String description() {
                return "General software component of the CoRE Ultra-Structure system";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.CORE_ANIMATION_SOFTWARE;
            }

        },
        CORE_MODEL() {

            @Override
            public String description() {
                return "The animation proceedure that implements the CoRE meta model behavior";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.CORE_MODEL;
            }

        },
        CORE_USER() {

            @Override
            public String description() {
                return "Users allowed to log into the CoRE system";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.CORE_USER;
            }

        },
        INVERSE_SOFTWARE() {

            @Override
            public String description() {
                return "The process that creates inverse network relationships";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.INVERSE_SOFTWARE;
            }

        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special Agency that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        PROPAGATION_SOFTWARE() {

            @Override
            public String description() {
                return "Animation procedure that performs logical deduction on network ruleforms";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.PROPAGATION_SOFTWARE;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "Special relationship used in metarule tables to indicate that no network transformation should be performed";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SAME;
            }

        },
        SPECIAL_SYSTEM_AGENCY() {

            @Override
            public String description() {
                return "Privileged agencys that have special meaning in the CoRE System";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SPECIAL_SYSTEM_AGENCY;
            }

        },
        SUPER_USER() {

            @Override
            public String description() {
                return "The god user that we can actually use to authenticate and log into the system";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SUPER_USER;
            }

        };

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public UUID id() {
            return new UUID(WellKnownTypes.AGENCY.ordinal(), ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.agency";
        }
    }

    public static enum WellKnownAttribute implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "A special Attribute that stands for any attribute";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.TEXT;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ANY;
            }

        },
        COPY() {

            @Override
            public String description() {
                return "A special Attribute that stands for the copy attribute";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.TEXT;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
            }

        },
        LOGIN() {

            @Override
            public String description() {
                return "The Attribute that contains the CoRE login name of the agency";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.TEXT;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.LOGIN;
            }

        },
        NAMESPACE() {

            @Override
            public String description() {
                return "A special Attribute that stands for the namespace attribute";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.TEXT;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NAMESPACE;
            }

        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special Attribute that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.TEXT;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        PASSWORD_HASH() {

            @Override
            public String description() {
                return "The Attribute that contains the password hash of the CoRE user";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.TEXT;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.PASSWORD_HASH;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "A special Attribute that stands for the same attribute";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.TEXT;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SAME;
            }

        },
        NULLABLE() {

            @Override
            public String description() {
                return "A special Attribute that indicates whether or not an attribute value is required";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute#valueType()
             */
            @Override
            public ValueType valueType() {
                return ValueType.BOOLEAN;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NULLABLE;
            }

        };

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public UUID id() {
            return new UUID(WellKnownTypes.ATTRIBUTE.ordinal(), ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.attribute";
        }

        abstract public ValueType valueType();
    }

    public static enum WellKnownInterval implements WellKnownObject {

        ANY() {

            @Override
            public String description() {
                return "A special Interval that stands for any interval";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#wkoName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ANY;
            }

        },
        COPY() {

            @Override
            public String description() {
                return "A special Interval that stands for copy interval";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
            }

        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special Interval that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NOT_APPLICABLE;
            }
        },
        SAME() {

            @Override
            public String description() {
                return "A special Interval that stands for the same interval";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#wkoName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SAME;
            }

        };

        public WellKnownUnit durationUnit() {
            return WellKnownUnit.NOT_APPLICABLE;
        }

        @Override
        public UUID id() {
            return new UUID(WellKnownTypes.INTERVAL.ordinal(), ordinal() + 1);
        }

        /**
         * @return
         */
        public WellKnownUnit startUnit() {
            return WellKnownUnit.NOT_APPLICABLE;
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.interval";
        }
    }

    public static enum WellKnownLocation implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "This is used in protocol rules to indicate that any Location will satisfy the conditions for that rule";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ANY;
            }

        },
        COPY() {

            @Override
            public String description() {
                return "A special Location that stands for copy location";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
            }

        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special Location that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "A special Location that stands for the same location";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SAME;
            }

        };

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public UUID id() {
            return new UUID(WellKnownTypes.LOCATION.ordinal(), ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ANY;
            }

        },
        COPY() {

            @Override
            public String description() {
                return "A special Product that stands for copy product";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
            }

        },
        KERNEL_WORKSPACE() {
            @Override
            public String description() {
                return "The defining product of the Kernel workspace";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.KERNEL_WORKSPACE;
            }
        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "Special product that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "Special product that stands for the same product supplied";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SAME;
            }

        },
        WORKSPACE() {
            @Override
            public String description() {
                return "Special product that parents the network of objects that make a workspace";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.WORKSPACE;
            }
        };

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public UUID id() {
            return new UUID(WellKnownTypes.PRODUCT.ordinal(), ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ANY;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.CONTAINS;
            }

        },
        COPY() {

            @Override
            public String description() {
                return "The well known Relationship copy that represents the copy relationship";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.COPY;
            }

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.DEVELOPED;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.DEVELOPED_BY;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.EQUALS;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.FORMER_MEMBER_OF;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.GREATER_THAN;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.GREATER_THAN_OR_EQUALS;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.HAD_MEMBER;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.HAS_EXCEPTION;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.HAS_HEAD;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.HAS_MEMBER;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.HAS_VERSION;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.HEAD_OF;
            }

        },
        IMPORTED_BY() {

            @Override
            public String description() {
                return "A is imported by B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.IMPORTS;
            }

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.IMPORTED_BY;
            }

        },
        IMPORTS() {

            @Override
            public String description() {
                return "A imports B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.IMPORTED_BY;
            }

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.IMPORTS;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.INCLUDES;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.IS_A;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.IS_CONTAINED_IN;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.IS_EXCEPTION_TO;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.IS_LOCATION_OF;
            }

        },
        IS_VALIDATED_BY() {

            @Override
            public String description() {
                return "A is validated by B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.VALIDATES;
            }

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.IS_VALIDATED_BY;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.LESS_THAN;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.LESS_THAN_OR_EQUALS;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.MAPS_TO_LOCATION;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.MEMBER_OF;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NOT_APPLICABLE;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.OWNED_BY;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.OWNS;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.PROTOTYPE;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.PROTOTYPE_OF;
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

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SAME;
            }

        },
        VALIDATES() {

            @Override
            public String description() {
                return "A is the location of B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.IS_VALIDATED_BY;
            }

            @Override
            public boolean preferred() {
                return true;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.VALIDATES;
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

            @Override
            public boolean preferred() {
                return false;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.VERSION_OF;
            }

        };

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public UUID id() {
            return new UUID(WellKnownTypes.RELATIONSHIP.ordinal(),
                            ordinal() + 1);
        }

        abstract public WellKnownRelationship inverse();

        public abstract boolean preferred();

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.relationship";
        }
    }

    public static enum WellKnownStatusCode implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "A special Status Code that stands for any status code";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#wkoName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ANY;
            }

        },
        COPY() {

            @Override
            public String description() {
                return "A special StatusCode that stands for copy status code";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
            }
        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special StatusCode that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "A special StatusCode that stands for the same status code";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#wkoName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SAME;
            }

        },
        UNSET() {

            @Override
            public String description() {
                return "The status code which indicates the status code is not set";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.UNSET;
            }

        };

        @Override
        public UUID id() {
            return new UUID(WellKnownTypes.STATUS_CODE.ordinal(), ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.status_code";
        }
    }

    public static enum WellKnownTypes {
        AGENCY, ATTRIBUTE, INTERVAL, LOCATION, PRODUCT, RELATIONSHIP,
        STATUS_CODE, UNIT;
    }

    public static enum WellKnownUnit implements WellKnownObject {
        ANY() {

            @Override
            public String description() {
                return "A special Unit that stands for any unit";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#wkoName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ANY;
            }

        },
        COPY() {

            @Override
            public String description() {
                return "A special Unit that stands for copy unit";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
            }

        },
        DAYS() {

            @Override
            public String description() {
                return "The time unit Minutes";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.DAYS;
            }

        },
        HOURS() {

            @Override
            public String description() {
                return "The time unit Hours";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.HOURS;
            }

        },
        MICROSECONDS() {

            @Override
            public String description() {
                return "The time unit Microseconds";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.MICROSECONDS;
            }

        },
        MILLISECONDS() {

            @Override
            public String description() {
                return "The time unit Milliseconds";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.MILLISECONDS;
            }

        },
        MINUTES() {

            @Override
            public String description() {
                return "The time unit Minutes";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.MINUTES;
            }

        },
        NANOSECONDS() {

            @Override
            public String description() {
                return "The time unit Nanoseconds";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NANOSECONDS;
            }

        },
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special Unit that stands for 'not applicable'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

        },
        SAME() {

            @Override
            public String description() {
                return "A special Unit that stands for the same unit";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#wkoName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SAME;
            }

        },
        SECONDS() {

            @Override
            public String description() {
                return "The time unit Seconds";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SECONDS;
            }

        },
        UNSET() {

            @Override
            public String description() {
                return "The undefined unit";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.UNSET;
            }

        };

        @Override
        public UUID id() {
            return new UUID(WellKnownTypes.UNIT.ordinal(), ordinal() + 1);
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.unit";
        }
    }

    String ANY                       = "(ANY)";
    String ANYTHING                  = "anything";
    String CONTAINS                  = "contains";
    String COORDINATE                = "Coordinate";
    String COPY                      = "(COPY)";
    String CORE                      = "CoRE";
    String CORE_ANIMATION_SOFTWARE   = "CoRE Animation Software";
    String CORE_MODEL                = "CoRE Model";
    String CORE_USER                 = "CoRE User";
    String DAYS                      = "days";
    String DEVELOPED                 = "developed";
    String DEVELOPED_BY              = "developed-by";
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
    String HOURS                     = "Hours";
    String IMPORTED_BY               = "imported by";
    String IMPORTS                   = "imports";
    String IN_WORKSPACE              = "in-workspace";
    String INCLUDES                  = "includes";
    String INVERSE_SOFTWARE          = "Inverse Software";
    String IS_A                      = "is-a";
    String IS_CONTAINED_IN           = "is-contained-in";
    String IS_EXCEPTION_TO           = "is-exception-to";
    String IS_LOCATION_OF            = "is-location-of";
    String IS_VALIDATED_BY           = "is-validated-by";
    String KERNEL_WORKSPACE          = "kernelWorkspace";
    String LESS_THAN                 = "<";
    String LESS_THAN_OR_EQUALS       = "<=";
    String LOCATION_CONTEXT          = "LocationContext";
    String LOCATION_PROTOTYPE_COPIER = "Location Protototype Copier";
    String LOGIN                     = "login";
    String MAPS_TO_LOCATION          = "maps-to-location";
    String MEMBER                    = "member";
    String MEMBER_OF                 = "member-of";
    String MICROSECONDS              = "Microseconds";
    String MILLISECONDS              = "Milliseonds";
    String MINUTES                   = "Minutes";
    String NAMESPACE                 = "namespace";
    String NANOSECONDS               = "Nanoseconds";
    String NOT_APPLICABLE            = "(N/A)";
    String NULLABLE                  = "Nullable";
    String OWNED_BY                  = "ownedBy";
    String OWNS                      = "owns";
    String PASSWORD_HASH             = "password-hash";
    String PROPAGATION_SOFTWARE      = "Propagation Software";
    String PROTOTYPE                 = "prototype";
    String PROTOTYPE_OF              = "prototype-of";
    String SAME                      = "(SAME)";
    String SECONDS                   = "Seconds";
    String SPECIAL_SYSTEM_AGENCY     = "Special System Agency";
    String SPECIAL_SYSTEM_EVENT      = "Special System Event";
    String SUPER_USER                = "CoRE Super User";
    String UNSET                     = "(UNSET)";
    String VALIDATES                 = "validates";
    String VERSION_OF                = "version-of";
    String WORKSPACE                 = "Workspace";
    String WORKSPACE_OF              = "workspace-of";

    /**
     * @return the descriptions of the wko
     */
    String description();

    /**
     *
     * @return the id of the wko
     */
    UUID id();

    /**
     *
     * @return the table name of the class of wko
     */
    String tableName();

    /**
     *
     * @return the name of the wko
     */
    String wkoName();
}
