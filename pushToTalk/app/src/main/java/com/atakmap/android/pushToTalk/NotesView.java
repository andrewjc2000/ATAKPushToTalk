package com.atakmap.android.pushToTalk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.text.Editable;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapView;

public class NotesView {

    private View notesView;
    private Context context;
    private MapView mapView;

    private EditText textPane;


    public NotesView(MapView mapView, Context context) {
        this.context = context;
        this.mapView = mapView;
        notesView = PluginLayoutInflater.inflate(context, R.layout.notes_layout, null);

        textPane = notesView.findViewById(R.id.plain_text_input);

    }

    public View getNotesView() {
        return notesView;
    }

    public void addText(String text) {
        Editable data = textPane.getText();
        data.insert(data.length(), buildEntry(text));
    }

    private String buildEntry(String data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd@HH:mm", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        String prefix = "\n\n[" + currentDateandTime + "] ";
        return prefix + data;
    }

}
