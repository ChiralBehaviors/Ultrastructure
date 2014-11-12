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
package com.chiralbehaviors.CoRE.agency;

import static com.chiralbehaviors.CoRE.agency.AgencyProduct.AGENCIES_FOR_PRODUCTS;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The authorization rule form that defines rules for relating agencies to
 * products.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "agency_product", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = AGENCIES_FOR_PRODUCTS, query = "SELECT n.agency "
                                                                  + "FROM AgencyProduct n "
                                                                  + "WHERE n.relationship = :relationship "
                                                                  + "AND n.product = :product"), })
public class AgencyProduct extends Ruleform {
    public static final String AGENCIES_FOR_PRODUCTS = "agencyProduct.agenciesForProducts";
    private static final long  serialVersionUID      = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "agency")
    private Agency             agency;

    // bi-directional many-to-one association to Location
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "product")
    private Product            product;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "relationship")
    private Relationship       relationship;

    public AgencyProduct() {
    }

    /**
     * @param updatedBy
     */
    public AgencyProduct(Agency updatedBy) {
        super(updatedBy);
    }

    public AgencyProduct(Agency agency, Relationship relationship,
                         Product product, Agency updatedBy) {
        super(updatedBy);
        this.relationship = relationship;
        this.product = product;
        this.agency = agency;
    }

    /**
     * @param id
     */
    public AgencyProduct(UUID id) {
        super(id);
    }

    public Agency getAgency() {
        return agency;
    }

    public Product getProduct() {
        return product;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, AgencyProduct> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.agencyProduct;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
}