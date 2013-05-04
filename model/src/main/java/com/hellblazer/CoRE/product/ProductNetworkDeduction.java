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
package com.hellblazer.CoRE.product;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the product_network_deduction database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "product_network_deduction", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_network_deduction_id_seq", sequenceName = "product_network_deduction_id_seq")
public class ProductNetworkDeduction implements Serializable {
    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to ProductNetwork
    @ManyToOne
    @JoinColumn(name = "deduction")
    private ProductNetwork     deduction;

    @Id
    @GeneratedValue(generator = "product_network_deduction_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to ProductNetwork
    @ManyToOne
    @JoinColumn(name = "premise1")
    private ProductNetwork     premise1;

    //bi-directional many-to-one association to ProductNetwork
    @ManyToOne
    @JoinColumn(name = "premise2")
    private ProductNetwork     premise2;

    @Column(name = "update_date")
    private Timestamp         updateDate;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Resource          updatedBy;

    public ProductNetworkDeduction() {
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Ruleform other = (Ruleform) obj;
        Long id = getId();
        if (id == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!id.equals(other.getId())) {
            return false;
        }
        return true;
    }

    public ProductNetwork getDeduction() {
        return deduction;
    }

    public Long getId() {
        return id;
    }

    public ProductNetwork getPremise1() {
        return premise1;
    }

    public ProductNetwork getPremise2() {
        return premise2;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public Resource getUpdatedBy() {
        return updatedBy;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        if (getId() == null) {
            return 31;
        }
        return getId().hashCode();
    }

    public void setDeduction(ProductNetwork productNetwork3) {
        deduction = productNetwork3;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPremise1(ProductNetwork productNetwork2) {
        premise1 = productNetwork2;
    }

    public void setPremise2(ProductNetwork productNetwork1) {
        premise2 = productNetwork1;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public void setUpdatedBy(Resource resource) {
        updatedBy = resource;
    }
}