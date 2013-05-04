package com.hellblazer.CoRE.event;

import static org.junit.Assert.*; 

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.test.DatabaseTestContext;

public class JobTest extends DatabaseTestContext {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public void testIsActive() {
        beginTransaction();

        TypedQuery<Job> findAll = em.createNamedQuery("job.findAll", Job.class);

        int num = findAll.getResultList().size();

        LOG.debug(String.format("Found %s Jobs", num));
        System.out.println("Found " + num + " Jobs");

        Job j = em.find(Job.class, 2L);
        assertNotNull(j);
        assertEquals("Active", j.getStatus().getName());
        assertTrue(Job.getActive(j, em));

        commitTransaction();
    }

    public void testNextStatusCodes() {
        beginTransaction();

        Job j = em.find(Job.class, 2L);
        JobModel jobModel = new ModelImpl(em, null).getJobModel();

        assertNotNull(j);
        LOG.debug(String.format("Job is: %s", j));
        assertTrue(jobModel.getNextStatusCodes(j.getService(),
                                               j.getStatus()).size() > 0);
        LOG.debug(String.format("Number of next status codes: %s",
                                jobModel.getNextStatusCodes(j.getService(),
                                                            j.getStatus()).size()));

        Set<StatusCode> expected = new HashSet<StatusCode>();
        expected.add(new StatusCode(2L, "Success"));
        expected.add(new StatusCode(3L, "Failure"));

        List<StatusCode> result = jobModel.getNextStatusCodes(j.getService(),
                                                              j.getStatus());
        assertEquals(expected.size(), result.size());
        for (StatusCode sc : expected) {
            assertTrue(result.contains(sc));
        }

        commitTransaction();
    }

    /*
     * (non-Javadoc)
     * @see com.hellblazer.CoRE.testing.HibernateDatabaseTestCase#prepareSettings()
     */
    @Override
    protected void prepareSettings() {
        LOG.trace("Entering prepareSettings");
        dataSetLocation = "JobTestSeedData.xml";
        beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);

        LOG.trace("Exiting prepareSettings");
    }

    /*
     * (non-Javadoc)
     * @see com.hellblazer.CoRE.testing.HibernateDatabaseTestCase#setSequences()
     */
    @Override
    protected void setSequences() throws Exception {
        LOG.trace("Entering setSequences");
        //        this.getConnection().getConnection().createStatement().execute("SELECT setval('resource_id_seq', 1)");
        //        this.getConnection().getConnection().createStatement().execute("SELECT setval('attribute_id_seq', 1)");
        //        this.getConnection().getConnection().createStatement().execute("SELECT setval('product_id_seq', 2)");
        //        this.getConnection().getConnection().createStatement().execute("SELECT setval('unit_id_seq', 1)");
        //        this.getConnection().getConnection().createStatement().execute("SELECT setval('product_attribute_id_seq', 1)");
        //        this.getConnection().getConnection().createStatement().execute("SELECT setval('relationship_id_seq', 2)");
        //        this.getConnection().getConnection().createStatement().execute("SELECT setval('research_id_seq', 5)");
        //        this.getConnection().getConnection().createStatement().execute("SELECT setval('product_network_id_seq', 1, false)");
        //        //getConnection().getConnection().createStatement().execute("SELECT setval('event_id_seq', 5)");
        LOG.trace("Exiting setSequences");
    }
}
