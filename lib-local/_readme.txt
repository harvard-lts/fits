This directory contains Maven dependencies that are not available publicly. They are arranged by the standard Maven deployment method. These are accessed by the pom.xml file.
For an example, an artifact should be deployed to this directory as follows:

mvn install:install-file -Dfile=[FILE_PATH] -DpomFile=<path to local .m2 repo POM> -DlocalRepositoryPath=[REPO_DIR]

Example for OTS JAR file:
mvn install:install-file -Dfile=./ots-1.0.62.jar -DpomFile=<path to>/.m2/repository/edu/harvard/huit/lts/ots/1.0.62/ots-1.0.62.pom  -DlocalRepositoryPath=./lib-local
 