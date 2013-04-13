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

package com.hellblazer.CoRE.event.animation;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.animation.AnimationContext;
import com.hellblazer.CoRE.animation.Animations;
import com.hellblazer.CoRE.animation.RuleformIdIterator;
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.kernel.WellKnownObject.WellKnownStatusCode;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public class JobAnimations extends Animations {
    private static class InDatabase {
        private static final JobAnimations SINGLETON;

        static {
            SINGLETON = new JobAnimations();
        }

        public static JobAnimations get() {
            establishContext();
            return SINGLETON;
        }
    }

    private static final Logger log = Logger.getLogger(JobAnimations.class.getCanonicalName());

    public static void add_job_chronology_rule(Long jobId, Timestamp timestamp,
                                               Long status, String notes)
                                                                         throws SQLException {
        InDatabase.get().addJobChronologyRule(jobId, timestamp, status, notes);
    }

    public static void automatically_generate_implicit_jobs_for_explicit_jobs(TriggerData triggerData)
                                                                                                      throws SQLException {
        InDatabase.get().automaticallyGenerateImplicitJobsForExplicitJobs(triggerData.getNew().getLong("id"));
    }

    public static void ensure_next_state_is_valid(TriggerData triggerData)
                                                                          throws SQLException {
        InDatabase.get().ensureNextStateIsValid(triggerData.getNew().getLong("id"),
                                                triggerData.getNew().getLong("service"),
                                                triggerData.getOld().getLong("status"),
                                                triggerData.getNew().getLong("status"));
    }

    public static void ensure_valid_initial_state(TriggerData triggerData)
                                                                          throws SQLException {
        if (!WellKnownStatusCode.UNSET.id().equals(triggerData.getNew().getLong("status"))) {
            if (log.isLoggable(Level.INFO)) {
                log.info(String.format("Setting status of job to unset (%s)",
                                       triggerData.getNew().getLong("status")));
            }
            triggerData.getNew().updateLong("status",
                                            WellKnownStatusCode.UNSET.id());
        }
    }

    public static void generate_implicit_jobs(Long service) {
        InDatabase.get().generateImplicitJobs(service);
    }

    public static Long get_initial_state(Long service) {
        return InDatabase.get().getInitialState(service);
    }

    /**
     * In database function entry point
     * 
     * @param serviceId
     * @return
     * @throws SQLException
     */
    public static Iterator<Long> get_status_code_ids_for_service(Long serviceId)
                                                                                throws SQLException {
        establishContext();
        return new RuleformIdIterator(
                                      InDatabase.get().getStatusCodeIdsForEvent(serviceId).iterator());
    }

    public static boolean is_job_active(Long job) {
        establishContext();
        return InDatabase.get().isJobActive(job);
    }

    public static boolean is_terminal_state(Long service, Long statusCode) {
        establishContext();
        return InDatabase.get().isTerminalState(service, statusCode);
    }

    public static void log_modified_service_status_code_sequencing(TriggerData triggerData)
                                                                                           throws SQLException {
        establishContext();
        InDatabase.get().logModifiedService(triggerData.getNew().getLong("service"));
    }

    public static void processChildChanges(TriggerData triggerData)
                                                                   throws SQLException {
        InDatabase.get().processChildChanges(triggerData.getNew().getLong("id"));
    }

    public static void processParentChanges(TriggerData triggerData)
                                                                    throws SQLException {
        InDatabase.get().processParentChanges(triggerData.getNew().getLong("id"));
    }

    public static void processSiblingChanges(TriggerData triggerData)
                                                                     throws SQLException {
        InDatabase.get().processSiblingChanges(triggerData.getNew().getLong("id"));
    }

    public static void validate_state_graph(TriggerData triggerData)
                                                                    throws SQLException {
        establishContext();
        InDatabase.get().validateStateGraph();
    }

    private final List<Entity> modifiedEvents = new ArrayList<Entity>();

    /**
     * Only valid within the DB
     */
    protected JobAnimations() {
        super();
    }

    /**
     * @param context
     */
    public JobAnimations(AnimationContext context) {
        super(context);
    }

    private void addJobChronologyRule(Long jobId, Timestamp timestamp,
                                      Long status, String notes) {
        context.getJobModel().addJobChronology(context.getEm().find(Job.class,
                                                                    jobId),
                                               timestamp,
                                               context.getEm().find(StatusCode.class,
                                                                    status),
                                               notes);
    }

    /**
     * @param job
     */
    public void automaticallyGenerateImplicitJobsForExplicitJobs(Job job) {
        context.getJobModel().automaticallyGenerateImplicitJobsForExplicitJobs(job);
    }

    /**
     * @param job
     */
    private void automaticallyGenerateImplicitJobsForExplicitJobs(long job) {
        automaticallyGenerateImplicitJobsForExplicitJobs(context.getEm().find(Job.class,
                                                                              job));
    }

    /**
     * @param job
     * @param service
     * @param currentStatus
     * @param nextStatus
     * @throws SQLException
     */
    private void ensureNextStateIsValid(long job, long service,
                                        long currentStatus, long nextStatus)
                                                                            throws SQLException {
        if (currentStatus == nextStatus) {
            return; // nothing to do
        }
        EntityManager em = context.getEm();
        context.getJobModel().ensureNextStateIsValid(em.find(Job.class, job),
                                                     em.find(Entity.class,
                                                             service),
                                                     em.find(StatusCode.class,
                                                             currentStatus),
                                                     em.find(StatusCode.class,
                                                             nextStatus));
    }

    /**
     * @param service
     */
    private void generateImplicitJobs(Long service) {
        context.getJobModel().generateImplicitJobs((Job) null);
    }

    public List<Job> getActiveJobs(Resource resource) {
        return null;
    }

    /**
     * @param service
     * @return
     */
    private Long getInitialState(Long service) {
        return context.getJobModel().getInitialState(context.getEm().find(Entity.class,
                                                                          service)).getId();
    }

    /**
     * @param serviceId
     * @return
     */
    private Collection<StatusCode> getStatusCodeIdsForEvent(Long serviceId) {
        return context.getJobModel().getStatusCodesFor(context.getEm().find(Entity.class,
                                                                            serviceId));
    }

    /**
     * @param job
     * @return
     */
    private boolean isJobActive(Long job) {
        return context.getJobModel().isActive(context.getEm().find(Job.class,
                                                                   job));
    }

    /**
     * @param service
     * @param statusCode
     * @return
     */
    private boolean isTerminalState(Long service, Long statusCode) {
        return context.getJobModel().isTerminalState(context.getEm().find(StatusCode.class,
                                                                          statusCode),
                                                     context.getEm().find(Entity.class,
                                                                          service));
    }

    public void logModifiedService(Long scs) {
        modifiedEvents.add(context.getEm().find(Entity.class, scs));
    }

    public void processChildChanges(Job job) {
        context.getJobModel().processChildChanges(job);
    }

    public void processParentChanges(Job job) {
        context.getJobModel().processParentChanges(job);
    }

    public void processSiblingChanges(Job job) {
        context.getJobModel().processSiblingChanges(job);
    }

    private void processSiblingChanges(long jobId) {
        processSiblingChanges(context.getEm().find(Job.class, jobId));
    }

    private void processChildChanges(long jobId) {
        processChildChanges(context.getEm().find(Job.class, jobId));
    }

    private void processParentChanges(long jobId) {
        processParentChanges(context.getEm().find(Job.class, jobId));
    }

    public void validateStateGraph() throws SQLException {
        try {
            context.getJobModel().validateStateGraph(modifiedEvents);
        } finally {
            modifiedEvents.clear();
        }
    }
}
