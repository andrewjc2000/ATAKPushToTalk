
package com.atakmap.android.pushToTalk;

import android.content.Context;
import android.content.Intent;
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter;

import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDownMapComponent;

import com.atakmap.coremap.log.Log;

/**
 * Boilerplate to create a map component containing the Push-To-Talk plugin
 * @author achafos3
 * @version 1.0
 */
public class PushToTalkMapComponent extends DropDownMapComponent {

    private static final String TAG = "PluginTemplateMapComponent";
    private Context pluginContext;
    private PushToTalkDropDownReceiver ddr;

    @Override
    public void onCreate(final Context context, Intent intent, final MapView view) {
        context.setTheme(R.style.ATAKPluginTheme);
        super.onCreate(context, intent, view);
        pluginContext = context;
        ddr = new PushToTalkDropDownReceiver(view, context);
        Log.d(TAG, "registering the plugin filter");
        DocumentedIntentFilter ddFilter = new DocumentedIntentFilter();
        ddFilter.addAction(PushToTalkDropDownReceiver.SHOW_PLUGIN);
        registerDropDownReceiver(ddr, ddFilter);
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
    }

}
