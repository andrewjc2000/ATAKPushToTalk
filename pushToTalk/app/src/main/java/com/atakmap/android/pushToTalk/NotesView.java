package com.atakmap.android.pushToTalk;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.text.Editable;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.chat.ChatManagerMapComponent;
import com.atakmap.android.contact.Contact;
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
        data.insert(data.length(), text);
    }

}
