/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.kernel;

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.util.postgres.PostgresDSL;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAttribute;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownInterval;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownLocation;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownUnit;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * @author hhildebrand
 *
 */
public class Bootstrap {
    public static void main(String[] argv) throws Exception {
        if (argv.length != 2) {
            System.err.println("Usage: Bootstrap <db.properties> <output file>");
            System.exit(1);
        }
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(new File(argv[0]))) {
            properties.load(is);
        }

        Connection conn = DriverManager.getConnection((String) properties.get("url"),
                                                      (String) properties.get("user"),
                                                      (String) properties.get("password"));
        conn.setAutoCommit(false);

        DSLContext create = PostgresDSL.using(conn);
        Bootstrap bootstrap = new Bootstrap(create);
        bootstrap.clear();
        create.transaction(config -> bootstrap.bootstrap());
        bootstrap.serialize(argv[1]);
    }

    private final Connection     connection;
    private final DSLContext     create;
    private final RecordsFactory records;

    public Bootstrap(DSLContext create) throws SQLException {
        connection = create.configuration()
                           .connectionProvider()
                           .acquire();
        connection.setAutoCommit(false);
        records = new RecordsFactory() {
            @Override
            public DSLContext create() {
                return create;
            }
        };
        this.create = create;
    }

    public void bootstrap() throws SQLException, IOException {
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
        connection.commit();
        constructKernelWorkspace();
    }

    public void clear() throws SQLException {
        KernelUtil.clear(create);
    }

    private void constructKernelWorkspace() throws IOException, SQLException {
        Agency core = records.resolve(WellKnownAgency.CORE.id());
        Product kernelWorkspace = find(WellKnownProduct.KERNEL_WORKSPACE);

        // Ain 
        populateAgencies(core, kernelWorkspace);
        populateAttributes(core, kernelWorkspace);
        populateIntervals(core, kernelWorkspace);
        populateLocations(core, kernelWorkspace);
        populateProducts(core, kernelWorkspace);
        populateRelationships(core, kernelWorkspace);
        populateStatusCodes(core, kernelWorkspace);
        populateUnits(core, kernelWorkspace);
        populateAnyFacets(core, kernelWorkspace);
        connection.commit();

        // Ain Soph
        ModelImpl model = new ModelImpl(create);

        new WorkspaceImporter(getClass().getResourceAsStream("/kernel.2.wsp"),
                              model).initialize()
                                    .load(kernelWorkspace);
        ExistentialAttributeRecord attributeValue = model.getPhantasmModel()
                                                         .getAttributeValue(kernelWorkspace,
                                                                            model.getKernel()
                                                                                 .getIRI());
        model.getPhantasmModel()
             .setValue(attributeValue, WellKnownObject.KERNEL_IRI);
        model.getPhantasmModel()
             .setAttributeValue(attributeValue);
        connection.commit();

        model.close();

        // Ain Soph Aur

    }

    /**
     *
     * @param wko
     * @return the {@link Product} corresponding to the well known object
     */
    private Product find(WellKnownProduct wko) {
        return records.resolve(wko.id());
    }

    private void insert(WellKnownAttribute wko) throws SQLException {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setId(wko.id());
        record.setName(wko.wkoName());
        record.setDomain(wko.domain());
        record.setDescription(wko.description());
        record.setUpdatedBy(WellKnownAgency.CORE.id());
        record.setIndexed(wko.indexed());
        record.setKeyed(wko.keyed());
        record.setValueType(wko.valueType()
                               .ordinal());
        record.setVersion(0);
        record.insert();
    }

    private void insert(WellKnownObject wko) throws SQLException {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setId(wko.id());
        record.setName(wko.wkoName());
        record.setDomain(wko.domain());
        record.setDescription(wko.description());
        record.setUpdatedBy(WellKnownAgency.CORE.id());
        record.setVersion(0);
        record.insert();
    }

    private void insert(WellKnownRelationship wko) throws SQLException {

        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setId(wko.id());
        record.setName(wko.wkoName());
        record.setDescription(wko.description());
        record.setDomain(wko.domain());
        record.setUpdatedBy(WellKnownAgency.CORE.id());
        record.setVersion(0);
        record.setInverse(wko.inverse()
                             .id());
        record.insert();
    }

    /**
     * @param auth
     * @param core
     * @param kernelWorkspace
     */
    private void populate(FacetRecord auth, Agency core,
                          Product kernelWorkspace) {
        records.newWorkspaceAuthorization(kernelWorkspace, auth, core)
               .insert();
    }

    private void populate(String key, WellKnownObject wko, Agency core,
                          Product kernelWorkspace) {
        records.newWorkspaceAuthorization(key, wko.id(),
                                          ReferenceType.Existential,
                                          kernelWorkspace, core)
               .insert();
    }

    private void populateAgencies(Agency core, Product kernelWorkspace) {
        populate("AnyAgency", WellKnownAgency.ANY, core, kernelWorkspace);
        populate("CopyAgency", WellKnownAgency.COPY, core, kernelWorkspace);
        populate("Core", WellKnownAgency.CORE, core, kernelWorkspace);
        populate("CoreAnimationSoftware",
                 WellKnownAgency.CORE_ANIMATION_SOFTWARE, core,
                 kernelWorkspace);
        populate("PropagationSoftware", WellKnownAgency.PROPAGATION_SOFTWARE,
                 core, kernelWorkspace);
        populate("SpecialSystemAgency", WellKnownAgency.SPECIAL_SYSTEM_AGENCY,
                 core, kernelWorkspace);
        populate("CoreModel", WellKnownAgency.CORE_MODEL, core,
                 kernelWorkspace);
        populate("InverseSoftware", WellKnownAgency.INVERSE_SOFTWARE, core,
                 kernelWorkspace);
        populate("SameAgency", WellKnownAgency.SAME, core, kernelWorkspace);
        populate("NotApplicableAgency", WellKnownAgency.NOT_APPLICABLE, core,
                 kernelWorkspace);
    }

    /**
     * @param core
     * @param kernelWorkspace
     */
    private void populateAnyFacets(Agency core, Product kernelWorkspace) {
        FacetRecord anyAgency = records.newFacet(core);
        anyAgency.setClassifier(WellKnownRelationship.ANY.id());
        anyAgency.setClassification(WellKnownAgency.ANY.id());
        anyAgency.setName("AnyAgency");
        anyAgency.setNotes("The facet that represents any agency");
        populate(anyAgency, core, kernelWorkspace);

        FacetRecord anyAttribute = records.newFacet(core);
        anyAttribute.setClassifier(WellKnownRelationship.ANY.id());
        anyAttribute.setClassification(WellKnownAttribute.ANY.id());
        anyAttribute.setName("AnyAttribute");
        anyAttribute.setNotes("The facet that represents any attribute");
        populate(anyAttribute, core, kernelWorkspace);

        FacetRecord anyInterval = records.newFacet(core);
        anyInterval.setClassifier(WellKnownRelationship.ANY.id());
        anyInterval.setClassification(WellKnownInterval.ANY.id());
        anyInterval.setName("AnyInterval");
        anyInterval.setNotes("The facet that represents any interval");
        populate(anyInterval, core, kernelWorkspace);

        FacetRecord anyLocation = records.newFacet(core);
        anyLocation.setClassifier(WellKnownRelationship.ANY.id());
        anyLocation.setClassification(WellKnownLocation.ANY.id());
        anyLocation.setName("AnyLocation");
        anyLocation.setNotes("The facet that represents any location");
        populate(anyLocation, core, kernelWorkspace);

        FacetRecord anyProduct = records.newFacet(core);
        anyProduct.setClassifier(WellKnownRelationship.ANY.id());
        anyProduct.setClassification(WellKnownProduct.ANY.id());
        anyProduct.setName("AnyProduct");
        anyProduct.setNotes("The facet that represents any product");
        populate(anyProduct, core, kernelWorkspace);

        FacetRecord anyRelationship = records.newFacet(core);
        anyRelationship.setClassifier(WellKnownRelationship.ANY.id());
        anyRelationship.setClassification(WellKnownRelationship.ANY.id());
        populate(anyRelationship, core, kernelWorkspace);
        anyRelationship.setName("AnyRelationship");
        anyRelationship.setNotes("The facet that represents any relationship");

        FacetRecord anyStatusCode = records.newFacet(core);
        anyStatusCode.setClassifier(WellKnownRelationship.ANY.id());
        anyStatusCode.setClassification(WellKnownStatusCode.ANY.id());
        anyStatusCode.setName("AnyStatusCode");
        anyAgency.setNotes("The facet that represents any statusCode");
        populate(anyStatusCode, core, kernelWorkspace);

        FacetRecord anyUnit = records.newFacet(core);
        anyUnit.setClassifier(WellKnownRelationship.ANY.id());
        anyUnit.setClassification(WellKnownUnit.ANY.id());
        anyUnit.setName("AnyUnit");
        anyUnit.setNotes("The facet that represents any unit");
        populate(anyUnit, core, kernelWorkspace);
    }

    private void populateAttributes(Agency core, Product kernelWorkspace) {
        populate("AnyAttribute", WellKnownAttribute.ANY, core, kernelWorkspace);
        populate("CopyAttribute", WellKnownAttribute.COPY, core,
                 kernelWorkspace);
        populate("NotApplicableAttribute", WellKnownAttribute.NOT_APPLICABLE,
                 core, kernelWorkspace);
        populate("SameAttribute", WellKnownAttribute.SAME, core,
                 kernelWorkspace);

    }

    private void populateIntervals(Agency core, Product kernelWorkspace) {
        populate("AnyInterval", WellKnownInterval.ANY, core, kernelWorkspace);
        populate("CopyInterval", WellKnownInterval.COPY, core, kernelWorkspace);
        populate("SameInterval", WellKnownInterval.SAME, core, kernelWorkspace);
        populate("NotApplicableInterval", WellKnownInterval.NOT_APPLICABLE,
                 core, kernelWorkspace);
    }

    private void populateLocations(Agency core, Product kernelWorkspace) {
        populate("AnyLocation", WellKnownLocation.ANY, core, kernelWorkspace);
        populate("CopyLocation", WellKnownLocation.COPY, core, kernelWorkspace);
        populate("NotApplicableLocation", WellKnownLocation.NOT_APPLICABLE,
                 core, kernelWorkspace);
        populate("SameLocation", WellKnownLocation.SAME, core, kernelWorkspace);
    }

    private void populateProducts(Agency core, Product kernelWorkspace) {
        populate("AnyProduct", WellKnownProduct.ANY, core, kernelWorkspace);
        populate("CopyProduct", WellKnownProduct.COPY, core, kernelWorkspace);
        populate("SameProduct", WellKnownProduct.SAME, core, kernelWorkspace);
        populate("NotApplicableProduct", WellKnownProduct.NOT_APPLICABLE, core,
                 kernelWorkspace);
        populate("Workspace", WellKnownProduct.WORKSPACE, core,
                 kernelWorkspace);
        populate("KernelWorkspace", WellKnownProduct.KERNEL_WORKSPACE, core,
                 kernelWorkspace);
    }

    private void populateRelationships(Agency core, Product kernelWorkspace) {
        populate("AnyRelationship", WellKnownRelationship.ANY, core,
                 kernelWorkspace);
        populate("CopyRelationship", WellKnownRelationship.COPY, core,
                 kernelWorkspace);
        populate("SameRelationship", WellKnownRelationship.SAME, core,
                 kernelWorkspace);
        populate("IsContainedIn", WellKnownRelationship.IS_CONTAINED_IN, core,
                 kernelWorkspace);
        populate("Contains", WellKnownRelationship.CONTAINS, core,
                 kernelWorkspace);
        populate("IsA", WellKnownRelationship.IS_A, core, kernelWorkspace);
        populate("Includes", WellKnownRelationship.INCLUDES, core,
                 kernelWorkspace);
        populate("HasException", WellKnownRelationship.HAS_EXCEPTION, core,
                 kernelWorkspace);
        populate("IsExceptionTo", WellKnownRelationship.IS_EXCEPTION_TO, core,
                 kernelWorkspace);
        populate("IsLocationOf", WellKnownRelationship.IS_LOCATION_OF, core,
                 kernelWorkspace);
        populate("MapsToLocation", WellKnownRelationship.MAPS_TO_LOCATION, core,
                 kernelWorkspace);
        populate("Prototype", WellKnownRelationship.PROTOTYPE, core,
                 kernelWorkspace);
        populate("PrototypeOf", WellKnownRelationship.PROTOTYPE_OF, core,
                 kernelWorkspace);
        populate("GreaterThan", WellKnownRelationship.GREATER_THAN, core,
                 kernelWorkspace);
        populate("LessThan", WellKnownRelationship.LESS_THAN, core,
                 kernelWorkspace);
        populate("Equals", WellKnownRelationship.EQUALS, core, kernelWorkspace);
        populate("LessThanOrEqual", WellKnownRelationship.LESS_THAN_OR_EQUAL,
                 core, kernelWorkspace);
        populate("GreaterThanOrEqual",
                 WellKnownRelationship.GREATER_THAN_OR_EQUAL, core,
                 kernelWorkspace);
        populate("Developed", WellKnownRelationship.DEVELOPED, core,
                 kernelWorkspace);
        populate("DevelopedBy", WellKnownRelationship.DEVELOPED_BY, core,
                 kernelWorkspace);
        populate("VersionOf", WellKnownRelationship.VERSION_OF, core,
                 kernelWorkspace);
        populate("HasVersion", WellKnownRelationship.HAS_VERSION, core,
                 kernelWorkspace);
        populate("HasMember", WellKnownRelationship.HAS_MEMBER, core,
                 kernelWorkspace);
        populate("MemberOf", WellKnownRelationship.MEMBER_OF, core,
                 kernelWorkspace);
        populate("HeadOf", WellKnownRelationship.HEAD_OF, core,
                 kernelWorkspace);
        populate("HasHead", WellKnownRelationship.HAS_HEAD, core,
                 kernelWorkspace);
        populate("HadMember", WellKnownRelationship.HAD_MEMBER, core,
                 kernelWorkspace);
        populate("FormerMemberOf", WellKnownRelationship.FORMER_MEMBER_OF, core,
                 kernelWorkspace);
        populate("NotApplicableRelationship",
                 WellKnownRelationship.NOT_APPLICABLE, core, kernelWorkspace);
        populate("OwnedBy", WellKnownRelationship.OWNED_BY, core,
                 kernelWorkspace);
        populate("Owns", WellKnownRelationship.OWNS, core, kernelWorkspace);
        populate("Imports", WellKnownRelationship.IMPORTS, core,
                 kernelWorkspace);
        populate("ImportedBy", WellKnownRelationship.IMPORTED_BY, core,
                 kernelWorkspace);
        populate("IsValidatedBy", WellKnownRelationship.IS_VALIDATED_BY, core,
                 kernelWorkspace);
        populate("Validates", WellKnownRelationship.VALIDATES, core,
                 kernelWorkspace);
    }

    private void populateStatusCodes(Agency core, Product kernelWorkspace) {
        populate("Unset", WellKnownStatusCode.UNSET, core, kernelWorkspace);
        populate("AnyStatusCode", WellKnownStatusCode.ANY, core,
                 kernelWorkspace);
        populate("CopyStatusCode", WellKnownStatusCode.COPY, core,
                 kernelWorkspace);
        populate("SameStatusCode", WellKnownStatusCode.SAME, core,
                 kernelWorkspace);
        populate("NotApplicableStatusCode", WellKnownStatusCode.NOT_APPLICABLE,
                 core, kernelWorkspace);
    }

    private void populateUnits(Agency core, Product kernelWorkspace) {
        populate("UnsetUnit", WellKnownUnit.UNSET, core, kernelWorkspace);
        populate("AnyUnit", WellKnownUnit.ANY, core, kernelWorkspace);
        populate("CopyUnit", WellKnownUnit.COPY, core, kernelWorkspace);
        populate("SameUnit", WellKnownUnit.SAME, core, kernelWorkspace);
        populate("NotApplicableUnit", WellKnownUnit.NOT_APPLICABLE, core,
                 kernelWorkspace);
        populate("Nanoseconds", WellKnownUnit.NANOSECONDS, core,
                 kernelWorkspace);
        populate("Microseconds", WellKnownUnit.MICROSECONDS, core,
                 kernelWorkspace);
        populate("Milliseconds", WellKnownUnit.MILLISECONDS, core,
                 kernelWorkspace);
        populate("Seconds", WellKnownUnit.SECONDS, core, kernelWorkspace);
        populate("Hours", WellKnownUnit.HOURS, core, kernelWorkspace);
        populate("Days", WellKnownUnit.DAYS, core, kernelWorkspace);
    }

    private void serialize(String fileName) throws IOException {
        Product kernelWorkspace = find(WellKnownProduct.KERNEL_WORKSPACE);
        WorkspaceSnapshot snapshot = new WorkspaceSnapshot(kernelWorkspace,
                                                           create);
        try (FileOutputStream os = new FileOutputStream(new File(fileName))) {
            snapshot.serializeTo(os);
        }
    }
}
