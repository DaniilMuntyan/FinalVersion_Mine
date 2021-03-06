package com.example.screensharing_finalversionmuntyan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ActivitySharing extends Activity {

    private Button startButton;
    private Button stopButton;
    private TextView textID;

    private boolean running = false;

    private MediaProjectionManager projectionManager;

    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        PublicStaticObjects.initSocket();

        textID = findViewById(R.id.sharingID);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if(PublicStaticObjects.isClicked()) {
            textID.setText(String.valueOf(PublicStaticObjects.getID()));
        }

        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                PublicStaticObjects.setStopped(false);
//                isStopped = false;
//                if(running) {
//                    return;
//                }
//                running = true;
                if(!PublicStaticObjects.isWasFirstClick()) {
                    Thread thread = new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                PublicStaticObjects.getObjectOutputStream().writeObject(-1);
                                Object object = PublicStaticObjects.getObjectInputStream().readObject();
                                id = (Integer) object;
                            } catch (IOException | ClassNotFoundException e) {
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
                    PublicStaticObjects.setIsClicked(true);
                    PublicStaticObjects.setID(id);
                    textID.setText(String.valueOf(id));
                    startActivityForResult(projectionManager.createScreenCaptureIntent(), 228);
                }
                PublicStaticObjects.setWasFirstClick(true);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                PublicStaticObjects.setStopped(true);
//                isStopped = true;
            /*    new Thread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        try {
                            PublicStaticObjects.getObjectOutputStream().writeObject(-2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });*/
            }
        });
    }

    private MediaProjection mediaProjection;
    private int displayWidth;
    private int displayHeight;

    private ImageReader imageReader;

    private Handler handler;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 228) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);

            if (mediaProjection != null) {

                //projectionStarted = true;
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int density = metrics.densityDpi;
                int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                        | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                displayHeight=size.y;
                displayWidth=size.x;


                imageReader = ImageReader.newInstance(size.x, size.y, PixelFormat.RGBA_8888, 2);

                mediaProjection.createVirtualDisplay("screencap",
                        size.x, size.y, density,
                        flags, imageReader.getSurface(), null, handler);
                imageReader.setOnImageAvailableListener(new ImageAvailableListener(), handler);
            }

        }
    }

    public void createImage(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, bytes);

        File file1 = new File(Environment.getExternalStorageDirectory() +"/captures");
        file1.mkdir();

        File file = new File(Environment.getExternalStorageDirectory() +
                "/captures/"+ System.currentTimeMillis() + ".jpg");
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes.toByteArray());
            outputStream.close();
            //Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    int i = 1;
    boolean isAlready = false;
    Bitmap bitmap;
    Bitmap bit;
    int pixelStride, rowStride, rowPadding;
    @SuppressLint("NewApi")
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
//            if(true) {
                //       Log.e(",", System.currentTimeMillis() +  " " + currentTime);
                Image image = null;
                FileOutputStream fos = null;
                //  Bitmap bitmap = null;

                ByteArrayOutputStream stream = null;
                try {
                    image = imageReader.acquireLatestImage();
                    if (image != null) {
                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();
                        if(!isAlready) {
                            pixelStride = planes[0].getPixelStride();
                            rowStride = planes[0].getRowStride();
                            rowPadding = rowStride - pixelStride * displayWidth;
                        }
                        bitmap = Bitmap.createBitmap(displayWidth + rowPadding / pixelStride,
                                displayHeight, Bitmap.Config.ARGB_4444);
                        bitmap.copyPixelsFromBuffer(buffer);
           //             Log.e("IN AVAILLIST", i++ + "");
                        bit = bitmap.copy(Bitmap.Config.ARGB_4444, false);
                        //   createImage(bit);
//                        Log.e("WTF", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + isStopped);
                        if(!PublicStaticObjects.isStopped()) {
                            sendImage(bit);
                        }
                        isAlready = true;
//                        if(!isStopped) {
//
//                        }
                 /*   Thread thread = new Thread(new Runnable() {
                        @Override
                        public synchronized void run() {
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bit.compress(Bitmap.CompressFormat.JPEG, PublicStaticObjects.getQuality(), bytes);
                            try {
                                PublicStaticObjects.getObjectOutputStream().writeObject(bytes.toByteArray());
                                PublicStaticObjects.getObjectOutputStream().flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                    thread.join();*/


                        //   sendImage(bitmap);
                    }

                } catch (Throwable e) {
                    Log.e("Throwable", e.getMessage());
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }

                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }

                    if (bitmap != null) {
                        bitmap.recycle();
                    }

                    if (image != null) {
                        image.close();
                    }
                }
            }
//        }
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] t = new byte[a.length + b.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        return t;
    }

    private synchronized void sendBytes(final byte[] buf) {
        try {
            PublicStaticObjects.getObjectOutputStream().writeObject(buf);
            PublicStaticObjects.getObjectOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void sendImage(final Bitmap bitmap) {
        Thread thread = new Thread(new Runnable() {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            @Override
            public synchronized void run() {
                bitmap.compress(Bitmap.CompressFormat.JPEG, PublicStaticObjects.getQuality(), bytes);
                try {
                    PublicStaticObjects.getObjectOutputStream().writeObject(bytes.toByteArray());
                    PublicStaticObjects.getObjectOutputStream().flush();
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
    }
}
