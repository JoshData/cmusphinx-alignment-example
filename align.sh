#!/bin/sh
# Usage: ./align.sh audio.wav transcript.txt

HERE=$(dirname $0)
SPHINX=$HERE/sphinx4
JAR=$SPHINX/sphinx4-core/target/sphinx4-core-1.0-SNAPSHOT.jar
DICTIONARY=$SPHINX/sphinx4-data/src/main/resources/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict
MODEL=$HERE/cmusphinx-en-us-5.2

if [ -z "$1" ]; then
	echo "Usage: ./align.sh sample.wav sample.txt"
	exit 1
fi

# Go.
java -cp $HERE:$JAR:$HERE/opencsv-3.3.jar \
	Aligner \
	$MODEL \
	$DICTIONARY \
	$@
