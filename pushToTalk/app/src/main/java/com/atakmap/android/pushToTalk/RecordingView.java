package com.atakmap.android.pushToTalk;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;

public class RecordingView {
    private static boolean recording = false;
    private View recordingView;
    private MapView mapView;

    public RecordingView(MapView mapView, final Context context) {
        this.mapView = mapView;
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
                toast("Recording has " + (recording ? " Started" : " Stopped"));
                toast("Current checkbox value: " + SettingsView.promptUserToConfirm);
            }
        });
        toggleRecordingButton.setOnLongClickListener(longClickListener);
    }

    public View getRecordingView() {
        return recordingView;
    }

    public static void toggleRecording() {
        RecordingView.recording = !RecordingView.recording;
    }

    private void toast(String str) {
        Toast.makeText(mapView.getContext(), str, Toast.LENGTH_SHORT).show();
    }
}
