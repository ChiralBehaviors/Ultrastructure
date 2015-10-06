/**
 * Generated Phantasm
 */
package com.chiralbehaviors.CoRE.kernel.phantasm.agency;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.phantasm.ScopedPhantasm;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Facet;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.PrimitiveState;

@Facet(classifier = @Key(name = "IsA") , classification = @Key(name = "CoreUser") , ruleformClass = Agency.class, workspace = "urn:uuid:00000000-0000-0004-0000-000000000003")
public interface CoreUser extends ScopedPhantasm<Agency> {

    @PrimitiveState(fieldName = "login", attribute = @Key(name = "Login") )
    String getLogin();

    @PrimitiveState(fieldName = "passwordRounds", attribute = @Key(name = "PasswordRounds") )
    Integer getPasswordRounds();

    @PrimitiveState(fieldName = "passwordHash", attribute = @Key(name = "PasswordHash") )
    String getPasswordHash();

    @PrimitiveState(fieldName = "accessToken", attribute = @Key(name = "AccessToken") )
    Object[] getAccessToken();

    @PrimitiveState(fieldName = "login", attribute = @Key(name = "Login") )
    void setLogin(String login);

    @PrimitiveState(fieldName = "passwordRounds", attribute = @Key(name = "PasswordRounds") )
    void setPasswordRounds(Integer passwordRounds);

    @PrimitiveState(fieldName = "passwordHash", attribute = @Key(name = "PasswordHash") )
    void setPasswordHash(String passwordHash);

    @PrimitiveState(fieldName = "accessToken", attribute = @Key(name = "AccessToken") )
    void setAccessToken(Object[] accessToken);

}