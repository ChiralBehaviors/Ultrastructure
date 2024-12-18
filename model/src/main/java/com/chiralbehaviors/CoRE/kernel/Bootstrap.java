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

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownInterval;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownLocation;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownUnit;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceLabelRecord;

/**
 * @author hhildebrand
 *
 */
abstract public class Bootstrap {

    public static void boostrap(String outputFile,
                                Bootstrap bootstrap) throws SQLException,
                                                     IOException {
        RecordsFactory.clear(bootstrap.create);
        bootstrap.bootstrap();
        bootstrap.serialize(outputFile);
    }

    protected final DSLContext   create;
    private final RecordsFactory records;

    public Bootstrap(DSLContext create) throws SQLException {
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
        constructKernelWorkspace();
    }

    protected Product constructKernelWorkspace() throws IOException,
                                                 SQLException {
        Agency core = records.resolve(WellKnownAgency.CORE.id());
        Product kernelWorkspace = find(WellKnownProduct.KERNEL_WORKSPACE);

        // Ain 
        populateAgencies(core, kernelWorkspace);
        populateIntervals(core, kernelWorkspace);
        populateLocations(core, kernelWorkspace);
        populateProducts(core, kernelWorkspace);
        populateRelationships(core, kernelWorkspace);
        populateStatusCodes(core, kernelWorkspace);
        populateUnits(core, kernelWorkspace);
        populateAnyFacets(core, kernelWorkspace);
        return kernelWorkspace;
    }

    /**
     *
     * @param wko
     * @return the {@link Product} corresponding to the well known object
     */
    protected Product find(WellKnownProduct wko) {
        return records.resolve(wko.id());
    }

    private void insert(WellKnownObject wko) throws SQLException {
        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setId(wko.id());
        record.setName(wko.wkoName());
        record.setDomain(wko.domain());
        record.setDescription(wko.description());
        record.setUpdatedBy(WellKnownAgency.CORE.id());
        record.insert();
    }

    private void insert(WellKnownRelationship wko) throws SQLException {

        ExistentialRecord record = create.newRecord(EXISTENTIAL);
        record.setId(wko.id());
        record.setName(wko.wkoName());
        record.setDescription(wko.description());
        record.setDomain(wko.domain());
        record.setUpdatedBy(WellKnownAgency.CORE.id());
        record.setInverse(wko.inverse()
                             .id());
        record.insert();
    }

    private void populate(FacetRecord auth, Product kernelWorkspace) {
        WorkspaceLabelRecord label = records.newWorkspaceLabel(auth.getName(),
                                                               auth.getId(),
                                                               ReferenceType.Facet,
                                                               kernelWorkspace);
//        auth.insert();
        label.insert();
    }

    private void populate(String key, WellKnownObject wko,
                          Product kernelWorkspace) {
        ExistentialRecord existential = create.selectFrom(EXISTENTIAL)
                                              .where(EXISTENTIAL.ID.equal(wko.id()))
                                              .fetchOne();
//        existential.insert();
        WorkspaceLabelRecord auth = records.newWorkspaceLabel(key, wko.id(),
                                                              ReferenceType.Existential,
                                                              kernelWorkspace);
        auth.insert();
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
        anyAgency.insert();
        populate(anyAgency, kernelWorkspace);

        FacetRecord anyInterval = records.newFacet();
        anyInterval.setClassifier(WellKnownRelationship.ANY.id());
        anyInterval.setClassification(WellKnownInterval.ANY.id());
        anyInterval.setName("AnyInterval");
        anyInterval.insert();
        populate(anyInterval, kernelWorkspace);

        FacetRecord anyLocation = records.newFacet();
        anyLocation.setClassifier(WellKnownRelationship.ANY.id());
        anyLocation.setClassification(WellKnownLocation.ANY.id());
        anyLocation.setName("AnyLocation");
        anyLocation.insert();
        populate(anyLocation, kernelWorkspace);

        FacetRecord anyProduct = records.newFacet();
        anyProduct.setClassifier(WellKnownRelationship.ANY.id());
        anyProduct.setClassification(WellKnownProduct.ANY.id());
        anyProduct.setName("AnyProduct");
        anyProduct.insert();
        populate(anyProduct, kernelWorkspace);

        FacetRecord anyRelationship = records.newFacet();
        anyRelationship.setClassifier(WellKnownRelationship.ANY.id());
        anyRelationship.setClassification(WellKnownRelationship.ANY.id());
        anyRelationship.setName("AnyRelationship");
        anyRelationship.insert();
        populate(anyRelationship, kernelWorkspace);

        FacetRecord anyStatusCode = records.newFacet();
        anyStatusCode.setClassifier(WellKnownRelationship.ANY.id());
        anyStatusCode.setClassification(WellKnownStatusCode.ANY.id());
        anyStatusCode.setName("AnyStatusCode");
        anyStatusCode.insert();
        populate(anyStatusCode, kernelWorkspace);

        FacetRecord anyUnit = records.newFacet();
        anyUnit.setClassifier(WellKnownRelationship.ANY.id());
        anyUnit.setClassification(WellKnownUnit.ANY.id());
        anyUnit.setName("AnyUnit");
        anyUnit.insert();
        populate(anyUnit, kernelWorkspace);
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

    abstract protected void serialize(String fileName) throws IOException;
}
