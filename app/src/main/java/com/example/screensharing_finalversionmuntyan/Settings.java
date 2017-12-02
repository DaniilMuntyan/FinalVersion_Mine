package com.example.screensharing_finalversionmuntyan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class Settings extends Activity {

    private EditText editTextIp;
    private EditText editTextPORT;

    private SeekBar seekBar;
    private TextView editTextBar;

    private Button buttonSave;

    private String folderPath = Environment.getExternalStorageDirectory().getPath() + "/ScreenSharing/";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"SetTextI18n", "WrongViewCast"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextIp = findViewById(R.id.editText_ip);
        editTextPORT = findViewById(R.id.editText_port);

        editTextBar = findViewById(R.id.textQuality);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(90);
        seekBar.incrementProgressBy(1);
        seekBar.setProgress(PublicStaticObjects.getQuality());

        editTextBar.setText(String.valueOf(seekBar.getProgress()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (seekBar.getProgress() == 0) {
                    seekBar.setProgress(1);
                }
                editTextBar.setText(String.valueOf(seekBar.getProgress()));
                PublicStaticObjects.setQuality(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    seekBar.setProgress(1);
                }
                editTextBar.setText(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    seekBar.setProgress(1);
                }
                editTextBar.setText(String.valueOf(seekBar.getProgress()));
            }
        });

        buttonSave = findViewById(R.id.button_save);

        Log.e("", (editTextPORT == null) + "");

        String s = String.valueOf(PublicStaticObjects.getIp());
        s = s.substring(1);

        editTextPORT.setText(String.valueOf(PublicStaticObjects.getPORT()));
        editTextIp.setText(s);

        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String IP = String.valueOf(editTextIp.getText());
                String PORT = String.valueOf(editTextPORT.getText());

                try {
                    InetAddress inetAddress = InetAddress.getByName(IP);
                } catch (Exception e) {
                    Context context = getApplicationContext();
                    CharSequence text = "It's not an IP!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }

                File file = new File(folderPath + "Config.txt");

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write((IP + " " + PORT).getBytes());
                    PublicStaticObjects.setIp(InetAddress.getByName(IP));
                    PublicStaticObjects.setPORT(Integer.valueOf(PORT));
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
