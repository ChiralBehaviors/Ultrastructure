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

import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_AUTHORIZATION;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.Record1;
import org.jooq.Record2;

import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyExistentialGroupingRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceAuthorizationRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class DatabaseBackedWorkspace implements EditableWorkspace {
    public class EntityList<T> extends AbstractList<T> {
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
            return (T) getRuleform(backingList.get(index));
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return backingList.size();
        }
    }

    private Product                     definingProductCache;
    private final UUID                  definingProductId;
    protected final Map<String, Object> cache = new HashMap<>();
    protected final Model               model;
    protected final WorkspaceScope      scope;

    public DatabaseBackedWorkspace(Product definingProduct, Model model) {
        assert definingProduct != null;
        this.definingProductId = definingProduct.getId();
        this.model = model;
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.AgencyExistentialGroupingRecord)
     */
    @Override
    public void add(AgencyExistentialGroupingRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord)
     */
    @Override
    public void add(ChildSequencingAuthorizationRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord)
     */
    @Override
    public void add(ExistentialAttributeAuthorizationRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord)
     */
    @Override
    public void add(ExistentialAttributeRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord)
     */
    @Override
    public void add(ExistentialNetworkAttributeAuthorizationRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord)
     */
    @Override
    public void add(ExistentialNetworkAttributeRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord)
     */
    @Override
    public void add(ExistentialNetworkAuthorizationRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord)
     */
    @Override
    public void add(ExistentialNetworkRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord)
     */
    @Override
    public void add(ExistentialRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord)
     */
    @Override
    public void add(FacetRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord)
     */
    @Override
    public void add(JobChronologyRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord)
     */
    @Override
    public void add(JobRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord)
     */
    @Override
    public void add(MetaProtocolRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord)
     */
    @Override
    public void add(NetworkInferenceRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord)
     */
    @Override
    public void add(ParentSequencingAuthorizationRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord)
     */
    @Override
    public void add(ProtocolRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord)
     */
    @Override
    public void add(SelfSequencingAuthorizationRecord ruleform) {
        put(null, ruleform);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord)
     */
    @Override
    public void add(StatusCodeSequencingRecord ruleform) {
        put(null, ruleform);

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#addImport(com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void addImport(String name, Product workspace) {
        if (!model.getPhantasmModel()
                  .isAccessible(getDefiningProduct(), model.getKernel()
                                                           .getIsA(),
                                model.getKernel()
                                     .getWorkspace())) {
            throw new IllegalArgumentException(String.format("Import is not classified as a Workspace: %s",
                                                             workspace));
        }
        scope.add(name, model.getWorkspaceModel()
                             .getScoped(workspace)
                             .getWorkspace());
        Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> links = model.getPhantasmModel()
                                                                               .link(getDefiningProduct(),
                                                                                     model.getKernel()
                                                                                          .getImports(),
                                                                                     workspace,
                                                                                     model.getCurrentPrincipal()
                                                                                          .getPrincipal());
        ExistentialNetworkAttributeRecord attribute = model.records()
                                                           .newExistentialNetworkAttribute(model.getKernel()
                                                                                                .getNamespace(),
                                                                                           model.getCurrentPrincipal()
                                                                                                .getPrincipal());
        attribute.setEdge(links.a.getId());
        attribute.insert();
        model.getPhantasmModel()
             .setValue(attribute, name);
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
    public <T> T get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Object cached = cache.get(key);
        if (cached != null) {
            return (T) cached;
        }
        Record2<UUID, ReferenceType> result = model.create()
                                                   .select(WORKSPACE_AUTHORIZATION.REFERENCE,
                                                           WORKSPACE_AUTHORIZATION.TYPE)
                                                   .from(WORKSPACE_AUTHORIZATION)
                                                   .where(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(getDefiningProduct().getId()))
                                                   .and(WORKSPACE_AUTHORIZATION.KEY.equal(key))
                                                   .fetchOne();
        if (result == null) {
            return null;
        }
        T ruleform;
        switch (result.value2()) {
            case Existential:
                ruleform = model.records()
                                .resolve(result.value1());
                break;
            default:
                throw new IllegalStateException(String.format("Unable to find result type: %s",
                                                              result.value2()));
        }
        cache.put(key, ruleform);
        return ruleform;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAccesor(java.lang.Class)
     */
    @Override
    public <T> T getAccessor(Class<T> accessorInterface) {
        return WorkspaceAccessHandler.getAccesor(accessorInterface, getScope());
    }

    @Override
    public <T> List<T> getCollection(Class<T> ruleformClass) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor#getCollection(java.lang.Class)
     */
    @Override
    public <T> List<T> getCollection(ReferenceType type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Product getDefiningProduct() {
        if (definingProductCache == null) {
            definingProductCache = model.records()
                                        .resolve(definingProductId);
        }
        return definingProductCache;
    }

    @Override
    public UUID getId(String name) {
        Record1<UUID> id = model.create()
                                .select(WORKSPACE_AUTHORIZATION.REFERENCE)
                                .from(WORKSPACE_AUTHORIZATION)
                                .where(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(getDefiningProduct().getId()))
                                .and(WORKSPACE_AUTHORIZATION.KEY.equal(name))
                                .fetchOne();
        return id != null ? id.value1() : null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#getImports()
     */
    @Override
    public Map<String, Tuple<Product, Integer>> getImports() {
        Map<String, Tuple<Product, Integer>> imports = new HashMap<>();
        for (ExistentialNetworkRecord link : model.getPhantasmModel()
                                                  .getImmediateChildrenLinks(getDefiningProduct(),
                                                                             model.getKernel()
                                                                                  .getImports())) {
            ExistentialNetworkAttributeRecord namespace = model.getPhantasmModel()
                                                               .getAttributeValue(link,
                                                                                  model.getKernel()
                                                                                       .getNamespace());
            ExistentialNetworkAttributeRecord lookupOrder = model.getPhantasmModel()
                                                                 .getAttributeValue(link,
                                                                                    model.getKernel()
                                                                                         .getLookupOrder());
            if (namespace == null) {
                throw new IllegalStateException(String.format("Import has no namespace attribute defined: %s",
                                                              link));
            }
            if (namespace.getTextValue() == null) {
                throw new IllegalStateException(String.format("Import has no name defined! : %s",
                                                              link));
            }
            Integer lookupOrderValue = lookupOrder == null ? -1
                                                           : lookupOrder.getIntegerValue();
            imports.put(namespace.getTextValue(), new Tuple<>(model.records()
                                                                   .resolve(link.getChild()),
                                                              lookupOrderValue));
        }
        return imports;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#getKeys()
     */
    @Override
    public List<String> getKeys() {
        return model.create()
                    .select(WORKSPACE_AUTHORIZATION.KEY)
                    .from(WORKSPACE_AUTHORIZATION)
                    .where(WORKSPACE_AUTHORIZATION.KEY.isNotNull())
                    .and(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(getDefiningProduct().getId()))
                    .fetch()
                    .stream()
                    .map(r -> r.value1())
                    .collect(Collectors.toList());
    }

    public Object getRuleform(WorkspaceAuthorizationRecord workspaceAuthorizationRecord) {
        // TODO Auto-generated method stub
        return null;
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
        return new WorkspaceSnapshot(getDefiningProduct(), model.create());
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

    @Override
    public void put(String key, AgencyExistentialGroupingRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord)
     */
    @Override
    public void put(String key, ChildSequencingAuthorizationRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord)
     */
    @Override
    public void put(String key,
                    ExistentialAttributeAuthorizationRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord)
     */
    @Override
    public void put(String key, ExistentialAttributeRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord)
     */
    @Override
    public void put(String key,
                    ExistentialNetworkAttributeAuthorizationRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord)
     */
    @Override
    public void put(String key, ExistentialNetworkAttributeRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    @Override
    public void put(String key,
                    ExistentialNetworkAuthorizationRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord)
     */
    @Override
    public void put(String key, ExistentialNetworkRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    @Override
    public void put(String key, ExistentialRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord)
     */
    @Override
    public void put(String key, FacetRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord)
     */
    @Override
    public void put(String key, JobChronologyRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord)
     */
    @Override
    public void put(String key, JobRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord)
     */
    @Override
    public void put(String key, MetaProtocolRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord)
     */
    @Override
    public void put(String key, NetworkInferenceRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord)
     */
    @Override
    public void put(String key, ParentSequencingAuthorizationRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord)
     */
    @Override
    public void put(String key, ProtocolRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord)
     */
    @Override
    public void put(String key, SelfSequencingAuthorizationRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord)
     */
    @Override
    public void put(String key, SiblingSequencingAuthorizationRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord)
     */
    @Override
    public void put(String key, StatusCodeSequencingRecord ruleform) {
        cache.put(key, ruleform);
        model.records()
             .newWorkspaceAuthorization(key, getDefiningProduct(), ruleform,
                                        model.getCurrentPrincipal()
                                             .getPrincipal())
             .insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#removeImport(com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void removeImport(Product workspace, Agency updatedBy) {
        if (!model.getPhantasmModel()
                  .isAccessible(getDefiningProduct(), model.getKernel()
                                                           .getIsA(),
                                model.getKernel()
                                     .getWorkspace())) {
            throw new IllegalArgumentException(String.format("Import is not classified as a Workspace: %s",
                                                             workspace));
        }
        scope.remove(model.getWorkspaceModel()
                          .getScoped(workspace)
                          .getWorkspace());
        model.getPhantasmModel()
             .unlink(getDefiningProduct(), model.getKernel()
                                                .getImports(),
                     workspace);
    }

    @Override
    public String toString() {
        return String.format("DatabaseBackedWorkspace[%s]",
                             getDefiningProduct().getName());
    }

    protected void add(ReferenceType type, UUID reference) {
        WorkspaceAuthorizationRecord authorization = model.records()
                                                          .newWorkspaceAuthorization(null,
                                                                                     getDefiningProduct(),
                                                                                     reference,
                                                                                     type,
                                                                                     model.getCurrentPrincipal()
                                                                                          .getPrincipal());
        authorization.insert();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord)
     */
    @Override
    public void add(SiblingSequencingAuthorizationRecord ruleform) {
        // TODO Auto-generated method stub

    }
}
