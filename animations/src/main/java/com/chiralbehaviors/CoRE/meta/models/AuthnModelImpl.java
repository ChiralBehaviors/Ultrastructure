/**
 * Copyright (c) 2018 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.jooq.Tables.AUTHENTICATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.TOKEN;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.jooq.tables.records.AuthenticationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.TokenRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreUser;
import com.chiralbehaviors.CoRE.meta.AuthnModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.bcrypt.BCrypt;

/**
 * @author halhildebrand
 *
 */
public class AuthnModelImpl implements AuthnModel {

    private final Model model;

    public AuthnModelImpl(Model model) {
        this.model = model;
    }

    @Override
    public boolean authenticate(CoreUser user, char[] password) {
        String passwordHash = model.create()
                                   .select(AUTHENTICATION.PASSWORD_HASH)
                                   .from(AUTHENTICATION)
                                   .where(AUTHENTICATION.AGENCY.equal(user.getRuleform()
                                                                          .getId()))
                                   .fetchOne()
                                   .component1();
        return BCrypt.checkpw(password.toString(), passwordHash);
    }

    @Override
    public TokenRecord authenticate(CoreUser user, UUID token, String ip) {
        TokenRecord record = model.create()
                                  .selectFrom(TOKEN)
                                  .where(TOKEN.ID.eq(token))
                                  .fetchOne();
        if (record == null) {
            LoggerFactory.getLogger(AuthnModelImpl.class)
                         .info("Expired or invalid token authenticating core user {} token {} from {}",
                               user.getRuleform()
                                   .getId(),
                               token, ip);
            return null;
        }
        if (!record.getIp()
                   .equals(ip)) {
            LoggerFactory.getLogger(AuthnModelImpl.class)
                         .info("Invalid IP authenticating core user {} token {} from {}",
                               user.getRuleform()
                                   .getId(),
                               token, ip);
            return null;
        }
        if (record.getCreated()
                  .plusSeconds(record.getTtl())
                  .compareTo(OffsetDateTime.now()) >= 0) {
            LoggerFactory.getLogger(AuthnModelImpl.class)
                         .info("Token timeout authenticating core user {} token {} from {}",
                               user.getRuleform()
                                   .getId(),
                               token, ip);
            record.delete();
            return null;
        }
        return record;
    }

    @Override
    public boolean changePassword(CoreUser user, char[] oldPassword,
                                  char[] newPassword) {
        if (!authenticate(user, oldPassword)) {
            return false;
        }
        AuthenticationRecord authentication = model.create()
                                                   .selectFrom(AUTHENTICATION)
                                                   .where(AUTHENTICATION.AGENCY.equal(user.getRuleform()
                                                                                          .getId()))
                                                   .fetchOne();
        authentication.setPasswordHash(BCrypt.hashpw(newPassword.toString(),
                                                     BCrypt.gensalt(authentication.getPasswordRounds())));
        model.create()
             .executeUpdate(authentication);

        model.create()
             .deleteFrom(TOKEN)
             .where(TOKEN.AGENCY.eq(user.getRuleform()
                                        .getId()))
             .execute();
        return true;
    }

    @Override
    public boolean create(CoreUser user, char[] password) {
        AuthenticationRecord authn = new AuthenticationRecord();
        authn.setAgency(user.getRuleform()
                            .getId());
        authn.setPasswordRounds(model.getCoreInstance()
                                     .get_Properties()
                                     .getPasswordRounds());
        authn.setPasswordHash(BCrypt.hashpw(password.toString(),
                                            BCrypt.gensalt(authn.getPasswordRounds())));
        try {
            model.create()
                 .executeInsert(authn);
        } catch (Exception e) {
            LoggerFactory.getLogger(AuthnModelImpl.class)
                         .info("Error creating core user", e);
            return false;
        }
        return true;
    }

    @Override
    public TokenRecord mintToken(CoreUser user, String ip, int ttlSeconds,
                                 UUID nonce, UUID[] roles) {
        TokenRecord token = new TokenRecord(RecordsFactory.GENERATOR.generate(),
                                            user.getRuleform()
                                                .getId(),
                                            nonce, roles, ttlSeconds, ip,
                                            OffsetDateTime.now());
        return token;
    }

}
