Ultrastructure
==============

An implementation of Ultrastructure using PostgreSQL.

See the [Ultrastructure Northwind Demo](http://chiralbehaviors.github.io/Northwind/) for an example of a non trivial Ultrastructure application.

Build Status: [![Build Status](https://chiralbehaviors.ci.cloudbees.com/buildStatus/job/Ultrastructure/badge/icon)](https://chiralbehaviors.ci.cloudbees.com/job/Ultrastructure/)

The license for this project is the [GNU Affero General Public License](http://www.gnu.org/licenses/agpl-3.0.en.html)

To run this software you will need a working PostgreSQL database version 9.3+.  You can either install from [prebuilt PostgreSQL installations](http://www.enterprisedb.com/products-services-training/pgdownload) or your own prefered method.

Note that the build assumes that the database is available on localhost:5432.  The build also assumes that the super user and database _postgres_ is set up with the default password (_postgres_).  If you have changed any of these values, please override the "testing.db.*" properties in the top level pom.

The project requires [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).  The build system is [Maven](http://maven.apache.org/).  In order to build, you will need both Maven and Java 1.8 installed.  Beyond Java, Maven and Postgres, nothing else should be required.

You can build the project:

    $ cd <project root>
    $ mvn clean install

The default build does not perform any DB activity, nor tests.  To run tests, you need to activate the profile “database.active”:

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


