/**
 * Copyright (c) 2014 Halloran Parry, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.object.painter;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hparry
 *
 */
public interface PainterWorkspace {

    StatusCode getNotDoingItYet();

    StatusCode getIrretrievablyFucked();

    StatusCode getDoingIt();

    StatusCode getDone();

    StatusCode getOkPaintingIsComingOut();

    StatusCode getShouldReallyDoSomePrep();

    StatusCode getMightThinkAboutPainting();

    StatusCode getDontWannaPaint();

    Location getStudio();

    Agency getArtist();

    Product getDoStudies();

    Product getPrimeCanvas();

    Product getStretchCanvas();

    Product getCreatePainting();

    Product getStylus();

    Product getCanvas();

    Product getPalette();

    Product getPaint();

    Product getBrush();

}
