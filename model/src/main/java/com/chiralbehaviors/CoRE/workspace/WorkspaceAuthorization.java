/**
 *
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.workspace;

import static com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization.DOES_WORKSPACE_AUTH_EXIST;
import static com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization.GET_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization.GET_AUTHORIZATIONS_BY_TYPE;
import static com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization.GET_AUTHORIZATION_BY_ID;
import static com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization.GET_WORKSPACE;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.product.Product;

@NamedQueries({ @NamedQuery(name = GET_WORKSPACE, query = "SELECT auth FROM WorkspaceAuthorization auth WHERE auth.definingProduct = :product"),
                @NamedQuery(name = GET_AUTHORIZATION, query = "SELECT auth FROM WorkspaceAuthorization auth "
                                                              + "WHERE auth.definingProduct = :product "
                                                              + "AND auth.key = :key"),
                @NamedQuery(name = GET_AUTHORIZATION_BY_ID, query = "SELECT auth FROM WorkspaceAuthorization auth, Product p "
                                                                    + "WHERE auth.definingProduct = p "
                                                                    + "AND p.id = :productId "
                                                                    + "AND auth.key = :key"),
                @NamedQuery(name = GET_AUTHORIZATIONS_BY_TYPE, query = "SELECT auth FROM WorkspaceAuthorization auth "
                                                                       + "WHERE auth.definingProduct = :product "
                                                                       + "AND auth.type= :type"),
                @NamedQuery(name = DOES_WORKSPACE_AUTH_EXIST, query = "SELECT COUNT(auth) FROM WorkspaceAuthorization auth "
                                                                      + "WHERE auth.id = :id") })
@Entity
@Table(name = "workspace_authorization", schema = "ruleform")
public class WorkspaceAuthorization extends Ruleform {
    public static final String DOES_WORKSPACE_AUTH_EXIST  = "workspaceAuthorization.doesAuthExist";
    public static final String GET_AUTHORIZATION          = "workspaceAuthorization.getAuthorization";
    public static final String GET_AUTHORIZATION_BY_ID    = "workspaceAuthorization.getAuthorizationById";
    public static final String GET_AUTHORIZATIONS_BY_TYPE = "workspaceAuthorization.getAuthorizationByType";
    public static final String GET_WORKSPACE              = "workspaceAuthorization.getWorkspace";

    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "defining_product")
    private Product definingProduct;

    private String key;
    @Type(type = "pg-uuid")
    private UUID   reference; // teh pointer ;)
    private String type;

    public WorkspaceAuthorization() {
        super();

    }

    public WorkspaceAuthorization(Ruleform ruleform, Product definingProduct,
                                  Agency updatedBy, EntityManager em) {
        this(ruleform, definingProduct, em);
        setUpdatedBy(updatedBy);
        em.persist(this);
    }

    public WorkspaceAuthorization(Ruleform ruleform, Product definingProduct,
                                  EntityManager em) {
        super();
        setDefiningProduct(definingProduct);
        setRuleform(ruleform, em);
    }

    public WorkspaceAuthorization(String key, Ruleform ruleform,
                                  Product definingProduct, Agency updatedBy,
                                  EntityManager em) {
        this(ruleform, definingProduct, updatedBy, em);
        setKey(key);
    }

    public Product getDefiningProduct() {
        return definingProduct;
    }

    @SuppressWarnings("unchecked")
    public <T extends Ruleform> T getEntity(EntityManager em) {
        return (T) em.getReference(CONCRETE_SUBCLASSES.get(type), reference);
    }

    public String getKey() {
        return key;
    }

    public UUID getReference() {
        return reference;
    }

    public Ruleform getRuleform(EntityManager em) {
        return getEntity(em);
    }

    public String getType() {
        return type;
    }

    public void setDefiningProduct(Product definingProduct) {
        this.definingProduct = definingProduct;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setReference(UUID reference) {
        this.reference = reference;
    }

    public void setRuleform(Ruleform ruleform, EntityManager em) {
        type = Ruleform.initializeAndUnproxy(ruleform)
                       .getClass()
                       .getSimpleName();
        reference = ruleform.getId();
        em.persist(ruleform);
        ruleform.setWorkspace(this);
    }

    @Override
    public String toString() {
        return String.format("WorkspaceAuthorization [definingProduct=%s, key=%s, type=%s, reference=%s]",
                             definingProduct, key, type, reference);
    }
}
