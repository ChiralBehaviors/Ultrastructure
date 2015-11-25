Ultrastructure
==============

An implementation of Ultrastructure using PostgreSQL.

==============

[![Build Status](https://chiralbehaviors.ci.cloudbees.com/view/Maintained%20Open%20Source/job/Ultrastructure/badge/icon)](https://chiralbehaviors.ci.cloudbees.com/view/Maintained%20Open%20Source/job/Ultrastructure/)

- [Ultrastrsucture Wiki](https://github.com/ChiralBehaviors/Ultrastructure/wiki) - for moar information

- [Ultrastructure Northwind Demo](http://chiralbehaviors.github.io/Northwind/) - an example of a non trivial Ultrastructure application.

The license for this project is the [GNU Affero General Public License](http://www.gnu.org/licenses/agpl-3.0.en.html)

The project requires [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).  The build system 
is [Maven](http://maven.apache.org/).  In order to build, you will need both Maven and Java 1.8 installed.  Beyond Java, 
Maven and Postgres, nothing else should be required.

To run this software you will need a working, accessable, PostgreSQL database version 9.3+.  You can either install 
from [prebuilt PostgreSQL installations](http://www.enterprisedb.com/products-services-training/pgdownload) or your own prefered method.  If you 
are running on a Mac, try the [Postgres.app](http://postgresapp.com/) as it's butt simple to setup and use.  You'll love it.

Note that the build assumes that the postgres database is available on localhost:5432.  The build also assumes that the super 
user and database _postgres_ is set up with the default password (_postgres_).  If you have changed 
any of these values, please override the _"dba.db.*"_ properties in the top level pom.xml (see [~/.m2/settings.xml](https://maven.apache.org/settings.html)).
  _If you have installed Postgres using the Heroku [Postgres.app](http://postgresapp.com/)_ please note 
that the default port is _5433_, so you'll have to be sure to set the _dba.db.port_
 value in your ~/.m2/settings.xml or edit the top level pom.xml (as it defines this value to be 5432).  You'll be wondering
 why you cannot connect if you don't.

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
which load and manipulate data in the database are required to ensure their database state is
set up correctly.  See [AbstractModelTest](https://github.com/ChiralBehaviors/Ultrastructure/blob/master/animations/src/test/java/com/chiralbehaviors/CoRE/meta/models/AbstractModelTest.java)
for how to subclass this test to ensure that the database state is sweet and clean for your testing (i.e. you merely
subclass it and write your tests using the inherited model/em state)

So, word.

To drop the database and start from scratch, simply add "-Ddrop" to the full build, or to just drop:

    $ cd drop-database
    $ mvn install -Ddrop

To create the database from scratch:

  $ cd drop-database  
  $ mvn install -P sudo-drop-me  
  
Then open PGAdmin and make sure that all your databases are belong to us.  I mean, dropped.  Don't drop postgres, or you'll be very sorry and have to return to go, and not collect 200 dollars.
Also, too, drop your login role other than your postgres or whatever you're using as your master superuser role

