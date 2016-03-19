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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceAuthorizationRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class DatabaseBackedWorkspace implements EditableWorkspace {
    public class EntityList<T extends Ruleform> extends AbstractList<T> {
        private final List<WorkspaceAuthorizationRecord> backingList;

        public EntityList(List<WorkspaceAuthorizationRecord> backingList) {
            this.backingList = backingList;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractList#get(int)
         */
        @SuppressWarnings("unchecked")
        @Override
        public T get(int index) {
            return (T) backingList.get(index)
                                  .getRuleform(em);
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return backingList.size();
        }
    }

    private final UUID                    definingProductId;
    private Product                       definingProductCache;
    protected final Map<String, Ruleform> cache = new HashMap<String, Ruleform>();
    protected final EntityManager         em;
    protected final Model                 model;
    protected final WorkspaceScope        scope;

    public DatabaseBackedWorkspace(Product definingProduct, Model model) {
        assert definingProduct != null;
        this.definingProductId = definingProduct.getId();
        this.model = model;
        this.em = model.getEntityManager();
        this.scope = new WorkspaceScope(this);
        // We need the kernel workspace to lookup workspaces, so special case the kernel
        if (!definingProduct.getId()
                            .equals(WellKnownProduct.KERNEL_WORKSPACE.id())) {
            List<Entry<String, Tuple<Product, Integer>>> imports = getSortedImports();
            for (Entry<String, Tuple<Product, Integer>> entry : imports) {
                scope.add(entry.getKey(), model.getWorkspaceModel()
                                               .getScoped(entry.getValue().a)
                                               .getWorkspace());
            }
        }
    }

    public List<Entry<String, Tuple<Product, Integer>>> getSortedImports() {
        List<Entry<String, Tuple<Product, Integer>>> imports = new ArrayList<>(getImports().entrySet());
        Collections.sort(imports, (a, b) -> {
            Integer aOrdering = a.getValue().b;
            Integer bOrdering = b.getValue().b;
            if (aOrdering.equals(bOrdering)) {
                return 0;
            }
            if (aOrdering < 0) {
                return -1;
            }
            if (bOrdering < 0) {
                return 1;
            }
            return aOrdering.compareTo(bOrdering);
        });
        return imports;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.NeuvoWorkspace#add(com.chiralbehaviors.CoRE.Ruleform)
     */
    @Override
    public <T extends Ruleform> void add(T ruleform) {
        WorkspaceAuthorizationRecord authorization = new WorkspaceAuthorizationRecord();
        authorization.setRuleform(ruleform, em);
        authorization.setDefiningProduct(getDefiningProduct());
        authorization.setUpdatedBy(ruleform.getUpdatedBy());
        em.persist(authorization);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#addImport(com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void addImport(String namespace, Product workspace) {
        ProductModel productModel = model.getProductModel();
        if (!productModel.isAccessible(getDefiningProduct(), model.getKernel()
                                                                  .getIsA(),
                                       model.getKernel()
                                            .getWorkspace())) {
            throw new IllegalArgumentException(String.format("Import is not classified as a Workspace: %s",
                                                             workspace));
        }
        scope.add(namespace, model.getWorkspaceModel()
                                  .getScoped(workspace)
                                  .getWorkspace());
        Tuple<ProductNetwork, ProductNetwork> links = productModel.link(getDefiningProduct(),
                                                                        model.getKernel()
                                                                             .getImports(),
                                                                        workspace,
                                                                        model.getCurrentPrincipal()
                                                                             .getPrincipal());
        ProductNetworkAttribute attribute = new ProductNetworkAttribute(model.getKernel()
                                                                             .getNamespace(),
                                                                        namespace,
                                                                        model.getCurrentPrincipal()
                                                                             .getPrincipal());
        attribute.setNetwork(links.a);
        em.persist(attribute);
        attribute.setValue(namespace);
        add(links.a);
        add(links.b);
        add(attribute);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#flushCache()
     */
    @Override
    public void flushCache() {
        cache.clear();
        definingProductCache = null;
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
        TypedQuery<WorkspaceAuthorizationRecord> query = em.createNamedQuery(WorkspaceAuthorizationRecord.GET_AUTHORIZATION_BY_ID,
                                                                             WorkspaceAuthorizationRecord.class);
        query.setParameter("productId", definingProductId);
        query.setParameter("key", key);
        try {
            WorkspaceAuthorizationRecord authorization = query.getSingleResult();
            T ruleform = authorization.getEntity(em);
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
        CriteriaQuery<WorkspaceAuthorizationRecord> query = cb.createQuery(WorkspaceAuthorizationRecord.class);
        Root<WorkspaceAuthorizationRecord> from = query.from(WorkspaceAuthorizationRecord.class);
        query.select(from)
             .where(cb.and(cb.equal(from.get(WorkspaceAuthorization_.type),
                                    ruleformClass.getSimpleName()),
                           cb.equal(from.get(WorkspaceAuthorization_.definingProduct),
                                    getDefiningProduct())));
        return new EntityList<T>(em.createQuery(query)
                                   .getResultList());
    }

    @Override
    public Product getDefiningProduct() {
        if (definingProductCache == null) {
            definingProductCache = model.getEntityManager()
                                        .getReference(Product.class,
                                                      definingProductId);
        }
        return definingProductCache;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#getImports()
     */
    @Override
    public Map<String, Tuple<Product, Integer>> getImports() {
        Map<String, Tuple<Product, Integer>> imports = new HashMap<>();
        for (ProductNetwork link : model.getProductModel()
                                        .getImmediateChildrenLinks(getDefiningProduct(),
                                                                   model.getKernel()
                                                                        .getImports())) {
            NetworkAttribute<?> namespace = model.getProductModel()
                                                 .getAttributeValue(link,
                                                                    model.getKernel()
                                                                         .getNamespace());
            NetworkAttribute<?> lookupOrder = model.getProductModel()
                                                   .getAttributeValue(link,
                                                                      model.getKernel()
                                                                           .getLookupOrder());
            if (namespace == null) {
                throw new IllegalStateException(String.format("Import has no namespace attribute defined: %s",
                                                              link));
            }
            if (namespace.getValue() == null) {
                throw new IllegalStateException(String.format("Import has no name defined! : %s",
                                                              link));
            }
            Integer lookupOrderValue = lookupOrder == null ? -1
                                                           : lookupOrder.getValue();
            imports.put(namespace.getValue(),
                        new Tuple<>(link.getChild(), lookupOrderValue));
        }
        return imports;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#getKeys()
     */
    @Override
    public List<String> getKeys() {
        List<String> keys = new ArrayList<>();
        for (WorkspaceAuthorizationRecord auth : WorkspaceSnapshot.getAuthorizations(getDefiningProduct(),
                                                                                     em)) {
            if (auth.getKey() != null) {
                keys.add(auth.getKey());
            }
        }
        return keys;
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
        WorkspaceAuthorizationRecord authorization = new WorkspaceAuthorizationRecord();
        authorization.setDefiningProduct(getDefiningProduct());
        authorization.setRuleform(ruleform, em);
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
        if (!productModel.isAccessible(getDefiningProduct(), model.getKernel()
                                                                  .getIsA(),
                                       model.getKernel()
                                            .getWorkspace())) {
            throw new IllegalArgumentException(String.format("Import is not classified as a Workspace: %s",
                                                             workspace));
        }
        scope.remove(model.getWorkspaceModel()
                          .getScoped(workspace)
                          .getWorkspace());
        productModel.unlink(getDefiningProduct(), model.getKernel()
                                                       .getImports(),
                            workspace);
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
