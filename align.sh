#!/bin/sh
# Usage: ./align.sh audio.wav transcript.txt

SPHINX=sphinx4-5prealpha-src
JAR=$SPHINX/sphinx4-core/target/sphinx4-core-1.0-SNAPSHOT.jar
MODEL=cmusphinx-en-us-5.2
DICTIONARY=$SPHINX/sphinx4-data/src/main/resources/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict

java -cp .:$JAR:opencsv-3.3.jar \
	Aligner \
	$MODEL \
	$DICTIONARY \
	$@
