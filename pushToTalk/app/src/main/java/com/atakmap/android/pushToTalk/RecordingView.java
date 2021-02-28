package com.atakmap.android.pushToTalk;

import java.io.InputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.pushToTalk.audioPipeline.MircophoneRecording;
import com.atakmap.android.pushToTalk.audioPipeline.Transcriber;

public class RecordingView {
    private static boolean recording = false;
    private static boolean processingRecording = false;
    private View recordingView;
    private MapView mapView;
    private Context context;

    private MircophoneRecording mic;

    public RecordingView(MapView mapView, final Context context) {
        this.context = context;
        this.mapView = mapView;
        mic = new MircophoneRecording(context);

        recordingView = PluginLayoutInflater.inflate(context, R.layout.recording_layout, null);
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.imageView) {
                    toast((recording ? "Stops" : "Starts") + " the recording of the ATAK PTT System");
                }
                return true;
            }
        };
        final ImageView toggleRecordingButton = recordingView.findViewById(R.id.imageView);
        toggleRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecording();
            }
        });
        toggleRecordingButton.setOnLongClickListener(longClickListener);
    }

    public View getRecordingView() {
        return recordingView;
    }

    private void toggleRecording() {
        if (!processingRecording) {
            RecordingView.recording = !RecordingView.recording;
            toast("Recording has " + (recording ? " Started" : " Stopped"));
            if (!recording) {
                processingRecording = true;
                toast("Processing Audio...");
                processRecording();
            } else {
                mic.startRecording();
            }
        } else {
            toast("Cannot start or stop a recording while one is currently being processed");
        }
    }

    public String getTranscription() {
        return "Some actual string here";
    }

    public void processRecording() {
        mic.stopRecording();
        InputStream recData = mic.getDataStream();
        //TODO: Might need to spin off another thread for this
        String result = mic.transcribe();

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

    public void sendMessage(String transcription) {
        // todo actually send it to a chat
        toast(transcription);
        processingRecording = false;
    }

    private void toast(String str) {
        Toast.makeText(mapView.getContext(), str, Toast.LENGTH_SHORT).show();
    }
}
