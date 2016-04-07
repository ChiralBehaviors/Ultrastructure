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
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

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
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceAuthorizationRecord;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * @author hhildebrand
 *
 */
public class Bootstrap {
    public static void boostrap(String outputFile,
                                DSLContext create) throws SQLException,
                                                   IOException {
        Bootstrap bootstrap = new Bootstrap(create);
        bootstrap.clear();
        create.transaction(config -> bootstrap.bootstrap());
        bootstrap.serialize(outputFile);
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length != 2) {
            System.err.println("Usage: Bootstrap <db.properties> <output file>");
            System.exit(1);
        }
        Properties properties = new Properties();
        String outputFile = argv[1];
        try (InputStream is = new FileInputStream(new File(argv[0]))) {
            properties.load(is);
        }

        Connection conn = DriverManager.getConnection((String) properties.get("url"),
                                                      (String) properties.get("user"),
                                                      (String) properties.get("password"));
        conn.setAutoCommit(false);

        try (DSLContext create = DSL.using(conn)) {
            boostrap(outputFile, create);
        }
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

            @Override
            public UUID currentPrincipalId() {
                return WellKnownAgency.CORE.id();
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
        RecordsFactory.clear(create);
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
        ModelImpl model = new ModelImpl(create.configuration()
                                              .connectionProvider()
                                              .acquire());

        new WorkspaceImporter(getClass().getResourceAsStream("/kernel.2.wsp"),
                              model).initialize()
                                    .load(kernelWorkspace);
        ExistentialAttributeRecord attributeValue = model.getPhantasmModel()
                                                         .getAttributeValue(kernelWorkspace,
                                                                            model.getKernel()
                                                                                 .getIRI());
        model.getPhantasmModel()
             .setValue(attributeValue, WellKnownObject.KERNEL_IRI);
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
        record.setValueType(wko.valueType());
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

    private void populate(FacetRecord auth, Product kernelWorkspace) {
        records.newWorkspaceAuthorization(null, kernelWorkspace, auth)
               .insert();
    }

    private void populate(String key, WellKnownObject wko,
                          Product kernelWorkspace) {
        WorkspaceAuthorizationRecord auth = records.newWorkspaceAuthorization(key,
                                                                              wko.id(),
                                                                              ReferenceType.Existential,
                                                                              kernelWorkspace);
        auth.insert();
        ExistentialRecord existential = create.selectFrom(EXISTENTIAL)
                                              .where(EXISTENTIAL.ID.equal(wko.id()))
                                              .fetchOne();
        existential.setWorkspace(auth.getId());
        existential.update();
    }

    private void populateAgencies(Agency core, Product kernelWorkspace) {
        populate("AnyAgency", WellKnownAgency.ANY, kernelWorkspace);
        populate("CopyAgency", WellKnownAgency.COPY, kernelWorkspace);
        populate("Core", WellKnownAgency.CORE, kernelWorkspace);
        populate("CoreAnimationSoftware",
                 WellKnownAgency.CORE_ANIMATION_SOFTWARE, kernelWorkspace);
        populate("PropagationSoftware", WellKnownAgency.PROPAGATION_SOFTWARE,
                 kernelWorkspace);
        populate("SpecialSystemAgency", WellKnownAgency.SPECIAL_SYSTEM_AGENCY,
                 kernelWorkspace);
        populate("CoreModel", WellKnownAgency.CORE_MODEL, kernelWorkspace);
        populate("InverseSoftware", WellKnownAgency.INVERSE_SOFTWARE,
                 kernelWorkspace);
        populate("SameAgency", WellKnownAgency.SAME, kernelWorkspace);
        populate("NotApplicableAgency", WellKnownAgency.NOT_APPLICABLE,
                 kernelWorkspace);
    }

    /**
     * @param core
     * @param kernelWorkspace
     */
    private void populateAnyFacets(Agency core, Product kernelWorkspace) {
        FacetRecord anyAgency = records.newFacet();
        anyAgency.setClassifier(WellKnownRelationship.ANY.id());
        anyAgency.setClassification(WellKnownAgency.ANY.id());
        anyAgency.setName("AnyAgency");
        anyAgency.setNotes("The facet that represents any agency");
        populate(anyAgency, kernelWorkspace);
        anyAgency.insert();

        FacetRecord anyAttribute = records.newFacet();
        anyAttribute.setClassifier(WellKnownRelationship.ANY.id());
        anyAttribute.setClassification(WellKnownAttribute.ANY.id());
        anyAttribute.setName("AnyAttribute");
        anyAttribute.setNotes("The facet that represents any attribute");
        anyAttribute.insert();
        populate(anyAttribute, kernelWorkspace);

        FacetRecord anyInterval = records.newFacet();
        anyInterval.setClassifier(WellKnownRelationship.ANY.id());
        anyInterval.setClassification(WellKnownInterval.ANY.id());
        anyInterval.setName("AnyInterval");
        anyInterval.setNotes("The facet that represents any interval");
        anyInterval.insert();
        populate(anyInterval, kernelWorkspace);

        FacetRecord anyLocation = records.newFacet();
        anyLocation.setClassifier(WellKnownRelationship.ANY.id());
        anyLocation.setClassification(WellKnownLocation.ANY.id());
        anyLocation.setName("AnyLocation");
        anyLocation.setNotes("The facet that represents any location");
        anyLocation.insert();
        populate(anyLocation, kernelWorkspace);

        FacetRecord anyProduct = records.newFacet();
        anyProduct.setClassifier(WellKnownRelationship.ANY.id());
        anyProduct.setClassification(WellKnownProduct.ANY.id());
        anyProduct.setName("AnyProduct");
        anyProduct.setNotes("The facet that represents any product");
        anyProduct.insert();
        populate(anyProduct, kernelWorkspace);

        FacetRecord anyRelationship = records.newFacet();
        anyRelationship.setClassifier(WellKnownRelationship.ANY.id());
        anyRelationship.setClassification(WellKnownRelationship.ANY.id());
        anyRelationship.setName("AnyRelationship");
        anyRelationship.setNotes("The facet that represents any relationship");
        anyRelationship.insert();
        populate(anyRelationship, kernelWorkspace);

        FacetRecord anyStatusCode = records.newFacet();
        anyStatusCode.setClassifier(WellKnownRelationship.ANY.id());
        anyStatusCode.setClassification(WellKnownStatusCode.ANY.id());
        anyStatusCode.setName("AnyStatusCode");
        anyStatusCode.setNotes("The facet that represents any statusCode");
        anyStatusCode.insert();
        populate(anyStatusCode, kernelWorkspace);

        FacetRecord anyUnit = records.newFacet();
        anyUnit.setClassifier(WellKnownRelationship.ANY.id());
        anyUnit.setClassification(WellKnownUnit.ANY.id());
        anyUnit.setName("AnyUnit");
        anyUnit.setNotes("The facet that represents any unit");
        anyUnit.insert();
        populate(anyUnit, kernelWorkspace);
    }

    private void populateAttributes(Agency core, Product kernelWorkspace) {
        populate("AnyAttribute", WellKnownAttribute.ANY, kernelWorkspace);
        populate("CopyAttribute", WellKnownAttribute.COPY, kernelWorkspace);
        populate("NotApplicableAttribute", WellKnownAttribute.NOT_APPLICABLE,
                 kernelWorkspace);
        populate("SameAttribute", WellKnownAttribute.SAME, kernelWorkspace);
        populate("Nullable", WellKnownAttribute.NULLABLE, kernelWorkspace);

    }

    private void populateIntervals(Agency core, Product kernelWorkspace) {
        populate("AnyInterval", WellKnownInterval.ANY, kernelWorkspace);
        populate("CopyInterval", WellKnownInterval.COPY, kernelWorkspace);
        populate("SameInterval", WellKnownInterval.SAME, kernelWorkspace);
        populate("NotApplicableInterval", WellKnownInterval.NOT_APPLICABLE,
                 kernelWorkspace);
    }

    private void populateLocations(Agency core, Product kernelWorkspace) {
        populate("AnyLocation", WellKnownLocation.ANY, kernelWorkspace);
        populate("CopyLocation", WellKnownLocation.COPY, kernelWorkspace);
        populate("NotApplicableLocation", WellKnownLocation.NOT_APPLICABLE,
                 kernelWorkspace);
        populate("SameLocation", WellKnownLocation.SAME, kernelWorkspace);
    }

    private void populateProducts(Agency core, Product kernelWorkspace) {
        populate("AnyProduct", WellKnownProduct.ANY, kernelWorkspace);
        populate("CopyProduct", WellKnownProduct.COPY, kernelWorkspace);
        populate("SameProduct", WellKnownProduct.SAME, kernelWorkspace);
        populate("NotApplicableProduct", WellKnownProduct.NOT_APPLICABLE,
                 kernelWorkspace);
        populate("Workspace", WellKnownProduct.WORKSPACE, kernelWorkspace);
        populate("KernelWorkspace", WellKnownProduct.KERNEL_WORKSPACE,
                 kernelWorkspace);
    }

    private void populateRelationships(Agency core, Product kernelWorkspace) {
        populate("AnyRelationship", WellKnownRelationship.ANY, kernelWorkspace);
        populate("CopyRelationship", WellKnownRelationship.COPY,
                 kernelWorkspace);
        populate("SameRelationship", WellKnownRelationship.SAME,
                 kernelWorkspace);
        populate("IsContainedIn", WellKnownRelationship.IS_CONTAINED_IN,
                 kernelWorkspace);
        populate("Contains", WellKnownRelationship.CONTAINS, kernelWorkspace);
        populate("IsA", WellKnownRelationship.IS_A, kernelWorkspace);
        populate("Includes", WellKnownRelationship.INCLUDES, kernelWorkspace);
        populate("HasException", WellKnownRelationship.HAS_EXCEPTION,
                 kernelWorkspace);
        populate("IsExceptionTo", WellKnownRelationship.IS_EXCEPTION_TO,
                 kernelWorkspace);
        populate("IsLocationOf", WellKnownRelationship.IS_LOCATION_OF,
                 kernelWorkspace);
        populate("MapsToLocation", WellKnownRelationship.MAPS_TO_LOCATION,
                 kernelWorkspace);
        populate("Prototype", WellKnownRelationship.PROTOTYPE, kernelWorkspace);
        populate("PrototypeOf", WellKnownRelationship.PROTOTYPE_OF,
                 kernelWorkspace);
        populate("GreaterThan", WellKnownRelationship.GREATER_THAN,
                 kernelWorkspace);
        populate("LessThan", WellKnownRelationship.LESS_THAN, kernelWorkspace);
        populate("Equals", WellKnownRelationship.EQUALS, kernelWorkspace);
        populate("LessThanOrEqual", WellKnownRelationship.LESS_THAN_OR_EQUAL,
                 kernelWorkspace);
        populate("GreaterThanOrEqual",
                 WellKnownRelationship.GREATER_THAN_OR_EQUAL, kernelWorkspace);
        populate("Developed", WellKnownRelationship.DEVELOPED, kernelWorkspace);
        populate("DevelopedBy", WellKnownRelationship.DEVELOPED_BY,
                 kernelWorkspace);
        populate("VersionOf", WellKnownRelationship.VERSION_OF,
                 kernelWorkspace);
        populate("HasVersion", WellKnownRelationship.HAS_VERSION,
                 kernelWorkspace);
        populate("HasMember", WellKnownRelationship.HAS_MEMBER,
                 kernelWorkspace);
        populate("MemberOf", WellKnownRelationship.MEMBER_OF, kernelWorkspace);
        populate("HeadOf", WellKnownRelationship.HEAD_OF, kernelWorkspace);
        populate("HasHead", WellKnownRelationship.HAS_HEAD, kernelWorkspace);
        populate("HadMember", WellKnownRelationship.HAD_MEMBER,
                 kernelWorkspace);
        populate("FormerMemberOf", WellKnownRelationship.FORMER_MEMBER_OF,
                 kernelWorkspace);
        populate("NotApplicableRelationship",
                 WellKnownRelationship.NOT_APPLICABLE, kernelWorkspace);
        populate("OwnedBy", WellKnownRelationship.OWNED_BY, kernelWorkspace);
        populate("Owns", WellKnownRelationship.OWNS, kernelWorkspace);
        populate("Imports", WellKnownRelationship.IMPORTS, kernelWorkspace);
        populate("ImportedBy", WellKnownRelationship.IMPORTED_BY,
                 kernelWorkspace);
        populate("IsValidatedBy", WellKnownRelationship.IS_VALIDATED_BY,
                 kernelWorkspace);
        populate("Validates", WellKnownRelationship.VALIDATES, kernelWorkspace);
    }

    private void populateStatusCodes(Agency core, Product kernelWorkspace) {
        populate("Unset", WellKnownStatusCode.UNSET, kernelWorkspace);
        populate("AnyStatusCode", WellKnownStatusCode.ANY, kernelWorkspace);
        populate("CopyStatusCode", WellKnownStatusCode.COPY, kernelWorkspace);
        populate("SameStatusCode", WellKnownStatusCode.SAME, kernelWorkspace);
        populate("NotApplicableStatusCode", WellKnownStatusCode.NOT_APPLICABLE,
                 kernelWorkspace);
    }

    private void populateUnits(Agency core, Product kernelWorkspace) {
        populate("UnsetUnit", WellKnownUnit.UNSET, kernelWorkspace);
        populate("AnyUnit", WellKnownUnit.ANY, kernelWorkspace);
        populate("CopyUnit", WellKnownUnit.COPY, kernelWorkspace);
        populate("SameUnit", WellKnownUnit.SAME, kernelWorkspace);
        populate("NotApplicableUnit", WellKnownUnit.NOT_APPLICABLE,
                 kernelWorkspace);
        populate("Nanoseconds", WellKnownUnit.NANOSECONDS, kernelWorkspace);
        populate("Microseconds", WellKnownUnit.MICROSECONDS, kernelWorkspace);
        populate("Minutes", WellKnownUnit.MINUTES, kernelWorkspace);
        populate("Milliseconds", WellKnownUnit.MILLISECONDS, kernelWorkspace);
        populate("Seconds", WellKnownUnit.SECONDS, kernelWorkspace);
        populate("Hours", WellKnownUnit.HOURS, kernelWorkspace);
        populate("Days", WellKnownUnit.DAYS, kernelWorkspace);
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
