#!/usr/bin/env bash

FITS_SCRIPT="$0"

# Resolve symlinks to this script
while [ -h "$FITS_SCRIPT" ] ; do
  ls=`ls -ld "$FITS_SCRIPT"`
  # Drop everything prior to ->
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    FITS_SCRIPT="$link"
  else
    FITS_SCRIPT=`dirname "$FITS_SCRIPT"`/"$link"
  fi
done

. "$(dirname $FITS_SCRIPT)/fits-env.sh"

cmd="java -classpath \"$APPCLASSPATH\" edu.harvard.hul.ois.fits.Fits $args"

eval "exec $cmd"
