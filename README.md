Ultrastructure
==============

An implementation of Ultrastructure using PostgreSQL.  The license for this project is the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

To run this software you will need a working PostgreSQL database version 9.3+.  You can either install from [prebuilt PostgreSQL installations](http://www.enterprisedb.com/products-services-training/pgdownload) or your own prefered method.

Note that the build assumes that the database is available on localhost:5432.  The build also assumes that the super user and database postgres is set up with the default password.  If you have changed any of these values, please override the "testing.db.*" properties in the top level pom.

You can build the project:

    $ cd <project root>
    $ mvn clean install

The default build does do any DB activity, nor tests.  To run tests, you need to activate the profile “database.active”:

    $ mvn -P database.active clean install

If you want to rebuild the database:

    $ mvn clean install -Ddrop -P database.active

You can use pgadmin3 to view "readable" schema views to browse the data.

Note that the build will create the CoRE database.  The schemas are maintained via liquibase (www.liquibase.org)
and will directly manipulate the database, upgrading and downgrading as necessary.  All tests
which load and manipulate data in the database are required to clean up after themselves.

So, word.

To drop the database and start from scratch, simply add "-Ddrop=true" to the full build, or:

    $ cd drop-database
    $ mvn install -Ddrop=true

