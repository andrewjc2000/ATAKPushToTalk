package com.atakmap.android.pushToTalk;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.contact.Contact;
import com.atakmap.android.contact.Contacts;
import com.atakmap.android.maps.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private static final HashMap<Contact, Boolean> contactMap = new HashMap<>();

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
        List<Contact> contactList = Contacts.getInstance().getAllContacts();
        if (contactList.isEmpty()) {
            TextView defaultText = settingsView.findViewById(R.id.defaultGroupChatText);
            defaultText.setText(R.string.noGroupChatsFoudMsg);
        } else {
            LinearLayout settingsLayout = settingsView.findViewById(R.id.settingsContainer);
            settingsLayout.removeView(settingsView.findViewById(R.id.defaultGroupChatText));
            contactMap.clear();
            int indexToAddAt = 1;
            for (final Contact contact: contactList) {
                contactMap.put(contact, false);
                CheckBox contactSelectedBox = new CheckBox(context);
                contactSelectedBox.setChecked(false);
                contactSelectedBox.setText(contact.getName());
                contactSelectedBox.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                contactSelectedBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            contactMap.put(contact, b);
                        }
                    }
                );
                settingsLayout.addView(contactSelectedBox, indexToAddAt++);
            }
        }
    }

    public static List<Contact> getSelectedContacts() {
        List<Contact> selectedContacts = new ArrayList<>();
        for (Contact c: contactMap.keySet()) {
            if (contactMap.get(c).equals(true)) {
                selectedContacts.add(c);
            }
        }
        return selectedContacts;
    }

    public static boolean getSettingEnabled(int id) {
        return booleanSettingsMap.get(id) == null ? false : booleanSettingsMap.get(id);
    }

    public View getSettingsView() {
        return settingsView;
    }
}
