#!/bin/bash

#FITS_HOME=`dirname "$0"`
FITS_HOME=`echo "$0" | sed 's,/[^/]*$,,'`
export FITS_HOME

# Uncomment the following line if you want "file utility" to dereference and follow symlinks.
# export POSIXLY_CORRECT=1

# concatenate args and use eval/exec to preserve spaces in paths, options and args
args=""
for arg in "$@" ; do
	args="$args \"$arg\""
done

JCPATH=${FITS_HOME}/lib
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/droid
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/jhove
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/nzmetool
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/nzmetool/adapters
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

cmd="java -classpath \"$APPCLASSPATH:$FITS_HOME/xml/nlnz\" edu.harvard.hul.ois.fits.report.Report $args"

eval "exec $cmd"
