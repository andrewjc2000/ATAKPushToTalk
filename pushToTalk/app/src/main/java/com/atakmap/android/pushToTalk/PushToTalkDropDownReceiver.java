
package com.atakmap.android.pushToTalk;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.plugintemplate.plugin.R;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;

import com.atakmap.coremap.log.Log;

public class PushToTalkDropDownReceiver extends DropDownReceiver implements
        OnStateListener {

    public static final String TAG = PushToTalkDropDownReceiver.class.getSimpleName();

    public static final String SHOW_PLUGIN = "com.atakmap.android.pushToTalk.SHOW_PLUGIN";
    private final View pushToTalkView;
    private final Context pluginContext;
    private boolean recording;

    public PushToTalkDropDownReceiver(final MapView mapView,
                                      final Context context) {
        super(mapView);
        this.pluginContext = context;
        pushToTalkView = PluginLayoutInflater.inflate(context, R.layout.main_layout, null);
        this.recording = false;
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.imageView) {
                    toast((recording ? "Stops" : "Starts") + " the recording of the ATAK PTT System");
                }
                return true;
            }
        };

        final ImageView toggleRecordingButton = pushToTalkView.findViewById(R.id.imageView);
        toggleRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecording();
                toast("Recording has " + (recording ? " Started" : " Stopped"));
            }
        });
        toggleRecordingButton.setOnLongClickListener(longClickListener);
    }

    private void toast(String str) {
        Toast.makeText(getMapView().getContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**************************** PUBLIC METHODS *****************************/

    public void toggleRecording() {
        this.recording = !recording;
    }

    public void disposeImpl() {
    }

    /**************************** INHERITED METHODS *****************************/

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null) {
            return;
        }

        if (action.equals(SHOW_PLUGIN)) {
            Log.d(TAG, "showing plugin drop down");
            showDropDown(pushToTalkView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                HALF_HEIGHT, false);
        }
    }

    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownVisible(boolean v) {
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
    }

}