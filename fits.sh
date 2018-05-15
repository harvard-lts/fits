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


# Check to see that we have the fits jar file in the CLASSPATH. It's absence
# implies that it doesn't exist in 'lib/', which likely means it wasn't compiled.
if [[ $APPCLASSPATH =~ .*fits-[[:digit:]]\.[[:digit:]]\.[[:digit:]]\.jar.* ]]
then
    cmd="java -classpath \"$APPCLASSPATH\" edu.harvard.hul.ois.fits.Fits $args"

    eval "exec $cmd"
else
    echo "Fits is not installed correctly. Unable to find the fits jar file. Did you run 'ant compile'?"
fi
