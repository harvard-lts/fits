#!/bin/sh
scriptdir=`dirname "$0"`
if [ $# -eq 3 ] 
then
	NOTES_PATH=/opt/ibm/notes
	NOTES_JVM_PATH=/opt/ibm/notes/jvm
  	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$NOTES_PATH
	
	$NOTES_JVM_PATH/bin/java -jar "$scriptdir/nsf-characterization-tool.jar" -f $3
else
	if [ $# -eq 2 ] 
	then
		NOTES_PATH=/opt/ibm/notes
		NOTES_JVM_PATH=/opt/ibm/notes/jvm
  		export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$NOTES_PATH
		$NOTES_JVM_PATH/bin/java -jar "$scriptdir/nsf-characterization-tool.jar" -v
	else
		echo "Usage: ./run.sh [Lotus Notes JVM Folder] [Lotus Notes Folder] [File]\n";
		exit 0;
	fi
fi
