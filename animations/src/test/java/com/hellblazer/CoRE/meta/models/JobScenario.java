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
        j1 = new Job(htsfTech, htsfIlluminaSequencing,
                     kernel.getNotApplicableProduct(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableResource(), core);
        j1.setStatus(active);
        em.persist(j1);

        j2 = new Job(j1, htsfTech, resuspend, kernel.getNotApplicableProduct(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableResource(), core);
        j2.setStatus(active);
        em.persist(j2);

        j3 = new Job(j1, htsfTech, libraryPrep,
                     kernel.getNotApplicableProduct(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableResource(), core);
        j3.setStatus(kernel.getUnset());
        em.persist(j3);

        j4 = new Job(j1, htsfTech, dge, kernel.getNotApplicableProduct(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableResource(), core);
        j4.setStatus(kernel.getUnset());
        em.persist(j4);

        j5 = new Job(j1, htsfTech, clusterGen,
                     kernel.getNotApplicableProduct(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableResource(), core);
        j5.setStatus(kernel.getUnset());
        em.persist(j5);

        j6 = new Job(j1, htsfTech, sequenceClusters,
                     kernel.getNotApplicableProduct(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableResource(), core);
        j6.setStatus(kernel.getUnset());
        em.persist(j6);

        j7 = new Job(j1, htsfTech, dataAnalysis,
                     kernel.getNotApplicableProduct(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableLocation(),
                     kernel.getNotApplicableResource(), core);
        j7.setStatus(kernel.getUnset());
        em.persist(j7);
    }

    private void constructJobChronology() {

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
        p01 = new Protocol(htsfIlluminaSequencing, kernel.getAnyResource(),
                           kernel.getNotApplicableProduct(),
                           kernel.getAnyLocation(), kernel.getAnyLocation(),
                           htsfTech, resuspend,
                           kernel.getNotApplicableProduct(), core);
        em.persist(p01);

        p02 = new Protocol(htsfIlluminaSequencing, kernel.getAnyResource(),
                           unpreparedSample, kernel.getAnyLocation(),
                           kernel.getAnyLocation(), htsfTech, libraryPrep,
                           kernel.getNotApplicableProduct(), core);
        em.persist(p02);

        p03 = new Protocol(doChipSeqPrep, kernel.getAnyResource(), chipSeq,
                           kernel.getAnyLocation(), kernel.getAnyLocation(),
                           htsfTech, kernel.getNotApplicableProduct(),
                           kernel.getNotApplicableProduct(), core);
        em.persist(p03);

        p04 = new Protocol(customPrimerAnalysis, kernel.getAnyResource(),
                           preparePrimers, kernel.getAnyLocation(),
                           kernel.getAnyLocation(), htsfTech,
                           kernel.getNotApplicableProduct(),
                           kernel.getNotApplicableProduct(), core);
        em.persist(p04);

        p05 = new Protocol(doDgePrep, kernel.getAnyResource(), dge,
                           kernel.getAnyLocation(), kernel.getAnyLocation(),
                           htsfTech, kernel.getNotApplicableProduct(),
                           kernel.getNotApplicableProduct(), core);
        em.persist(p05);

        p06 = new Protocol(doMiRnaPrep, kernel.getAnyResource(), miRNA,
                           kernel.getAnyLocation(), kernel.getAnyLocation(),
                           htsfTech, kernel.getNotApplicableProduct(),
                           kernel.getNotApplicableProduct(), core);
        em.persist(p06);

        p07 = new Protocol(doPairedEndAnalysisPrep, kernel.getAnyResource(),
                           pairedEndAnalysis, kernel.getAnyLocation(),
                           kernel.getAnyLocation(), htsfTech,
                           kernel.getNotApplicableProduct(),
                           kernel.getNotApplicableProduct(), core);
        em.persist(p07);

        p08 = new Protocol(htsfIlluminaSequencing, kernel.getAnyResource(),
                           clusterGen, kernel.getAnyLocation(),
                           kernel.getAnyLocation(), htsfTech,
                           kernel.getNotApplicableProduct(),
                           kernel.getNotApplicableProduct(), core);
        em.persist(p08);

        Protocol p9 = new Protocol(htsfIlluminaSequencing,
                                   kernel.getAnyResource(), sequenceClusters,
                                   kernel.getAnyLocation(),
                                   kernel.getAnyLocation(), htsfTech,
                                   kernel.getNotApplicableProduct(),
                                   kernel.getNotApplicableProduct(), core);
        em.persist(p9);

        p10 = new Protocol(htsfIlluminaSequencing, kernel.getAnyResource(),
                           dataAnalysis, kernel.getAnyLocation(),
                           kernel.getAnyLocation(), htsfTech,
                           kernel.getNotApplicableProduct(),
                           kernel.getNotApplicableProduct(), core);
        em.persist(p10);
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
        success.setPropagateChildren(true);
        em.persist(success);

        failure = new StatusCode("Failure", "Something went wrong", core);
        failure.setFailParent(true);
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
        constructJobChronology();

        em.getTransaction().commit();

    }
}
