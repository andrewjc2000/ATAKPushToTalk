package com.atakmap.android.pushToTalk;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.text.Editable;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;

/**
 * Class representing the Notes tab of the Push-To-Talk plugin
 * @author jkelly80
 * @version 1.0
 */
public class NotesView {

    private View notesView;
    private Context context;
    private MapView mapView;

    private EditText textPane;


    /**
     * 2-argument constructor for NotesView that sets up the layout elements needed for the
     * Notes tab
     * @param mapView the encompassing MapView component for the plugin
     * @param context the current Android context
     */
    public NotesView(MapView mapView, Context context) {
        this.context = context;
        this.mapView = mapView;
        this.notesView = PluginLayoutInflater.inflate(context, R.layout.notes_layout, null);
        this.textPane = notesView.findViewById(R.id.plain_text_input);
    }

    public View getAndroidComponent() {
        return notesView;
    }

    /**
     * Adds some text to the overall Editable text element of the Notes View
     * @param text the text to be added to the editable text
     */
    public void addText(String text) {
        Editable data = textPane.getText();
        data.insert(data.length(), buildEntry(text));
    }

    private String buildEntry(String data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd@HH:mm", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        String prefix = "\n\n[" + currentDateAndTime + "] ";
        return prefix + data;
    }

}
