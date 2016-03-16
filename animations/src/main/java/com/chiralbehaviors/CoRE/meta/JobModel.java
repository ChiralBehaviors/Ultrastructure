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

package com.chiralbehaviors.CoRE.meta;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.jooq.tables.ChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.JobChronology;
import com.chiralbehaviors.CoRE.jooq.tables.MetaProtocol;
import com.chiralbehaviors.CoRE.jooq.tables.ParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.Protocol;
import com.chiralbehaviors.CoRE.jooq.tables.SelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.SiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public interface JobModel {
    final static int          MAXIMUM_JOB_DEPTH = 20;
    final static InferenceMap NO_TRANSFORMATION = new InferenceMap(false, false,
                                                                   false, false,
                                                                   false,
                                                                   false);

    /**
     * Sets the status of the given JobRecord. This should not be done directly
     * on the job itself because we log the change in the JobChronology
     * ruleform.
     *
     * @param job
     * @param newStatus
     * @param message
     *            an optional message about why the status was changed, the
     *            circumstances surrounding the change, etc.
     * @return the merged job
     */
    JobRecord changeStatus(JobRecord job, ExistentialRecord newStatus,
                           ExistentialRecord updagedBy, String notes);

    /**
     * Creates and persist a StatusCodeSequencing object for each sequential
     * pair of StatusCodes in the codes variable. So if codes is [A, B, C,] 2
     * StatusCodeSequencing objects will be created: A->B, B->C
     *
     * @param service
     *            the service with which these status codes are associated
     * @param codes
     *            the ordered list of codes to be sequenced
     * @param updatedBy
     */
    void createStatusCodeChain(ExistentialRecord service,
                               ExistentialRecord[] codes,
                               ExistentialRecord updatedBy);

    /**
     * @param service
     * @param codes
     * @param updatedBy
     */
    void createStatusCodeSequencings(ExistentialRecord service,
                                     List<Tuple<ExistentialRecord, ExistentialRecord>> codes,
                                     ExistentialRecord updatedBy);

    /**
     * Ensure that the nextStatus is a valid status transition from the
     * currentStatus for the service
     *
     * @param job
     * @param service
     * @param currentStatus
     * @param nextStatus
     * @return
     * @throws SQLException
     */
    void ensureNextStateIsValid(JobRecord job, ExistentialRecord service,
                                ExistentialRecord currentStatus,
                                ExistentialRecord nextStatus) throws SQLException;

    /**
     * @param parent
     * @throws SQLException
     */
    void ensureValidParentStatus(JobRecord parent) throws SQLException;

    /**
     *
     * @param nextSibling
     * @param nextSiblingStatus
     * @throws SQLException
     */
    void ensureValidServiceAndStatus(ExistentialRecord nextSibling,
                                     ExistentialRecord nextSiblingStatus) throws SQLException;

    Map<Protocol, Map<MetaProtocol, List<String>>> findMetaProtocolGaps(JobRecord job);

    Map<Protocol, List<String>> findProtocolGaps(JobRecord job);

    /**
     * For a given job, generates all the implicit jobs that need to be done
     *
     * This is the jesus nut of the the event cluster animation.
     *
     * @param updatedBy
     * @param jobId
     *
     * @return the list of jobs generated
     * @throws SQLException
     */
    List<JobRecord> generateImplicitJobs(JobRecord job,
                                         ExistentialRecord updatedBy);

    /**
     * Generate all the implicit sub jobs for the job
     *
     * @param job
     * @param updatedBy
     */
    void generateImplicitJobsForExplicitJobs(JobRecord job,
                                             ExistentialRecord updatedBy);

    /**
     * Retrieve a list of all currently active "explicit" (top level) Jobs.
     * "Explicit" means a JobRecord that has no parent JobRecord. "Active" means
     * Jobs whose current state is neither "(UNSET)"/pending nor a terminal
     * state for the JobRecord's ExistentialRecord.
     *
     * @return the list of all active, top level jobs
     */
    List<JobRecord> getActiveExplicitJobs();

    /**
     * Answer the list of active jobs that are assigned to a agency
     *
     * @param agency
     * @return the list of active jobs assigned to the agency
     */
    List<JobRecord> getActiveJobsFor(ExistentialRecord agency);

    /**
     * Answer the list of active jobs that are assigned to a agency, in a any of
     * the indicated states
     *
     * @param agency
     * @param desiredStates
     * @return the list of active jobs assigned to the agency that have the
     *         desired status
     */
    List<JobRecord> getActiveJobsFor(ExistentialRecord agency,
                                     List<ExistentialRecord> desiredStates);

    /**
     * Answer the list of active jobs that are assigned to a agency, in a
     * particular state
     *
     * @param agency
     * @param desiredState
     * @return the list of active jobs assigned to the agency that have the
     *         desired status
     */
    List<JobRecord> getActiveJobsFor(ExistentialRecord agency,
                                     ExistentialRecord desiredState);

    /**
     *
     * @param job
     * @param service
     * @return
     */
    List<JobRecord> getActiveSubJobsForService(JobRecord job,
                                               ExistentialRecord service);

    /**
     * Answer the list of active sub jobs (children) of the job
     *
     * @param job
     * @return the list of active sub jobs of the job
     */
    List<JobRecord> getActiveSubJobsOf(JobRecord job);

    /**
     * Answer the recursive list of all sub jobs - at any level - of a job that
     * are active or terminated
     *
     * @param job
     * @return the full list of all sub jobs of a job that are active or
     *         terminated
     */
    Collection<JobRecord> getAllActiveOrTerminatedSubJobsOf(JobRecord job);

    /**
     * Answer the recursive list of all sub jobs - at any level - of a job that
     * are active
     *
     * @param job
     * @return the recursive list of all sub jobs - at any level - of a job that
     *         are active
     */
    Collection<JobRecord> getAllActiveSubJobsOf(JobRecord job);

    /**
     *
     * @param parent
     * @param agency
     * @return
     */
    List<JobRecord> getAllActiveSubJobsOf(JobRecord parent,
                                          ExistentialRecord agency);

    /**
     *
     * @param parent
     * @param agency
     * @param jobs
     */
    void getAllActiveSubJobsOf(JobRecord parent, ExistentialRecord agency,
                               List<JobRecord> jobs);

    /**
     * Answer the list of all active subjobs
     *
     * @param job
     * @param tally
     * @return
     */
    Collection<JobRecord> getAllActiveSubJobsOf(JobRecord job,
                                                Collection<JobRecord> tally);

    /**
     * Get all direct and indirect child jobs of this job, regardless of status
     *
     * @param job
     * @return
     */
    List<JobRecord> getAllChildren(JobRecord job);

    /**
     * Answer the list of sequencing authorizations that have the job's service
     * as parent
     *
     * @param job
     * @return the list of sequencing authorizations that have the job's service
     *         as parent
     */
    List<ChildSequencingAuthorization> getChildActions(JobRecord job);

    /**
     * @param node
     * @return
     */
    List<ChildSequencingAuthorization> getChildActions(ExistentialRecord node);

    /**
     * Gets all immediate children of the parent job having the specified
     * service. Does not differentiate between unset, active, or terminated jobs
     *
     * @param parent
     * @param service
     * @return
     */
    List<JobRecord> getChildJobsByService(JobRecord parent,
                                          ExistentialRecord service);

    /**
     * Returns an ordered list of all JobChronology rules for the given job.
     * Entries are ordered by the ascending timeStamp (oldest is first, most
     * recent is last)
     *
     * If the given JobRecord is either null or has a null id property, an empty
     * list is returned.
     *
     * @param job
     * @return
     */
    List<JobChronology> getChronologyForJob(JobRecord job);

    /**
     * Answer the immediate child jobs of the job that are active or terminal
     *
     * @param job
     * @return the immediate child jobs of the job that are active or terminal
     */
    List<JobRecord> getDirectActiveOrTerminalSubJobsOf(JobRecord job);

    /**
     * Answer the initial state of a service
     *
     * @param service
     * @return the initial state of a service
     */
    ExistentialRecord getInitialState(ExistentialRecord service);

    List<ExistentialRecord> getInitialStates(ExistentialRecord service);

    /**
     * Answer the list of MetaProtocols that can be applied to the job
     *
     * @param job
     * @return the list of MetaProtocols that can be applied to the job
     */
    List<MetaProtocol> getMetaprotocols(JobRecord job);

    /**
     * @param service
     * @return
     */
    List<MetaProtocol> getMetaProtocolsFor(ExistentialRecord service);

    /**
     * Returns the individual JobChronology rule that reflects the most recent
     * change to the given JobRecord.
     *
     * If the given JobRecord is either null or has a null id property, an empty
     * list is returned.
     *
     * @param job
     * @return
     */
    JobChronology getMostRecentChronologyEntry(JobRecord job);

    /**
     * Answer the list of child status codes for the service for the parent code
     *
     * @param service
     * @param parent
     * @return the list of child status codes for the service for the parent
     *         code
     */
    List<ExistentialRecord> getNextStatusCodes(ExistentialRecord service,
                                               ExistentialRecord parent);

    /**
     * Answer the list of parent actions of the job
     *
     * @param job
     * @return the list of parent actions of the job
     */
    List<ParentSequencingAuthorization> getParentActions(JobRecord job);

    /**
     * @param node
     * @return
     */
    List<ParentSequencingAuthorization> getParentActions(ExistentialRecord node);

    /**
     * Answer the list of unique protocols applicable for a job
     *
     * @param job
     * @return the list of unique protocols applicable for a job
     */
    Map<Protocol, InferenceMap> getProtocols(JobRecord job);

    /**
     * Answer the matched list of inferred protocols for a job, given the meta
     * protocol transformation
     *
     * @param job
     * @param metaprotocol
     * @return The list of protocol mappings for a service that are inferred by
     *         the meta protocol. The list is a Tuple of protocols, and a
     *         boolean map indicating which field was inferred in the protocol.
     */
    Map<Protocol, InferenceMap> getProtocols(JobRecord job,
                                             MetaProtocol metaprotocol);

    /**
     * @param service
     * @return The list of protocol mappings for a service.
     */
    List<Protocol> getProtocolsFor(ExistentialRecord service);

    /**
     * @param job
     * @return
     */
    List<SelfSequencingAuthorization> getSelfActions(JobRecord job);

    /**
     * Answer the list of sibling actions for the job
     *
     * @param job
     * @return the list of sibling actions for the job
     */
    List<SiblingSequencingAuthorization> getSiblingActions(JobRecord job);

    /**
     * @param node
     * @return
     */
    List<SiblingSequencingAuthorization> getSiblingActions(ExistentialRecord node);

    /**
     * @param service
     * @return
     */
    List<StatusCodeSequencing> getStatusCodeSequencingsFor(ExistentialRecord service);

    /**
     * Answer the collection of status codes for a service
     *
     * @param service
     * @return the collection of status codes for a service
     */
    Collection<ExistentialRecord> getStatusCodesFor(ExistentialRecord service);

    /**
     * Answer the list of terminal states for the supplied job
     *
     * @param job
     * @return
     */

    List<ExistentialRecord> getTerminalStates(JobRecord job);

    /**
     * @return the list of jobs that have no parent
     */
    List<JobRecord> getTopLevelJobs();

    /**
     * answer the list of jobs with children assigned to an agency
     *
     * @param agency
     * @return
     */
    List<JobRecord> getTopLevelJobsWithSubJobsAssignedToAgency(ExistentialRecord agency);

    /**
     * Answer the list of siblings of a service that have the unset status
     *
     * @param parent
     *            - the parent who children are the siblings
     * @param service
     *            - the service
     * @return the list of siblings of a service that have the unset status
     */
    List<JobRecord> getUnsetSiblings(JobRecord parent,
                                     ExistentialRecord service);

    /**
     * Answer true if the job has active siblings, false otherwise
     *
     * @param job
     * @return true if the job has active siblings, false otherwise
     */
    boolean hasActiveSiblings(JobRecord job);

    /**
     * Answer true if the service has an initial state, false otherwise
     *
     * @param service
     * @return true if the service has an initial state, false otherwise
     */
    boolean hasInitialState(ExistentialRecord service);

    /**
     * Answer true if the service's status graph has terminal strongly connected
     * components
     *
     * @param service
     * @return true if the service's status graph has terminal strongly
     *         connected components
     * @throws SQLException
     */
    boolean hasNonTerminalSCCs(ExistentialRecord service) throws SQLException;

    /**
     * Answer true if the service's status graph has strongly connected
     * components
     *
     * @param service
     * @return true if the service's status graph has strongly connected
     *         components
     */
    boolean hasScs(ExistentialRecord service);

    /**
     * Insert a new job
     *
     * @param parent
     * @param protocol
     * @return
     */
    List<JobRecord> insert(JobRecord parent, Protocol protocol);

    /**
     * @param job
     * @return true if the job is in a non terminal state
     */
    boolean isActive(JobRecord job);

    /**
     * Answer true if the status code is the terminal state for the event
     *
     * @param sc
     * @param service
     * @return true, if the status code is the terminal state for the event,
     *         false otherwise
     */
    boolean isTerminalState(ExistentialRecord sc, ExistentialRecord service);

    /**
     * Log the status change of a job at the timestamp
     *
     * @param job
     * @param notes
     */
    void log(JobRecord job, String notes);

    /**
     * @param service
     * @param updatedBy
     * @return a job in which every field has the appropriate NotApplicable
     *         ruleform. Status is set to UNSET
     */
    JobRecord newInitializedJob(ExistentialRecord service,
                                ExistentialRecord updatedBy);

    /**
     * @param service
     * @param updatedBy
     * @return a metaprotocol in which every unspecified field is initialized to
     *         Same
     */
    MetaProtocol newInitializedMetaProtocol(ExistentialRecord service,
                                            ExistentialRecord updatedBy);

    /**
     * @param service
     * @param updatedBy
     * @return a protocol in which every field has the appropriate NotApplicable
     *         ruleform.
     */
    Protocol newInitializedProtocol(ExistentialRecord service,
                                    ExistentialRecord updatedBy);

    /**
     * Process all the implicit status changes of the children of a job
     *
     * @param job
     */
    void processChildSequencing(JobRecord job);

    /**
     * Process all the implicit status changes of a job
     *
     * @param job
     */
    void processJobSequencing(JobRecord job);

    /**
     * Process all the implicit status changes of the parent of a job
     *
     * @param job
     */
    void processParentSequencing(JobRecord job);

    /**
     * @param job
     */
    void processSelfSequencing(JobRecord job);

    /**
     * Process all the implicit status changes of the siblings of a job
     *
     * @param job
     */
    void processSiblingSequencing(JobRecord job);

    /**
     * Validate that the status graph of the list of services have no loops that
     * can't be escaped into a terminal state
     *
     * @param modifiedProducts
     * @throws SQLException
     */
    void validateStateGraph(Collection<ExistentialRecord> modifiedProducts) throws SQLException;

}