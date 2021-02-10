package com.atakmap.android.plugintemplate.ptt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.plugintemplate.plugin.R;

public class StartStopDemo extends AppCompatActivity {

    public StartStopDemo() {
        final Toast toast = Toast.makeText(this, "Stop the recording.", Toast.LENGTH_SHORT);
        Log.d("Kill a m", "Pwease");
        final View helloView;
        helloView = PluginLayoutInflater.inflate(this,
                R.layout.main_layout, null);
        Button stop = helloView.findViewById(R.id.stopButton);
        //stop.setOnClickListener((v) -> {toast("Kill me please");});
        View.OnClickListener longClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.stopButton:
                        toast.show();
                        break;
                    case R.id.startButton:
                        toast.show();
                        break;
                }
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        final Button stop = (Button) findViewById(R.id.stopButton);
        final Button start = (Button) findViewById(R.id.startButton);
        Log.d("Kill a m", "Pwease");
        Toast.makeText(this, "Yo what's up.", Toast.LENGTH_SHORT).show();


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Kill he", "Dese");
                toast("Death by fire");
            }
        });
    }

    public void toast(String str) {
        Toast.makeText(this, str,
                Toast.LENGTH_LONG).show();
    }

    protected void buttonStop(View view) {
        Toast.makeText(this, "Stop the recording.", Toast.LENGTH_SHORT).show();
    }

    protected void buttonStart(View view) {
        Toast.makeText(this, "Start the recording.", Toast.LENGTH_SHORT).show();
    }
}
