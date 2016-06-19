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

import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_LABEL;

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
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
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
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceLabelRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class DatabaseBackedWorkspace implements EditableWorkspace {

    private final UUID                  definingProductId;
    protected final Map<String, Object> cache = new HashMap<>();
    protected final Model               model;
    protected final WorkspaceScope      scope;

    public DatabaseBackedWorkspace(Product definingProduct, Model model) {
        assert definingProduct != null;
        definingProductId = definingProduct.getId();
        this.model = model;
        scope = new WorkspaceScope(this);
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
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#add(com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord)
     */
    @Override
    public void add(SiblingSequencingAuthorizationRecord ruleform) {
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
                  .isAccessible(getDefiningProduct().getId(), model.getKernel()
                                                                   .getIsA()
                                                                   .getId(),
                                model.getKernel()
                                     .getWorkspace()
                                     .getId())) {
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
                                                                                     workspace

        );
        ExistentialNetworkAttributeRecord attribute = model.records()
                                                           .newExistentialNetworkAttribute(model.getKernel()
                                                                                                .getNamespace()

        );
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
                                                   .select(WORKSPACE_LABEL.REFERENCE,
                                                           WORKSPACE_LABEL.TYPE)
                                                   .from(WORKSPACE_LABEL)
                                                   .where(WORKSPACE_LABEL.WORKSPACE.equal(getDefiningProduct().getId()))
                                                   .and(WORKSPACE_LABEL.KEY.equal(key))
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
        if (key != null) {
            cache.put(key, ruleform);
        }
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
    public Product getDefiningProduct() {
        return model.records()
                    .resolve(definingProductId);
    }

    @Override
    public UUID getId(String name) {
        Record1<UUID> id = model.create()
                                .select(WORKSPACE_LABEL.REFERENCE)
                                .from(WORKSPACE_LABEL)
                                .where(WORKSPACE_LABEL.WORKSPACE.equal(getDefiningProduct().getId()))
                                .and(WORKSPACE_LABEL.KEY.equal(name))
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
                                                                                  .getImports(),
                                                                             ExistentialDomain.Product)) {
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
                    .select(WORKSPACE_LABEL.KEY)
                    .from(WORKSPACE_LABEL)
                    .where(WORKSPACE_LABEL.KEY.isNotNull())
                    .and(WORKSPACE_LABEL.WORKSPACE.equal(getDefiningProduct().getId()))
                    .fetch()
                    .stream()
                    .map(r -> r.value1())
                    .collect(Collectors.toList());
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord)
     */
    @Override
    public void put(String key, ChildSequencingAuthorizationRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord)
     */
    @Override
    public void put(String key,
                    ExistentialAttributeAuthorizationRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord)
     */
    @Override
    public void put(String key, ExistentialAttributeRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord)
     */
    @Override
    public void put(String key,
                    ExistentialNetworkAttributeAuthorizationRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord)
     */
    @Override
    public void put(String key, ExistentialNetworkAttributeRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    @Override
    public void put(String key,
                    ExistentialNetworkAuthorizationRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord)
     */
    @Override
    public void put(String key, ExistentialNetworkRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    @Override
    public void put(String key, ExistentialRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord)
     */
    @Override
    public void put(String key, FacetRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord)
     */
    @Override
    public void put(String key, JobChronologyRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord)
     */
    @Override
    public void put(String key, JobRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord)
     */
    @Override
    public void put(String key, MetaProtocolRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord)
     */
    @Override
    public void put(String key, NetworkInferenceRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord)
     */
    @Override
    public void put(String key, ParentSequencingAuthorizationRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord)
     */
    @Override
    public void put(String key, ProtocolRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord)
     */
    @Override
    public void put(String key, SelfSequencingAuthorizationRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord)
     */
    @Override
    public void put(String key, SiblingSequencingAuthorizationRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#put(java.lang.String, com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord)
     */
    @Override
    public void put(String key, StatusCodeSequencingRecord ruleform) {
        if (key != null) {
            cache.put(key, ruleform);
            model.records()
                 .newWorkspaceLabel(key, getDefiningProduct(), ruleform)
                 .insert();
        }
        ruleform.setWorkspace(definingProductId);
        ruleform.update();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace#removeImport(com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void removeImport(Product workspace, Agency updatedBy) {
        if (!model.getPhantasmModel()
                  .isAccessible(getDefiningProduct().getId(), model.getKernel()
                                                                   .getIsA()
                                                                   .getId(),
                                model.getKernel()
                                     .getWorkspace()
                                     .getId())) {
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
        WorkspaceLabelRecord authorization = model.records()
                                                  .newWorkspaceLabel(null,
                                                                     getDefiningProduct(),
                                                                     reference,
                                                                     type

        );
        authorization.insert();
    }
}
