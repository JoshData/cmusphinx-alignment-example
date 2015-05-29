import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.util.List;

import edu.cmu.sphinx.api.SpeechAligner;
import edu.cmu.sphinx.util.TimeFrame;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.linguist.acoustic.Unit;

import com.opencsv.CSVWriter;

/**
 * This is a simple tool to align audio to text and dump a database
 * for the training/evaluation.
 *
 * You need to provide a model, dictionary, audio and the text to align.
 */
public class Aligner {

    /**
     * @param args acoustic model, dictionary, audio file, text
     */
    public static void main(String args[]) throws Exception {
        // audio and transcript file paths
        File file = new File(args[2]);
        File transcript_file = new File(args[3]);

        // read transcript from file
        BufferedReader transcript_file_reader = new BufferedReader(new FileReader(transcript_file));
        StringBuffer transcript = new StringBuffer();
        String line = null;
        while ((line = transcript_file_reader.readLine()) !=null)
            transcript.append(line).append("\n");

        // perform alignment
        SpeechAligner aligner = new SpeechAligner(args[0], args[1], null);
        List<WordResult> results = aligner.align(file.toURI().toURL(), transcript.toString());

        // write out results
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(System.out), ',');
        for (WordResult result : results) {
            StringBuilder pronunciation = new StringBuilder();
            for (Unit unit : result.getPronunciation().getUnits())
                pronunciation.append(unit).append(' ');

            writer.writeNext(new String[] {
                result.getWord().toString(),
                pronunciation.toString().trim(),
                Boolean.toString(result.isFiller()),
                Double.toString(result.getScore()),
                Long.toString(result.getTimeFrame().getStart()),
                Long.toString(result.getTimeFrame().getEnd()),
            });
        }
        writer.close();
    }
}