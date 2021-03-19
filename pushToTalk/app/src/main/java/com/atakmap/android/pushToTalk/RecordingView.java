package com.atakmap.android.pushToTalk;

import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import com.atakmap.android.pushToTalk.audioPipeline.SpeechTranscriber;

import com.atakmap.coremap.filesystem.FileSystemUtils;
import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.chat.ChatManagerMapComponent;
import com.atakmap.android.maps.MapView;

public class RecordingView {
    private static boolean recording = false;
    private static boolean processingRecording = false;
    private View recordingView;
    private MapView mapView;
    private Context context;

    private SpeechTranscriber scribe;

    private final String TAG = "RecordingView";



    public RecordingView(MapView mapView, final Context context) {
        this.context = context;
        this.mapView = mapView;
        toast("One");
        this.scribe = new SpeechTranscriber(context);
        toast("Two");

        recordingView = PluginLayoutInflater.inflate(context, R.layout.recording_layout, null);
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.imageView) {
                    toast(context.getFilesDir().toString());
                    String error = "";

                    try {



                        File startDir = context.getFilesDir();
                        File maybe = FileSystemUtils.getItem(FileSystemUtils.TOOL_DATA_DIRECTORY);

                        File myDir = new File(maybe, "myTools");

                        toast(maybe.toString());

                        toast("" + myDir.createNewFile());


                    } catch (Exception e) {
                        error = e.getMessage();
                        toast("Error: " + error);
                    }


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
                processingRecording = true; //FIXME: This is a racy variable
                toast("Processing Audio...");
                processRecording();
            } else {
                //Start the recording here
                while(!scribe.startRecording()) {
                    Log.i(TAG, "Waiting for recording to be ready");
                };
            }
        } else {
            toast("Cannot start or stop a recording while one is currently being processed");
        }
    }

    public String getTranscription() {
        //Dump transcription here
        while (!scribe.isResultReady()) {
            Log.i(TAG, "Waiting for result to be ready");
        };
        return scribe.getResult();
    }

    public void processRecording() {
        //Make sure recording is stopped
        scribe.stopRecording();
        //TODO: Might need to spin off another thread for this
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

    public void sendMessage(String transcription) {
        ChatManagerMapComponent.getInstance().sendMessage(transcription, SettingsView.getSelectedContacts());
        toast(transcription);
        processingRecording = false;
    }

    private void toast(String str) {
        Toast.makeText(mapView.getContext(), str, Toast.LENGTH_SHORT).show();
    }
}
