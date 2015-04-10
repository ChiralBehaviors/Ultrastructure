/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.internal.SessionImpl;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAttribute;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownInterval;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownLocation;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownUnit;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceSnapshot;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

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
        bootstrap.serialize(argv[1]);
        System.exit(0);
    }

    private final Connection    connection;
    private final EntityManager em;

    public Bootstrap(EntityManager em) throws SQLException {
        connection = em.unwrap(SessionImpl.class).connection();
        connection.setAutoCommit(false);
        this.em = em;
    }

    public void bootstrap() throws SQLException {
        KernelUtil.alterTriggers(connection, false);
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
        KernelUtil.alterTriggers(connection, true);
        constructKernelWorkspace();
    }

    public void clear() throws SQLException {
        KernelUtil.clear(em);
    }

    public void insert(WellKnownAttribute wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, updated_by, value_type, version) VALUES (?, ?, ?, ?, ?, 1)",
                                                                        wko.tableName()));
        try {
            s.setObject(1, wko.id());
            s.setString(2, wko.wkoName());
            s.setString(3, wko.description());
            s.setObject(4, WellKnownAgency.CORE.id());
            s.setInt(5, wko.valueType().ordinal());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    public void insert(WellKnownInterval wki) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, start_unit, duration_unit, description, updated_by, version) VALUES (?, ?, ?, ?, ?, ?, 1)",
                                                                        wki.tableName()));
        try {
            s.setObject(1, wki.id());
            s.setString(2, wki.wkoName());
            s.setObject(3, wki.startUnit().id());
            s.setObject(4, wki.durationUnit().id());
            s.setString(5, wki.description());
            s.setObject(6, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wki), e);
        }
    }

    public void insert(WellKnownLocation wkl) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, updated_by, version) VALUES (?, ?, ?, ?, 1)",
                                                                        wkl.tableName()));
        try {
            s.setObject(1, wkl.id());
            s.setString(2, wkl.wkoName());
            s.setString(3, wkl.description());
            s.setObject(4, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert  %s", wkl),
                                   e);
        }
    }

    public void insert(WellKnownObject wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, updated_by, version) VALUES (?, ?, ?, ?, 1)",
                                                                        wko.tableName()));
        try {
            s.setObject(1, wko.id());
            s.setString(2, wko.wkoName());
            s.setString(3, wko.description());
            s.setObject(4, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    public void insert(WellKnownRelationship wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, updated_by, inverse, preferred, version) VALUES (?, ?, ?, ?, ?, ?, 1)",
                                                                        wko.tableName()));
        try {
            s.setObject(1, wko.id());
            s.setString(2, wko.wkoName());
            s.setString(3, wko.description());
            s.setObject(4, WellKnownAgency.CORE.id());
            s.setObject(5, wko.inverse().id());
            s.setByte(6, (byte) (wko.preferred() ? 1 : 0));
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    public void insert(WellKnownStatusCode wko) throws SQLException {
        PreparedStatement s = connection.prepareStatement(String.format("INSERT into %s (id, name, description, propagate_children, updated_by, version) VALUES (?, ?, ?, ?, ?, 1)",
                                                                        wko.tableName()));
        try {
            s.setObject(1, wko.id());
            s.setString(2, wko.wkoName());
            s.setString(3, wko.description());
            s.setInt(4, wko == WellKnownStatusCode.UNSET ? Ruleform.TRUE
                                                        : Ruleform.FALSE);
            s.setObject(5, WellKnownAgency.CORE.id());
            s.execute();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to insert %s", wko), e);
        }
    }

    private void serialize(String fileName) throws IOException {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new CoREModule());
        objMapper.registerModule(new Hibernate4Module());
        Product kernelWorkspace = find(WellKnownProduct.KERNEL_WORKSPACE);
        em.detach(kernelWorkspace);
        objMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName),
                                                              new WorkspaceSnapshot(
                                                                                    kernelWorkspace,
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
        populate("Imports", find(WellKnownRelationship.IMPORTS), core,
                 kernelWorkspace);
        populate("ImportedBy", find(WellKnownRelationship.IMPORTED_BY), core,
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
        populate("Nanoseconds", find(WellKnownUnit.NANOSECONDS), core,
                 kernelWorkspace);
        populate("Microseconds", find(WellKnownUnit.MICROSECONDS), core,
                 kernelWorkspace);
        populate("Milliseconds", find(WellKnownUnit.MILLISECONDS), core,
                 kernelWorkspace);
        populate("Seconds", find(WellKnownUnit.SECONDS), core, kernelWorkspace);
        populate("Hours", find(WellKnownUnit.HOURS), core, kernelWorkspace);
        populate("Days", find(WellKnownUnit.DAYS), core, kernelWorkspace);

        populate("AnyInterval", find(WellKnownInterval.ANY), core,
                 kernelWorkspace);
        populate("CopyInterval", find(WellKnownInterval.COPY), core,
                 kernelWorkspace);
        populate("SameInterval", find(WellKnownInterval.SAME), core,
                 kernelWorkspace);
        populate("NotApplicableInterval",
                 find(WellKnownInterval.NOT_APPLICABLE), core, kernelWorkspace);
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
