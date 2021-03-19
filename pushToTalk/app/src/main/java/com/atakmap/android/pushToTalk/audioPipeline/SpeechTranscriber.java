package com.atakmap.android.pushToTalk.audioPipeline;

import com.atakmap.coremap.filesystem.FileSystemUtils;

import android.util.Log;
import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;
import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class SpeechTranscriber {

    /**
     * Is this SpeechTranscriber finished setting up
     **/
    private AtomicBoolean ready = new AtomicBoolean(false);

    /**
     *
     **/
    private AtomicBoolean resultReady = new AtomicBoolean(false);

    /**
     * The resulting string trancsription produces
     **/
    private String result;

    /**
     * Logging tag
     **/
    private static final String TAG = "SpeechTranscriber";

    private static final String NGRAM_SEARCH = "ngram";

    /**
     * Actually does the recognition + recording
     **/
    private SpeechRecognizer recog;
    /**
     * Holds the code that is run once recognition finishes
     **/
    private RecognitionListener listeners = new RecognitionListener() {

            @Override
            public void onBeginningOfSpeech() {
                Log.i(TAG, "Starting Recording/Transcription");
            }

            @Override
            public void onEndOfSpeech() {
                Log.i(TAG, "End of Speech");
                resultReady.set(true);
            }

            @Override
            public void onPartialResult(Hypothesis hypothesis) {
                if (hypothesis != null) {
                    Log.i(TAG, "Partial result recieved. Starting processing...");
                    result = hypothesis.getHypstr();
                    Log.i("Partial Result", result);
                    resultReady.set(true);
                }
            }

            @Override
            public void onResult(Hypothesis hypothesis) {
                Log.i(TAG, "Result recieved. Starting processing...");
                result = hypothesis.getHypstr();
                Log.i("Result", result);
                resultReady.set(true);
            }

            @Override
            public void onError(Exception exception) {
                Log.e(TAG, exception.getMessage());
                resultReady.set(true);
            }

            @Override
            public void onTimeout() {
                Log.i(TAG, "Transcriber timing out");
                resultReady.set(true);
            }

        };


    /**
     * Constructs and sets up a new SpeechTranscriber
     **/
    public SpeechTranscriber(Context context) {


        try {

            File maybe = FileSystemUtils.getItem(FileSystemUtils.TOOL_DATA_DIRECTORY);
            File myDir = new File(maybe, "pTTDataDir");
            boolean result = myDir.mkdir();

            final Assets assets = new Assets(context, myDir.getAbsolutePath());
            final File assetDir = assets.syncAssets();

            (new Thread() {
                    public void run(){
                        try {
                            setupRecognizer(assetDir);
                            Log.i(TAG, "Started SpeechTranscriber Setup");
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            throw new Error(TAG + ": Broken");
                        }
                        Log.i(TAG, "Finished SpeechTranscriber Setup");
                    }
                }).start();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            throw new Error("Setup failed. Killing the process");
        }

    }

    /**
     * Tries to start recording. Returns false if this
     * SpeechTranscriber isn't ready yet. Starts listening
     * for the good word. This shouldn't matter in practice
     * as we aren't using this feature, so it doesn't matter.
     **/
    public boolean startRecording() {
        if (ready.get()) {
            //configure timeout in orders of 10 seconds
            int tenSeconds = 10000;

            recog.startListening(NGRAM_SEARCH, tenSeconds * 6);
            return true;
        }
        return false;
    }

    /**
     * Stops recording
     **/
    public void stopRecording() {
        recog.stop();
    }

    /**
     * Returns the Result of transcription
     * if its ready, the empty string otherwise
     **/
    public String getResult() {
        if (resultReady.get()) {
            return result;
        }
        return "";
    }

    public boolean isResultReady() {
        return resultReady.get();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recog = SpeechRecognizerSetup.defaultSetup()
            .setAcousticModel(new File(assetsDir, "en-us-ptm"))
            .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
            .getRecognizer();
        recog.addListener(listeners);

        recog.addNgramSearch(NGRAM_SEARCH, new File(assetsDir, "en-70k-0.2-pruned.lm"));

        ready.set(true);
    }


}
