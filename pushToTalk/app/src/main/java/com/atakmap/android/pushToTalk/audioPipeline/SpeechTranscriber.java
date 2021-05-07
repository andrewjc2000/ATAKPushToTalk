package com.atakmap.android.pushToTalk.audioPipeline;

import com.atakmap.coremap.filesystem.FileSystemUtils;
import com.atakmap.coremap.log.Log;

import android.content.Context;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * Class responsible for initiating and terminating audio recording,
 * as well as converting that recording to transcribed text.
 * @author jkelly80
 * @version 1.0
 */
public class SpeechTranscriber {

    /**
     * Is this SpeechTranscriber finished setting up
     */
    private final AtomicBoolean isReady = new AtomicBoolean(false);

    public synchronized boolean getIsReady() {
        return isReady.get();
    }

    /**
     * Is the result of the transcription ready
     */
    private final AtomicBoolean resultReady = new AtomicBoolean(false);

    public boolean getResultReady() {
        return resultReady.get();
    }

    /**
     * The resulting string transcription produces
     */
    private String result;

    /**
     * Logging tag
     **/
    private static final String TAG = "SpeechTranscriber";

    private static final String NGRAM_SEARCH = "ngram";

    /**
     * Actually does the recognition + recording
     */
    private SpeechRecognizer recognizer;
    /**
     * Holds the code that is run once recognition finishes
     */
    private final RecognitionListener listener;

    private class SimpleListener implements RecognitionListener {
        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onPartialResult(Hypothesis hypothesis) {
            if (hypothesis != null) {
                result = hypothesis.getHypstr();
                resultReady.set(true);
            }
        }

        @Override
        public void onResult(Hypothesis hypothesis) {
            if (hypothesis != null) {
                result = hypothesis.getHypstr();
                resultReady.set(true);
            }
        }

        @Override
        public void onError(Exception exception) {
            Log.e(TAG, exception.toString());
            result = "There was an error in capturing your response.";
            resultReady.set(true);
        }

        @Override
        public void onTimeout() {
            result = "Your speech exceeded the time limit.";
            resultReady.set(true);
        }
    }

    /**
     * Constructs and sets up a new SpeechTranscriber
     **/
    public SpeechTranscriber(final Context context) {
        this.listener = new SimpleListener();
        try {
            File maybe = FileSystemUtils.getItem(FileSystemUtils.TOOL_DATA_DIRECTORY);
            File myDir = new File(maybe, "pTTDataDir");
            final Assets assets = new Assets(context, myDir.getAbsolutePath());
            final File assetDir = assets.syncAssets();
            new Thread() {
                public void run() {
                    try {
                        setupRecognizer(assetDir);
                    } catch (Exception e) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        PrintStream stream = new PrintStream(outputStream);
                        e.printStackTrace(stream);
                        Log.e(TAG, e.toString() + "\n" + outputStream.toString());
                        Toast.makeText(
                            context, "There was an error when setting up plugin resources.",
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }.start();
        } catch (IOException e) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(outputStream);
            e.printStackTrace(stream);
            Log.e(TAG, e.toString() + "\n" + outputStream.toString());
            Toast.makeText(
                context, "There was an error when setting up plugin resources.",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    // default timeout is 1 minute
    private static final int RECORDING_TIMEOUT_MILLISECONDS = 60000;

    /**
     * Tries to start recording. Returns false if this
     * SpeechTranscriber isn't ready yet. Starts listening
     * for the good word. This shouldn't matter in practice
     * as we aren't using this feature, so it doesn't matter.
     **/
    public boolean startRecording() {
        if (isReady.get()) {
            resultReady.set(false);
            recognizer.startListening(NGRAM_SEARCH, RECORDING_TIMEOUT_MILLISECONDS);
            return true;
        }
        return false;
    }

    /**
     * Stops recording
     **/
    public void stopRecording() {
        recognizer.stop();
    }

    /**
     * Returns the Result of transcription
     * if its ready, and the empty string otherwise
     **/
    public String getResult() {
        if (resultReady.get()) {
            return result == null ? "" : result;
        }
        return "";
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
            .setAcousticModel(new File(assetsDir, "en-us-ptm"))
            .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
            .getRecognizer();
        recognizer.addListener(listener);
        /* NOTE: language is English by default. If you ever wanted to change that, here is
         * where you would do it.
         */
        recognizer.addNgramSearch(NGRAM_SEARCH, new File(assetsDir, "en-70k-0.2-pruned.lm"));
        isReady.set(true);
    }

}
