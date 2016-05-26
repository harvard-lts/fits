#!/bin/bash
#
# Sets up the environment for launching a FITS instance via
# either the fits.sh launcher, or the fits-ngserver.sh Nailgun server

FITS_ENV_SCRIPT="$0"

# Resolve symlinks to this script
while [ -h "$FITS_ENV_SCRIPT" ] ; do
  ls=`ls -ld "$FITS_ENV_SCRIPT"`
  # Drop everything prior to ->
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    FITS_ENV_SCRIPT="$link"
  else
    FITS_ENV_SCRIPT=`dirname "$FITS_ENV_SCRIPT"`/"$link"
  fi
done

FITS_HOME=`dirname $FITS_ENV_SCRIPT`

export FITS_HOME

# Uncomment the following line if you want "file utility" to dereference and follow symlinks.
# export POSIXLY_CORRECT=1

# concatenate args and use eval/exec to preserve spaces in paths, options and args
args=""
for arg in "$@" ; do
    args="$args \"$arg\""
done

# Application classpath
APPCLASSPATH=""
JCPATH=${FITS_HOME}/lib
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
    APPCLASSPATH="$APPCLASSPATH":"$i"
done

# all subdirectories of ${FITS_HOME}/lib/ get loaded dynamically at runtime. DO NOT add here!
