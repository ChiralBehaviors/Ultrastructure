/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.meta.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.RollbackException;

import org.junit.Test;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ValueType;
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.entity.EntityAttribute;
import com.hellblazer.CoRE.entity.EntityNetwork;
import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.ServiceSequencingAuthorization;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.event.StatusCodeSequencing;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public class JobModelTest extends AbstractModelTest {

    @Test
    public void testGenerateImplicitJobs() throws Exception {

        @SuppressWarnings("unused")
        JobModel jobModel = model.getJobModel();
        loadJobTestData();
    }

    @Test
    public void testIsTerminalState() throws Exception {
        em.getTransaction().begin();

        JobModel jobModel = model.getJobModel();
        StatusCode startState = new StatusCode("top-level", kernel.getCore());
        em.persist(startState);

        StatusCode state1 = new StatusCode("state-1", kernel.getCore());
        em.persist(state1);

        StatusCode state2 = new StatusCode("state-2", kernel.getCore());
        em.persist(state2);

        StatusCode terminalState = new StatusCode("terminal state",
                                                  kernel.getCore());
        em.persist(terminalState);

        Entity service = new Entity("My Service", kernel.getCore());
        em.persist(service);

        StatusCodeSequencing sequence1 = new StatusCodeSequencing(
                                                                  service,
                                                                  startState,
                                                                  state1,
                                                                  kernel.getCore());
        em.persist(sequence1);

        StatusCodeSequencing sequence2 = new StatusCodeSequencing(
                                                                  service,
                                                                  state1,
                                                                  state2,
                                                                  kernel.getCore());
        em.persist(sequence2);

        StatusCodeSequencing sequence3 = new StatusCodeSequencing(
                                                                  service,
                                                                  state2,
                                                                  terminalState,
                                                                  kernel.getCore());
        em.persist(sequence3);

        em.getTransaction().commit();

        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));
        assertFalse(String.format("%s is a terminal state", startState),
                    jobModel.isTerminalState(startState, service));
        assertFalse(String.format("%s is a terminal state", state1),
                    jobModel.isTerminalState(state1, service));
        assertFalse(String.format("%s is a terminal state", state2),
                    jobModel.isTerminalState(state2, service));

        em.getTransaction().begin();

        StatusCodeSequencing loop = new StatusCodeSequencing(service,
                                                             terminalState,
                                                             state1,
                                                             kernel.getCore());
        em.persist(loop);
        try {
            em.getTransaction().commit();
            fail("Expected failure due to circularity");
        } catch (RollbackException e) {
            // expected
        }

        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));

        em.getTransaction().begin();

        StatusCode loopState = new StatusCode("loop-state", kernel.getCore());
        em.persist(loopState);

        loop = new StatusCodeSequencing(service, state2, loopState,
                                        kernel.getCore());
        loop.setSequenceNumber(2);
        em.persist(loop);

        StatusCodeSequencing terminate = new StatusCodeSequencing(
                                                                  service,
                                                                  loopState,
                                                                  terminalState,
                                                                  kernel.getCore());
        em.persist(terminate);

        StatusCodeSequencing back = new StatusCodeSequencing(service,
                                                             loopState, state1,
                                                             kernel.getCore());
        back.setSequenceNumber(2);
        em.persist(back);
        em.persist(terminate);
        em.getTransaction().commit();
    }

    protected void loadJobTestData() {
        em.getTransaction().begin();
        Resource core = kernel.getCore();

        Relationship sampleType = new Relationship("Sample Type",
                                                   "The sample type of A is B",
                                                   core);
        Relationship sampleTypeOf = new Relationship(
                                                     "Sample Type Of",
                                                     "A is the sample type of B",
                                                     core, sampleType);
        sampleType.setInverse(sampleTypeOf);
        em.persist(sampleType);
        em.persist(sampleTypeOf);

        Relationship dissolvedIn = new Relationship(
                                                    "Dissolved In",
                                                    "Solvent that a sample is dissolved in",
                                                    core);
        Relationship solventOf = new Relationship("Solvent of",
                                                  "A is the solvent of B",
                                                  core, dissolvedIn);
        dissolvedIn.setInverse(solventOf);
        em.persist(dissolvedIn);
        em.persist(solventOf);

        Relationship htfsLibPrepStatus = new Relationship(
                                                          "HTSF Library Preparation Status",
                                                          "Does the HTSF need to prep the sample, or did the customer do it already?",
                                                          core);
        Relationship htfsLibPrepStatusOf = new Relationship(
                                                            "HTSF Library Preparation Status Of",
                                                            "A is the prep status B",
                                                            core,
                                                            htfsLibPrepStatus);
        htfsLibPrepStatus.setInverse(htfsLibPrepStatusOf);
        em.persist(htfsLibPrepStatus);
        em.persist(htfsLibPrepStatusOf);

        Relationship runType = new Relationship("Run Type",
                                                "The Run Type of A is B", core);
        Relationship runTypeOf = new Relationship("Run Type Of",
                                                  "A is the run type B", core,
                                                  runType);
        runType.setInverse(runTypeOf);
        em.persist(runType);
        em.persist(runTypeOf);

        Entity teBuffer = new Entity("TE Buffer", core);
        em.persist(teBuffer);

        Entity htsfSample = new Entity("HTSF Sample", core);
        em.persist(htsfSample);

        Entity unpreparedSample = new Entity(
                                             "Unprepared Sample for HTSF Sequencing",
                                             core);
        em.persist(unpreparedSample);

        Entity sampleX = new Entity("Sample X", core);
        em.persist(sampleX);

        EntityNetwork en1 = new EntityNetwork(sampleX, sampleType, htsfSample,
                                              core);
        em.persist(en1);
        EntityNetwork en2 = new EntityNetwork(sampleX, dissolvedIn, teBuffer,
                                              core);
        em.persist(en2);

        Resource htsfTech = new Resource("HTSF Technician", core);
        em.persist(htsfTech);

        Resource htsf = new Resource("HTSF", core);
        em.persist(htsf);

        StatusCode success = new StatusCode("Success", "Something went right",
                                            core);
        em.persist(success);

        StatusCode failure = new StatusCode("Failure", "Something went wrong",
                                            core);
        em.persist(failure);

        StatusCode active = new StatusCode("Active", "Working on it now", core);
        em.persist(active);

        StatusCode approvedButInactive = new StatusCode(
                                                        "Approved, but Inactive",
                                                        "We going to do it, but we&apos;re not allowed to right now",
                                                        core);
        em.persist(approvedButInactive);

        StatusCode abandoned = new StatusCode(
                                              "Abandoned",
                                              "We were going to do it, something happened in earlier processing that will prevent us.  This can be garbage-collected now",
                                              core);
        em.persist(abandoned);

        Entity resuspend = new Entity("Resuspend in Suitable Buffer",
                                      "Gotta get it in H2O for sequencing",
                                      core);
        em.persist(resuspend);

        Entity htsfIlluminaSequencing = new Entity(
                                                   "HTSF Illumina Sequencing",
                                                   "Illumina sequencing at UNC High-Throughput Sequencing Facility",
                                                   core);
        em.persist(htsfIlluminaSequencing);

        Entity chipSeq = new Entity("Chip-Seq", core);
        em.persist(chipSeq);

        Entity doChipSeqPrep = new Entity("Do Chip-Seq Preparation", core);
        em.persist(doChipSeqPrep);

        Entity customPrimerAnalysis = new Entity("Custom Primer Analysis", core);
        em.persist(customPrimerAnalysis);

        Entity preparePrimers = new Entity("Prepare Primers", core);
        em.persist(preparePrimers);

        Entity dge = new Entity("DGE", core);
        em.persist(dge);

        Entity doDgePrep = new Entity("Do DGE Preparation", core);
        em.persist(doDgePrep);

        Entity miRNA = new Entity("miRNA", core);
        em.persist(miRNA);

        Entity doMiRnaPrep = new Entity("Do miRNA Analysis Preparation", core);
        em.persist(doMiRnaPrep);

        Entity pairedEndAnalysis = new Entity("Paired-End Analysis", core);
        em.persist(pairedEndAnalysis);

        Entity doPairedEndAnalysisPrep = new Entity(
                                                    "Do Paired-End Analysis Preparation",
                                                    core);
        em.persist(doPairedEndAnalysisPrep);

        Attribute a1 = new Attribute(
                                     "Number of Lanes",
                                     "How many Illumina lanes do you want to run?",
                                     core);
        a1.setValueType(ValueType.INTEGER);
        em.persist(a1);

        Attribute a2 = new Attribute(
                                     "Number of Cycles",
                                     "How many Illumina cycles do you want to run?",
                                     core);
        a2.setValueType(ValueType.INTEGER);
        em.persist(a2);

        EntityAttribute ea1 = new EntityAttribute(a2, 3, core);
        ea1.setEntity(htsfIlluminaSequencing);
        em.persist(ea1);

        EntityAttribute ea2 = new EntityAttribute(a1, 36, core);
        ea2.setEntity(htsfIlluminaSequencing);
        em.persist(ea2);

        Entity libraryPrep = new Entity("Library Preparation", core);
        em.persist(libraryPrep);

        Entity clusterGen = new Entity("Cluster Generation", core);
        em.persist(clusterGen);

        Entity sequenceClusters = new Entity("Sequence Clusters", core);
        em.persist(sequenceClusters);

        Entity dataAnalysis = new Entity("Data Analysis", core);
        em.persist(dataAnalysis);

        Entity dnaSequencing = new Entity("DNA Sequencing", core);
        em.persist(dnaSequencing);

        Entity figureOutWhyCLusterSeqFailed = new Entity(
                                                         "Figure Out Why Cluster Sequencing Failed",
                                                         core);
        em.persist(figureOutWhyCLusterSeqFailed);

        StatusCodeSequencing seq1 = new StatusCodeSequencing(
                                                             htsfIlluminaSequencing,
                                                             active, success,
                                                             1, core);
        em.persist(seq1);

        StatusCodeSequencing seq2 = new StatusCodeSequencing(
                                                             htsfIlluminaSequencing,
                                                             active, failure,
                                                             2, core);
        em.persist(seq2);

        StatusCodeSequencing seq3 = new StatusCodeSequencing(libraryPrep,
                                                             active, success,
                                                             1, core);
        em.persist(seq3);

        StatusCodeSequencing seq4 = new StatusCodeSequencing(libraryPrep,
                                                             active, failure,
                                                             2, core);
        em.persist(seq4);

        StatusCodeSequencing seq5 = new StatusCodeSequencing(doChipSeqPrep,
                                                             active, success,
                                                             1, core);
        em.persist(seq5);

        StatusCodeSequencing seq6 = new StatusCodeSequencing(doChipSeqPrep,
                                                             active, failure,
                                                             2, core);
        em.persist(seq6);

        StatusCodeSequencing seq7 = new StatusCodeSequencing(resuspend, active,
                                                             success, 1, core);
        em.persist(seq7);

        StatusCodeSequencing seq8 = new StatusCodeSequencing(resuspend, active,
                                                             failure, 2, core);
        em.persist(seq8);

        StatusCodeSequencing seq9 = new StatusCodeSequencing(preparePrimers,
                                                             active, success,
                                                             1, core);
        em.persist(seq9);

        StatusCodeSequencing seq10 = new StatusCodeSequencing(preparePrimers,
                                                              active, failure,
                                                              2, core);
        em.persist(seq10);

        StatusCodeSequencing seq11 = new StatusCodeSequencing(doDgePrep,
                                                              active, success,
                                                              1, core);
        em.persist(seq11);

        StatusCodeSequencing seq12 = new StatusCodeSequencing(doDgePrep,
                                                              active, failure,
                                                              2, core);
        em.persist(seq12);

        StatusCodeSequencing seq13 = new StatusCodeSequencing(doMiRnaPrep,
                                                              active, success,
                                                              1, core);
        em.persist(seq13);

        StatusCodeSequencing seq14 = new StatusCodeSequencing(doMiRnaPrep,
                                                              active, failure,
                                                              2, core);
        em.persist(seq14);

        StatusCodeSequencing seq15 = new StatusCodeSequencing(
                                                              doPairedEndAnalysisPrep,
                                                              active, success,
                                                              1, core);
        em.persist(seq15);

        StatusCodeSequencing seq16 = new StatusCodeSequencing(
                                                              doPairedEndAnalysisPrep,
                                                              active, failure,
                                                              2, core);
        em.persist(seq16);

        StatusCodeSequencing seq17 = new StatusCodeSequencing(clusterGen,
                                                              active, success,
                                                              1, core);

        em.persist(seq17);

        StatusCodeSequencing seq18 = new StatusCodeSequencing(clusterGen,
                                                              active, failure,
                                                              2, core);
        em.persist(seq18);

        StatusCodeSequencing seq19 = new StatusCodeSequencing(sequenceClusters,
                                                              active, success,
                                                              1, core);
        em.persist(seq19);

        StatusCodeSequencing seq20 = new StatusCodeSequencing(sequenceClusters,
                                                              active, failure,
                                                              2, core);
        em.persist(seq20);

        StatusCodeSequencing seq21 = new StatusCodeSequencing(dataAnalysis,
                                                              active, success,
                                                              1, core);
        em.persist(seq21);

        StatusCodeSequencing seq22 = new StatusCodeSequencing(dataAnalysis,
                                                              active, failure,
                                                              2, core);
        em.persist(seq22);

        StatusCodeSequencing seq23 = new StatusCodeSequencing(chipSeq, active,
                                                              success, 1, core);
        em.persist(seq23);

        StatusCodeSequencing seq24 = new StatusCodeSequencing(chipSeq, active,
                                                              failure, 2, core);
        em.persist(seq24);

        StatusCodeSequencing seq25 = new StatusCodeSequencing(
                                                              customPrimerAnalysis,
                                                              active, success,
                                                              1, core);
        em.persist(seq25);

        StatusCodeSequencing seq26 = new StatusCodeSequencing(
                                                              customPrimerAnalysis,
                                                              active, failure,
                                                              2, core);
        em.persist(seq26);

        StatusCodeSequencing seq27 = new StatusCodeSequencing(dge, active,
                                                              success, 1, core);
        em.persist(seq27);

        StatusCodeSequencing seq28 = new StatusCodeSequencing(dge, active,
                                                              failure, 2, core);
        em.persist(seq28);

        StatusCodeSequencing seq29 = new StatusCodeSequencing(miRNA, active,
                                                              success, 1, core);
        em.persist(seq29);

        StatusCodeSequencing seq30 = new StatusCodeSequencing(miRNA, active,
                                                              failure, 2, core);
        em.persist(seq30);

        StatusCodeSequencing seq31 = new StatusCodeSequencing(
                                                              pairedEndAnalysis,
                                                              active, success,
                                                              1, core);
        em.persist(seq31);

        StatusCodeSequencing seq32 = new StatusCodeSequencing(
                                                              pairedEndAnalysis,
                                                              active, failure,
                                                              2, core);
        em.persist(seq32);

        StatusCodeSequencing seq33 = new StatusCodeSequencing(
                                                              figureOutWhyCLusterSeqFailed,
                                                              active, success,
                                                              1, core);
        em.persist(seq33);

        StatusCodeSequencing seq34 = new StatusCodeSequencing(
                                                              figureOutWhyCLusterSeqFailed,
                                                              active, failure,
                                                              2, core);
        em.persist(seq34);

        ServiceSequencingAuthorization esa1 = new ServiceSequencingAuthorization(
                                                                             core);
        esa1.setParent(resuspend);
        esa1.setStatusCode(success);
        esa1.setNextSibling(libraryPrep);
        esa1.setNextSiblingStatus(active);
        em.persist(esa1);

        ServiceSequencingAuthorization esa2 = new ServiceSequencingAuthorization(
                                                                             core);
        esa2.setParent(libraryPrep);
        esa2.setStatusCode(success);
        esa2.setSequenceNumber(1);
        esa2.setNextSibling(doChipSeqPrep);
        esa2.setNextSiblingStatus(active);
        em.persist(esa2);

        ServiceSequencingAuthorization esa3 = new ServiceSequencingAuthorization(
                                                                             core);
        esa3.setParent(libraryPrep);
        esa3.setStatusCode(success);
        esa3.setSequenceNumber(2);
        esa3.setNextSibling(preparePrimers);
        esa3.setNextSiblingStatus(active);
        em.persist(esa3);

        ServiceSequencingAuthorization esa4 = new ServiceSequencingAuthorization(
                                                                             core);
        esa4.setParent(libraryPrep);
        esa4.setStatusCode(success);
        esa4.setSequenceNumber(3);
        esa4.setNextSibling(doDgePrep);
        esa4.setNextSiblingStatus(active);
        em.persist(esa4);

        ServiceSequencingAuthorization esa5 = new ServiceSequencingAuthorization(
                                                                             core);
        esa5.setParent(libraryPrep);
        esa5.setStatusCode(success);
        esa5.setSequenceNumber(4);
        esa5.setNextSibling(doMiRnaPrep);
        esa5.setNextSiblingStatus(active);
        em.persist(esa5);

        ServiceSequencingAuthorization esa6 = new ServiceSequencingAuthorization(
                                                                             core);
        esa6.setParent(libraryPrep);
        esa6.setStatusCode(success);
        esa6.setSequenceNumber(5);
        esa6.setNextSibling(doPairedEndAnalysisPrep);
        esa6.setNextSiblingStatus(active);
        em.persist(esa6);

        ServiceSequencingAuthorization esa7 = new ServiceSequencingAuthorization(
                                                                             core);
        esa7.setParent(doChipSeqPrep);
        esa7.setStatusCode(success);
        esa7.setNextSibling(clusterGen);
        esa7.setNextSiblingStatus(active);
        em.persist(esa7);

        ServiceSequencingAuthorization esa8 = new ServiceSequencingAuthorization(
                                                                             core);
        esa8.setParent(preparePrimers);
        esa8.setStatusCode(success);
        esa8.setNextSibling(clusterGen);
        esa8.setNextSiblingStatus(active);
        em.persist(esa8);

        ServiceSequencingAuthorization esa9 = new ServiceSequencingAuthorization(
                                                                             core);
        esa9.setParent(doDgePrep);
        esa9.setStatusCode(success);
        esa9.setNextSibling(clusterGen);
        esa9.setNextSiblingStatus(active);
        em.persist(esa9);

        ServiceSequencingAuthorization esa10 = new ServiceSequencingAuthorization(
                                                                              core);
        esa10.setParent(doMiRnaPrep);
        esa10.setStatusCode(success);
        esa10.setNextSibling(clusterGen);
        esa10.setNextSiblingStatus(active);
        em.persist(esa10);

        ServiceSequencingAuthorization esa11 = new ServiceSequencingAuthorization(
                                                                              core);
        esa11.setParent(doPairedEndAnalysisPrep);
        esa11.setStatusCode(success);
        esa11.setNextSibling(clusterGen);
        esa11.setNextSiblingStatus(active);
        em.persist(esa11);

        ServiceSequencingAuthorization esa12 = new ServiceSequencingAuthorization(
                                                                              core);
        esa12.setParent(clusterGen);
        esa12.setStatusCode(success);
        esa12.setNextSibling(sequenceClusters);
        esa12.setNextSiblingStatus(active);
        em.persist(esa12);

        ServiceSequencingAuthorization esa13 = new ServiceSequencingAuthorization(
                                                                              core);
        esa13.setParent(sequenceClusters);
        esa13.setStatusCode(success);
        esa13.setNextSibling(dataAnalysis);
        esa13.setNextSiblingStatus(active);
        em.persist(esa13);

        ServiceSequencingAuthorization esa14 = new ServiceSequencingAuthorization(
                                                                              core);
        esa14.setParent(sequenceClusters);
        esa14.setStatusCode(failure);
        esa14.setNextSibling(figureOutWhyCLusterSeqFailed);
        esa14.setNextSiblingStatus(active);
        em.persist(esa14);

        Protocol p1 = new Protocol(core);
        p1.setRequester(kernel.getAnyResource());
        p1.setService(resuspend);
        p1.setMaterial(htsfSample);
        p1.setRequestedService(htsfIlluminaSequencing);
        p1.setProductOrdered(teBuffer);
        p1.setSubService(kernel.getAnyEntity());
        p1.setProduct(kernel.getAnyEntity());
        p1.setDeliverFrom(kernel.getAnyLocation());
        p1.setDeliverTo(kernel.getAnyLocation());
        p1.setAssignTo(htsfTech);
        em.persist(p1);

        Protocol p2 = new Protocol(core);
        p2.setRequester(kernel.getAnyResource());
        p2.setService(libraryPrep);
        p2.setMaterial(htsfSample);
        p2.setRequestedService(htsfIlluminaSequencing);
        p2.setProductOrdered(unpreparedSample);
        p2.setSubService(kernel.getAnyEntity());
        p2.setProduct(kernel.getAnyEntity());
        p2.setDeliverFrom(kernel.getAnyLocation());
        p2.setDeliverTo(kernel.getAnyLocation());
        p2.setAssignTo(htsfTech);
        em.persist(p2);

        Protocol p3 = new Protocol(core);
        p3.setRequester(kernel.getAnyResource());
        p3.setService(doChipSeqPrep);
        p3.setMaterial(htsfSample);
        p3.setRequestedService(htsfIlluminaSequencing);
        p3.setProductOrdered(kernel.getAnyEntity());
        p3.setProduct(kernel.getAnyEntity());
        p3.setDeliverFrom(kernel.getAnyLocation());
        p3.setDeliverTo(kernel.getAnyLocation());
        p3.setSubService(chipSeq);
        p3.setAssignTo(htsfTech);
        em.persist(p3);

        Protocol p4 = new Protocol(core);
        p4.setRequester(kernel.getAnyResource());
        p4.setService(customPrimerAnalysis);
        p4.setMaterial(htsfSample);
        p4.setRequestedService(htsfIlluminaSequencing);
        p4.setProductOrdered(kernel.getAnyEntity());
        p4.setProduct(kernel.getAnyEntity());
        p4.setDeliverFrom(kernel.getAnyLocation());
        p4.setDeliverTo(kernel.getAnyLocation());
        p4.setAssignTo(htsfTech);
        p4.setSubService(preparePrimers);
        em.persist(p4);

        Protocol p5 = new Protocol(core);
        p5.setRequester(kernel.getAnyResource());
        p5.setService(doDgePrep);
        p5.setMaterial(htsfSample);
        p5.setRequestedService(htsfIlluminaSequencing);
        p5.setProductOrdered(kernel.getAnyEntity());
        p5.setProduct(kernel.getAnyEntity());
        p5.setDeliverFrom(kernel.getAnyLocation());
        p5.setDeliverTo(kernel.getAnyLocation());
        p5.setAssignTo(htsfTech);
        p5.setSubService(dge);
        em.persist(p5);

        Protocol p6 = new Protocol(core);
        p6.setRequester(kernel.getAnyResource());
        p6.setService(doMiRnaPrep);
        p6.setMaterial(htsfSample);
        p6.setRequestedService(htsfIlluminaSequencing);
        p6.setProductOrdered(kernel.getAnyEntity());
        p6.setProduct(kernel.getAnyEntity());
        p6.setDeliverFrom(kernel.getAnyLocation());
        p6.setDeliverTo(kernel.getAnyLocation());
        p6.setAssignTo(htsfTech);
        p6.setSubService(miRNA);
        em.persist(p6);

        Protocol p7 = new Protocol(core);
        p7.setRequester(kernel.getAnyResource());
        p7.setService(doPairedEndAnalysisPrep);
        p7.setMaterial(htsfSample);
        p7.setRequestedService(htsfIlluminaSequencing);
        p7.setProductOrdered(kernel.getAnyEntity());
        p7.setProduct(kernel.getAnyEntity());
        p7.setDeliverFrom(kernel.getAnyLocation());
        p7.setDeliverTo(kernel.getAnyLocation());
        p7.setAssignTo(htsfTech);
        p7.setSubService(pairedEndAnalysis);
        em.persist(p7);

        Protocol p8 = new Protocol(core);
        p8.setRequester(kernel.getAnyResource());
        p8.setService(clusterGen);
        p8.setMaterial(htsfSample);
        p8.setRequestedService(htsfIlluminaSequencing);
        p8.setProductOrdered(kernel.getAnyEntity());
        p8.setProduct(kernel.getAnyEntity());
        p8.setDeliverFrom(kernel.getAnyLocation());
        p8.setDeliverTo(kernel.getAnyLocation());
        p8.setAssignTo(htsfTech);
        p8.setSubService(kernel.getAnyEntity());
        em.persist(p8);

        Protocol p9 = new Protocol(core);
        p9.setRequester(kernel.getAnyResource());
        p9.setSequenceNumber(2);
        p9.setService(sequenceClusters);
        p9.setMaterial(htsfSample);
        p9.setRequestedService(htsfIlluminaSequencing);
        p9.setProductOrdered(kernel.getAnyEntity());
        p9.setProduct(kernel.getAnyEntity());
        p9.setDeliverFrom(kernel.getAnyLocation());
        p9.setDeliverTo(kernel.getAnyLocation());
        p9.setAssignTo(htsfTech);
        p9.setSubService(kernel.getAnyEntity());
        em.persist(p9);

        Protocol p10 = new Protocol(core);
        p10.setRequester(kernel.getAnyResource());
        p10.setSequenceNumber(3);
        p10.setService(dataAnalysis);
        p10.setMaterial(htsfSample);
        p10.setRequestedService(htsfIlluminaSequencing);
        p10.setProductOrdered(kernel.getAnyEntity());
        p10.setProduct(kernel.getAnyEntity());
        p10.setDeliverFrom(kernel.getAnyLocation());
        p10.setDeliverTo(kernel.getAnyLocation());
        p10.setAssignTo(htsfTech);
        p10.setSubService(kernel.getAnyEntity());
        em.persist(p9);

        MetaProtocol mp1 = new MetaProtocol(core);
        mp1.setService(htsfIlluminaSequencing);
        mp1.setSequenceNumber(1);
        mp1.setServiceType(sampleType);
        mp1.setProductOrdered(dissolvedIn);
        em.persist(mp1);

        MetaProtocol mp2 = new MetaProtocol(core);
        mp2.setService(htsfIlluminaSequencing);
        mp2.setSequenceNumber(2);
        mp2.setServiceType(sampleType);
        mp2.setProductOrdered(htfsLibPrepStatus);
        em.persist(mp2);

        MetaProtocol mp3 = new MetaProtocol(core);
        mp3.setService(htsfIlluminaSequencing);
        mp3.setSequenceNumber(3);
        mp3.setServiceType(sampleType);
        mp3.setProductOrdered(kernel.getAnyRelationship());
        em.persist(mp3);

        MetaProtocol mp4 = new MetaProtocol(core);
        mp4.setService(htsfIlluminaSequencing);
        mp4.setSequenceNumber(4);
        mp4.setServiceType(sampleType);
        mp4.setProductOrdered(kernel.getAnyRelationship());
        em.persist(mp4);

        Job j1 = new Job(core);
        j1.setRequester(kernel.getAnyResource());
        j1.setService(htsfIlluminaSequencing);
        j1.setProduct(kernel.getAnyEntity());
        j1.setDeliverFrom(kernel.getAnyLocation());
        j1.setDeliverTo(kernel.getAnyLocation());
        j1.setMaterial(sampleX);
        j1.setAssignTo(htsfTech);
        j1.setStatus(active);
        em.persist(j1);

        Job j2 = new Job(core);
        j2.setRequester(kernel.getAnyResource());
        j2.setProduct(kernel.getAnyEntity());
        j2.setParent(j1);
        j2.setDeliverFrom(kernel.getAnyLocation());
        j2.setDeliverTo(kernel.getAnyLocation());
        j2.setService(resuspend);
        j2.setMaterial(sampleX);
        j2.setAssignTo(htsfTech);
        j2.setStatus(active);
        em.persist(j2);

        Job j3 = new Job(core);
        j3.setRequester(kernel.getAnyResource());
        j3.setProduct(kernel.getAnyEntity());
        j3.setDeliverFrom(kernel.getAnyLocation());
        j3.setDeliverTo(kernel.getAnyLocation());
        j3.setParent(j1);
        j3.setService(libraryPrep);
        j3.setMaterial(sampleX);
        j3.setAssignTo(htsfTech);
        j3.setStatus(kernel.getUnset());
        em.persist(j3);

        Job j4 = new Job(core);
        j4.setRequester(kernel.getAnyResource());
        j4.setProduct(kernel.getAnyEntity());
        j4.setDeliverFrom(kernel.getAnyLocation());
        j4.setDeliverTo(kernel.getAnyLocation());
        j4.setParent(j1);
        j4.setService(dge);
        j4.setMaterial(sampleX);
        j4.setAssignTo(htsfTech);
        j4.setStatus(kernel.getUnset());
        em.persist(j4);

        Job j5 = new Job(core);
        j5.setRequester(kernel.getAnyResource());
        j5.setProduct(kernel.getAnyEntity());
        j5.setDeliverFrom(kernel.getAnyLocation());
        j5.setDeliverTo(kernel.getAnyLocation());
        j5.setParent(j1);
        j5.setService(clusterGen);
        j5.setMaterial(sampleX);
        j5.setAssignTo(htsfTech);
        j5.setStatus(kernel.getUnset());
        em.persist(j5);

        Job j6 = new Job(core);
        j6.setRequester(kernel.getAnyResource());
        j6.setProduct(kernel.getAnyEntity());
        j6.setDeliverFrom(kernel.getAnyLocation());
        j6.setDeliverTo(kernel.getAnyLocation());
        j6.setParent(j1);
        j6.setService(sequenceClusters);
        j6.setMaterial(sampleX);
        j6.setAssignTo(htsfTech);
        j6.setStatus(kernel.getUnset());
        em.persist(j6);

        Job j7 = new Job(core);
        j7.setRequester(kernel.getAnyResource());
        j7.setProduct(kernel.getAnyEntity());
        j7.setDeliverFrom(kernel.getAnyLocation());
        j7.setDeliverTo(kernel.getAnyLocation());
        j7.setParent(j1);
        j7.setService(dataAnalysis);
        j7.setMaterial(sampleX);
        j7.setAssignTo(htsfTech);
        j7.setStatus(kernel.getUnset());
        em.persist(j7);

        em.getTransaction().commit();

    }
}
