Ultrastructure
==============

An implementation of Ultrastructure using PostgreSQL, provided as a RESTful service

To run this software you will need a bit of infrastructure in place:

First, you'll need the PL/SQL virtual appliance that runs a preconfigured PostgreSQL database.
You can download this appliance here: https://www.dropbox.com/s/2645ji8nd1peol6/CoRE-650-appliance.zip
Mac users: you'll need Stuffit Expander to unzip this file. Go to http://www.stuffit.com/ and grab it (it's free). The default OSX extractor won't work.

To configure the VM, you have to create a host-only network. Fire up VirtualBox, open up Preferences -> Network and create a new host-only network from there. It shouldn't need any configuration. After that, you should be able to start the VM successfully. 

The ubuntu user password is 'ubuntu' and the postgres password is 'postgress!'
    
You will need the Apache Maven build tool (available at http://maven.apache.org) version 3.x


You also need to edit your maven global settings.xml, you'll have to open the file ~/.m2/settings.xml.  If
you have not modified this, then simply cut and paste the following:

    <?xml version="1.0" encoding="UTF-8"?>
    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
        <profiles>
            <profile>
                <id>test-db</id>
                <properties>
                    <testing.db.server>192.168.56.101</testing.db.server>
                    <testing.db.password>postgres</testing.db.password>
                    <db.server>192.168.56.101</db.server>
                </properties>
            </profile>
        </profiles>
        
        <activeProfiles>
            <activeProfile>test-db</activeProfile>
        </activeProfiles>
    </settings>

You can build the project:

    $ cd <project root>
    $ mvn clean install

Now you can work directly with the database:

    $ cd schema
    $ mvn clean process-test-resources

Now you can use pgadmin3 to view the sample data.  Use the "readable" schema views to browse the data.

Note that the build will create the CoRE database.  The schemas are maintained via liquibase (www.liquibase.org)
and will directly manipulate the database, upgrading and downgrading as necessary.  All tests
which load and manipulate data in the database are required to clean up after themselves.

So, word.

To drop the database and start from scratch, simply add "-Ddrop=true" to the full build, or:

    $ cd drop-database
    $ mvn install -Ddrop=true

