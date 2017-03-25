Ultrastructure
==============

An implementation of Ultrastructure 


See the [Ultrastrsucture Wiki](https://github.com/ChiralBehaviors/Ultrastructure/wiki) for moar information

==============

[![Build Status](https://chiralbehaviors.ci.cloudbees.com/view/Maintained%20Open%20Source/job/Ultrastructure/badge/icon)](https://chiralbehaviors.ci.cloudbees.com/view/Maintained%20Open%20Source/job/Ultrastructure/)[![Coverage Status](https://coveralls.io/repos/github/ChiralBehaviors/Ultrastructure/badge.svg)](https://coveralls.io/github/ChiralBehaviors/Ultrastructure)

The license for this project is the [GNU Affero General Public License](http://www.gnu.org/licenses/agpl-3.0.en.html)

The project requires [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).  The build system 
is [Maven](http://maven.apache.org/) version 3.x.  In order to build, you will need both Maven and Java 1.8 installed.  Beyond Java, 
Maven and Postgres, nothing else should be required.

To run this software you will need a working, accessable, PostgreSQL database version 9.3+.  You can either install 
from [prebuilt PostgreSQL installations](http://www.enterprisedb.com/products-services-training/pgdownload) or your own prefered method.  If you 
are running on a Mac, try the [Postgres.app](http://postgresapp.com/) as it's butt simple to setup and use.  You'll love it.

Note that the build assumes that the postgres database is available on localhost:5432.  The build also assumes that the super 
user and database _postgres_ is set up with the default password (_postgres_).  If you have changed 
any of these values, please override the _"dba.db.*"_ properties in the top level pom.xml (see [~/.m2/settings.xml](https://maven.apache.org/settings.html)).  Or you can override these properties using "-Ddba.db.*=xxx" when you invoke maven.

  _If your installed Postgres is not using the default port - 5432_ - you will have to be sure to set the _dba.db.port_ value in your ~/.m2/settings.xml or override this at maven invocation with "-Ddba.db.port=5433".  You'll be wondering why you cannot connect if you don't.

You can build the project:

    $ cd <project root>
    $ mvn clean install

The default build will perform DB activity in the tests as they exercise live sql state.

If you want to rebuild the database:

    $ mvn clean install -Ddrop -P database.active

You can use pgadmin3 to view "readable" schema views to browse the data.

Note that the build will create the CoRE database.  The schemas are maintained via liquibase (www.liquibase.org)
and will directly manipulate the database, upgrading and downgrading as necessary. 
See [AbstractModelTest](animations/src/test/java/com/chiralbehaviors/CoRE/meta/models/AbstractModelTest.java) for how to subclass  for easy testing (i.e. you merely subclass it and write your tests using the inherited model/DSL state)

To drop the database and start from scratch, simply add "-Ddrop" to the full build, or to just drop:

    $ cd drop-database
    $ mvn install -Ddrop

To create the database from scratch:

  $ cd drop-database  
  $ mvn install -P sudo-drop-me  
  
Then open PGAdmin3 and make sure that all your databases are belong to us.  I mean, dropped.  Don't drop postgres, or you'll be very sorry and have to return to go, and not collect 200 dollars.
Also, too, drop your login role other than your postgres or whatever you're using as your master superuser role.

