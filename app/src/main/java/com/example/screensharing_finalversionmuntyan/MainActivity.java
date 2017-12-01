package com.example.screensharing_finalversionmuntyan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button buttonHelp;
    private Button buttonSettings;
    private Button buttonShare;
    private Button buttonWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonHelp = (Button)findViewById(R.id.button_help);
        buttonSettings = (Button)findViewById(R.id.button_settings);
        buttonShare = (Button)findViewById(R.id.button_share);
        buttonWatch = (Button)findViewById(R.id.button_watch);
        buttonHelp = (Button)findViewById(R.id.button_help);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivitySharing.class);
                startActivity(intent);
            }
        });

        buttonWatch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityWatch.class);
                startActivity(intent);
            }
        });
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityHelp.class);
                startActivity(intent);
            }
        });
    }
}