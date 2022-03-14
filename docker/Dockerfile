# This file creates a FITS image.
#
# Usage:
#   # Build the image
#   mvn -DskipTests clean package
#   docker build -f docker/Dockerfile -t fits .
#
#   # Run FITS on a file
#   docker run --rm -v `pwd`:/work:z fits -i file.txt
#
#   # Run FITS on a directory
#   docker run --rm -v `pwd`:/work:z fits -r -n -i in-dir -o out-dir
#
#   # Run FITS with alternate configuration
#   docker run --rm -v `pwd`:/work:z fits -f fits-custom.xml -i file.txt

FROM docker.io/eclipse-temurin:17-jre-focal

ARG FILE_VERSION=5.41

RUN apt-get update && \
    apt-get install -yqq make gcc unzip && \
    rm -rf /var/lib/apt/lists/*

# Install file https://github.com/file/file
RUN cd /var/tmp && \
    curl -so file-${FILE_VERSION}.tar.gz https://astron.com/pub/file/file-${FILE_VERSION}.tar.gz && \
    tar xzf file-${FILE_VERSION}.tar.gz && \
    cd file-${FILE_VERSION} && \
    ./configure && \
    make -j4 && \
    make install && \
    ldconfig && \
    cd .. && \
    rm -rf file-${FILE_VERSION}*

# Install FITS
COPY target/fits-*.zip /opt/fits.zip
RUN unzip -q /opt/fits.zip -d /opt/fits && \
    rm /opt/fits.zip && \
    mkdir -p /var/log/fits

# Bind the current working directory here so that FITS can access files on the host
WORKDIR /work

ENTRYPOINT ["/opt/fits/fits.sh"]
