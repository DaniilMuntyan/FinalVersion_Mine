package com.example.screensharing_finalversionmuntyan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class ActivityWatch extends AppCompatActivity {

    private FloatingActionButton startFab;
    private FloatingActionButton stopFab;
    private ImageView imageView;
    private ActivityWatch activityWatch;

    private EditText editText;

    private boolean canJoin = false;

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        activityWatch = this;
        editText = (EditText) findViewById(R.id.editText_ID);

        startFab =  (FloatingActionButton) findViewById(R.id.startFab);
        imageView =  (ImageView) findViewById(R.id.imageView);

        PublicStaticObjects.initSocket();

        startFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Can I join someone?
                mustBeAlive = true;
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PublicStaticObjects.getObjectOutputStream().writeObject(-3);
                            PublicStaticObjects.getObjectOutputStream().writeObject(Integer.valueOf(String.valueOf(editText.getText())));
                            try {
                                Object object = PublicStaticObjects.getObjectInputStream().readObject();
                                if (!object.equals("-3")) {
                                    canJoin = true;
                                } else {
                                    canJoin = false;
//                        TODO: toast
//                        Toast.makeText();
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (canJoin) {
                    Thread thread1 = new Thread(new Receiver(activityWatch, imageView));
                    thread1.setPriority(Thread.MAX_PRIORITY);
                    thread1.start();
                }
            }
        });

    /*    stopFab.setOnClickListener((v) -> {
            new Thread(() -> {
                try {
                    PublicStaticObjects.getObjectOutputStream().writeObject(-4);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            if(thread == null) {
                return;
            }
            if(thread.isAlive()) {
                thread.interrupt();
                mustBeAlive = false;
            }
        });*/

    }

    public static boolean isMustBeAlive() {
        return mustBeAlive;
    }

    public static void setMustBeAlive(boolean mustBeAlive) {
        ActivityWatch.mustBeAlive = mustBeAlive;
    }

    private static boolean mustBeAlive = false;



}
