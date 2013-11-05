#!/bin/bash
#
# Install the IBM DB2Jcc driver to the local m2 repository. This is needed in
# order to have the maven shade plugin insert the DB2Jcc driver into the shaded
# jar. Simply execute
#
#    $ ./m2-install-db2jcc.sh
#
# once to place the DB2Jcc driver into your local m2 repository.

mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
  -Dfile=lib/db2jcc4.jar -DgroupId=com.ibm.db2.jcc \
  -DartifactId=DB2Jcc -Dversion=4.7.85 -Dpackaging=jar

