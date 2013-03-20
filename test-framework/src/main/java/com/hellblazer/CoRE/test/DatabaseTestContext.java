package com.hellblazer.CoRE.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DatabaseTestContext {

    private static final String MASTER_DUMP_XML = "/com/hellblazer/CoRE/MasterDump.xml";

    private static enum TriggerStatus {
        DISABLE, ENABLE
    }

    public static final String        DTD                  = "/com/hellblazer/CoRE/core.dtd";

    private static final Logger       LOG                  = LoggerFactory.getLogger(DatabaseTestContext.class);

    protected List<DatabaseOperation> beforeTestOperations = new ArrayList<DatabaseOperation>();

    protected IDataSet                dataSet;
    protected EntityManagerFactory    emf;
    protected EntityManager           em;

    /**
     * This should be a location on the classpath. It can be absolute (beginning
     * with a "/") or relative, in which case, it is looked up relative to the
     * current class. This is convenient, because you can put your testing files
     * in the same package as your test classes and simply refer to them by just
     * their name.
     */
    protected String                  dataSetLocation;

    @After
    public void afterTest() throws Exception {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        beginTransaction();

        IDatabaseConnection c = getConnection();

        changeRoleAndDisableConstraints(c.getConnection());

        // DatabaseOperation.DELETE_ALL.execute( c, createDTDDataSet() );

        Reader r = new InputStreamReader(
                                         getInputStreamFromClasspath(MASTER_DUMP_XML));
        IDataSet ds = new FlatXmlDataSet(r);
        try {
            DatabaseOperation.DELETE_ALL.execute(c, ds);
        } catch (BatchUpdateException e) {
            throw e.getNextException();
        }

        revertRoleAndEnableConstraints(c.getConnection());

        commitTransaction();
        closeEntityManager();
    }

    /**
     * Iterates through all the tables of the "ruleform" schema (by querying the
     * information_schema), disabling or enabling all triggers (both user
     * triggers, as well as system [e.g., relational integrity] triggers).
     * 
     * @param c
     *            Connection to the testing database. It is assumed that the
     *            active role for this Connection is the database's super-user
     *            (otherwise an Exception will be thrown when a non-super-user
     *            attempts to disable system triggers)
     * @param status
     *            indicates whether we are disabling or enabling triggers
     * @throws java.lang.Exception
     */
    private void alterTriggerStatus(Connection c, TriggerStatus status)
                                                                       throws Exception {
        ResultSet r = c.createStatement().executeQuery("SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name");
        while (r.next()) {
            String table = r.getString("name");
            String query = "ALTER TABLE " + table + " " + status
                           + " TRIGGER ALL";
            c.createStatement().execute(query);
        }
        r.close();
    }

    /**
     * Prepare the database for the tests.
     * 
     * For ease of use, we temporarily switch database user roles back to the
     * "postgres" super-user to allow us to disable all triggers and foreign key
     * constraints. Since there are a lot of complex trigger and foreign key
     * constraints in our system, it is easier to completely disable these
     * checks before loading test data. The assumption is that said test data
     * has been dumped from a database in a valid state; we just need to get it
     * into our test system. Once the data has been loaded, all disabled
     * triggers and constraints are re-enabled, allowing the actual tests to
     * operate on the database as it would exist in a real-world scenario.
     * 
     * @throws Exception
     */
    @Before
    public void beforeTest() throws Exception {
        initEntityManager();
        prepareDataSet();
        beginTransaction();

        /*
         * We're going to keep a reference to the IDatabaseConnection.
         * Currently the implementation of getConnection() sets the role
         * to "core" so that individual tests don't have to worry about it.
         *
         * We're going to do some explicit connection management to achieve
         * the role switching and trigger disabling / enabling.
         */
        IDatabaseConnection c = getConnection();

        changeRoleAndDisableConstraints(c.getConnection());

        /*
         * Do your operations, using the IDatabaseConnection whose underlying
         * Connection we've been tweaking.
         */
        for (DatabaseOperation op : beforeTestOperations) {
            LOG.trace(String.format("Executing operation %s", op.toString()));
            try {
                op.execute(c, dataSet);
            } catch (BatchUpdateException e) {
                if (e.getNextException() != null) {
                    throw e.getNextException();
                }
                throw e;
            }
        }

        revertRoleAndEnableConstraints(c.getConnection());

        minimizeSequences();
        restartSpecialSequences();

        commitTransaction();
    };

    /**
     * Initiates a database transaction.
     */
    protected void beginTransaction() {
        em.getTransaction().begin();
    }

    /**
     * Sets the {@link Connection}'s active role to <code>postgres</code> and
     * disables all triggers.
     * 
     * @param c
     *            Connection to the testing database. Assumes that the initial
     *            connection to the database was made as a super-user, and not
     *            an unprivileged user account.
     * @throws java.lang.Exception
     */
    private final void changeRoleAndDisableConstraints(Connection c)
                                                                    throws Exception {
        c.createStatement().execute("SET SESSION ROLE postgres");
        c.createStatement().execute("SET search_path = ruleform,public");
        alterTriggerStatus(c, TriggerStatus.DISABLE);
    }

    /**
     * Commits the current transaction, if it is still active.
     */
    protected final void commitTransaction() {
        em.getTransaction().commit();
    }

    /**
     * Create an IDataSet that is validated using our DTD.
     * 
     * @param classpathLocation
     *            classpath location of a DBUnit dataset file.
     * @return a dataset with the data from the given file, validated against
     *         our core DTD file.
     * @throws Exception
     */
    protected IDataSet createDataSetWithDTD(String classpathLocation)
                                                                     throws Exception {
        Reader r = new InputStreamReader(
                                         getInputStreamFromClasspath(classpathLocation));

        // This is the DTD
        IDataSet dtd = createDTDDataSet();

        // Squish them together...
        IDataSet dataSet = new FlatXmlDataSet(r, dtd);

        return dataSet;
    }

    private void initEntityManager() throws Exception {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/jpa.properties"));
        emf = Persistence.createEntityManagerFactory("CoRE", properties);
        em = emf.createEntityManager();

    }

    private void closeEntityManager() throws SQLException {
        em.close();
        emf.close();
    }

    /**
     * Creates a dataset based on our default core DTD. This can be used to
     * validate datasets.
     * 
     * @return
     * @throws Exception
     */
    private IDataSet createDTDDataSet() throws Exception {
        Reader r = new InputStreamReader(getInputStreamFromClasspath(DTD));
        IDataSet dtd = new FlatDtdDataSet(r);
        return dtd;
    }

    /**
     * Convenience method for directly executing raw SQL in the database.
     * 
     * @param sql
     *            A valid SQL string (sans trailing semi-colon). It is executed
     *            directly, so if it's malformed and hoses your database, it's
     *            your own fault.
     * @throws Exception
     */
    private void execute(String sql) throws Exception {
        getConnection().getConnection().createStatement().execute(sql);
    }

    /**
     * Wraps a JDBC database connection for use by DBUnit. Enables the use of
     * schema-qualified table names by DBUnit and the use of a custom
     * {@link IDataTypeFactory}.
     * 
     * Since we have a lot of triggers and constraints that make loading and
     * clearing the testing database problematic, this class assumes that tests
     * are being run as a database super-user. Inside this method, the "normal"
     * database user role is set; the writer of database tests need not worry
     * about it. We do this so that we may switch back to the super-user role
     * when necessary (in {@link #beforeTest()} and {@link #afterTest()} in
     * particular).
     * 
     * @return IDatabaseConnection
     * @throws Exception
     */
    protected final IDatabaseConnection getConnection() throws Exception {
        IDatabaseConnection db = new DatabaseConnection(
                                                        em.unwrap(java.sql.Connection.class));

        db.getConnection().createStatement().execute("SET SESSION ROLE core");
        db.getConnection().createStatement().execute("SET SESSION search_path = ruleform, public");

        // Tweak some configuration settings on the connection
        DatabaseConfig configuration = db.getConfig();

        // Enable the use of schema-qualified table names
        configuration.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES,
                                 true);

        // Enable the use of batched JDBC statements for faster tests.
        configuration.setFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS,
                                 true);

        // We want to be able to query both tables and views
        configuration.setProperty(DatabaseConfig.PROPERTY_TABLE_TYPE,
                                  new String[] { "TABLE", "VIEW" });

        // Use our own data type factory
        configuration.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                                  new DataTypeFactory());

        return db;
    }

    protected EntityManager getEntityManager() throws IOException {
        return em;
    }

    protected InputStream getInputStreamFromClasspath(String classpathLocation)
                                                                               throws URISyntaxException {
        InputStream is = this.getClass().getResourceAsStream(classpathLocation);
        if (is == null) {
            throw new IllegalStateException(
                                            String.format("Unable to locate %s relative to class %s",
                                                          classpathLocation,
                                                          getClass()));
        }
        return is;
    }

    /**
     * Sets the identifier sequences for all tables to the next permissible
     * number, given the contents of the related table. For instance, if the
     * Entity table has a record with ID = <code>250</code>, and
     * <code>250</code> is the highest ID in the table, then this method will
     * set the sequence backing the ID column to return <code>251</code> on its
     * next incrementing.
     * 
     * This method calls a custom-written stored procedure within the database.
     * 
     * @throws SQLException
     * @throws Exception
     */
    protected final void minimizeSequences() throws SQLException, Exception {
        execute("SELECT core_admin.minimize_sequences()");
    }

    /**
     * Ensures that the DataSet for this test is set up properly
     * 
     * @throws Exception
     */
    private void prepareDataSet() throws Exception {
        /*
         * We need to explicitly begin and commit transactions in all methods that interact with the
         * database.
         */
        beginTransaction();

        prepareSettings();

        if (dataSetLocation == null) {
            throw new RuntimeException(
                                       "Please set a dataSetLocation for this class");
        }

        dataSet = createDataSetWithDTD(dataSetLocation);

        setSequences();

        commitTransaction();
    }

    /**
     * Each subclass must override this to set the specific settings for that
     * class' methods.
     */
    protected abstract void prepareSettings();

    /**
     * <p>
     * Convenience method for restarting a database sequence object at "1". The
     * next call to the sequence will return "1".
     * </p>
     * <p>
     * <em>Note</em>: The implementation of this method assumes the underlying
     * database is PostgreSQL.
     * </p>
     * 
     * @param sequenceName
     *            the name of the sequence object to be restarted. Can be
     *            schema-qualified.
     * @throws SQLException
     * @throws Exception
     */
    protected void restartSequence(String sequenceName) throws SQLException,
                                                       Exception {
        setSequence(sequenceName, 1L, false);
    }

    /**
     * Sets all sequences in the <code>sequences</code> schema back to 1.
     * 
     * @throws SQLException
     * @throws Exception
     */
    protected final void restartSpecialSequences() throws SQLException,
                                                  Exception {
        execute("SELECT core_admin.reset_special_sequences()");
    }

    /**
     * Sets the {@link Connection}'s active role to <code>core</code> and
     * enables all triggers.
     * 
     * @param c
     *            Connection to the testing database.
     * @throws java.lang.Exception
     */
    private final void revertRoleAndEnableConstraints(Connection c)
                                                                   throws Exception {
        alterTriggerStatus(c, TriggerStatus.ENABLE);
        c.createStatement().execute("SET ROLE core");
    }

    /**
     * Internal method to facilitate the resetting of PostgreSQL database
     * sequence objects.
     * 
     * @param sequenceName
     *            the name of the sequence to be set. Can be schema-qualified.
     * @param number
     *            the number to set the sequence to
     * @param isCalled
     *            indicates whether or not <code>number</code> has been issued
     *            by the sequence yet. If this is <code>false</code>, then the
     *            next call to the sequence object will return
     *            <code>number</code>. If it is <code>true</code>, the next call
     *            will return <code>number</code> + 1.
     * @throws SQLException
     * @throws Exception
     */
    private void setSequence(String sequenceName, long number, boolean isCalled)
                                                                                throws SQLException,
                                                                                Exception {
        execute("SELECT setval('" + sequenceName + "', " + number + ", "
                + isCalled + ")");
    }

    /**
     * Each subclass can override this to ensure that sequence objects are set
     * properly. If it is not overridden, it does nothing.
     * 
     * @throws Exception
     */
    protected void setSequences() throws Exception {
    }

    /**
     * <p>
     * Convenience method for restarting a database sequence object at an
     * arbitrary number. The next call to the sequence will return
     * <code>number</code> + 1.
     * </p>
     * <p>
     * <em>Note</em>: The implementation of this method assumes the underlying
     * database is PostgreSQL.
     * </p>
     * 
     * @param sequenceName
     *            the name of the sequence object to be restarted. Can be
     *            schema-qualified.
     * @param number
     *            the last number the restarted sequence will have given.
     * @throws SQLException
     * @throws Exception
     */
    protected void setSequenceWithLastCalled(String sequenceName, long number)
                                                                              throws SQLException,
                                                                              Exception {
        setSequence(sequenceName, number, true);
    }

    /**
     * Validates an actual DBUnit dataset against an expected dataset, using the
     * tables whose names are specified by <code>tableNames</code>. The test
     * passes if all the tables denoted by <code>tableNames</code> are the same
     * in both datasets (modulo any differences filtered out by the methods of
     * the <code>DBUnitSupport</code> class, which are used internally).
     * 
     * @param expectedDataSet
     *            a representation of the expected state of the database at the
     *            end of a test
     * @param actualDataSet
     *            a representation of the actual state of the database at the
     *            end of a test
     * @param tableNames
     *            a list of names of tables or views that exist in both
     *            <code>expectedDataSet</code> and <code>actualDataSet</code>
     *            which should be compared.
     * @throws DatabaseUnitException
     */
    private void validate(IDataSet expectedDataSet, IDataSet actualDataSet,
                          String[] tableNames) throws DatabaseUnitException {
        for (String name : tableNames) {
            ITable actual = actualDataSet.getTable(name);
            ITable expected = expectedDataSet.getTable(name);

            Assertion.assertEquals(DBUnitSupport.processTable(expected),
                                   DBUnitSupport.processTable(actual));
        }
    }

    /**
     * <p>
     * Convenience method for validating a DBUnit result set. Compares all
     * tables denoted by <code>tableNames</code> from the DBUnit dataset given
     * by <code>fileLocation</code> to those contained in the database following
     * a test.
     * </p>
     * 
     * @param classpathLocation
     *            the classpath location of the DBUnit dataset file to compare
     *            against the database contents following a test
     * @param tableNames
     *            a list of names of tables or views that exist in both the
     *            DBUnit dataset and the database which should be compared.
     * @throws Exception
     */
    protected void validate(String classpathLocation, String[] tableNames)
                                                                          throws Exception {
        IDataSet actualDataSet = getConnection().createDataSet();
        IDataSet expectedDataSet = createDataSetWithDTD(classpathLocation);

        this.validate(expectedDataSet, actualDataSet, tableNames);
    }

}
