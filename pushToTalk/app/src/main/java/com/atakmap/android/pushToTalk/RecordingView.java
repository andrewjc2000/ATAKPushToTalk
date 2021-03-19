package com.atakmap.android.pushToTalk;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.chat.ChatManagerMapComponent;
import com.atakmap.android.contact.Contact;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.pushToTalk.audioPipeline.SpeechTranscriber;

// import com.atakmap.android.pushToTalk.audioPipeline.MicrophoneRecording;
// import com.atakmap.android.pushToTalk.audioPipeline.Transcriber;

public class RecordingView {
    private static boolean recording = false;
    private static boolean processingRecording = false;
    private View recordingView;
    private MapView mapView;
    private Context context;

    private SpeechTranscriber scribe;

    public RecordingView(MapView mapView, final Context context) {
        this.context = context;
        this.mapView = mapView;
        scribe = new SpeechTranscriber(context);
        recordingView = PluginLayoutInflater.inflate(context, R.layout.recording_layout, null);
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.startButton || v.getId() == R.id.stopButton) {
                    toast((recording ? "Stops" : "Starts") + " the recording of the ATAK PTT System");
                }
                return true;
            }
        };
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording || !SettingsView.getSelectedContacts().isEmpty()) {
                    FrameLayout container = recordingView.findViewById(R.id.buttonContainer);
                    View removedView = container.getChildAt(1);
                    container.removeView(removedView);
                    container.addView(removedView, 0);
                    toggleRecording();
                } else {
                    toast("You must select one or more contacts in Settings before you record");
                }
            }
        };
        final ImageView startButton = recordingView.findViewById(R.id.startButton);
        final ImageView stopButton = recordingView.findViewById(R.id.stopButton);
        startButton.setOnLongClickListener(longClickListener);
        stopButton.setOnLongClickListener(longClickListener);
        startButton.setOnClickListener(clickListener);
        stopButton.setOnClickListener(clickListener);
    }

    public View getRecordingView() {
        return recordingView;
    }

    private void toggleRecording() {
        if (!processingRecording) {
            RecordingView.recording = !RecordingView.recording;
            if (!recording) {
                toast("Stopping Recording");
                processingRecording = true;
                toast("Processing Audio...");
                processRecording();
            } else {
                if (scribe.startRecording()) {
                    toast("Recording...");
                } else {
                    toast("Plugin still configuring, please try again later.");
                }
            }
        } else {
            toast("Cannot start or stop a recording while one is currently being processed");
        }
    }

    public String getTranscription() {
        int waitCountMax = 100;
        int waitCount = 0;
        while (!scribe.isResultReady() && (waitCount < waitCountMax)) {
            Log.i("RecordingView", "Waiting on Transcription to be ready!");
            waitCount++;
        }
        return scribe.getResult();
    }

    public void processRecording() {
        scribe.stopRecording();
        String result = getTranscription();

        boolean showConfirmationPrompt = SettingsView.getSettingEnabled(R.id.showPromptBeforeSending);
        if (showConfirmationPrompt) {
            showConfirmationPrompt(result);
        } else {
            sendMessage(result);
        }
    }

    public void showConfirmationPrompt(final String transcription) {
        final EditText input = (EditText) PluginLayoutInflater.inflate(context, R.layout.transcription_confirm_promt, null);;
        input.setText(transcription);

        new AlertDialog.Builder(mapView.getContext())
            .setView(input)
            .setTitle("Edit Transcription Before Sending")
            .setPositiveButton("Send Message",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendMessage(input.getText().toString());
                    }
                }
            )
            .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        processingRecording = false;
                    }
                }
            )
            .show();
    }

    public synchronized void sendMessage(String transcription) {
        List<Contact> toSend = SettingsView.getSelectedContacts();
        if (toSend.isEmpty()) {
            toast("Did not send message because no contacts were selected.");
        } else {
            toast(transcription);
            ChatManagerMapComponent.getInstance().sendMessage(transcription, toSend);
        }
        processingRecording = false;
    }

    private void toast(String str) {
        Toast.makeText(mapView.getContext(), str, Toast.LENGTH_SHORT).show();
    }
}
