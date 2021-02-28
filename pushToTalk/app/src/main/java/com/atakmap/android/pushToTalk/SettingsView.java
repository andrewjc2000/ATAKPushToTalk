package com.atakmap.android.pushToTalk;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;

public class SettingsView {
    private View settingsView;
    private MapView mapView;

    /**
     * All Configured Settings
     */
    public static boolean promptUserToConfirm = true;

    public SettingsView(MapView mapView, final Context context) {
        settingsView = PluginLayoutInflater.inflate(context, R.layout.settings_layout, null);
        CheckBox promptToConfirmCheckBox = settingsView.findViewById(R.id.showPromptBeforeSending);
        promptToConfirmCheckBox.setChecked(promptUserToConfirm);
        promptToConfirmCheckBox.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SettingsView.promptUserToConfirm = b;
                }
            }
        );
    }

    public View getSettingsView() {
        return settingsView;
    }
}
