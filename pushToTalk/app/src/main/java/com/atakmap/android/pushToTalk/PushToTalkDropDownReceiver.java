
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

/**
 * The overall receiver component containing the ATAK Push-to-Talk plugin
 * @author achafos3
 * @version 1.0
 */
public class PushToTalkDropDownReceiver extends DropDownReceiver implements OnStateListener {

    public static final String TAG = PushToTalkDropDownReceiver.class.getSimpleName();
    public static final String SHOW_PLUGIN = "com.atakmap.android.pushToTalk.SHOW_PLUGIN";

    private View pushToTalkView;
    private Context pluginContext;

    /**
     * Initializes the overall "receiver" component that consitutes the entire layout
     * and functionality of the Push-to-Talk plugin. Here, the tabbed header is initialized
     * with the three screens of our application.
     * @param mapView the encompassing MapView component for the plugin
     * @param context the current Android context
     */
    public PushToTalkDropDownReceiver(final MapView mapView,
                                      final Context context) {
        super(mapView);
        try {
            this.pluginContext = context;
            pushToTalkView = PluginLayoutInflater.inflate(context, R.layout.navigation, null);
            TabHost tabHost = pushToTalkView.findViewById(R.id.tabHost);
            tabHost.setup();
            NotesView notesView = new NotesView(getMapView(), context);
            final View notesAndroidComponent = notesView.getAndroidComponent();
            final View recordingView = new RecordingView(getMapView(), context, notesView).getRecordingView();
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
            TabHost.TabSpec notesSpec = tabHost.newTabSpec("notes").setIndicator("Notes");
            notesSpec.setContent(
                new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String s) {
                        return notesAndroidComponent;
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
            tabHost.addTab(notesSpec);
            tabHost.addTab(settingsSpec);
        } catch (Throwable t) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(outputStream);
            t.printStackTrace(stream);
            Log.e(TAG, outputStream.toString());
            toast("There was an error initializing this plugin. See logs for more information");
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
        if (action != null && action.equals(SHOW_PLUGIN)) {
            showDropDown(pushToTalkView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                HALF_HEIGHT, false);
        }
    }

    /**
     * All of these methods with empty bodies are necessary because they are inherited from an
     * interface, which means they must be overridden. However, we don't need any specific
     * functionality on these events, so we leave these method bodies blank.
     */
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
