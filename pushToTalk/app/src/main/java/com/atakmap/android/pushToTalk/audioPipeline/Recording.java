package com.atakmap.android.pushToTalk.audioPipeline;

import java.io.File;

/**
 * Provides an interface for Audio Recordings. Each method of audio input should
 * implement this interface. This provides a way of using both microphone input
 * and radio input (should that be possible).
 **/
public interface Recording {

    /**
     * Returns true if this recording is ready to be transcribed
     **/
    public boolean hasAudio();

    /**
     * Starts recording audio
     **/
    public void startRecording();

    /**
     * Stops recording audio
     **/
    public void stopRecording();

    /**
     * Returns a wav file with the saved audio
     **/
    public File getAudio();

    /**
     * Provides a mechanism to potentiall delete recordings to save space
     **/
    public void cleanUp();

}
