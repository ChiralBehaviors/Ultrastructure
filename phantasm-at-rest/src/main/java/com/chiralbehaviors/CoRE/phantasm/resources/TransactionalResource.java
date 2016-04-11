/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.phantasm.resources;

import java.util.function.Function;

import javax.ws.rs.WebApplicationException;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;

/**
 * @author hhildebrand
 */
public class TransactionalResource {
    private final static Logger log = LoggerFactory.getLogger(TransactionalResource.class);

    protected <T> T mutate(AuthorizedPrincipal principal,
                           Function<Model, T> txn,
                           DSLContext create) throws WebApplicationException {
        Model model = new ModelImpl(create);
        if (principal == null) {
            principal = new AuthorizedPrincipal(model.getKernel()
                                                     .getUnauthenticatedAgency());
        }
        try {
            return model.executeAs(principal, () -> {
                try {
                    return create.transactionResult(c -> {
                        return txn.apply(model);
                    });
                } catch (WebApplicationException e) {
                    throw e;
                } catch (Throwable e) {
                    log.error("error applying transaction", e);
                    throw new WebApplicationException(e, 500);
                }
            });
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("error applying transaction", e);
            throw new WebApplicationException(e, 500);
        }
    }
}