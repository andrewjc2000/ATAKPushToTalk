package com.atakmap.android.pushToTalk.audioPipeline;

import java.io.File;
import android.content.Context;

public class MicrophoneRecording implements Recording {

    private File recording;
    private boolean ready;
    private Context context;

    public MicrophoneRecording(Context context) {
        this.context = context;
        this.ready = false;
        this.recording = null;
    }

    public boolean hasAudio() {
        return ready;
    }

    public void recordAudio() {

    }
}
