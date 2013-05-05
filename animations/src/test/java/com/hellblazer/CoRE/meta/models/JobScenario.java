package com.hellblazer.CoRE.meta.models;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ValueType;
import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.ProductSequencingAuthorization;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.event.StatusCodeSequencing;
import com.hellblazer.CoRE.meta.Kernel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductAttribute;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.resource.Resource;

public class JobScenario {

    public Attribute                      a1;
    public Attribute                      a2;
    public StatusCode                     abandoned;
    public StatusCode                     active;
    public StatusCode                     approvedButInactive;
    public Product                        chipSeq;
    public Product                        clusterGen;
    public Resource                       core;
    public Product                        customPrimerAnalysis;
    public Product                        dataAnalysis;
    public Product                        dge;
    public Relationship                   dissolvedIn;
    public Product                        dnaSequencing;
    public Product                        doChipSeqPrep;
    public Product                        doDgePrep;
    public Product                        doMiRnaPrep;
    public Product                        doPairedEndAnalysisPrep;
    public StatusCode                     failure;
    public Product                        figureOutWhyCLusterSeqFailed;
    public Relationship                   htfsLibPrepStatus;
    public Relationship                   htfsLibPrepStatusOf;
    public Resource                       htsf;
    public Product                        htsfIlluminaSequencing;
    public Product                        htsfSample;
    public Resource                       htsfTech;
    public Job                            j1;
    public Job                            j2;
    public Job                            j3;
    public Job                            j4;
    public Job                            j5;
    public Job                            j6;
    public Job                            j7;
    public Product                        libraryPrep;
    public Product                        miRNA;
    public MetaProtocol                   mp1;
    public MetaProtocol                   mp2;
    public MetaProtocol                   mp3;
    public MetaProtocol                   mp4;
    public Protocol                       p01;
    public Protocol                       p02;
    public Protocol                       p03;
    public Protocol                       p04;
    public Protocol                       p05;
    public Protocol                       p06;
    public Protocol                       p07;
    public Protocol                       p08;
    public Protocol                       p10;
    public ProductAttribute               pa1;
    public ProductAttribute               pa2;
    public Product                        pairedEndAnalysis;
    public ProductNetwork                 pn1;
    public ProductNetwork                 pn2;
    public Product                        preparePrimers;
    public ProductSequencingAuthorization psa02;
    public ProductSequencingAuthorization psa03;
    public ProductSequencingAuthorization psa04;
    public ProductSequencingAuthorization psa05;
    public ProductSequencingAuthorization psa06;
    public ProductSequencingAuthorization psa07;
    public ProductSequencingAuthorization psa08;
    public ProductSequencingAuthorization psa09;
    public ProductSequencingAuthorization psa1;
    public ProductSequencingAuthorization psa10;
    public ProductSequencingAuthorization psa11;
    public ProductSequencingAuthorization psa12;
    public ProductSequencingAuthorization psa13;
    public ProductSequencingAuthorization psa14;
    public Product                        resuspend;
    public Relationship                   runType;
    public Relationship                   runTypeOf;
    public Relationship                   sampleType;
    public Relationship                   sampleTypeOf;
    public Product                        sampleX;
    public StatusCodeSequencing           seq01;
    public StatusCodeSequencing           seq02;
    public StatusCodeSequencing           seq03;
    public StatusCodeSequencing           seq04;
    public StatusCodeSequencing           seq05;
    public StatusCodeSequencing           seq06;
    public StatusCodeSequencing           seq07;
    public StatusCodeSequencing           seq08;
    public StatusCodeSequencing           seq09;
    public StatusCodeSequencing           seq10;
    public StatusCodeSequencing           seq11;
    public StatusCodeSequencing           seq12;
    public StatusCodeSequencing           seq13;
    public StatusCodeSequencing           seq14;
    public StatusCodeSequencing           seq15;
    public StatusCodeSequencing           seq16;
    public StatusCodeSequencing           seq17;
    public StatusCodeSequencing           seq18;
    public StatusCodeSequencing           seq19;
    public StatusCodeSequencing           seq20;
    public StatusCodeSequencing           seq21;
    public StatusCodeSequencing           seq22;
    public StatusCodeSequencing           seq23;
    public StatusCodeSequencing           seq24;
    public StatusCodeSequencing           seq25;
    public StatusCodeSequencing           seq26;
    public StatusCodeSequencing           seq27;
    public StatusCodeSequencing           seq28;
    public StatusCodeSequencing           seq29;
    public StatusCodeSequencing           seq30;
    public StatusCodeSequencing           seq31;
    public StatusCodeSequencing           seq32;
    public StatusCodeSequencing           seq33;
    public StatusCodeSequencing           seq34;
    public Product                        sequenceClusters;
    public Relationship                   solventOf;
    public StatusCode                     success;
    public Product                        teBuffer;
    public Product                        unpreparedSample;
    private final EntityManager           em;
    private final Kernel                  kernel;

    public JobScenario(Model model) {
        this.kernel = model.getKernel();
        this.em = model.getEntityManager();
        core = kernel.getCore();
        loadJobTestData();
    }

    private void constructAttributes() {
        a1 = new Attribute("Number of Lanes",
                           "How many Illumina lanes do you want to run?", core);
        a1.setValueType(ValueType.INTEGER);
        em.persist(a1);

        a2 = new Attribute("Number of Cycles",
                           "How many Illumina cycles do you want to run?", core);
        a2.setValueType(ValueType.INTEGER);
        em.persist(a2);
    }

    private void constructJobs() {
        j1 = new Job(core);
        j1.setRequester(kernel.getAnyResource());
        j1.setService(htsfIlluminaSequencing);
        j1.setProduct(kernel.getAnyProduct());
        j1.setDeliverFrom(kernel.getAnyLocation());
        j1.setDeliverTo(kernel.getAnyLocation());
        j1.setMaterial(sampleX);
        j1.setAssignTo(htsfTech);
        j1.setStatus(active);
        em.persist(j1);

        j2 = new Job(core);
        j2.setRequester(kernel.getAnyResource());
        j2.setService(resuspend);
        j2.setProduct(kernel.getAnyProduct());
        j2.setParent(j1);
        j2.setDeliverFrom(kernel.getAnyLocation());
        j2.setDeliverTo(kernel.getAnyLocation());
        j2.setMaterial(sampleX);
        j2.setAssignTo(htsfTech);
        j2.setStatus(active);
        em.persist(j2);

        j3 = new Job(core);
        j3.setRequester(kernel.getAnyResource());
        j3.setService(libraryPrep);
        j3.setProduct(kernel.getAnyProduct());
        j3.setDeliverFrom(kernel.getAnyLocation());
        j3.setDeliverTo(kernel.getAnyLocation());
        j3.setParent(j1);
        j3.setMaterial(sampleX);
        j3.setAssignTo(htsfTech);
        j3.setStatus(kernel.getUnset());
        em.persist(j3);

        j4 = new Job(core);
        j4.setRequester(kernel.getAnyResource());
        j4.setService(dge);
        j4.setProduct(kernel.getAnyProduct());
        j4.setDeliverFrom(kernel.getAnyLocation());
        j4.setDeliverTo(kernel.getAnyLocation());
        j4.setParent(j1);
        j4.setMaterial(sampleX);
        j4.setAssignTo(htsfTech);
        j4.setStatus(kernel.getUnset());
        em.persist(j4);

        j5 = new Job(core);
        j5.setRequester(kernel.getAnyResource());
        j5.setService(clusterGen);
        j5.setProduct(kernel.getAnyProduct());
        j5.setDeliverFrom(kernel.getAnyLocation());
        j5.setDeliverTo(kernel.getAnyLocation());
        j5.setParent(j1);
        j5.setMaterial(sampleX);
        j5.setAssignTo(htsfTech);
        j5.setStatus(kernel.getUnset());
        em.persist(j5);

        j6 = new Job(core);
        j6.setRequester(kernel.getAnyResource());
        j6.setService(sequenceClusters);
        j6.setProduct(kernel.getAnyProduct());
        j6.setDeliverFrom(kernel.getAnyLocation());
        j6.setDeliverTo(kernel.getAnyLocation());
        j6.setParent(j1);
        j6.setMaterial(sampleX);
        j6.setAssignTo(htsfTech);
        j6.setStatus(kernel.getUnset());
        em.persist(j6);

        j7 = new Job(core);
        j7.setRequester(kernel.getAnyResource());
        j7.setService(dataAnalysis);
        j7.setProduct(kernel.getAnyProduct());
        j7.setDeliverFrom(kernel.getAnyLocation());
        j7.setDeliverTo(kernel.getAnyLocation());
        j7.setParent(j1);
        j7.setMaterial(sampleX);
        j7.setAssignTo(htsfTech);
        j7.setStatus(kernel.getUnset());
        em.persist(j7);
    }

    private void constructMetaProtocols() {
        mp1 = new MetaProtocol(core);
        mp1.setService(htsfIlluminaSequencing);
        mp1.setSequenceNumber(1);
        mp1.setServiceType(sampleType);
        mp1.setProductOrdered(dissolvedIn);
        em.persist(mp1);

        mp2 = new MetaProtocol(core);
        mp2.setService(htsfIlluminaSequencing);
        mp2.setSequenceNumber(2);
        mp2.setServiceType(sampleType);
        mp2.setProductOrdered(htfsLibPrepStatus);
        em.persist(mp2);

        mp3 = new MetaProtocol(core);
        mp3.setService(htsfIlluminaSequencing);
        mp3.setSequenceNumber(3);
        mp3.setServiceType(sampleType);
        mp3.setProductOrdered(kernel.getAnyRelationship());
        em.persist(mp3);

        mp4 = new MetaProtocol(core);
        mp4.setService(htsfIlluminaSequencing);
        mp4.setSequenceNumber(4);
        mp4.setServiceType(sampleType);
        mp4.setProductOrdered(kernel.getAnyRelationship());
        em.persist(mp4);
    }

    private void constructProductAttributes() {
        pa1 = new ProductAttribute(a2, 3, core);
        pa1.setProduct(htsfIlluminaSequencing);
        em.persist(pa1);

        pa2 = new ProductAttribute(a1, 36, core);
        pa2.setProduct(htsfIlluminaSequencing);
        em.persist(pa2);
    }

    private void constructProductNetworks() {
        pn1 = new ProductNetwork(sampleX, sampleType, htsfSample, core);
        em.persist(pn1);
        pn2 = new ProductNetwork(sampleX, dissolvedIn, teBuffer, core);
        em.persist(pn2);
    }

    private void constructProducts() {
        teBuffer = new Product("TE Buffer", core);
        em.persist(teBuffer);

        htsfSample = new Product("HTSF Sample", core);
        em.persist(htsfSample);

        unpreparedSample = new Product("Unprepared Sample for HTSF Sequencing",
                                       core);
        em.persist(unpreparedSample);

        sampleX = new Product("Sample X", core);
        em.persist(sampleX);
    }

    private void constructProtocols() {
        p01 = new Protocol(core);
        p01.setRequester(kernel.getAnyResource());
        p01.setService(resuspend);
        p01.setMaterial(htsfSample);
        p01.setRequestedService(htsfIlluminaSequencing);
        p01.setProductOrdered(teBuffer);
        p01.setSubService(kernel.getAnyProduct());
        p01.setProduct(kernel.getAnyProduct());
        p01.setDeliverFrom(kernel.getAnyLocation());
        p01.setDeliverTo(kernel.getAnyLocation());
        p01.setAssignTo(htsfTech);
        em.persist(p01);

        p02 = new Protocol(core);
        p02.setRequester(kernel.getAnyResource());
        p02.setService(libraryPrep);
        p02.setMaterial(htsfSample);
        p02.setRequestedService(htsfIlluminaSequencing);
        p02.setProductOrdered(unpreparedSample);
        p02.setSubService(kernel.getAnyProduct());
        p02.setProduct(kernel.getAnyProduct());
        p02.setDeliverFrom(kernel.getAnyLocation());
        p02.setDeliverTo(kernel.getAnyLocation());
        p02.setAssignTo(htsfTech);
        em.persist(p02);

        p03 = new Protocol(core);
        p03.setRequester(kernel.getAnyResource());
        p03.setService(doChipSeqPrep);
        p03.setMaterial(htsfSample);
        p03.setRequestedService(htsfIlluminaSequencing);
        p03.setProductOrdered(kernel.getAnyProduct());
        p03.setProduct(kernel.getAnyProduct());
        p03.setDeliverFrom(kernel.getAnyLocation());
        p03.setDeliverTo(kernel.getAnyLocation());
        p03.setSubService(chipSeq);
        p03.setAssignTo(htsfTech);
        em.persist(p03);

        p04 = new Protocol(core);
        p04.setRequester(kernel.getAnyResource());
        p04.setService(customPrimerAnalysis);
        p04.setMaterial(htsfSample);
        p04.setRequestedService(htsfIlluminaSequencing);
        p04.setProductOrdered(kernel.getAnyProduct());
        p04.setProduct(kernel.getAnyProduct());
        p04.setDeliverFrom(kernel.getAnyLocation());
        p04.setDeliverTo(kernel.getAnyLocation());
        p04.setAssignTo(htsfTech);
        p04.setSubService(preparePrimers);
        em.persist(p04);

        p05 = new Protocol(core);
        p05.setRequester(kernel.getAnyResource());
        p05.setService(doDgePrep);
        p05.setMaterial(htsfSample);
        p05.setRequestedService(htsfIlluminaSequencing);
        p05.setProductOrdered(kernel.getAnyProduct());
        p05.setProduct(kernel.getAnyProduct());
        p05.setDeliverFrom(kernel.getAnyLocation());
        p05.setDeliverTo(kernel.getAnyLocation());
        p05.setAssignTo(htsfTech);
        p05.setSubService(dge);
        em.persist(p05);

        p06 = new Protocol(core);
        p06.setRequester(kernel.getAnyResource());
        p06.setService(doMiRnaPrep);
        p06.setMaterial(htsfSample);
        p06.setRequestedService(htsfIlluminaSequencing);
        p06.setProductOrdered(kernel.getAnyProduct());
        p06.setProduct(kernel.getAnyProduct());
        p06.setDeliverFrom(kernel.getAnyLocation());
        p06.setDeliverTo(kernel.getAnyLocation());
        p06.setAssignTo(htsfTech);
        p06.setSubService(miRNA);
        em.persist(p06);

        p07 = new Protocol(core);
        p07.setRequester(kernel.getAnyResource());
        p07.setService(doPairedEndAnalysisPrep);
        p07.setMaterial(htsfSample);
        p07.setRequestedService(htsfIlluminaSequencing);
        p07.setProductOrdered(kernel.getAnyProduct());
        p07.setProduct(kernel.getAnyProduct());
        p07.setDeliverFrom(kernel.getAnyLocation());
        p07.setDeliverTo(kernel.getAnyLocation());
        p07.setAssignTo(htsfTech);
        p07.setSubService(pairedEndAnalysis);
        em.persist(p07);

        p08 = new Protocol(core);
        p08.setRequester(kernel.getAnyResource());
        p08.setService(clusterGen);
        p08.setMaterial(htsfSample);
        p08.setRequestedService(htsfIlluminaSequencing);
        p08.setProductOrdered(kernel.getAnyProduct());
        p08.setProduct(kernel.getAnyProduct());
        p08.setDeliverFrom(kernel.getAnyLocation());
        p08.setDeliverTo(kernel.getAnyLocation());
        p08.setAssignTo(htsfTech);
        p08.setSubService(kernel.getAnyProduct());
        em.persist(p08);

        Protocol p9 = new Protocol(core);
        p9.setRequester(kernel.getAnyResource());
        p9.setSequenceNumber(2);
        p9.setService(sequenceClusters);
        p9.setMaterial(htsfSample);
        p9.setRequestedService(htsfIlluminaSequencing);
        p9.setProductOrdered(kernel.getAnyProduct());
        p9.setProduct(kernel.getAnyProduct());
        p9.setDeliverFrom(kernel.getAnyLocation());
        p9.setDeliverTo(kernel.getAnyLocation());
        p9.setAssignTo(htsfTech);
        p9.setSubService(kernel.getAnyProduct());
        em.persist(p9);

        p10 = new Protocol(core);
        p10.setRequester(kernel.getAnyResource());
        p10.setSequenceNumber(3);
        p10.setService(dataAnalysis);
        p10.setMaterial(htsfSample);
        p10.setRequestedService(htsfIlluminaSequencing);
        p10.setProductOrdered(kernel.getAnyProduct());
        p10.setProduct(kernel.getAnyProduct());
        p10.setDeliverFrom(kernel.getAnyLocation());
        p10.setDeliverTo(kernel.getAnyLocation());
        p10.setAssignTo(htsfTech);
        p10.setSubService(kernel.getAnyProduct());
        em.persist(p9);
    }

    private void constructRelationships() {

        sampleType = new Relationship("Sample Type",
                                      "The sample type of A is B", core);
        sampleTypeOf = new Relationship("Sample Type Of",
                                        "A is the sample type of B", core,
                                        sampleType);
        sampleType.setInverse(sampleTypeOf);
        em.persist(sampleType);
        em.persist(sampleTypeOf);

        dissolvedIn = new Relationship("Dissolved In",
                                       "Solvent that a sample is dissolved in",
                                       core);
        solventOf = new Relationship("Solvent of", "A is the solvent of B",
                                     core, dissolvedIn);
        dissolvedIn.setInverse(solventOf);
        em.persist(dissolvedIn);
        em.persist(solventOf);

        htfsLibPrepStatus = new Relationship(
                                             "HTSF Library Preparation Status",
                                             "Does the HTSF need to prep the sample, or did the customer do it already?",
                                             core);
        htfsLibPrepStatusOf = new Relationship(
                                               "HTSF Library Preparation Status Of",
                                               "A is the prep status B", core,
                                               htfsLibPrepStatus);
        htfsLibPrepStatus.setInverse(htfsLibPrepStatusOf);
        em.persist(htfsLibPrepStatus);
        em.persist(htfsLibPrepStatusOf);

        runType = new Relationship("Run Type", "The Run Type of A is B", core);
        runTypeOf = new Relationship("Run Type Of", "A is the run type B",
                                     core, runType);
        runType.setInverse(runTypeOf);
        em.persist(runType);
        em.persist(runTypeOf);
    }

    private void constructResources() {
        htsfTech = new Resource("HTSF Technician", core);
        em.persist(htsfTech);

        htsf = new Resource("HTSF", core);
        em.persist(htsf);
    }

    private void constructSequences() {
        seq01 = new StatusCodeSequencing(htsfIlluminaSequencing, active,
                                         success, 1, core);
        em.persist(seq01);

        seq02 = new StatusCodeSequencing(htsfIlluminaSequencing, active,
                                         failure, 2, core);
        em.persist(seq02);

        seq03 = new StatusCodeSequencing(libraryPrep, active, success, 1, core);
        em.persist(seq03);

        seq04 = new StatusCodeSequencing(libraryPrep, active, failure, 2, core);
        em.persist(seq04);

        seq05 = new StatusCodeSequencing(doChipSeqPrep, active, success, 1,
                                         core);
        em.persist(seq05);

        seq06 = new StatusCodeSequencing(doChipSeqPrep, active, failure, 2,
                                         core);
        em.persist(seq06);

        seq07 = new StatusCodeSequencing(resuspend, active, success, 1, core);
        em.persist(seq07);

        seq08 = new StatusCodeSequencing(resuspend, active, failure, 2, core);
        em.persist(seq08);

        seq09 = new StatusCodeSequencing(preparePrimers, active, success, 1,
                                         core);
        em.persist(seq09);

        seq10 = new StatusCodeSequencing(preparePrimers, active, failure, 2,
                                         core);
        em.persist(seq10);

        seq11 = new StatusCodeSequencing(doDgePrep, active, success, 1, core);
        em.persist(seq11);

        seq12 = new StatusCodeSequencing(doDgePrep, active, failure, 2, core);
        em.persist(seq12);

        seq13 = new StatusCodeSequencing(doMiRnaPrep, active, success, 1, core);
        em.persist(seq13);

        seq14 = new StatusCodeSequencing(doMiRnaPrep, active, failure, 2, core);
        em.persist(seq14);

        seq15 = new StatusCodeSequencing(doPairedEndAnalysisPrep, active,
                                         success, 1, core);
        em.persist(seq15);

        seq16 = new StatusCodeSequencing(doPairedEndAnalysisPrep, active,
                                         failure, 2, core);
        em.persist(seq16);

        seq17 = new StatusCodeSequencing(clusterGen, active, success, 1, core);

        em.persist(seq17);

        seq18 = new StatusCodeSequencing(clusterGen, active, failure, 2, core);
        em.persist(seq18);

        seq19 = new StatusCodeSequencing(sequenceClusters, active, success, 1,
                                         core);
        em.persist(seq19);

        seq20 = new StatusCodeSequencing(sequenceClusters, active, failure, 2,
                                         core);
        em.persist(seq20);

        seq21 = new StatusCodeSequencing(dataAnalysis, active, success, 1, core);
        em.persist(seq21);

        seq22 = new StatusCodeSequencing(dataAnalysis, active, failure, 2, core);
        em.persist(seq22);

        seq23 = new StatusCodeSequencing(chipSeq, active, success, 1, core);
        em.persist(seq23);

        seq24 = new StatusCodeSequencing(chipSeq, active, failure, 2, core);
        em.persist(seq24);

        seq25 = new StatusCodeSequencing(customPrimerAnalysis, active, success,
                                         1, core);
        em.persist(seq25);

        seq26 = new StatusCodeSequencing(customPrimerAnalysis, active, failure,
                                         2, core);
        em.persist(seq26);

        seq27 = new StatusCodeSequencing(dge, active, success, 1, core);
        em.persist(seq27);

        seq28 = new StatusCodeSequencing(dge, active, failure, 2, core);
        em.persist(seq28);

        seq29 = new StatusCodeSequencing(miRNA, active, success, 1, core);
        em.persist(seq29);

        seq30 = new StatusCodeSequencing(miRNA, active, failure, 2, core);
        em.persist(seq30);

        seq31 = new StatusCodeSequencing(pairedEndAnalysis, active, success, 1,
                                         core);
        em.persist(seq31);

        seq32 = new StatusCodeSequencing(pairedEndAnalysis, active, failure, 2,
                                         core);
        em.persist(seq32);

        seq33 = new StatusCodeSequencing(figureOutWhyCLusterSeqFailed, active,
                                         success, 1, core);
        em.persist(seq33);

        seq34 = new StatusCodeSequencing(figureOutWhyCLusterSeqFailed, active,
                                         failure, 2, core);
        em.persist(seq34);
    }

    private void constructSequencingAuthorizations() {
        psa1 = new ProductSequencingAuthorization(core);
        psa1.setParent(resuspend);
        psa1.setStatusCode(success);
        psa1.setNextSibling(libraryPrep);
        psa1.setNextSiblingStatus(active);
        em.persist(psa1);

        psa02 = new ProductSequencingAuthorization(core);
        psa02.setParent(libraryPrep);
        psa02.setStatusCode(success);
        psa02.setSequenceNumber(1);
        psa02.setNextSibling(doChipSeqPrep);
        psa02.setNextSiblingStatus(active);
        em.persist(psa02);

        psa03 = new ProductSequencingAuthorization(core);
        psa03.setParent(libraryPrep);
        psa03.setStatusCode(success);
        psa03.setSequenceNumber(2);
        psa03.setNextSibling(preparePrimers);
        psa03.setNextSiblingStatus(active);
        em.persist(psa03);

        psa04 = new ProductSequencingAuthorization(core);
        psa04.setParent(libraryPrep);
        psa04.setStatusCode(success);
        psa04.setSequenceNumber(3);
        psa04.setNextSibling(doDgePrep);
        psa04.setNextSiblingStatus(active);
        em.persist(psa04);

        psa05 = new ProductSequencingAuthorization(core);
        psa05.setParent(libraryPrep);
        psa05.setStatusCode(success);
        psa05.setSequenceNumber(4);
        psa05.setNextSibling(doMiRnaPrep);
        psa05.setNextSiblingStatus(active);
        em.persist(psa05);

        psa06 = new ProductSequencingAuthorization(core);
        psa06.setParent(libraryPrep);
        psa06.setStatusCode(success);
        psa06.setSequenceNumber(5);
        psa06.setNextSibling(doPairedEndAnalysisPrep);
        psa06.setNextSiblingStatus(active);
        em.persist(psa06);

        psa07 = new ProductSequencingAuthorization(core);
        psa07.setParent(doChipSeqPrep);
        psa07.setStatusCode(success);
        psa07.setNextSibling(clusterGen);
        psa07.setNextSiblingStatus(active);
        em.persist(psa07);

        psa08 = new ProductSequencingAuthorization(core);
        psa08.setParent(preparePrimers);
        psa08.setStatusCode(success);
        psa08.setNextSibling(clusterGen);
        psa08.setNextSiblingStatus(active);
        em.persist(psa08);

        psa09 = new ProductSequencingAuthorization(core);
        psa09.setParent(doDgePrep);
        psa09.setStatusCode(success);
        psa09.setNextSibling(clusterGen);
        psa09.setNextSiblingStatus(active);
        em.persist(psa09);

        psa10 = new ProductSequencingAuthorization(core);
        psa10.setParent(doMiRnaPrep);
        psa10.setStatusCode(success);
        psa10.setNextSibling(clusterGen);
        psa10.setNextSiblingStatus(active);
        em.persist(psa10);

        psa11 = new ProductSequencingAuthorization(core);
        psa11.setParent(doPairedEndAnalysisPrep);
        psa11.setStatusCode(success);
        psa11.setNextSibling(clusterGen);
        psa11.setNextSiblingStatus(active);
        em.persist(psa11);

        psa12 = new ProductSequencingAuthorization(core);
        psa12.setParent(clusterGen);
        psa12.setStatusCode(success);
        psa12.setNextSibling(sequenceClusters);
        psa12.setNextSiblingStatus(active);
        em.persist(psa12);

        psa13 = new ProductSequencingAuthorization(core);
        psa13.setParent(sequenceClusters);
        psa13.setStatusCode(success);
        psa13.setNextSibling(dataAnalysis);
        psa13.setNextSiblingStatus(active);
        em.persist(psa13);

        psa14 = new ProductSequencingAuthorization(core);
        psa14.setParent(sequenceClusters);
        psa14.setStatusCode(failure);
        psa14.setNextSibling(figureOutWhyCLusterSeqFailed);
        psa14.setNextSiblingStatus(active);
        em.persist(psa14);
    }

    private void constructServices() {
        htsfIlluminaSequencing = new Product(
                                             "HTSF Illumina Sequencing",
                                             "Illumina sequencing at UNC High-Throughput Sequencing Facility",
                                             core);
        em.persist(htsfIlluminaSequencing);

        chipSeq = new Product("Chip-Seq", core);
        em.persist(chipSeq);

        doChipSeqPrep = new Product("Do Chip-Seq Preparation", core);
        em.persist(doChipSeqPrep);

        customPrimerAnalysis = new Product("Custom Primer Analysis", core);
        em.persist(customPrimerAnalysis);

        preparePrimers = new Product("Prepare Primers", core);
        em.persist(preparePrimers);

        dge = new Product("DGE", core);
        em.persist(dge);

        doDgePrep = new Product("Do DGE Preparation", core);
        em.persist(doDgePrep);

        miRNA = new Product("miRNA", core);
        em.persist(miRNA);

        doMiRnaPrep = new Product("Do miRNA Analysis Preparation", core);
        em.persist(doMiRnaPrep);

        pairedEndAnalysis = new Product("Paired-End Analysis", core);
        em.persist(pairedEndAnalysis);

        doPairedEndAnalysisPrep = new Product(
                                              "Do Paired-End Analysis Preparation",
                                              core);
        em.persist(doPairedEndAnalysisPrep);

        libraryPrep = new Product("Library Preparation", core);
        em.persist(libraryPrep);

        clusterGen = new Product("Cluster Generation", core);
        em.persist(clusterGen);

        sequenceClusters = new Product("Sequence Clusters", core);
        em.persist(sequenceClusters);

        dataAnalysis = new Product("Data Analysis", core);
        em.persist(dataAnalysis);

        dnaSequencing = new Product("DNA Sequencing", core);
        em.persist(dnaSequencing);

        figureOutWhyCLusterSeqFailed = new Product(
                                                   "Figure Out Why Cluster Sequencing Failed",
                                                   core);
        em.persist(figureOutWhyCLusterSeqFailed);
    }

    private void constructStatusCodes() {
        success = new StatusCode("Success", "Something went right", core);
        em.persist(success);

        failure = new StatusCode("Failure", "Something went wrong", core);
        em.persist(failure);

        active = new StatusCode("Active", "Working on it now", core);
        em.persist(active);

        approvedButInactive = new StatusCode(
                                             "Approved, but Inactive",
                                             "We going to do it, but we&apos;re not allowed to right now",
                                             core);
        em.persist(approvedButInactive);

        abandoned = new StatusCode(
                                   "Abandoned",
                                   "We were going to do it, something happened in earlier processing that will prevent us.  This can be garbage-collected now",
                                   core);
        em.persist(abandoned);

        resuspend = new Product("Resuspend in Suitable Buffer",
                                "Gotta get it in H2O for sequencing", core);
        em.persist(resuspend);
    }

    private void loadJobTestData() {
        em.getTransaction().begin();

        constructRelationships();
        constructProducts();
        constructProductNetworks();
        constructResources();
        constructStatusCodes();
        constructServices();
        constructAttributes();
        constructProductAttributes();
        constructSequences();
        constructSequencingAuthorizations();
        constructProtocols();
        constructMetaProtocols();
        constructJobs();

        em.getTransaction().commit();

    }
}
