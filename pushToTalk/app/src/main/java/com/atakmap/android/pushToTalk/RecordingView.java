package com.atakmap.android.pushToTalk;

import java.util.List;

import android.util.Log;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.chat.ChatManagerMapComponent;
import com.atakmap.android.contact.Contact;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.pushToTalk.audioPipeline.SpeechTranscriber;

/**
 * Screen in which recording and transcription take place. This creates a basic GUI
 * with a large recording button, and also sets up a prompt that optionally appears
 * depending on settings enabling the user to review a transcription before it is sent.
 * @author achafos3
 * @version 1.0
 */
public class RecordingView {
    private static boolean recording = false;
    /**
     * Is set to true during the entire period in which the transcription is being processed
     * AND edited. Is not set back to false until either the transcription sending is cancelled
     * or completed.
     */
    private static boolean processingRecording = false;
    private View recordingView;
    private MapView mapView;
    private Context context;
    private NotesView notes;
    private SpeechTranscriber scribe;

    /**
     * Initializes the Recording View, which entails setting up the proper short/long press
     * handlers, which in turn call other functions here and in the transcriber class in order
     * to obtain relevant settings and perform transcription
     * @param mapView the encompassing MapView component for the plugin
     * @param context the current Android context
     * @param notes the NotesView to which all transcriptions will be sent, in addition to chat
     */
    public RecordingView(MapView mapView, final Context context, NotesView notes) {
        this.context = context;
        this.mapView = mapView;
        this.notes = notes;
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
                if (recording || canStartRecording()) {
                    // swaps the current button with the one immediately behind it. On the GUI,
                    // the "Stop Recording" button is behind the "Start Recording" button or vice versa.
                    RelativeLayout container = recordingView.findViewById(R.id.buttonContainer);
                    View removedView = container.getChildAt(1);
                    container.removeView(removedView);
                    container.addView(removedView, 0);
                    toggleRecording();
                } else {
                    if (SettingsView.getSelectedContacts().isEmpty()) {
                        toast("You must select one or more contacts in Settings before you record");
                    } else {
                        toast("Plugin still configuring, please try again later.");
                    }
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

    /**
     * Getter for the Android layout component corresponding to this screen.
     * Used to set up the tabbed view in PushToTalkDropDownReceiver
     * @return the Android component as described above
     */
    public View getRecordingView() {
        return recordingView;
    }

    private boolean canStartRecording() {
        return !recording && !SettingsView.getSelectedContacts().isEmpty() && scribe.getReady();
    }

    private void toggleRecording() {
        if (!processingRecording) {
            if (recording) {
                RecordingView.recording = false;
                toast("Recording has stopped.");
                processingRecording = true;
                processRecording();
            } else {
                if (scribe.startRecording()) {
                    RecordingView.recording = true;
                    toast("Recording...");
                }
            }
        } else {
            toast("Cannot start or stop a recording while one is currently being processed");
        }
    }

    private String getTranscription() {
        int waitCountMax = 100;
        int waitCount = 0;
        while (!scribe.isResultReady() && (waitCount < waitCountMax)) {
            Log.i("RecordingView", "Waiting on Transcription to be ready!");
            waitCount++;
        }
        return scribe.getResult();
    }

    private void processRecording() {
        scribe.stopRecording();
        String result = getTranscription();
        boolean showConfirmationPrompt = SettingsView.getSettingEnabled(R.id.showPromptBeforeSending);
        // showConfirmationPrompt will also make a call to sendMessage, just only after the user
        // confirms the transcription is correct and presses "Send Message"
        if (showConfirmationPrompt) {
            showConfirmationPrompt(result);
        } else {
            sendMessage(result);
        }
    }

    private void showConfirmationPrompt(final String transcription) {
        final EditText input =
            (EditText) PluginLayoutInflater.inflate(context, R.layout.transcription_confirm_promt, null);
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

    private synchronized void sendMessage(String transcription) {
        //Send to Notes
        notes.addText(transcription);
        //Send to contacts
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
