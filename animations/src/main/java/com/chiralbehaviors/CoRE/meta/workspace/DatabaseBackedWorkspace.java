/**
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

package com.chiralbehaviors.CoRE.meta.workspace;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.ProductModel;
import com.chiralbehaviors.CoRE.network.NetworkAttribute;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.ProductNetworkAttribute;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;

/**
 * @author hhildebrand
 *
 */
public class DatabaseBackedWorkspace implements EditableWorkspace {
    public class EntityList<T extends Ruleform> extends AbstractList<T> {
        private final List<WorkspaceAuthorization> backingList;

        public EntityList(List<WorkspaceAuthorization> backingList) {
            this.backingList = backingList;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractList#get(int)
         */
        @SuppressWarnings("unchecked")
        @Override
        public T get(int index) {
            return (T) backingList.get(index).getRuleform();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return backingList.size();
        }
    }

    private final UUID                    definingProduct;
    protected final Map<String, Ruleform> cache = new HashMap<String, Ruleform>();
    protected final EntityManager         em;
    protected final Model                 model;
    protected final WorkspaceScope        scope;

    public DatabaseBackedWorkspace(Product definingProduct, Model model) {
        assert definingProduct != null;
        this.definingProduct = definingProduct.getId();
        this.model = model;
        this.em = model.getEntityManager();
        this.scope = new WorkspaceScope(this);
        // We need the kernel workspace to lookup workspaces, so special case the kernel
        if (!definingProduct.getId().equals(WellKnownProduct.KERNEL_WORKSPACE.id())) {
            for (Map.Entry<String, Product> entry : getImports().entrySet()) {
                scope.add(entry.getKey(),
                          model.getWorkspaceModel().getScoped(entry.getValue()).getWorkspace());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#add(com.chiralbehaviors.CoRE.Ruleform)
     */
    @Override
    public <T extends Ruleform> void add(T ruleform) {
        WorkspaceAuthorization authorization = new WorkspaceAuthorization();
        authorization.setRuleform(ruleform);
        authorization.setDefiningProduct(getDefiningProduct());
        authorization.setUpdatedBy(ruleform.getUpdatedBy());
        em.persist(authorization);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#addImport(com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void addImport(String namespace, Product workspace, Agency updatedBy) {
        ProductModel productModel = model.getProductModel();
        if (!productModel.isAccessible(getDefiningProduct(),
                                       model.getKernel().getIsA(),
                                       model.getKernel().getWorkspace())) {
            throw new IllegalArgumentException(
                                               String.format("Import is not classified as a Workspace: %s",
                                                             workspace));
        }
        scope.add(namespace,
                  model.getWorkspaceModel().getScoped(workspace).getWorkspace());
        ProductNetwork link = productModel.link(getDefiningProduct(),
                                                model.getKernel().getImports(),
                                                workspace, updatedBy);
        ProductNetworkAttribute attribute = new ProductNetworkAttribute(
                                                                        model.getKernel().getNamespaceAttribute(),
                                                                        namespace,
                                                                        updatedBy);
        attribute.setNetwork(link);
        em.persist(attribute);
        add(link);
        add(attribute);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#flushCache()
     */
    @Override
    public void flushCache() {
        cache.clear();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#get(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Ruleform> T get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Ruleform cached = cache.get(key);
        if (cached != null) {
            return (T) cached;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkspaceAuthorization> query = cb.createQuery(WorkspaceAuthorization.class);
        Root<WorkspaceAuthorization> from = query.from(WorkspaceAuthorization.class);
        query.select(from).where(cb.and(cb.equal(from.get(WorkspaceAuthorization_.key),
                                                 key),
                                        cb.equal(from.get(WorkspaceAuthorization_.definingProduct),
                                                 getDefiningProduct())));
        try {
            WorkspaceAuthorization authorization = em.createQuery(query).getSingleResult();
            T ruleform = authorization.getEntity();
            cache.put(key, ruleform);
            return ruleform;
        } catch (NoResultException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAccesor(java.lang.Class)
     */
    @Override
    public <T> T getAccessor(Class<T> accessorInterface) {
        return WorkspaceAccessHandler.getAccesor(accessorInterface, getScope());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#getCollection(java.lang.Class)
     */
    @Override
    public <T extends Ruleform> List<T> getCollection(Class<T> ruleformClass) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkspaceAuthorization> query = cb.createQuery(WorkspaceAuthorization.class);
        Root<WorkspaceAuthorization> from = query.from(WorkspaceAuthorization.class);
        query.select(from).where(cb.and(cb.equal(from.get(WorkspaceAuthorization_.type),
                                                 ruleformClass.getSimpleName()),
                                        cb.equal(from.get(WorkspaceAuthorization_.definingProduct),
                                                 getDefiningProduct())));
        return new EntityList<T>(em.createQuery(query).getResultList());
    }

    @Override
    public Product getDefiningProduct() {
        return em.find(Product.class, definingProduct);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#getImports()
     */
    @Override
    public Map<String, Product> getImports() {
        Map<String, Product> imports = new HashMap<>();
        for (ProductNetwork link : model.getProductModel().getImmediateChildrenLinks(getDefiningProduct(),
                                                                                     model.getKernel().getImports())) {
            NetworkAttribute<?> attribute = model.getProductModel().getAttributeValue(link,
                                                                                      model.getKernel().getNamespaceAttribute());
            if (attribute == null) {
                throw new IllegalStateException(
                                                String.format("Import has no namespace attribute defined: %s",
                                                              link));
            }
            imports.put(attribute.getValue(), link.getChild());
        }
        return imports;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#getScope()
     */
    @Override
    public WorkspaceScope getScope() {
        return scope;
    }

    @Override
    public WorkspaceSnapshot getSnapshot() {
        return new WorkspaceSnapshot(getDefiningProduct(), em);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.Ruleform)
     */
    @Override
    public <T extends Ruleform> void put(String key, T ruleform) {
        cache.put(key, ruleform);
        WorkspaceAuthorization authorization = new WorkspaceAuthorization();
        authorization.setDefiningProduct(getDefiningProduct());
        authorization.setRuleform(ruleform);
        authorization.setKey(key);
        authorization.setUpdatedBy(ruleform.getUpdatedBy());
        em.persist(authorization);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#removeImport(com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void removeImport(Product workspace, Agency updatedBy) {
        ProductModel productModel = model.getProductModel();
        if (!productModel.isAccessible(getDefiningProduct(),
                                       model.getKernel().getIsA(),
                                       model.getKernel().getWorkspace())) {
            throw new IllegalArgumentException(
                                               String.format("Import is not classified as a Workspace: %s",
                                                             workspace));
        }
        scope.remove(model.getWorkspaceModel().getScoped(workspace).getWorkspace());
        productModel.unlink(getDefiningProduct(),
                            model.getKernel().getImports(), workspace);
    }

    @Override
    public void replaceFrom(EntityManager em) {
        // nothing to do, as we're backed by the DB
    }

    @Override
    public void retarget(EntityManager em) {
        // nothing to do, as we're backed by the DB
    }

    @Override
    public String toString() {
        return String.format("DatabaseBackedWorkspace[%s]",
                             getDefiningProduct().getName());
    }
}
