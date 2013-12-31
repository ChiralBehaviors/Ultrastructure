/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.meta;

import java.util.Collection;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.unit.Unit;
import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.ProductChildSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductParentSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductSiblingSequencingAuthorization;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.meta.graph.Graph;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public interface WorkspaceModel {

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #Agency} that are referred to by the workspace
     *         relationship
     */
    Collection<Agency> getAgencies(Product workspace, Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #Attribute} that are referred to by the workspace
     *         relationship
     */
    Collection<Attribute> getAttributes(Product workspace,
                                        Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #Job} that are referred to by the workspace
     *         relationship
     */
    Collection<Job> getJobs(Product workspace, Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #Location} that are referred to by the workspace
     *         relationship
     */
    Collection<Location> getLocations(Product workspace,
                                      Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #MetaProtocol} that are referred to by the workspace
     *         relationship
     */
    Collection<MetaProtocol> getMetaProtocols(Product workspace,
                                              Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #ProductChildSequencingAuthorization} that are
     *         referred to by the workspace relationship
     */
    Collection<ProductChildSequencingAuthorization> getProductChildSequencingAuthorizations(Product workspace,
                                                                                            Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #ProductParentSequencingAuthorization} that are
     *         referred to by the workspace relationship
     */
    Collection<ProductParentSequencingAuthorization> getProductParentSequencingAuthorizations(Product workspace,
                                                                                              Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #Product} that are referred to by the workspace
     *         relationship
     */
    Collection<Product> getProducts(Product workspace, Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #ProductSiblingSequencingAuthorization} that are
     *         referred to by the workspace relationship
     */
    Collection<ProductSiblingSequencingAuthorization> getProductSiblingSequencingAuthorizations(Product workspace,
                                                                                                Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #Protocol} that are referred to by the workspace
     *         relationship
     */
    Collection<Protocol> getProtocols(Product workspace,
                                      Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #Relationship} that are referred to by the workspace
     *         relationship
     */
    Collection<Relationship> getRelationships(Product workspace,
                                              Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #StatusCode} that are referred to by the workspace
     *         relationship
     */
    Collection<StatusCode> getStatusCodes(Product workspace,
                                          Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #StatusCodeSequencing} that are referred to by the
     *         workspace relationship
     */
    Collection<StatusCodeSequencing> getStatusCodeSequences(Product workspace,
                                                            Relationship relationship);

    /**
     * 
     * @param workspace
     * @param relationship
     * @return the {@link #Unit} that are referred to by the workspace
     *         relationship
     */
    Collection<Unit> getUnits(Product workspace, Relationship relationship);

    public abstract Graph getStatusCodeGraph(Product product);

}
