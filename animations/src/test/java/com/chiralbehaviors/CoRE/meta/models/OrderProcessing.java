/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.meta.models;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public interface OrderProcessing {

    StatusCode getAbandoned();

    Product getABC486();

    StatusCode getActive();

    StatusCode getAvailable();

    Location getBHT37();

    Agency getBillingComputer();

    Agency getCarfleurBon();

    Product getCheckCredit();

    StatusCode getCompleted();

    Product getDeliver();

    Location getFactory1();

    Agency getFactory1Agency();

    Product getFee();

    Agency getGeorgetownUniversity();

    Agency getOrderFullfillment();

    Agency getOrgA();

    Product getPick();

    Product getPrintPurchaseOrder();

    Location getRC31();

    Location getRSB225();

    Product getShip();

    StatusCode getWaitingOnPurchaseOrder();

}
