package com.atakmap.android.pushToTalk.audioPipeline;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import android.content.Context;
import android.media.AudioRecord;
import android.media.AudioFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.frontend.util.WavWriter;

public class MicrophoneRecording implements Recording {

    /**
     * Holds the buffers waiting to be written to file
     **/
    private LinkedBlockingQueue<ByteBuffer> writeQueue = new LinkedBlockingQueue<>();

    /**
     * Writes data to file
     **/
    private class WriteThread extends Thread {

        @Override
        public void run() {
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                while(isRecording.get() || writeQueue.size() > 0) {
                    ByteBuffer data = writeQueue.take();
                    output.write(data.array());
                }
                byte[] data = output.toByteArray();
                InputStream raw = new ByteArrayInputStream(data);
                WaveHeader header = new WaveHeader(WaveHeader.FORMAT_PCM,
                                                   (short)1,
                                                   SAMPLE_RATE,
                                                   (short)16,
                                                   data.length);
                header.write(writeStream);
                writeStream.write(data);
                System.out.println("Wav file written sucessfully");
                fileReady.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Reads audio data in
     **/
    private class RecordingThread extends Thread {

        private void record() {
            //Extra 20 is for additional comfort room
            ByteBuffer buffer = ByteBuffer.allocateDirect(minBufferSize + 20);
            int code = recorder.read(buffer,  minBufferSize);
            if (code < 0) {
                throw new Error("Error recording the audio");
            }
            try {
                writeQueue.put(buffer);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("There was a problem");
            }
        }

        @Override
        public void run() {
            while (isRecording.get()) {
                //Do recording logic here
                record();
            }
            //Recording is now finished
            //Set ready to true
            ready.set(true);
        }

    }

    private File recording;
    private FileOutputStream writeStream;
    private AtomicBoolean ready;
    private AtomicBoolean fileReady;
    private Context context;
    private AtomicBoolean isRecording;
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
        this.ready = new AtomicBoolean(false);
        this.fileReady = new AtomicBoolean(false);
        this.isRecording = new AtomicBoolean(false);
        FILE_NAME = "audio_transcription_" + count + ".wav";
        this.recording = new File(context.getFilesDir(), FILE_NAME);
        count++;
        configureRecording();

        try {
            writeStream = new FileOutputStream(recording);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Bad file system");
        }
    }

    private void configureRecording() {
        this.minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING);
        this.minBufferSize += BUFFER_FUDGE;
        //9 is constant for unprocessed
        recorder = new AudioRecord(9, SAMPLE_RATE, CHANNEL, ENCODING, minBufferSize);

        int state = recorder.getState();
        if (state == AudioRecord.STATE_UNINITIALIZED) {
            throw new Error("Audio Configuration Failed!");
        }
    }

    @Override
    public boolean hasAudio() {
        return ready.get() && fileReady.get();
    }

    @Override
    public void startRecording() {
        isRecording.set(true);
        (new RecordingThread()).start();
        (new WriteThread()).start();
    }

    @Override
    public void stopRecording() {
        //Stop recording thread
        isRecording.set(false);
        //Blocks until the file is ready
        while(!fileReady.get()) {}
    }

    @Override
    public void cleanUp() {}

    @Override
    public File getAudio() {
        return recording;
    }
}
