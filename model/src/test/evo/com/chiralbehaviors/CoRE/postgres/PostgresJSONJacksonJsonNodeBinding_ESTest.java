/*
 * This file was automatically generated by EvoSuite
 * Fri Apr 08 20:17:50 GMT 2016
 */

package com.chiralbehaviors.CoRE.postgres;

import static org.evosuite.runtime.EvoAssertions.assertThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLFeatureNotSupportedException;

import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true)
public class PostgresJSONJacksonJsonNodeBinding_ESTest
        extends PostgresJSONJacksonJsonNodeBinding_ESTest_scaffolding {

    @Test(timeout = 4000)
    public void test0() throws Throwable {
        PostgresJSONJacksonJsonNodeBinding postgresJSONJacksonJsonNodeBinding0 = new PostgresJSONJacksonJsonNodeBinding();
        Converter<Object, JsonNode> converter0 = postgresJSONJacksonJsonNodeBinding0.converter();
        assertNotNull(converter0);
    }

    @Test(timeout = 4000)
    public void test1() throws Throwable {
        PostgresJSONJacksonJsonNodeBinding postgresJSONJacksonJsonNodeBinding0 = new PostgresJSONJacksonJsonNodeBinding();
        try {
            postgresJSONJacksonJsonNodeBinding0.get((BindingGetSQLInputContext<JsonNode>) null);
            fail("Expecting exception: SQLFeatureNotSupportedException");

        } catch (SQLFeatureNotSupportedException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            assertThrownBy("com.chiralbehaviors.CoRE.postgres.PostgresJSONJacksonJsonNodeBinding",
                           e);
        }
    }

    @Test(timeout = 4000)
    public void test2() throws Throwable {
        PostgresJSONJacksonJsonNodeBinding postgresJSONJacksonJsonNodeBinding0 = new PostgresJSONJacksonJsonNodeBinding();
        // Undeclared exception!
        try {
            postgresJSONJacksonJsonNodeBinding0.set((BindingSetStatementContext<JsonNode>) null);
            fail("Expecting exception: NullPointerException");

        } catch (NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            assertThrownBy("com.chiralbehaviors.CoRE.postgres.PostgresJSONJacksonJsonNodeBinding",
                           e);
        }
    }

    @Test(timeout = 4000)
    public void test3() throws Throwable {
        PostgresJSONJacksonJsonNodeBinding postgresJSONJacksonJsonNodeBinding0 = new PostgresJSONJacksonJsonNodeBinding();
        // Undeclared exception!
        try {
            postgresJSONJacksonJsonNodeBinding0.register((BindingRegisterContext<JsonNode>) null);
            fail("Expecting exception: NullPointerException");

        } catch (NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            assertThrownBy("com.chiralbehaviors.CoRE.postgres.PostgresJSONJacksonJsonNodeBinding",
                           e);
        }
    }

    @Test(timeout = 4000)
    public void test4() throws Throwable {
        PostgresJSONJacksonJsonNodeBinding postgresJSONJacksonJsonNodeBinding0 = new PostgresJSONJacksonJsonNodeBinding();
        try {
            postgresJSONJacksonJsonNodeBinding0.set((BindingSetSQLOutputContext<JsonNode>) null);
            fail("Expecting exception: SQLFeatureNotSupportedException");

        } catch (SQLFeatureNotSupportedException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            assertThrownBy("com.chiralbehaviors.CoRE.postgres.PostgresJSONJacksonJsonNodeBinding",
                           e);
        }
    }

    @Test(timeout = 4000)
    public void test5() throws Throwable {
        PostgresJSONJacksonJsonNodeBinding postgresJSONJacksonJsonNodeBinding0 = new PostgresJSONJacksonJsonNodeBinding();
        // Undeclared exception!
        try {
            postgresJSONJacksonJsonNodeBinding0.get((BindingGetResultSetContext<JsonNode>) null);
            fail("Expecting exception: NullPointerException");

        } catch (NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            assertThrownBy("com.chiralbehaviors.CoRE.postgres.PostgresJSONJacksonJsonNodeBinding",
                           e);
        }
    }

    @Test(timeout = 4000)
    public void test6() throws Throwable {
        PostgresJSONJacksonJsonNodeBinding postgresJSONJacksonJsonNodeBinding0 = new PostgresJSONJacksonJsonNodeBinding();
        // Undeclared exception!
        try {
            postgresJSONJacksonJsonNodeBinding0.get((BindingGetStatementContext<JsonNode>) null);
            fail("Expecting exception: NullPointerException");

        } catch (NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            assertThrownBy("com.chiralbehaviors.CoRE.postgres.PostgresJSONJacksonJsonNodeBinding",
                           e);
        }
    }

    @Test(timeout = 4000)
    public void test7() throws Throwable {
        PostgresJSONJacksonJsonNodeBinding postgresJSONJacksonJsonNodeBinding0 = new PostgresJSONJacksonJsonNodeBinding();
        // Undeclared exception!
        try {
            postgresJSONJacksonJsonNodeBinding0.sql((BindingSQLContext<JsonNode>) null);
            fail("Expecting exception: NullPointerException");

        } catch (NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            assertThrownBy("com.chiralbehaviors.CoRE.postgres.PostgresJSONJacksonJsonNodeBinding",
                           e);
        }
    }
}
