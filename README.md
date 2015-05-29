cmusphinx forced alignment example
==================================

So... building [cmusphinx](http://cmusphinx.sourceforge.net/wiki/download) isn't exactly easy and running its forced alignment tool isn't well documented. (Forced alignment is matching a transcript with corresponding audio and getting time codes for each word in the transcript.)

This project documents how I got it to work.

Here's how I built cmusphinx 4-5prealpha on an Ubuntu 14.04 x64 machine:

	sudo apt-get update
	sudo apt-get install unzip bison swig python-dev default-jdk maven

	wget http://downloads.sourceforge.net/project/cmusphinx/sphinxbase/5prealpha/sphinxbase-5prealpha.tar.gz
	wget http://downloads.sourceforge.net/project/cmusphinx/sphinx4/5prealpha/sphinx4-5prealpha-src.zip

	tar -zxf sphinxbase-5prealpha.tar.gz

	cd sphinxbase-5prealpha
	./configure
	make
	sudo make install
	cd ..

	unzip sphinx4-5prealpha-src.zip 
	cd sphinx4-5prealpha-src/sphinx4-core
	mvn clean install
	cd ../..

You'll need an acoustic model. Here I'm using English:

	wget http://downloads.sourceforge.net/project/cmusphinx/Acoustic%20and%20Language%20Models/US%20English%20Generic%20Acoustic%20Model/cmusphinx-en-us-5.2.tar.gz
	tar -zxf cmusphinx-en-us-5.2.tar.gz

To test that this all worked so far, try out forced alignment with a sample 16khz 16bit mono wav file (it must be in that format). First get the file and its transcription:

	wget -O sample_original.wav http://hawksoft.com/hawkvoice/samples/ulaw.wav
	sox sample_original.wav -b 16 sample.wav channels 1 rate 16k
	echo "It's a dense crowd in two distinct ways. The fruit of a figg tree is apple shaped." > sample.txt

Try out the stock aligner:

	java -cp sphinx4-5prealpha-src/sphinx4-core/target/sphinx4-core-1.0-SNAPSHOT.jar edu.cmu.sphinx.tools.aligner.Aligner cmusphinx-en-us-5.2/ sphinx4-5prealpha-src/sphinx4-data/src/main/resources/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict sample.wav "$(cat sample.txt)"

It should look like it works until the very end where I get a "Exception in thread "main" java.io.IOException: Invalid argument" because it tries to write out a whole bunch of new .wav files but can't do so. In any case, you should see some output toward the end that looks like this:

	INFO: Decoding result is [{it's, 1.000000, [170:200]}, {a, 1.000000, [200:390]}, {dense, 1.000000, [390:800]}]

Compile my driver class instead, and get the opencsv library:

	wget http://downloads.sourceforge.net/project/opencsv/opencsv/3.3/opencsv-3.3.jar
	javac -cp sphinx4-5prealpha-src/sphinx4-core/target/sphinx4-core-1.0-SNAPSHOT.jar:opencsv-3.3.jar Aligner.java 

And run the same example with my driver:

	java -cp .:sphinx4-5prealpha-src/sphinx4-core/target/sphinx4-core-1.0-SNAPSHOT.jar:opencsv-3.3.jar Aligner cmusphinx-en-us-5.2/ sphinx4-5prealpha-src/sphinx4-data/src/main/resources/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict sample.wav sample.txt 2>/dev/null

Or just:

	./align.sh sample.wav sample.txt 2>/dev/null

You'll get:

	"it's","IH T S","false","0.0","170","200"
	"a","AH","false","-5540774.0","200","390"
	"crowd","K R AW D","false","-1.13934288E8","850","1300"
	"in","IH N","false","-1.95127088E8","1300","1470"
	"two","T UW","false","-2.23176048E8","1470","1700"
	"distinct","D IH S T IH NG K T","false","-2.6345264E8","1700","2230"
	"ways","W EY Z","false","-3.58427808E8","2230","2730"
	"the","DH AH","false","-4.72551168E8","2920","3100"
	"fruit","F R UW T","false","-5.24233504E8","3220","3530"
	"of","AH V","false","-5.79971456E8","3530","3640"
	"a","AH","false","-5.99515456E8","3640","3760"
	"figg","F IH G","false","-6.2017152E8","3760","4060"
	"tree","T R IY","false","-6.72126656E8","4060","4490"
	"is","IH Z","false","-7.4763744E8","4490","4570"
	"apple","AE P AH L","false","-7.73581184E8","4630","5040"
	"shaped","SH EY P T","false","-8.44424704E8","5040","5340"

My driver outputs a CSV table, to standard output (you can redirect it to a file if needed), with columns:

* the word (as it appeared in the transcript)
* the phonemic prounciation of the word (from the dictionary)
* whether this word was a filler (automatically inserted?)
* the confidence of this word's alignment (not sure if higher is better...)
* the start time of the word, in miliseconds
* the end time of the word, in miliseconds
