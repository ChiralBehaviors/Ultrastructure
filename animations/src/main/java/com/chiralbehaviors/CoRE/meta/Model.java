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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreInstance;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.java.PhantasmDefinition;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * The meta model for the CoRE
 *
 * @author hhildebrand
 *
 */
public interface Model extends AutoCloseable {

    /**
     * Apply the phantasm to the target.
     *
     * @param phantasm
     * @param target
     * @return
     */
    <T extends ExistentialRuleform, R extends Phantasm> R apply(Class<R> phantasm,
                                                                Phantasm target);

    /**
     * Answer the cached facet definition for a phantasm class
     *
     * @param phantasm
     * @return
     */
    PhantasmDefinition cached(Class<? extends Phantasm> phantasm);

    /**
     * Cast the phantasm to another facet
     *
     * @param targetPhantasm
     * @param ruleform
     * @return
     */
    <T extends ExistentialRuleform, R extends Phantasm> R cast(T source,
                                                               Class<R> phantasm);

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
    <T extends ExistentialRuleform, R extends Phantasm> R construct(Class<R> phantasm,
                                                                    ExistentialDomain domain,
                                                                    String name,
                                                                    String description) throws InstantiationException;

    /**
     * Answer the entity manager used for this model instance
     *
     * @return
     */
    DSLContext create();

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

    void flush();

    /**
     * Flush any caches the workspaces have
     */
    void flushWorkspaces();

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
     * @return the Job Model
     */
    JobModel getJobModel();

    /**
     * Answer the access model for the kernel rules
     *
     * @return the kernel definition
     */
    Kernel getKernel();

    PhantasmModel getPhantasmModel();

    /**
     * @return the UnitCode model
     */
    WorkspaceModel getWorkspaceModel();

    void inferNetworks();

    AuthorizedPrincipal principalFrom(Agency agency, List<Agency> list);

    AuthorizedPrincipal principalFromIds(Agency agency,
                                         List<UUID> roles);

    RecordsFactory records();

    WorkspaceSnapshot snapshot();

    /**
     * Wrap the ruleform with an instance of a phantasm using the model
     *
     * @param phantasm
     * @param ruleform
     * @return
     */
    <T extends ExistentialRuleform, R extends Phantasm> R wrap(Class<R> phantasm,
                                                               ExistentialRuleform ruleform);
}
