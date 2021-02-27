package com.atakmap.android.pushToTalk.audioPipeline;

import java.io.File;
import android.content.Context;
import android.media.MediaRecorder;


public class MicrophoneRecording implements Recording {

    private File recording;
    private boolean ready;
    private Context context;
    private MediaRecorder recorder;
    private final String FILE_NAME;
    private static int count = 0;

    public MicrophoneRecording(Context context) {
        this.context = context;
        this.ready = false;
        this.recording = null;
        count++;
        FILE_NAME = "audio_transcription_" + count + ".wav";
        recording = new File(context.getFilesDir(), FILE_NAME);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recording);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    @Override
    public boolean hasAudio() {
        return ready;
    }

    @Override
    public void startRecording() {
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();

    }

    @Override
    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        ready = true;
    }

    @Override
    public void cleanUp() {}
}
