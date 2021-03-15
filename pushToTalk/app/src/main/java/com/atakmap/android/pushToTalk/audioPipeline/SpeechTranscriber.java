package com.atakmap.android.pushToTalk.audioPipeline;

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
            public void onEndOfSpeech() {}

            @Override
            public void onPartialResult(Hypothesis hypothesis) {}

            @Override
            public void onResult(Hypothesis hypothesis) {
                Log.i(TAG, "Result recieved. Starting processing...");
                result = hypothesis.getHypstr();
                resultReady.set(true);
            }

            @Override
            public void onError(Exception exception) {
                Log.e(TAG, exception.getMessage());
            }

            @Override
            public void onTimeout() {}

        };


    /**
     * Constructs and sets up a new SpeechTranscriber
     **/
    public SpeechTranscriber(Context context) {
        try {
            final Assets assets = new Assets(context);
            final File assetDir = assets.syncAssets();
            (new Thread() {
                    public void run(){
                        try {
                            setupRecognizer(assetDir);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }).start();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
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
            recog.startListening("To Hell With Georgia!");
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
        ready.set(true);
    }


}
