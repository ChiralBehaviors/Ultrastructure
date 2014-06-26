/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.meta;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.product.Product;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 * 
 */
public interface JobModel {
    static InferenceMap NO_TRANSFORMATION = new InferenceMap(false, false,
                                                             false, false,
                                                             false, false,
                                                             false, false,
                                                             false, false,
                                                             false, false);

    /**
     * Generate all the implicit sub jobs for the job
     * 
     * @param job
     * @param updatedBy
     */
    void generateImplicitJobsForExplicitJobs(Job job,
                                                          Agency updatedBy);

    /**
     * Sets the status of the given Job. This should not be done directly on the
     * job itself because we log the change in the JobChronology ruleform.
     * 
     * @param job
     * @param newStatus
     * @param message
     *            an optional message about why the status was changed, the
     *            circumstances surrounding the change, etc.
     * @return the merged job
     */
    Job changeStatus(Job job, StatusCode newStatus, Agency updagedBy,
                     String notes);

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
    void createStatusCodeChain(Product service, StatusCode[] codes,
                               Agency updatedBy);

    /**
     * @param service
     * @param codes
     * @param updatedBy
     */
    void createStatusCodeSequencings(Product service,
                                     List<Tuple<StatusCode, StatusCode>> codes,
                                     Agency updatedBy);

    /**
     * Ensure that the nextStatus is a valid status transition from the
     * currentStatus for the service
     * 
     * @param job
     * @param service
     * @param currentStatus
     * @param nextStatus
     * @throws SQLException
     */
    void ensureNextStateIsValid(Job job, Product service,
                                StatusCode currentStatus, StatusCode nextStatus)
                                                                                throws SQLException;

    /**
     * @param parent
     * @throws SQLException
     */
    void ensureValidParentStatus(Job parent) throws SQLException;

    /**
     * 
     * @param nextSibling
     * @param nextSiblingStatus
     * @throws SQLException
     */
    void ensureValidServiceAndStatus(Product nextSibling,
                                     StatusCode nextSiblingStatus)
                                                                  throws SQLException;

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
    List<Job> generateImplicitJobs(Job job, Agency updatedBy);

    /**
     * Retrieve a list of all currently active "explicit" (top level) Jobs.
     * "Explicit" means a Job that has no parent Job. "Active" means Jobs whose
     * current state is neither "(UNSET)"/pending nor a terminal state for the
     * Job's Product.
     * 
     * @return the list of all active, top level jobs
     */
    List<Job> getActiveExplicitJobs();

    /**
     * Answer the list of active jobs that are assigned to a agency
     * 
     * @param agency
     * @return the list of active jobs assigned to the agency
     */
    List<Job> getActiveJobsFor(Agency agency);

    /**
     * 
     * @param job
     * @param service
     * @return
     */
    List<Job> getActiveSubJobsForService(Job job, Product service);

    /**
     * Answer the list of active sub jobs (children) of the job
     * 
     * @param job
     * @return the list of active sub jobs of the job
     */
    List<Job> getActiveSubJobsOf(Job job);

    /**
     * Answer the recursive list of all sub jobs - at any level - of a job that
     * are active or terminated
     * 
     * @param job
     * @return the full list of all sub jobs of a job that are active or
     *         terminated
     */
    Collection<Job> getAllActiveOrTerminatedSubJobsOf(Job job);

    /**
     * Answer the recursive list of all sub jobs - at any level - of a job that
     * are active
     * 
     * @param job
     * @return the recursive list of all sub jobs - at any level - of a job that
     *         are active
     */
    Collection<Job> getAllActiveSubJobsOf(Job job);

    /**
     * 
     * @param parent
     * @param agency
     * @return
     */
    List<Job> getAllActiveSubJobsOf(Job parent, Agency agency);

    /**
     * 
     * @param parent
     * @param agency
     * @param jobs
     */
    void getAllActiveSubJobsOf(Job parent, Agency agency, List<Job> jobs);

    /**
     * Answer the list of all active subjobs
     * 
     * @param job
     * @param tally
     * @return
     */
    Collection<Job> getAllActiveSubJobsOf(Job job, Collection<Job> tally);

    /**
     * Get all direct and indirect child jobs of this job, regardless of status
     * 
     * @param job
     * @return
     */
    List<Job> getAllChildren(Job job);

    /**
     * Answer the list of sequencing authorizations that have the job's service
     * as parent
     * 
     * @param job
     * @return the list of sequencing authorizations that have the job's service
     *         as parent
     */
    List<ProductChildSequencingAuthorization> getChildActions(Job job);

    /**
     * @param node
     * @return
     */
    List<ProductChildSequencingAuthorization> getChildActions(Product node);

    /**
     * Gets all immediate children of the parent job having the specified
     * service. Does not differentiate between unset, active, or terminated jobs
     * 
     * @param parent
     * @param service
     * @return
     */
    List<Job> getChildJobsByService(Job parent, Product service);

    /**
     * Returns an ordered list of all JobChronology rules for the given job.
     * Entries are ordered by the ascending timeStamp (oldest is first, most
     * recent is last)
     * 
     * If the given Job is either null or has a null id property, an empty list
     * is returned.
     * 
     * @param job
     * @return
     */
    List<JobChronology> getChronologyForJob(Job job);

    /**
     * Answer the immediate child jobs of the job that are active or terminal
     * 
     * @param job
     * @return the immediate child jobs of the job that are active or terminal
     */
    List<Job> getDirectActiveOrTerminalSubJobsOf(Job job);

    /**
     * Answer the initial state of a service
     * 
     * @param service
     * @return the initial state of a service
     */
    StatusCode getInitialState(Product service);

    /**
     * Returns a list of initially available sub-jobs (i.e., ones that do not
     * depend on any others having been completed yet) of a given job
     * 
     * @param job
     * @return
     */
    List<Job> getInitialSubJobs(Job job);

    /**
     * Answer the list of MetaProtocols that can be applied to the job
     * 
     * @param job
     * @return the list of MetaProtocols that can be applied to the job
     */
    List<MetaProtocol> getMetaprotocols(Job job);

    /**
     * @param service
     * @return
     */
    List<MetaProtocol> getMetaProtocolsFor(Product service);

    /**
     * Returns the individual JobChronology rule that reflects the most recent
     * change to the given Job.
     * 
     * If the given Job is either null or has a null id property, an empty list
     * is returned.
     * 
     * @param job
     * @return
     */
    JobChronology getMostRecentChronologyEntry(Job job);

    /**
     * Answer the list of child status codes for the service for the parent code
     * 
     * @param service
     * @param parent
     * @return the list of child status codes for the service for the parent
     *         code
     */
    List<StatusCode> getNextStatusCodes(Product service, StatusCode parent);

    /**
     * Answer the list of parent actions of the job
     * 
     * @param job
     * @return the list of parent actions of the job
     */
    List<ProductParentSequencingAuthorization> getParentActions(Job job);

    /**
     * @param node
     * @return
     */
    List<ProductParentSequencingAuthorization> getParentActions(Product node);

    /**
     * Answer the list of unique protocols applicable for a job
     * 
     * @param job
     * @return the list of unique protocols applicable for a job
     */
    Map<Protocol, InferenceMap> getProtocols(Job job);

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
    Map<Protocol, InferenceMap> getProtocols(Job job, MetaProtocol metaprotocol);

    /**
     * @param service
     * @return The list of protocol mappings for a service.
     */
    List<Protocol> getProtocolsFor(Product service);

    /**
     * Answer the list of sibling actions for the job
     * 
     * @param job
     * @return the list of sibling actions for the job
     */
    List<ProductSiblingSequencingAuthorization> getSiblingActions(Job job);

    /**
     * @param node
     * @return
     */
    List<ProductSiblingSequencingAuthorization> getSiblingActions(Product node);

    /**
     * @param service
     * @return
     */
    List<StatusCodeSequencing> getStatusCodeSequencingsFor(Product service);

    /**
     * Answer the collection of status codes for a service
     * 
     * @param service
     * @return the collection of status codes for a service
     */
    Collection<StatusCode> getStatusCodesFor(Product service);

    /**
     * Answer the list of terminal states for the supplied job
     * 
     * @param job
     * @return
     */

    List<StatusCode> getTerminalStates(Job job);

    /**
     * @return the list of jobs that have no parent
     */
    List<Job> getTopLevelJobs();

    /**
     * answer the list of jobs with children assigned to an agency
     * 
     * @param agency
     * @return
     */
    List<Job> getTopLevelJobsWithSubJobsAssignedToAgency(Agency agency);

    /**
     * Answer the list of siblings of a service that have the unset status
     * 
     * @param parent
     *            - the parent who children are the siblings
     * @param service
     *            - the service
     * @return the list of siblings of a service that have the unset status
     */
    List<Job> getUnsetSiblings(Job parent, Product service);

    /**
     * Answer true if the job has active siblings, false otherwise
     * 
     * @param job
     * @return true if the job has active siblings, false otherwise
     */
    boolean hasActiveSiblings(Job job);

    /**
     * Answer true if the service has an initial state, false otherwise
     * 
     * @param service
     * @return true if the service has an initial state, false otherwise
     */
    boolean hasInitialState(Product service);

    /**
     * Answer true if the service's status graph has strongly connected
     * components
     * 
     * @param service
     * @return true if the service's status graph has strongly connected
     *         components
     */
    boolean hasScs(Product service);

    /**
     * Answer true if the service's status graph has terminal strongly connected
     * components
     * 
     * @param service
     * @return true if the service's status graph has terminal strongly
     *         connected components
     * @throws SQLException
     */
    boolean hasTerminalSCCs(Product service) throws SQLException;

    /**
     * Insert a new job
     * 
     * @param parent
     * @param protocol
     * @return
     */
    Job insert(Job parent, Protocol protocol);

    /**
     * @param job
     * @return true if the job is in a non terminal state
     */
    boolean isActive(Job job);

    /**
     * Answer true if the status code is the terminal state for the event
     * 
     * @param sc
     * @param service
     * @return true, if the status code is the terminal state for the event,
     *         false otherwise
     */
    boolean isTerminalState(StatusCode sc, Product service);

    /**
     * Answer true if the next status code is a valid status transition of the
     * service given the parent
     * 
     * @param service
     * @param parent
     * @param next
     * @return true if the next status code is a valid status transition of the
     *         service given the
     */
    boolean isValidNextStatus(Product service, StatusCode parent,
                              StatusCode next);

    /**
     * Log the status change of a job at the timestamp
     * 
     * @param job
     * @param notes
     */
    void log(Job job, String notes);

    void logModifiedService(UUID scs);

    /**
     * @param service
     * @param updatedBy
     * @return a job in which every field has the appropriate NotApplicable
     *         ruleform. Status is set to UNSET
     */
    Job newInitializedJob(Product service, Agency updatedBy);

    /**
     * @param service
     * @param updatedBy
     * @return a metaprotocol in which every unspecified field is initialized to
     *         Same
     */
    MetaProtocol newInitializedMetaProtocol(Product service, Agency updatedBy);

    /**
     * @param service
     * @param updatedBy
     * @return a protocol in which every field has the appropriate NotApplicable
     *         ruleform.
     */
    Protocol newInitializedProtocol(Product service, Agency updatedBy);

    /**
     * Process all the implicit status changes of the children of a job
     * 
     * @param job
     */
    void processChildSequencing(Job job);

    /**
     * Process all the implicit status changes of a job
     * 
     * @param job
     */
    void processJobSequencing(Job job);

    /**
     * Process all the implicit status changes of the parent of a job
     * 
     * @param job
     */
    void processParentSequencing(Job job);

    /**
     * Process all the implicit status changes of the siblings of a job
     * 
     * @param job
     */
    void processSiblingSequencing(Job job);

    /**
     * Validate that the status graph of the list of services have no loops that
     * can't be escaped into a terminal state
     * 
     * @param modifiedProducts
     * @throws SQLException
     */
    void validateStateGraph(List<Product> modifiedProducts) throws SQLException;

}