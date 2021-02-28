package com.atakmap.android.pushToTalk.audioPipeline;

import java.io.File;
import android.content.Context;
import android.media.AudioRecord;
import android.media.AudioFormat;


public class MicrophoneRecording implements Recording {

    private class RecordingThread extends Thread {

        @Override
        public void run() {
            while (isRecording) {
                //Do recording logic here


            }
            //Recording is now finished
            //Set ready to true
            ready = true;
        }

    }

    private File recording;
    private boolean ready;
    private Context context;
    private boolean isRecording = false;
    private int minBufferSize;
    private AudioRecord recorder;

    private final String FILE_NAME;
    private static int count = 0;
    private static final int SAMPLE_RATE = 16000;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int BUFFER_FUDGE = 0; //Increase size of buffer
                                               //for better preformance


    public MicrophoneRecording(Context context) {
        this.context = context;
        this.ready = false;
        this.recording = null;
        count++;
        FILE_NAME = "audio_transcription_" + count + ".wav";
        configureRecording();
    }

    private void configureRecording() {
        this.minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING);
        this.minBufferSize += BUFFER_FUDGE;
        //9 is constant for unprocessed
        recorder = new AudioRecord(9, SAMPLE_RATE, CHANNEL, ENCODING, minBufferSize);
    }

    @Override
    public boolean hasAudio() {
        return ready;
    }

    @Override
    public void startRecording() {
        isRecording = true;
        (new RecordingThread()).start();
    }

    @Override
    public void stopRecording() {
        //Stop recording thread
        isRecording = false;
    }

    @Override
    public void cleanUp() {}

    @Override
    public File getAudio() {
        return recording;
    }
}
