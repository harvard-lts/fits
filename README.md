FITS
====

![build status](https://github.com/harvard-lts/fits/actions/workflows/build.yml/badge.svg)

System Requirements
-------------------
FITS is a Java program and requires Java version 1.8 or higher. To find out your Java version type java -version in a command-line window.

Building FITS
-------------
As of release 1.3.0 FITS is built using [Apache Maven](https://maven.apache.org/).
The build artifacts are fits-<version>.jar and fits-<version>.zip. The JAR file contains the compiled Java source files contained in this project whereas the ZIP file contains the final artifact which can be extracted and used to process input files for analysis.
The ZIP file can be built with the following command, which will also run the entire test suite:

    mvn clean package

Because the outcome of some of the tests is system dependent, it is
recommended to run them in a Docker container so that the results are
consistent. To do so, first install Docker, Podman, or an equivalent
container service, and execute the following:

    # The build only needs to be run once
    docker build -f docker/Dockerfile-test -t fits-test .
    docker run --rm -v `pwd`:/fits:z -v ~/.m2:/root/.m2:z fits-test mvn clean test

To build yet skip the tests, use the following command:

    mvn clean package -DskipTests

NOTE: A few Maven dependencies are not in public repositories. These are within the source tree in the lib-local directory. Other dependencies are within the lib directory for use by the FITS custom classloader.

Installation
------------
Download the latest official binary release from our [Downloads](http://fitstool.org/downloads) page.

**IMPORTANT NOTE**: The code on this GitHub site is not meant for direct installation since it does NOT include the necessary fits.jar file (which is a primary build artifact of this project). If you want to use this GitHub site for installing FITS, you must first download (or Git clone) the code then build the project using Maven. (See above.)

If this is your first time downloading FITS, create a directory for FITS, for example:

    On Windows: C:\Program Files\Fits
    On Mac OS X: /Applications/Fits
    On *nix: /home/myuser/Fits

Extract the contents of your ZIP file to your FITS directory. You should end up with a another directory under your top-level FITS directory that has a version number embedded in it, for example on Windows:

    C:\Program Files\Fits\fits-1.4.1 (or whatever the current version is)

Running FITS
------------
FITS can be run on a command-line or within a program using the Java API.

FITS from the command-line
--------------------------
Run FITS on the command-line using one of the start-up scripts (fits.bat on Windows, fits.sh on Mac OS X and *nix). 

For example on Windows:

    Open up a command line interface window: Click on Start -> Type in cmd in the lower-left box and hit enter
    Navigate to the directory where you installed FITS, for example: cd "..\..\Program Files\fits\fits-0.9.0"
    Execute FITS using the start-up script with the -h parameter to see the parameter options: fits.bat -h

For example on *nix:

    Open up a terminal window.
    Navigate to the directory where you installed FITS
    If it not already, make the fits.sh file executable
        chmod +x fits.sh
    Run the script named fits.sh
        ./fits.sh

Here are a couple examples of running FITS to get you started. These are relatively simple examples assuming Windows - more complex examples can be found in the on-line user manual. 

    Run FITS against its release text file printing the FITS output to the terminal: fits.bat -i version.properties (or, on Linux, ./fits.sh -i version.properties)
    Run FITS against its release text file saving the FITS output to a file: fits.bat -i version.properties -o myoutput.txt
    Output the technical metadata only (in the TextMD format) for the file to the terminal: fits.bat -x -i version.properties
    Output the FITS output plus technical metadata (in the TextMD format) for the text file to the terminal: fits.bat -xc -i version.properties

Logging
-------
Whether using the default log4j2.xml configuration file contained within the application deployment or configuring an external log4j2.xml file, the default logging output file, fits.log, is configured to be written to the directory from which the FITS is launched. This can be modified by finding the following line within the log4j2.xml file in the top-level directory of the FITS deployment:

    fileName="./fits.log"

Modify the path to fits.log to have this log file written to a different place on the file system.
To use a log4j2.xml (or log4j2.properties) file external to the FITS deployment, when launching FITS add the following property to the deployment script:

    -Dlog4j2.configurationFile=/path/to/log4j2.xml

For more information on configuring the verboseness of logging using ERROR, WARN, INFO, DEBUG, see the [log4j site](https://logging.apache.org/log4j/2.x/manual/)

Using FITS Java API
-------------------
See the [Developer Manual](http://fitstool.org/developer-manual).

Learn more
----------
After you are up and running see the [User Manual](http://fitstool.org/user-manual) for more documentation.


Development
-----------

### JDK

While FITS only requires Java 8 to run, it requires **Java 11** or later to _build_.

### Formatting

This project uses a code formatter to apply the [palantir-java-format](https://github.com/palantir/palantir-java-format)
to ensure consist formatting based on the [Google Style Guide](https://google.github.io/styleguide/javaguide.html).
To run the formatter:

    mvn spotless:apply

When the project builds, it checks the formatting and will fail if there are any files that are not formatted per the standard.

### Tools

Some of the tools that FITS uses are not bundled in the source tree, and are pulled in when Maven is run. They
are downloaded as part of the normal build process, but can also be installed directly using `mvn generate-resources`.
Each tool that's installed has a pom file in the `tool-poms` directory that defines how it's installed.

exiftool is installed using the script at `src/main/script/ExiftoolInstall` and is configured using the properties file
`tools.properties`.

Tika is installed exclusive through its pom file `tika-pom.xml`. 

### Just

You can optionally install [Just](https://github.com/casey/just) and use it to execute shortcut commands within the
project for doing things like running tests, formatting, and building. Execute `just` to see the available commands:

```shell
Available recipes:
    build               # Builds FITS
    build-image         # Builds the Docker image
    build-test-image    # Builds the Docker image that's used for running the tests
    default             # Lists available commands
    format              # Applies the code formatter
    install-tools       # Install FITS tool dependencies into the tools directory
    run +ARGS           # Executes FITS within a Docker container. This requires that the image has already been built (just build-image).
    test                # Runs the tests within a Docker container. Requires the image to already exist (just build-test-image). The image does NOT need to be rebuilt between runs.
    test-filter PATTERN # Runs the tests that match the pattern within a Docker container. Requires the image to already exist (just build-test-image). The image does NOT need to be rebuilt between runs.
```

The commands are defined in [justfile](justfile).

License Details
---------------
FITS is released under the [GNU LGPL](http://www.gnu.org/licenses/lgpl.html) open source license. The source code for FITS is included in the downloadable ZIP files.

The tools bundled with FITS use the following open source licenses:

  * Jhove (LGPL version 2.1 or any later version)
  * Exiftool (GPL version 1 or any later version; or the artistic license)
  * National Library of New Zealand Metadata Extractor (Apache Public License version 2)
  * DROID (BSD (new version))
  * FFIdent (LGPL)
  * Tika (Apache Public License version 2)
  * MediaInfo [BSD-like](https://mediaarea.net/en-us/MediaInfo/License)

The source code for each of the above tools is available on their websites.

In addition, FITS includes the following open source libraries:

  * [JDOM](http://www.jdom.org/) (Apache-like license, modified Apache version 1.1)
  * staxmate (BSD (new version))
  * stax2 (LGPL version 2.1)
  * Woodstox (LGPL version 2.1)
  * [xercesImpl](http://xerces.apache.org/xerces2-j/) (Apache Public License version 2)
  * [xml-apis](http://xerces.apache.org/xml-commons/) (Apache Public License version 2)
  * [xmlunit](http://www.xmlunit.org/) (BSD (new version))
  * [JNA](https://github.com/java-native-access/jna) (Dual license LGPL version 2.1 and Apache Public License version 2)

For more license details, see [FITS License](http://projects.iq.harvard.edu/fits/code-license)
