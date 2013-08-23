#!/bin/bash
#
# This helper script launches a nailgun server with the FITS
# classpath, making it simple to launch a persistent JVM for FITS.
# 
# The one required parameter is the path to nailgun's jar; it can also be
# specified via the NAILGUN_JAR environment variable.

. "$(dirname $BASH_SOURCE)/fits-env.sh"

if [[ ! $NAILGUN_JAR ]] && [[ ! $1 ]]; then
	echo "Error: Path to Nailgun JAR must be specified!" >&2
	echo "Usage: fits-ngserver.sh [path-to-nailgun.jar]" >&2
	echo "The path may also be specified via the NAILGUN_JAR environment variable." >&2
	exit 64
else
	NAILGUN_JAR=$1
fi

cmd="java -classpath \"$APPCLASSPATH:$NAILGUN_JAR\" com.martiansoftware.nailgun.NGServer"

echo "You may now run FITS by typing: ng edu.harvard.hul.ois.fits.Fits [options]" >&2

eval "exec $cmd"
