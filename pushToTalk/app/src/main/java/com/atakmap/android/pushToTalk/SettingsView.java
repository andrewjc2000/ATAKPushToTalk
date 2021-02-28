package com.atakmap.android.pushToTalk;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;

import java.util.HashMap;
import java.util.Map;

public class SettingsView {
    private View settingsView;
    private MapView mapView;

    /**
     * All Configured Settings
     */
    private static Map<Integer, Boolean> booleanSettingsMap;
    private static final int[] checkBoxIds = {R.id.showPromptBeforeSending, R.id.phoneticAlphabet, R.id.convertNumbers};
    static {
        booleanSettingsMap = new HashMap<>();
        for (int id: checkBoxIds) {
            booleanSettingsMap.put(id, true);
        }
    }

    public SettingsView(MapView mapView, final Context context) {
        this.mapView = mapView;
        settingsView = PluginLayoutInflater.inflate(context, R.layout.settings_layout, null);
        for (int id: checkBoxIds) {
            CheckBox checkBox = settingsView.findViewById(id);
            checkBox.setChecked(booleanSettingsMap.get(id) == null ? true : booleanSettingsMap.get(id));
            final int idCopy = id;
            checkBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        booleanSettingsMap.put(idCopy, b);
                    }
                }
            );
        }
    }

    public static boolean getSettingEnabled(int id) {
        return booleanSettingsMap.get(id) == null ? false : booleanSettingsMap.get(id);
    }

    public View getSettingsView() {
        return settingsView;
    }
}
