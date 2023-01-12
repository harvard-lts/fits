# FITS

![build status](https://github.com/harvard-lts/fits/actions/workflows/build.yml/badge.svg)

## System Requirements

FITS is a Java program and requires Java version 11 or higher. To find out your Java version type java -version in a
command-line window.

Some of the tools that FITS uses have additional requirements. If you are not using these tools, then you can safely
ignore the requirements.

- ExifTool
  - Perl 5.004 or later
  - Optional Perl modula dependencies as described in the "DEPENDENCIES" section of the [ExifTool README](https://github.com/exiftool/exiftool).
- Jpylyzer
  - Python 2.7 or 3.2+
- File
  - The "file" command is expected to be installed on non-Windows systems
- MediaInfo
  - The linux copy of MediaInfo distributed with FITS was built for Ubuntu Jammy. It _may_ work on other distributions
    as well, but, if it doesn't, install MediaInfo directly on your system **and** delete the copy in FITS at `tools/mediainfo`.
    After deleting the copy in FITS, it will then use the system copy.

## Installation

You can either install FITS directly on your system or run it through a Docker container.

### Direct Installation

**IMPORTANT NOTE**: The git source code repository is not meant for direct installation. If you want to install directly
from the source instead of using a release artifact, then refer to the [Development](#Development) section.

1. Download the latest official release artifact from our [Downloads](http://fitstool.org/downloads) page.
2. Create a FITS directory, and unzip the artifact into it.

For example:

```shell
mkdir ~/fits
unzip -d ~/Downloads/fits-1.6.0.zip ~/fits/fits-1.6.0
```

#### Media Info

If you install FITS on Linux, and you are **not** using Ubuntu, then you may need to manually install Media Info. To
do this, you can either replace the Media Info binaries that are distributed with FITS, or you can delete the distributed
binaries and install Media Info at the system level.

Whichever approach you use, first, delete `tools/mediainfo/linux/libmediainfo.so.0` and `tools/mediainfo/linux/libzen.so.0`.
Then, if you're installing it at the system level, use your system's package manager to install Media Info.

If, instead, you'd like to replace the binaries:

1. Download the packages that were built for your system from the [Media Info download page](https://mediaarea.net/en/MediaInfo/Download).
   You want the `libmediainfo` and `libzen` packages. The packages will likely be either rpm or deb packages.
2. You need to extract the packages and locate the `libmediainfo.so.0` and `libzen.so.0` binaries within them.
3. Copy these binaries into `tools/mediainfo/linux`
4. If Media Info is still not working on your system, it's possible that your system is missing a required Media Info
   dependency. You will need to identify what is missing, and install it. This is not an issue, if you install Media
   Info directly through your system's package manager.

### Docker Installation

To run FITS using Docker, you'll need Docker (or Docker-compatible service) installed.

1. Download a copy of the [release artifact](http://fitstool.org/downloads).
2. Extract it to any directory.
3. Build the Docker image using the distributed Dockerfile.
4. Optionally, delete the FITS directory as it is no longer needed.

For example:

```shell
mkdir ~/fits
unzip -d ~/Downloads/fits-1.6.0.zip ~/fits/fits-1.6.0
cd ~/fits/fits-1.6.0
docker build -f Dockerfile -t fits:latest -t fits:1.6.0 .
```

After building the image, you can use it directly to analyze files. The following are some examples. Note these
examples mount the current working directory within the Docker container, which means that the only files that are
accessible within the container are files that are relative the current working directory. Additionally, these commands
**do not** need to be run within the FITS root and can be run anywhere on the system.

```shell
# Run FITS on a file
docker run --rm -v `pwd`:/work fits -i file.txt

# Run a specific version of FITS on a file
docker run --rm -v `pwd`:/work fits:1.6.0 -i file.txt

# Run FITS on a directory
docker run --rm -v `pwd`:/work fits -r -n -i in-dir -o out-dir

# Run FITS with alternate configuration
docker run --rm -v `pwd`:/work fits -f fits-custom.xml -i file.txt
```

## Configuration

### Logging

Whether using the default `log4j2.xml` configuration file contained within the application deployment or configuring an
external `log4j2.xml` file, the default logging output file, fits.log, is configured to be written to the directory from
which FITS is launched. This can be modified by finding the following line within the `log4j2.xml` file in the
top-level directory of the FITS deployment:

    fileName="./fits.log"

Modify the path to fits.log to have this log file written to a different place on the file system.
To use a `log4j2.xml` file external to the FITS deployment, when launching FITS add the following
property to the deployment script:

    -Dlog4j2.configurationFile=/path/to/log4j2.xml

For more information on configuring the verboseness of logging using ERROR, WARN, INFO, DEBUG, see the
[log4j site](https://logging.apache.org/log4j/2.x/manual/)

## Running FITS

FITS can be run on a command-line or within a program using the Java API.

### FITS from the command-line

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

Here are a couple examples of running FITS to get you started. These are relatively simple examples assuming Windows -
more complex examples can be found in the on-line user manual. 

    Run FITS against its release text file printing the FITS output to the terminal: fits.bat -i version.properties (or, on Linux, ./fits.sh -i version.properties)
    Run FITS against its release text file saving the FITS output to a file: fits.bat -i version.properties -o myoutput.txt
    Output the technical metadata only (in the TextMD format) for the file to the terminal: fits.bat -x -i version.properties
    Output the FITS output plus technical metadata (in the TextMD format) for the text file to the terminal: fits.bat -xc -i version.properties

## Using FITS Java API

See the [Developer Manual](http://fitstool.org/developer-manual).

## Learn more

After you are up and running see the [User Manual](http://fitstool.org/user-manual) for more documentation.

## Development

### Building

As of release 1.3.0 FITS is built using [Apache Maven](https://maven.apache.org/).
The build artifacts are fits-<version>.jar and fits-<version>.zip. The JAR file contains the compiled Java source files
contained in this project whereas the ZIP file contains the final artifact which can be extracted and used to process
input files for analysis.

The ZIP file can be built with the following command, which will also run the entire test suite:

    mvn clean package

Because the outcome of some of the tests is system dependent, it is
recommended to run them in a Docker container so that the results are
consistent. To do so, first install Docker, Podman, or an equivalent
container service, and execute the following:

    # The build only needs to be run once
    docker build -f docker/Dockerfile-test -t fits-test .
    docker run --rm -v `pwd`:/fits -v ~/.m2:/root/.m2 fits-test mvn clean test

To build yet skip the tests, use the following command:

    mvn clean package -DskipTests

NOTE: A few Maven dependencies are not in public repositories. These are within the source tree in the lib-local
directory. Other dependencies are within the lib directory for use by the FITS custom classloader.

### Formatting

This project uses a code formatter to apply the [palantir-java-format](https://github.com/palantir/palantir-java-format)
to ensure consist formatting based on the [Google Style Guide](https://google.github.io/styleguide/javaguide.html).
To run the formatter:

    mvn spotless:apply

When the project builds, it checks the formatting and will fail if there are any files that are not formatted per the standard.

### Overwriting test expectations

The test expectation xml files can be overwritten with the current FITS output by running the tests with the
`-Doverwrite=true` flag. For example:

```shell
docker run --rm -v `pwd`:/fits -v ~/.m2:/root/.m2 fits-test mvn -Doverwrite=true clean test
```

However, generally speaking, test expectation files should be changed as little as possible so that the diffs are
as clear as possible.

### Tools

Some of the tools that FITS is distributed with are not bundled in the source tree. Instead, they are pulled in when
Maven is run. They are downloaded as part of the normal build process, but can also be installed directly using
`mvn generate-resources`. Each tool that's installed has a pom file in the `tool-poms` directory that defines how it's
installed.

ExifTool, MediaInfo, jpylyzer, and the Windows file utility are installed using the script at `src/main/script/ToolInstaller`,
and are configured using the properties file `tools.properties`.

Tika and JHOVE are installed exclusively through their pom files `tika-pom.xml` and `jhove-pom.xml` respectively. 

DROID is installed via the `droid-pom.xml` file, and its signatures are updated by invoking `mvn -P update-droid-sigs generate-resources`.
The signature file updates must be invoked specifically, and do not automatically run. Additionally, the signature file
changes must be committed into git.

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
    test-overwrite      # Overwrites all of the test expecation xmls with the current FITS output
    update-droid-sigs   # Update DROID signature files
```

The commands are defined in [justfile](justfile).

## License Details

FITS is released under the [GNU LGPL](http://www.gnu.org/licenses/lgpl.html) open source license. The source code for
FITS is included in the downloadable ZIP files.

The tools bundled with FITS use the following open source licenses:

  * Jhove (LGPL version 2.1 or any later version)
  * ExifTool (GPL version 1 or any later version; or the artistic license)
  * National Library of New Zealand Metadata Extractor (Apache Public License version 2)
  * DROID (BSD (new version))
  * FFIdent (LGPL)
  * Tika (Apache Public License version 2)
  * MediaInfo [BSD-like](https://mediaarea.net/en-us/MediaInfo/License)
  * Jpylyzer (LGPL version 3)

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
