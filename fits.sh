#!/bin/bash

. "$(dirname $BASH_SOURCE)/fits-env.sh"

cmd="java -classpath \"$APPCLASSPATH\" edu.harvard.hul.ois.fits.Fits $args"

eval "exec $cmd"
