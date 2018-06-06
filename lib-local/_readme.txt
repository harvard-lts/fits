This directory contains Maven dependencies that are not available publicly. They are arranged by the standard Maven deployment method. These are accessed by the pom.xml file.
For an example, an artifact should be deployed to this directory as follows:

mvn install:install-file -Dfile=[FILE_PATH] -DgroupId=[GROUP] -DartifactId=[ARTIFACT] -Dversion=[VERS] -Dpackaging=jar -DlocalRepositoryPath=[REPO_DIR]

mvn install:install-file -Dfile=local/location/PDFBox-0.7.3.jar -DgroupId=org.pdfbox -DartifactId=PDFBox -Dversion=0.7.3 -Dpackaging=jar -DlocalRepositoryPath=./lib-local
