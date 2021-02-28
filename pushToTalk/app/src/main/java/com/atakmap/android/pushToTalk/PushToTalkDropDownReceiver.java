
package com.atakmap.android.pushToTalk;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;

import com.atakmap.coremap.log.Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PushToTalkDropDownReceiver extends DropDownReceiver implements
        OnStateListener {

    public static final String TAG = PushToTalkDropDownReceiver.class.getSimpleName();

    public static final String SHOW_PLUGIN = "com.atakmap.android.pushToTalk.SHOW_PLUGIN";
    private View pushToTalkView;
    private Context pluginContext;

    public PushToTalkDropDownReceiver(final MapView mapView,
                                      final Context context) {
        super(mapView);
        try {
            this.pluginContext = context;
            pushToTalkView = PluginLayoutInflater.inflate(context, R.layout.navigation, null);
            TabHost tabHost = pushToTalkView.findViewById(R.id.tabHost);
            tabHost.setup();
            final View recordingView = new RecordingView(getMapView(), context).getRecordingView();
            final View settingsView = new SettingsView(getMapView(), context).getSettingsView();
            TabHost.TabSpec recordingSpec = tabHost.newTabSpec("recording").setIndicator("Record Audio");
            recordingSpec.setContent(
                new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String s) {
                        return recordingView;
                    }
                }
            );
            TabHost.TabSpec settingsSpec = tabHost.newTabSpec("settings").setIndicator("Settings");
            settingsSpec.setContent(
                new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String s) {
                        return settingsView;
                    }
                }
            );
            tabHost.addTab(recordingSpec);
            tabHost.addTab(settingsSpec);
        } catch (Throwable t) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(baos);
            t.printStackTrace(stream);
            toast(baos.toString());
        }
    }

    private void toast(String str) {
        Toast.makeText(getMapView().getContext(), str, Toast.LENGTH_LONG).show();
    }

    /**************************** PUBLIC METHODS *****************************/

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
