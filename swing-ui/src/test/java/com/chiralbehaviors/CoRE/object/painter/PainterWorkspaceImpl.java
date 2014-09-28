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

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hparry
 *
 */
public class PainterWorkspaceImpl implements PainterWorkspace {

    private Product       brush;
    private Product       paint;
    private Product       palette;
    private Product       canvas;
    private Product       stylus;

    //services
    private Product       createPainting;
    private Product       stretchCanvas;
    private Product       primeCanvas;
    private Product       doStudies;

    private Agency        artist;

    private Location      studio;

    private StatusCode    dontWannaPaint;
    private StatusCode    mightThinkAboutPainting;
    private StatusCode    shouldReallyDoSomePrep;
    private StatusCode    okPaintingIsComingOut;
    private StatusCode    done;
    private StatusCode    doingIt;
    private StatusCode    irretrievablyFucked;
    private StatusCode    notDoingItYet;
    private EntityManager em;
    private Model         model;

    public PainterWorkspaceImpl(EntityManager em, Model model) {
        this.em = em;
        this.model = model;
    }

    /**
     * 
     */
    public void load() {
        loadAgencies();
        loadProducts();
        loadLocations();
        loadStatusCodes();
    }

    /**
     * 
     */
    private void loadLocations() {
        studio = new Location("Studio", "The studio supertype", artist);
        em.persist(studio);

    }

    /**
     * 
     */
    private void loadAgencies() {
        artist = new Agency("Artist", "The artist supertype",
                            model.getKernel().getCore());
        em.persist(artist);

    }

    /**
     * 
     */
    private void loadStatusCodes() {
        dontWannaPaint = new StatusCode(
                                        "DontWannaPaint",
                                        "The 'I don't want to paint yet' status",
                                        artist);
        em.persist(dontWannaPaint);
        mightThinkAboutPainting = new StatusCode(
                                                 "MightThinkAboutPainting",
                                                 "I might think about painting but I'm not there yet status",
                                                 artist);
        em.persist(mightThinkAboutPainting);
        shouldReallyDoSomePrep = new StatusCode(
                                                "ShouldReallyDoSomePrep",
                                                "The 'I should really do some prep work for this painting' status",
                                                artist);
        em.persist(shouldReallyDoSomePrep);
        okPaintingIsComingOut = new StatusCode(
                                               "OkPaintingIsComingOut",
                                               "The 'Hey look a painting is being made' status",
                                               artist);
        em.persist(okPaintingIsComingOut);
        done = new StatusCode("Done", "The done status", artist);
        em.persist(done);
        doingIt = new StatusCode("DoingIt", "The doing it status", artist);
        em.persist(doingIt);
        irretrievablyFucked = new StatusCode(
                                             "IrretrievablyFucked",
                                             "The 'Failed is such a boring word' status",
                                             artist);
        em.persist(irretrievablyFucked);
        notDoingItYet = new StatusCode("NotDoingItYet",
                                       "The 'Procrastination abounds' status",
                                       artist);
        em.persist(notDoingItYet);

    }

    /**
     * 
     */
    private void loadProducts() {

        brush = new Product("Brush", "The paintbrush supertype", artist);
        em.persist(brush);
        paint = new Product("Paint", "The paint supertype", artist);
        em.persist(paint);
        palette = new Product("Palette", "The palette supertype", artist);
        em.persist(palette);
        canvas = new Product("Canvas", "The canvas supertype", artist);
        em.persist(canvas);
        stylus = new Product("Stylus", "The stylus supertype", artist);
        em.persist(stylus);
        createPainting = new Product("CreatePainting",
                                     "The create painting service", artist);
        em.persist(createPainting);
        stretchCanvas = new Product("StretchCanvas",
                                    "The stretch canvas service", artist);
        em.persist(stretchCanvas);
        primeCanvas = new Product("PrimeCanvas", "The prime canvas service",
                                  artist);
        em.persist(primeCanvas);
        doStudies = new Product("doStudies", "The do studies service", artist);
        em.persist(doStudies);

    }

    /**
     * @return the brush
     */
    @Override
    public Product getBrush() {
        return brush;
    }

    /**
     * @return the paint
     */
    @Override
    public Product getPaint() {
        return paint;
    }

    /**
     * @return the palette
     */
    @Override
    public Product getPalette() {
        return palette;
    }

    /**
     * @return the canvas
     */
    @Override
    public Product getCanvas() {
        return canvas;
    }

    /**
     * @return the stylus
     */
    @Override
    public Product getStylus() {
        return stylus;
    }

    /**
     * @return the createPainting
     */
    @Override
    public Product getCreatePainting() {
        return createPainting;
    }

    /**
     * @return the stretchCanvas
     */
    @Override
    public Product getStretchCanvas() {
        return stretchCanvas;
    }

    /**
     * @return the primeCanvas
     */
    @Override
    public Product getPrimeCanvas() {
        return primeCanvas;
    }

    /**
     * @return the doStudies
     */
    @Override
    public Product getDoStudies() {
        return doStudies;
    }

    /**
     * @return the artist
     */
    @Override
    public Agency getArtist() {
        return artist;
    }

    /**
     * @return the studio
     */
    @Override
    public Location getStudio() {
        return studio;
    }

    /**
     * @return the dontWannaPaint
     */
    @Override
    public StatusCode getDontWannaPaint() {
        return dontWannaPaint;
    }

    /**
     * @return the mightThinkAboutPainting
     */
    @Override
    public StatusCode getMightThinkAboutPainting() {
        return mightThinkAboutPainting;
    }

    /**
     * @return the shouldReallyDoSomePrep
     */
    @Override
    public StatusCode getShouldReallyDoSomePrep() {
        return shouldReallyDoSomePrep;
    }

    /**
     * @return the okPaintingIsComingOut
     */
    @Override
    public StatusCode getOkPaintingIsComingOut() {
        return okPaintingIsComingOut;
    }

    /**
     * @return the done
     */
    @Override
    public StatusCode getDone() {
        return done;
    }

    /**
     * @return the doingIt
     */
    @Override
    public StatusCode getDoingIt() {
        return doingIt;
    }

    /**
     * @return the irretrievablyFucked
     */
    @Override
    public StatusCode getIrretrievablyFucked() {
        return irretrievablyFucked;
    }

    /**
     * @return the notDoingItYet
     */
    @Override
    public StatusCode getNotDoingItYet() {
        return notDoingItYet;
    }

}
