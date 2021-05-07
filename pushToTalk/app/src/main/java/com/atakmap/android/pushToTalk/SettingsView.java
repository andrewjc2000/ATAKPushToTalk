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

/**
 * Screen in which various settings can be configured. Particularly, the user
 * can choose to enable or disable a confirmation prompt being displayed before sending a
 * transcription, and more critically allows the user to select various contacts to which the
 * transcription is sent, a prerequisite for starting recording at all.
 *
 * Currently, settings are not persisted to the system - they reset every time the app is restarted
 * or random-access memory is reset in some way.
 * @author achafos3
 * @version 1.0
 */
public class SettingsView {
    private View settingsView;
    private MapView mapView;

    /**
     * A mapping from Checkbox IDs to booleans. A Checkbox ID being mapped to true represents
     * it being enabled, and an ID being mapped to false represents it being disabled.
     */
    private static final Map<Integer, Boolean> booleanSettingsMap;
    private static final int[] checkBoxIds = {
        R.id.showPromptBeforeSending, R.id.phoneticAlphabet, R.id.convertNumbers
    };
    static {
        booleanSettingsMap = new HashMap<>();
        for (int id: checkBoxIds) {
            booleanSettingsMap.put(id, true);
        }
    }
    /**
     * A mapping from contacts to boolean values, representing whether or not the user
     * has selected each particular contact for transcriptions to be sent to. This map is left
     * empty at static-time because contact information cannot be obtained until ATAK is ready,
     * which at a minimum is guaranteed to be true in the constructor.
     */
    private static final HashMap<Contact, Boolean> contactMap = new HashMap<>();

    /**
     * Initializes the Setting screen layout. First, sets up various checkbox handlers to set
     * their respective boolean values when active or inactive. Then, initializes the Contact
     * map, adding a checkbox for each contact and setting up similar handlers to appropriately
     * change boolean values in the contact map.
     * @param mapView the encompassing MapView component for the plugin
     * @param context the current Android context
     */
    public SettingsView(MapView mapView, final Context context) {
        this.mapView = mapView;
        settingsView = PluginLayoutInflater.inflate(context, R.layout.settings_layout, null);
        /* For every checkbox ID in the saved list, try to read in the value it maps to.
         * If the value can't be found, set it to true by default. Then, create a new checkbox
         * GUI element, which its state being checked dependent on the boolean value.
         */
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
        /*
         * For every contact found in the global contacts list, add a checkbox element such that
         * the contact can be selected. Each checkbox is not selected, by default.
         */
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
                contactSelectedBox.setLayoutParams(
                    new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                );
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

    /**
     * Gets the Contacts that the user has selected for the transcription to be sent to.
     * @return a List of contacts as described above
     */
    public static List<Contact> getSelectedContacts() {
        List<Contact> selectedContacts = new ArrayList<>();
        for (Contact c: contactMap.keySet()) {
            if (contactMap.get(c).equals(true)) {
                selectedContacts.add(c);
            }
        }
        return selectedContacts;
    }

    /**
     * Of the non-contact settings, returns true if a checkbox is enabled and false otherwise.
     * @param id the Android-generated ID of the setting in question
     * @return a boolean value as described above
     */
    public static boolean getSettingEnabled(int id) {
        return booleanSettingsMap.get(id) == null ? false : booleanSettingsMap.get(id);
    }

    /**
     * Getter for the Android layout component corresponding to this screen.
     * Used to set up the tabbed view in PushToTalkDropDownReceiver
     * @return the Android component as described above
     */
    public View getSettingsView() {
        return settingsView;
    }
}
