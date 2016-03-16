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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialAttribute;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreInstance;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.java.PhantasmDefinition;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;

/**
 * The meta model for the CoRE
 *
 * @author hhildebrand
 *
 */
public interface Model extends AutoCloseable {

    /**
     * @param phantasm
     * @return
     */
    static Class<?> getExistentialRuleform(Class<?> phantasm) {
        if (!phantasm.isInterface()) {
            throw new IllegalArgumentException(String.format("%s is not an interface",
                                                             phantasm));
        }
        if (!Phantasm.class.isAssignableFrom(phantasm)) {
            throw new IllegalArgumentException(String.format("%s is not a Phantasm",
                                                             phantasm));
        }

        Type type = ((ParameterizedType) phantasm.getGenericInterfaces()[0]).getActualTypeArguments()[0];
        return (Class<?>) type;
    }

    /**
     * Apply the phantasm to the target.
     * 
     * @param phantasm
     * @param target
     * @return
     */
    Phantasm apply(Class<Phantasm> phantasm, Phantasm target);

    /**
     * Answer the cached facet definition for a phantasm class
     * 
     * @param phantasm
     * @return
     */
    PhantasmDefinition<?> cached(Class<? extends Phantasm> phantasm);

    /**
     * Cast the phantasm to another facet
     * 
     * @param targetPhantasm
     * @param ruleform
     * @return
     */
    Phantasm cast(Phantasm source, Class<Phantasm> phantasm);

    @Override
    void close();

    /**
     * Create a new instance of the phantasm's existential ruleform type using
     * the model
     * 
     * @param phantasm
     * @param ruleform
     * @return
     * @throws InstantiationException
     */
    Phantasm construct(Class<Phantasm> phantasm, String name,
                       String description) throws InstantiationException;

    /**
     * Execute the function within in the context of the authenticated
     * principal.
     * 
     * @param principal
     * @param function
     *            -
     * @throws Exception
     */
    <V> V executeAs(AuthorizedPrincipal principal,
                    Callable<V> function) throws Exception;

    /**
     * Find the ruleform instances that match the supplied attribute
     *
     * @param attributeValue
     *            - the attribute value to match
     * @return the collection of ruleform instances that match the attribute
     */
    ExistentialRecord find(ExistentialAttribute attributeValue);

    /**
     * Find an instance using the id
     *
     * @param id
     * @return the instance corresponding to the supplied id, or null if the
     *         instance does not exist
     */
    <RuleForm extends Ruleform> RuleForm find(UUID id,
                                              Class<RuleForm> ruleform);

    /**
     * Find all rows of a ruleform. not a smart thing to do, really. will need
     * to be paged n' probably removed. eventually
     * 
     * @param ruleform
     * @return
     */
    <RuleForm extends Ruleform> List<RuleForm> findAll(Class<RuleForm> ruleform);

    /**
     * Find all the instances of the RuleForm that have been updated by the
     * agency
     *
     * @param updatedBy
     * @param ruleform
     * @return
     */
    <RuleForm extends Ruleform> List<RuleForm> findUpdatedBy(ExistentialRecord updatedBy,
                                                             Class<Ruleform> ruleform);

    /**
     * Flush any caches the workspaces have
     */
    void flushWorkspaces();

    /**
     * @return the Agency model
     */
    ExistentialModel getAgencyModel();

    /**
     * @return the Attribute model
     */
    ExistentialModel getAttributeModel();

    /**
     * @return the agency that represents this instance of the CoRE
     */
    CoreInstance getCoreInstance();

    /**
     * 
     * @return the current thread's authorized principal
     */
    AuthorizedPrincipal getCurrentPrincipal();

    /**
     * Answer the entity manager used for this model instance
     *
     * @return
     */
    EntityManager getEntityManager();

    /**
     * @return the Interval model
     */
    ExistentialModel getIntervalModel();

    /**
     * @return the Job Model
     */
    JobModel getJobModel();

    /**
     * Answer the access model for the kernel rules
     *
     * @return the kernel definition
     */
    Kernel getKernel();

    /**
     * @return the Location model
     */
    ExistentialModel getLocationModel();

    /**
     * @return the Product model
     */
    ExistentialModel getProductModel();

    /**
     * @return the Relationship model
     */
    ExistentialModel getRelationshipModel();

    /**
     * @return the StatusCode model
     */
    StatusCodeModel getStatusCodeModel();

    /**
     * @return the UnitCode model
     */
    ExistentialModel getUnitModel();

    /**
     * @return the UnitCode model
     */
    WorkspaceModel getWorkspaceModel();

    /**
     * Infer networks for the existential ruleform
     * 
     * @param ruleform
     */
    void inferNetworks(ExistentialRecord ruleform);

    /**
     * Lookup the ruleform using the UUID and wrap an instance of a phantasm
     * using the model
     * 
     * @param phantasm
     * @param uuid
     * @return
     */
    Phantasm lookup(Class<Phantasm> phantasm, UUID uuid);

    AuthorizedPrincipal principalFrom(ExistentialRecord principal,
                                      List<UUID> capabilities);

    /**
     * Wrap the ruleform with an instance of a phantasm using the model
     * 
     * @param phantasm
     * @param ruleform
     * @return
     */
    Phantasm wrap(Class<Phantasm> phantasm, Phantasm ruleform);
}
