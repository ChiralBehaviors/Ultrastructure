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

package com.chiralbehaviors.CoRE.kernel;

import java.util.UUID;

import com.chiralbehaviors.CoRE.UuidGenerator;
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
        AGENCY() {

            @Override
            public String description() {
                return "The abstract notion of a agency. All existential entities defined in the Agency ruleform are instances of 'Agency'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.agency;
            }

        },
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

        };

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public String id() {
            return UuidGenerator.toBase64(new UUID(
                                                   WellKnownTypes.AGENCY.ordinal(),
                                                   ordinal() + 1));
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ANY;
            }

        },
        ATTRIBUTE() {

            @Override
            public String description() {
                return "The abstract notion of an attribute. All existential entities defined in the Attribute ruleform are instances of 'Attribute'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ATTRIBUTE;
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
        NOT_APPLICABLE() {

            @Override
            public String description() {
                return "A special Attribute that stands for 'not applicable'";
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
                return "A special Attribute that stands for the same attribute";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.SAME;
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
        COPY() {

            @Override
            public String description() {
                return "A special Attribute that stands for the copy attribute";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
            }

        },
        ;

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public String id() {
            return UuidGenerator.toBase64(new UUID(
                                                   WellKnownTypes.ATTRIBUTE.ordinal(),
                                                   ordinal() + 1));
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.attribute";
        }

        public ValueType valueType() {
            return ValueType.BOOLEAN;
        }
    }

    public static enum WellKnownInterval implements WellKnownObject {

        INTERVAL() {

            @Override
            public String description() {
                return "The abstract notion of an interval. All existential entities defined in the Interval ruleform are instances of 'Interval'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.INTERVAL;
            }

        },
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

        };

        @Override
        public String id() {
            return UuidGenerator.toBase64(new UUID(
                                                   WellKnownTypes.INTERVAL.ordinal(),
                                                   ordinal() + 1));
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.interval";
        }

        /**
         * @return
         */
        public WellKnownUnit unit() {
            return WellKnownUnit.UNIT;
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
        LOCATION() {

            @Override
            public String description() {
                return "The abstract notion of a location. All existential entities defined in the Location ruleform are instances of 'Location'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.LOCATION;
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

        };

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public String id() {
            return UuidGenerator.toBase64(new UUID(
                                                   WellKnownTypes.LOCATION.ordinal(),
                                                   ordinal() + 1));
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
        ENTITY() {

            @Override
            public String description() {
                return "A special Product that stands for the originally supplied product";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.ENTITY;
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
        PRODUCT() {

            @Override
            public String description() {
                return "The abstract notion of a product. All existential entities defined in the Product ruleform are instances of 'Product'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.PRODUCT;
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
        };

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public String id() {
            return UuidGenerator.toBase64(new UUID(
                                                   WellKnownTypes.PRODUCT.ordinal(),
                                                   ordinal() + 1));
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

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.HEAD_OF;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        IN_WORKSPACE() {

            @Override
            public String description() {
                return "A is in workspace B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.WORKSPACE_OF;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.IN_WORKSPACE;
            }

            @Override
            boolean preferred() {
                return false;
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.MEMBER_OF;
            }

            @Override
            boolean preferred() {
                return true;
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.NOT_APPLICABLE;
            }

            @Override
            boolean preferred() {
                return false;
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.OWNED_BY;
            }

            @Override
            boolean preferred() {
                return false;
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.OWNS;
            }

            @Override
            boolean preferred() {
                return true;
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.PROTOTYPE_OF;
            }

            @Override
            boolean preferred() {
                return true;
            }

        },
        RELATIONSHIP() {

            @Override
            public String description() {
                return "The abstract notion of a relationship. All existential entities defined in the Relationship ruleform are instances of 'Relationsihip'";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.RELATIONSHIP;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.RELATIONSHIP;
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
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
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.VERSION_OF;
            }

            @Override
            boolean preferred() {
                return false;
            }

        },
        WORKSPACE_OF() {

            @Override
            public String description() {
                return "A is a workspace containing B";
            }

            @Override
            public WellKnownRelationship inverse() {
                return WellKnownRelationship.IN_WORKSPACE;
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.WORKSPACE_OF;
            }

            @Override
            boolean preferred() {
                return true;
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

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.COPY;
            }

            @Override
            boolean preferred() {
                return true;
            }

        };

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#id()
         */
        @Override
        public String id() {
            return UuidGenerator.toBase64(new UUID(
                                                   WellKnownTypes.RELATIONSHIP.ordinal(),
                                                   ordinal() + 1));
        }

        abstract public WellKnownRelationship inverse();

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.relationship";
        }

        abstract boolean preferred();
    }

    public static enum WellKnownStatusCode implements WellKnownObject {
        STATUS_CODE() {

            @Override
            public String description() {
                return "The abstract notion of an status code. All existential entities defined in the StatusCode ruleform are instances of 'Status Code'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.STATUS_CODE;
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

        },
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

        };

        @Override
        public String id() {
            return UuidGenerator.toBase64(new UUID(
                                                   WellKnownTypes.STATUS_CODE.ordinal(),
                                                   ordinal() + 1));
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
        UNIT() {

            @Override
            public String description() {
                return "The abstract notion of an unit. All existential entities defined in the Unit ruleform are instances of 'Unit'";
            }

            /* (non-Javadoc)
             * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#productName()
             */
            @Override
            public String wkoName() {
                return WellKnownObject.UNIT;
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

        },
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

        };

        @Override
        public String id() {
            return UuidGenerator.toBase64(new UUID(
                                                   WellKnownTypes.UNIT.ordinal(),
                                                   ordinal() + 1));
        }

        /* (non-Javadoc)
         * @see com.chiralbehaviors.CoRE.kernel.WellKnownObject#tableName()
         */
        @Override
        public String tableName() {
            return "ruleform.unit";
        }
    }

    String STATUS_CODE               = "Status Code";

    String agency                    = "Agency";
    String ANY                       = "(ANY)";
    String ANYTHING                  = "anything";
    String ATTRIBUTE                 = "Attribute";
    String COPY                      = "(COPY)";
    String CONTAINS                  = "contains";
    String COORDINATE                = "Coordinate";
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
    String IN_WORKSPACE              = "in-workspace";
    String INCLUDES                  = "includes";
    String INVERSE_SOFTWARE          = "Inverse Software";
    String INTERVAL                  = "Interval";
    String IS_A                      = "is-a";
    String IS_CONTAINED_IN           = "is-contained-in";
    String IS_EXCEPTION_TO           = "is-exception-to";
    String IS_LOCATION_OF            = "is-location-of";
    String KERNEL_WORKSPACE          = "kernelWorkspace";
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
    String OWNED_BY                  = "ownedBy";
    String OWNS                      = "owns";
    String PASSWORD_HASH             = "password-hash";
    String PRODUCT                   = "PRODUCT";
    String PROPAGATION_SOFTWARE      = "Propagation Software";
    String PROTOTYPE                 = "prototype";
    String PROTOTYPE_OF              = "prototype-of";
    String RELATIONSHIP              = "Relationship";
    String SAME                      = "(SAME)";
    String SPECIAL_SYSTEM_AGENCY     = "Special System Agency";
    String SPECIAL_SYSTEM_EVENT      = "Special System Event";
    String SUPER_USER                = "CoRE Super User";
    String UNIT                      = "Unit";
    String UNSET                     = "(UNSET)";
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
    String id();

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
