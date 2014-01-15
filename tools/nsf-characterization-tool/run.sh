#!/bin/sh
scriptdir=`dirname "$0"`
if [ $# -eq 2 ] 
then
	NOTES_PATH="$1"
	NOTES_JVM_PATH="$1/jvm"
  	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$NOTES_PATH
	
	$NOTES_JVM_PATH/bin/java -jar "$scriptdir/nsf-characterization-tool.jar" -f $2
else
	if [ $# -eq 1 ] 
	then
		NOTES_PATH="$1"
		NOTES_JVM_PATH="$1/jvm"
  		export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$NOTES_PATH
		$NOTES_JVM_PATH/bin/java -jar "$scriptdir/nsf-characterization-tool.jar" -v
	else
		echo "Usage: ./run.sh [Lotus Notes Folder] [File]\n";
		exit 0;
	fi
fi
