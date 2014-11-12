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

package com.chiralbehaviors.CoRE.kernel;

import static com.chiralbehaviors.CoRE.kernel.KernelImpl.ZERO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.UuidGenerator;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownInterval;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownLocation;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownUnit;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class Bootstrap {
    public static void main(String[] argv) throws Exception {
        if (argv.length != 2) {
            System.err.println("Usage: Bootstrap <jpa.properties> <output file>");
            System.exit(1);
        }
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(new File(argv[0]))) {
            properties.load(is);
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                                          properties);
        EntityManager em = emf.createEntityManager();
        Bootstrap bootstrap = new Bootstrap(em);
        bootstrap.clear();
        em.getTransaction().begin();
        bootstrap.bootstrap();
        em.getTransaction().commit();
        em.clear();
        bootstrap.serialize(argv[1]);
    }

    private final Connection    connection;
    private final EntityManager em;

    public Bootstrap(EntityManager em) throws SQLException {
        connection = em.unwrap(Connection.class);
        connection.setAutoCommit(false);
        this.em = em;
    }

    public void bootstrap() throws SQLException {
        KernelImpl.alterTriggers(connection, false);
        for (WellKnownAgency wko : WellKnownAgency.values()) {
            insert(wko);
        }
        for (WellKnownAttribute wko : WellKnownAttribute.values()) {
            insert(wko);
        }
        for (WellKnownInterval wko : WellKnownInterval.values()) {
            insert(wko);
        }
        for (WellKnownLocation wko : WellKnownLocation.values()) {
            insert(wko);
        }
        for (WellKnownProduct wko : WellKnownProduct.values()) {
            insert(wko);
        }
        for (WellKnownRelationship wko : WellKnownRelationship.values()) {
            insert(wko);
        }
        for (WellKnownStatusCode wko : WellKnownStatusCode.values()) {
            insert(wko);
        }
        for (WellKnownUnit wko : WellKnownUnit.values()) {
            insert(wko);
        }
        createNullInference();
        createRootNetworks();
        KernelImpl.alterTriggers(connection, true);
        constructKernelWorkspace();
    }

    public void clear() throws SQLException {
        KernelImpl.clear(em);
    }

    public void insert(WellKnownAttribute wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, pinned, updated_by, value_type) VALUES (?, ?, ?, ?, ?, ?)",
                                                                        wko.tableName()));
        try {
            s.setString(1, wko.id());
            s.setString(2, wko.wkoName());
            s.setString(3, wko.description());
            s.setByte(4, (byte) 1);
            s.setString(5, WellKnownAgency.CORE.id());
            s.setInt(6, wko.valueType().ordinal());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    public void insert(WellKnownInterval wki) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, unit, description, pinned, updated_by) VALUES (?, ?, ?, ?, ?, ?)",
                                                                        wki.tableName()));
        try {
            s.setString(1, wki.id());
            s.setString(2, wki.wkoName());
            s.setString(3, wki.unit().id());
            s.setString(4, wki.description());
            s.setByte(5, (byte) 1);
            s.setString(6, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wki), e);
        }
    }

    public void insert(WellKnownLocation wkl) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, pinned, updated_by) VALUES (?, ?, ?, ?, ?)",
                                                                        wkl.tableName()));
        try {
            s.setString(1, wkl.id());
            s.setString(2, wkl.wkoName());
            s.setString(3, wkl.description());
            s.setByte(4, (byte) 1);
            s.setString(5, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert  %s", wkl),
                                   e);
        }
    }

    public void insert(WellKnownObject wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, pinned, updated_by) VALUES (?, ?, ?, ?, ?)",
                                                                        wko.tableName()));
        try {
            s.setString(1, wko.id());
            s.setString(2, wko.wkoName());
            s.setString(3, wko.description());
            s.setByte(4, (byte) 1);
            s.setString(5, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    public void insert(WellKnownRelationship wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, pinned, updated_by, inverse, preferred) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                                                        wko.tableName()));
        try {
            s.setString(1, wko.id());
            s.setString(2, wko.wkoName());
            s.setString(3, wko.description());
            s.setByte(4, (byte) 1);
            s.setString(5, WellKnownAgency.CORE.id());
            s.setString(6, wko.inverse().id());
            s.setByte(7, (byte) (wko.preferred() ? 1 : 0));
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    public void insert(WellKnownStatusCode wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, propagate_children, pinned, updated_by) VALUES (?, ?, ?, ?, ?, ?)",
                                                                        wko.tableName()));
        try {
            s.setString(1, wko.id());
            s.setString(2, wko.wkoName());
            s.setString(3, wko.description());
            s.setInt(4, wko == WellKnownStatusCode.UNSET ? Ruleform.TRUE
                                                        : Ruleform.FALSE);
            s.setByte(5, (byte) 1);
            s.setString(6, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    public void insertNetwork(WellKnownObject wko) throws SQLException {
        String tableName = wko.tableName() + "_network";
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, parent, relationship, child, updated_by) VALUES (?, ?, ?, ?, ?)",
                                                                        tableName));
        try {
            s.setString(1, UuidGenerator.toBase64(new UUID(0, 0)));
            s.setString(2, wko.id());
            s.setString(3, WellKnownRelationship.RELATIONSHIP.id());
            s.setString(4, wko.id());
            s.setString(5, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert root %s",
                                                 tableName), e);
        }
    }

    private void createNullInference() throws SQLException {
        PreparedStatement s = connection.prepareStatement("INSERT into ruleform.network_inference (id, premise1, premise2, inference, updated_by) VALUES (?, ?, ?, ?, ?)");
        try {
            s.setString(1, UuidGenerator.toBase64(new UUID(0, 0)));
            s.setString(2, WellKnownRelationship.RELATIONSHIP.id());
            s.setString(3, WellKnownRelationship.RELATIONSHIP.id());
            s.setString(4, WellKnownRelationship.RELATIONSHIP.id());
            s.setString(5, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException("Unable to insert null inference", e);
        }
    }

    private void createRootNetworks() throws SQLException {
        for (WellKnownObject wko : new WellKnownObject[] {
                WellKnownAgency.AGENCY, WellKnownAttribute.ATTRIBUTE,
                WellKnownInterval.INTERVAL, WellKnownLocation.LOCATION,
                WellKnownProduct.PRODUCT, WellKnownRelationship.RELATIONSHIP,
                WellKnownStatusCode.STATUS_CODE, WellKnownUnit.UNIT }) {
            insertNetwork(wko);
        }
    }

    private void serialize(String fileName) throws IOException {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new CoREModule());
        objMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName),
                                                              new WorkspaceSnapshot(
                                                                                    find(WellKnownProduct.KERNEL_WORKSPACE),
                                                                                    em));
    }

    protected void constructKernelWorkspace() {
        Agency core = find(WellKnownAgency.CORE);
        Product kernelWorkspace = find(WellKnownProduct.KERNEL_WORKSPACE);
        Product workspace = find(WellKnownProduct.WORKSPACE);
        Relationship isA = find(WellKnownRelationship.IS_A);
        ProductNetwork pn = new ProductNetwork(kernelWorkspace, isA, workspace,
                                               core);

        WorkspaceAuthorization netAuth = new WorkspaceAuthorization(
                                                                    pn,
                                                                    kernelWorkspace,
                                                                    core);
        em.persist(netAuth);
        populate("Attribute", find(WellKnownAttribute.ATTRIBUTE), core,
                 kernelWorkspace);
        populate("AnyAttribute", find(WellKnownAttribute.ANY), core,
                 kernelWorkspace);
        populate("CopyAttribute", find(WellKnownAttribute.COPY), core,
                 kernelWorkspace);
        populate("NotApplicableAttribute",
                 find(WellKnownAttribute.NOT_APPLICABLE), core, kernelWorkspace);
        populate("SameAttribute", find(WellKnownAttribute.SAME), core,
                 kernelWorkspace);
        populate("LoginAttribute", find(WellKnownAttribute.LOGIN), core,
                 kernelWorkspace);
        populate("PasswordHashAttribute",
                 find(WellKnownAttribute.PASSWORD_HASH), core, kernelWorkspace);

        populate("Product", find(WellKnownProduct.ENTITY), core,
                 kernelWorkspace);
        populate("AnyProduct", find(WellKnownProduct.ANY), core,
                 kernelWorkspace);
        populate("CopyProduct", find(WellKnownProduct.COPY), core,
                 kernelWorkspace);
        populate("SameProduct", find(WellKnownProduct.SAME), core,
                 kernelWorkspace);
        populate("NotApplicableProduct", find(WellKnownProduct.NOT_APPLICABLE),
                 core, kernelWorkspace);
        populate("Workspace", find(WellKnownProduct.WORKSPACE), core,
                 kernelWorkspace);
        populate("KernelWorkspace", find(WellKnownProduct.KERNEL_WORKSPACE),
                 core, kernelWorkspace);

        populate("Location", find(WellKnownLocation.LOCATION), core,
                 kernelWorkspace);
        populate("AnyLocation", find(WellKnownLocation.ANY), core,
                 kernelWorkspace);
        populate("CopyLocation", find(WellKnownLocation.COPY), core,
                 kernelWorkspace);
        populate("NotApplicableLocation",
                 find(WellKnownLocation.NOT_APPLICABLE), core, kernelWorkspace);
        populate("SameLocation", find(WellKnownLocation.SAME), core,
                 kernelWorkspace);

        populate("CoreUser", find(WellKnownAgency.CORE_USER), core,
                 kernelWorkspace);
        populate("Agency", find(WellKnownAgency.AGENCY), core, kernelWorkspace);
        populate("AnyAgency", find(WellKnownAgency.ANY), core, kernelWorkspace);
        populate("CopyAgency", find(WellKnownAgency.COPY), core,
                 kernelWorkspace);
        populate("Core", find(WellKnownAgency.CORE), core, kernelWorkspace);
        populate("CoreAnimationSoftware",
                 find(WellKnownAgency.CORE_ANIMATION_SOFTWARE), core,
                 kernelWorkspace);
        populate("PropagationSoftware",
                 find(WellKnownAgency.PROPAGATION_SOFTWARE), core,
                 kernelWorkspace);
        populate("SpecialSystemAgency",
                 find(WellKnownAgency.SPECIAL_SYSTEM_AGENCY), core,
                 kernelWorkspace);
        populate("CoreModel", find(WellKnownAgency.CORE_MODEL), core,
                 kernelWorkspace);
        populate("SuperUser", find(WellKnownAgency.SUPER_USER), core,
                 kernelWorkspace);
        populate("InverseSoftware", find(WellKnownAgency.INVERSE_SOFTWARE),
                 core, kernelWorkspace);
        populate("SameAgency", find(WellKnownAgency.SAME), core,
                 kernelWorkspace);
        populate("NotApplicableAgency", find(WellKnownAgency.NOT_APPLICABLE),
                 core, kernelWorkspace);

        populate("AnyRelationship", find(WellKnownRelationship.ANY), core,
                 kernelWorkspace);
        populate("CopyRelationship", find(WellKnownRelationship.COPY), core,
                 kernelWorkspace);
        populate("SameRelationship", find(WellKnownRelationship.SAME), core,
                 kernelWorkspace);
        populate("IsContainedIn", find(WellKnownRelationship.IS_CONTAINED_IN),
                 core, kernelWorkspace);
        populate("Contains", find(WellKnownRelationship.CONTAINS), core,
                 kernelWorkspace);
        populate("IsA", find(WellKnownRelationship.IS_A), core, kernelWorkspace);
        populate("Includes", find(WellKnownRelationship.INCLUDES), core,
                 kernelWorkspace);
        populate("HasException", find(WellKnownRelationship.HAS_EXCEPTION),
                 core, kernelWorkspace);
        populate("IsExceptionTo", find(WellKnownRelationship.IS_EXCEPTION_TO),
                 core, kernelWorkspace);
        populate("IsLocationOf", find(WellKnownRelationship.IS_LOCATION_OF),
                 core, kernelWorkspace);
        populate("MapsToLocation",
                 find(WellKnownRelationship.MAPS_TO_LOCATION), core,
                 kernelWorkspace);
        populate("Prototype", find(WellKnownRelationship.PROTOTYPE), core,
                 kernelWorkspace);
        populate("PrototypeOf", find(WellKnownRelationship.PROTOTYPE_OF), core,
                 kernelWorkspace);
        populate("GreaterThan", find(WellKnownRelationship.GREATER_THAN), core,
                 kernelWorkspace);
        populate("LessThan", find(WellKnownRelationship.LESS_THAN), core,
                 kernelWorkspace);
        populate("Equals", find(WellKnownRelationship.EQUALS), core,
                 kernelWorkspace);
        populate("LessThanOrEqual",
                 find(WellKnownRelationship.LESS_THAN_OR_EQUAL), core,
                 kernelWorkspace);
        populate("GreaterThanOrEqual",
                 find(WellKnownRelationship.GREATER_THAN_OR_EQUAL), core,
                 kernelWorkspace);
        populate("Developed", find(WellKnownRelationship.DEVELOPED), core,
                 kernelWorkspace);
        populate("DevelopedBy", find(WellKnownRelationship.DEVELOPED_BY), core,
                 kernelWorkspace);
        populate("VersionOf", find(WellKnownRelationship.VERSION_OF), core,
                 kernelWorkspace);
        populate("HasVersion", find(WellKnownRelationship.HAS_VERSION), core,
                 kernelWorkspace);
        populate("HasMember", find(WellKnownRelationship.HAS_MEMBER), core,
                 kernelWorkspace);
        populate("MemberOf", find(WellKnownRelationship.MEMBER_OF), core,
                 kernelWorkspace);
        populate("HeadOf", find(WellKnownRelationship.HEAD_OF), core,
                 kernelWorkspace);
        populate("HasHead", find(WellKnownRelationship.HAS_HEAD), core,
                 kernelWorkspace);
        populate("HadMember", find(WellKnownRelationship.HAD_MEMBER), core,
                 kernelWorkspace);
        populate("FormerMemberOf",
                 find(WellKnownRelationship.FORMER_MEMBER_OF), core,
                 kernelWorkspace);
        populate("NotApplicableRelationship",
                 find(WellKnownRelationship.NOT_APPLICABLE), core,
                 kernelWorkspace);
        populate("OwnedBy", find(WellKnownRelationship.OWNED_BY), core,
                 kernelWorkspace);
        populate("Owns", find(WellKnownRelationship.OWNS), core,
                 kernelWorkspace);
        populate("InWorkspace", find(WellKnownRelationship.IN_WORKSPACE), core,
                 kernelWorkspace);
        populate("WorkspaceOf", find(WellKnownRelationship.WORKSPACE_OF), core,
                 kernelWorkspace);

        populate("Unset", find(WellKnownStatusCode.UNSET), core,
                 kernelWorkspace);
        populate("AnyStatusCode", find(WellKnownStatusCode.ANY), core,
                 kernelWorkspace);
        populate("CopyStatusCode", find(WellKnownStatusCode.COPY), core,
                 kernelWorkspace);
        populate("SameStatusCode", find(WellKnownStatusCode.SAME), core,
                 kernelWorkspace);
        populate("NotApplicableStatusCode",
                 find(WellKnownStatusCode.NOT_APPLICABLE), core,
                 kernelWorkspace);

        populate("UnsetUnit", find(WellKnownUnit.UNSET), core, kernelWorkspace);
        populate("AnyUnit", find(WellKnownUnit.ANY), core, kernelWorkspace);
        populate("CopyUnit", find(WellKnownUnit.COPY), core, kernelWorkspace);
        populate("SameUnit", find(WellKnownUnit.SAME), core, kernelWorkspace);
        populate("NotApplicableUnit", find(WellKnownUnit.NOT_APPLICABLE), core,
                 kernelWorkspace);

        populate("AnyInterval", find(WellKnownInterval.ANY), core,
                 kernelWorkspace);
        populate("CopyInterval", find(WellKnownInterval.COPY), core,
                 kernelWorkspace);
        populate("SameInterval", find(WellKnownInterval.SAME), core,
                 kernelWorkspace);
        populate("NotApplicableInterval",
                 find(WellKnownInterval.NOT_APPLICABLE), core, kernelWorkspace);

        populate("RootAgencyNetwork", em.find(AgencyNetwork.class, ZERO), core,
                 kernelWorkspace);
        populate("RootAttributeNetwork", em.find(AttributeNetwork.class, ZERO),
                 core, kernelWorkspace);
        populate("RootIntervalNetwork", em.find(IntervalNetwork.class, ZERO),
                 core, kernelWorkspace);
        populate("RootLocationNetwork", em.find(LocationNetwork.class, ZERO),
                 core, kernelWorkspace);
        populate("RootProductNetwork", em.find(ProductNetwork.class, ZERO),
                 core, kernelWorkspace);
        populate("RootRelationshipNetwork",
                 em.find(RelationshipNetwork.class, ZERO), core,
                 kernelWorkspace);
        populate("RootStatusCodeNetwork",
                 em.find(StatusCodeNetwork.class, ZERO), core, kernelWorkspace);
        populate("RootUnitNetwork", em.find(UnitNetwork.class, ZERO), core,
                 kernelWorkspace);
        AgencyAttributeAuthorization loginAuth = new AgencyAttributeAuthorization(
                                                                                  isA,
                                                                                  find(WellKnownAgency.CORE_USER),
                                                                                  find(WellKnownAttribute.LOGIN),
                                                                                  core);
        populate("LoginAuth", loginAuth, core, kernelWorkspace);
        AgencyAttributeAuthorization passwordHashAuth = new AgencyAttributeAuthorization(
                                                                                         isA,
                                                                                         find(WellKnownAgency.CORE_USER),
                                                                                         find(WellKnownAttribute.PASSWORD_HASH),
                                                                                         core);
        populate("PasswordHashAuth", passwordHashAuth, core, kernelWorkspace);
        AgencyNetwork edge = new AgencyNetwork(
                                               find(WellKnownAgency.SUPER_USER),
                                               isA,
                                               find(WellKnownAgency.CORE_USER),
                                               core);

        WorkspaceAuthorization edgeAuth = new WorkspaceAuthorization(
                                                                     edge,
                                                                     kernelWorkspace,
                                                                     core);
        em.persist(edgeAuth);
    }

    /**
     *
     * @param wko
     * @return the {@link Agency} corresponding to the well known object
     */
    Agency find(WellKnownAgency wko) {
        return em.find(Agency.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link Attribute} corresponding to the well known object
     */
    Attribute find(WellKnownAttribute wko) {
        return em.find(Attribute.class, wko.id());
    }

    Interval find(WellKnownInterval wko) {
        return em.find(Interval.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link Location} corresponding to the well known object
     */
    Location find(WellKnownLocation wko) {
        return em.find(Location.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link Product} corresponding to the well known object
     */
    Product find(WellKnownProduct wko) {
        return em.find(Product.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link Relationship} corresponding to the well known object
     */
    Relationship find(WellKnownRelationship wko) {
        return em.find(Relationship.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link StatusCode} corresponding to the well known object
     */
    StatusCode find(WellKnownStatusCode wko) {
        return em.find(StatusCode.class, wko.id());
    }

    Unit find(WellKnownUnit wko) {
        return em.find(Unit.class, wko.id());
    }

    void populate(String key, Ruleform ruleform, Agency core,
                  Product kernelWorkspace) {
        em.persist(new WorkspaceAuthorization(key, ruleform, kernelWorkspace,
                                              core));
    }
}
