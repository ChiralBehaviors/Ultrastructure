package com.hellblazer.CoRE.meta;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.JobChronology;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.resource.Resource;

/*
 * functions from _add_job_chronology_rule.xml, change_job_status
 */

public class WorkflowAnimator {

    private EntityManager em;

    public WorkflowAnimator(EntityManager em) {
        this.em = em;
    }

    public void changeJobStatus_persist(Job job, StatusCode newStatus,
                                        String note, Resource updatedBy) {
        if (!job.getStatus().equals(newStatus)) {
            job.setStatus(newStatus);
            JobChronology jc = addJobChronologyRule(job, note, updatedBy);
            em.persist(job);
            em.persist(jc);

        }
    }

    private JobChronology addJobChronologyRule(Job job, String notes,
                                               Resource updatedBy) {
        JobChronology jc = new JobChronology();
        jc.setJob(job);
        jc.setNotes(notes);
        jc.setUpdatedBy(updatedBy);
        return jc;
    }

}
