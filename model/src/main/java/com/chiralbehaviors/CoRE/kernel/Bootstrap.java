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
        em.getTransaction().begin();
        bootstrap.clear();
        bootstrap.bootstrap();
        em.getTransaction().commit();
        em.clear();
        bootstrap.serialize(argv[1]);
    }

    private static final String ZERO = UuidGenerator.toBase64(new UUID(0, 0));

    private final Connection    connection;
    private final EntityManager em;

    public Bootstrap(EntityManager em) throws SQLException {
        connection = em.unwrap(Connection.class);
        connection.setAutoCommit(false);
        this.em = em;
    }

    public void bootstrap() throws SQLException {
        KernelLoader.alterTriggers(connection, false);
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
        KernelLoader.alterTriggers(connection, true);
        constructKernelWorkspace();
    }

    public void clear() throws SQLException {
        KernelLoader.clear(em);
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
        workspace.link(isA, kernelWorkspace, core, core, em);

        populate("attribute", find(WellKnownAttribute.ATTRIBUTE), core,
                 kernelWorkspace);
        populate("anyAttribute", find(WellKnownAttribute.ANY), core,
                 kernelWorkspace);
        populate("copyAttribute", find(WellKnownAttribute.COPY), core,
                 kernelWorkspace);
        populate("notApplicableAttribute",
                 find(WellKnownAttribute.NOT_APPLICABLE), core, kernelWorkspace);
        populate("sameAttribute", find(WellKnownAttribute.SAME), core,
                 kernelWorkspace);
        populate("loginAttribute", find(WellKnownAttribute.LOGIN), core,
                 kernelWorkspace);
        populate("passwordHashAttribute",
                 find(WellKnownAttribute.PASSWORD_HASH), core, kernelWorkspace);

        populate("product", find(WellKnownProduct.ENTITY), core,
                 kernelWorkspace);
        populate("anyProduct", find(WellKnownProduct.ANY), core,
                 kernelWorkspace);
        populate("copyProduct", find(WellKnownProduct.COPY), core,
                 kernelWorkspace);
        populate("sameProduct", find(WellKnownProduct.SAME), core,
                 kernelWorkspace);
        populate("notApplicableProduct", find(WellKnownProduct.NOT_APPLICABLE),
                 core, kernelWorkspace);
        populate("workspace", find(WellKnownProduct.WORKSPACE), core,
                 kernelWorkspace);
        populate("kernelWorkspace", find(WellKnownProduct.KERNEL_WORKSPACE),
                 core, kernelWorkspace);

        populate("location", find(WellKnownLocation.LOCATION), core,
                 kernelWorkspace);
        populate("anyLocation", find(WellKnownLocation.ANY), core,
                 kernelWorkspace);
        populate("copyLocation", find(WellKnownLocation.COPY), core,
                 kernelWorkspace);
        populate("notApplicableLocation",
                 find(WellKnownLocation.NOT_APPLICABLE), core, kernelWorkspace);
        populate("sameLocation", find(WellKnownLocation.SAME), core,
                 kernelWorkspace);

        populate("coreUser", find(WellKnownAgency.CORE_USER), core,
                 kernelWorkspace);
        populate("agency", find(WellKnownAgency.AGENCY), core, kernelWorkspace);
        populate("anyAgency", find(WellKnownAgency.ANY), core, kernelWorkspace);
        populate("copyAgency", find(WellKnownAgency.COPY), core,
                 kernelWorkspace);
        populate("core", find(WellKnownAgency.CORE), core, kernelWorkspace);
        populate("coreAnimationSoftware",
                 find(WellKnownAgency.CORE_ANIMATION_SOFTWARE), core,
                 kernelWorkspace);
        populate("propagationSoftware",
                 find(WellKnownAgency.PROPAGATION_SOFTWARE), core,
                 kernelWorkspace);
        populate("specialSystemAgency",
                 find(WellKnownAgency.SPECIAL_SYSTEM_AGENCY), core,
                 kernelWorkspace);
        populate("coreModel", find(WellKnownAgency.CORE_MODEL), core,
                 kernelWorkspace);
        populate("superUser", find(WellKnownAgency.SUPER_USER), core,
                 kernelWorkspace);
        populate("inverseSoftware", find(WellKnownAgency.INVERSE_SOFTWARE),
                 core, kernelWorkspace);
        populate("sameAgency", find(WellKnownAgency.SAME), core,
                 kernelWorkspace);
        populate("notApplicableAgency", find(WellKnownAgency.NOT_APPLICABLE),
                 core, kernelWorkspace);

        populate("anyRelationship", find(WellKnownRelationship.ANY), core,
                 kernelWorkspace);
        populate("copyRelationship", find(WellKnownRelationship.COPY), core,
                 kernelWorkspace);
        populate("sameRelationship", find(WellKnownRelationship.SAME), core,
                 kernelWorkspace);
        populate("isContainedIn", find(WellKnownRelationship.IS_CONTAINED_IN),
                 core, kernelWorkspace);
        populate("contains", find(WellKnownRelationship.CONTAINS), core,
                 kernelWorkspace);
        populate("isA", find(WellKnownRelationship.IS_A), core, kernelWorkspace);
        populate("includes", find(WellKnownRelationship.INCLUDES), core,
                 kernelWorkspace);
        populate("hasException", find(WellKnownRelationship.HAS_EXCEPTION),
                 core, kernelWorkspace);
        populate("isExceptionTo", find(WellKnownRelationship.IS_EXCEPTION_TO),
                 core, kernelWorkspace);
        populate("isLocationOf", find(WellKnownRelationship.IS_LOCATION_OF),
                 core, kernelWorkspace);
        populate("mapsToLocation",
                 find(WellKnownRelationship.MAPS_TO_LOCATION), core,
                 kernelWorkspace);
        populate("prototype", find(WellKnownRelationship.PROTOTYPE), core,
                 kernelWorkspace);
        populate("prototypeOf", find(WellKnownRelationship.PROTOTYPE_OF), core,
                 kernelWorkspace);
        populate("greaterThan", find(WellKnownRelationship.GREATER_THAN), core,
                 kernelWorkspace);
        populate("lessThan", find(WellKnownRelationship.LESS_THAN), core,
                 kernelWorkspace);
        populate("equals", find(WellKnownRelationship.EQUALS), core,
                 kernelWorkspace);
        populate("lessThanOrEqual",
                 find(WellKnownRelationship.LESS_THAN_OR_EQUAL), core,
                 kernelWorkspace);
        populate("greaterThanOrEqual",
                 find(WellKnownRelationship.GREATER_THAN_OR_EQUAL), core,
                 kernelWorkspace);
        populate("developed", find(WellKnownRelationship.DEVELOPED), core,
                 kernelWorkspace);
        populate("developedBy", find(WellKnownRelationship.DEVELOPED_BY), core,
                 kernelWorkspace);
        populate("versionOf", find(WellKnownRelationship.VERSION_OF), core,
                 kernelWorkspace);
        populate("hasVersion", find(WellKnownRelationship.HAS_VERSION), core,
                 kernelWorkspace);
        populate("hasMember", find(WellKnownRelationship.HAS_MEMBER), core,
                 kernelWorkspace);
        populate("memberOf", find(WellKnownRelationship.MEMBER_OF), core,
                 kernelWorkspace);
        populate("headOf", find(WellKnownRelationship.HEAD_OF), core,
                 kernelWorkspace);
        populate("hasHead", find(WellKnownRelationship.HAS_HEAD), core,
                 kernelWorkspace);
        populate("hadMember", find(WellKnownRelationship.HAD_MEMBER), core,
                 kernelWorkspace);
        populate("formerMemberOf",
                 find(WellKnownRelationship.FORMER_MEMBER_OF), core,
                 kernelWorkspace);
        populate("notApplicableRelationship",
                 find(WellKnownRelationship.NOT_APPLICABLE), core,
                 kernelWorkspace);
        populate("ownedBy", find(WellKnownRelationship.OWNED_BY), core,
                 kernelWorkspace);
        populate("owns", find(WellKnownRelationship.OWNS), core,
                 kernelWorkspace);
        populate("inWorkspace", find(WellKnownRelationship.IN_WORKSPACE), core,
                 kernelWorkspace);
        populate("workspaceOf", find(WellKnownRelationship.WORKSPACE_OF), core,
                 kernelWorkspace);

        populate("unset", find(WellKnownStatusCode.UNSET), core,
                 kernelWorkspace);
        populate("anyStatusCode", find(WellKnownStatusCode.ANY), core,
                 kernelWorkspace);
        populate("copyStatusCode", find(WellKnownStatusCode.COPY), core,
                 kernelWorkspace);
        populate("sameStatusCode", find(WellKnownStatusCode.SAME), core,
                 kernelWorkspace);
        populate("notApplicableStatusCode",
                 find(WellKnownStatusCode.NOT_APPLICABLE), core,
                 kernelWorkspace);

        populate("unsetUnit", find(WellKnownUnit.UNSET), core, kernelWorkspace);
        populate("anyUnit", find(WellKnownUnit.ANY), core, kernelWorkspace);
        populate("copyUnit", find(WellKnownUnit.COPY), core, kernelWorkspace);
        populate("sameUnit", find(WellKnownUnit.SAME), core, kernelWorkspace);
        populate("notApplicableUnit", find(WellKnownUnit.NOT_APPLICABLE), core,
                 kernelWorkspace);

        populate("anyInterval", find(WellKnownInterval.ANY), core,
                 kernelWorkspace);
        populate("copyInterval", find(WellKnownInterval.COPY), core,
                 kernelWorkspace);
        populate("sameInterval", find(WellKnownInterval.SAME), core,
                 kernelWorkspace);
        populate("notApplicableInterval",
                 find(WellKnownInterval.NOT_APPLICABLE), core, kernelWorkspace);

        populate("rootAgencyNetwork", em.find(AgencyNetwork.class, ZERO), core,
                 kernelWorkspace);
        populate("rootAttributeNetwork", em.find(AttributeNetwork.class, ZERO),
                 core, kernelWorkspace);
        populate("rootIntervalNetwork", em.find(IntervalNetwork.class, ZERO),
                 core, kernelWorkspace);
        populate("rootLocationNetwork", em.find(LocationNetwork.class, ZERO),
                 core, kernelWorkspace);
        populate("rootProductNetwork", em.find(ProductNetwork.class, ZERO),
                 core, kernelWorkspace);
        populate("rootRelationshipNetwork",
                 em.find(RelationshipNetwork.class, ZERO), core,
                 kernelWorkspace);
        populate("rootStatusCodeNetwork",
                 em.find(StatusCodeNetwork.class, ZERO), core, kernelWorkspace);
        populate("rootUnitNetwork", em.find(UnitNetwork.class, ZERO), core,
                 kernelWorkspace);
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
