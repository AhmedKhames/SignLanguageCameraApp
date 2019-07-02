package com.example.signlanguagecameraapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity  implements SurfaceHolder.Callback{



    private Camera camera;
    private Camera.PictureCallback jpegCallback;
    private final int CAMERA_REQUEST_CODE = 1;
    private SurfaceView cameraSurfaceView;
    private SurfaceHolder cameraSurfaceHolder;
    private Button startCaptureButton;
    private TextView percentageTextView,resultTextView;
    private File imgFile;
    private int cnt;
    private String pictureFilePath;
    private  UploadFiles uploadFiles;
    private String URL = "http://192.168.1.4:5000";
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cameraSurfaceView = (SurfaceView)findViewById(R.id.camera_view);
        startCaptureButton = (Button)findViewById(R.id.start_capture) ;
        //percentageTextView = (TextView)findViewById(R.id.percentage);
        resultTextView = (TextView)findViewById(R.id.the_correct_prediction);

        cameraSurfaceHolder = cameraSurfaceView.getHolder();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else {
            cameraSurfaceHolder.addCallback(this);
            cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

            startCaptureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                            startCapture();

                }
            });


        jpegCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                camera.startPreview();
                Bitmap img = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                img = rotate(img);

                img = Bitmap.createScaledBitmap(img,300,300,false);

                //compress the bitmap to jpg
                ByteArrayOutputStream compressedImage = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG,40,compressedImage);


                if ((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                        (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                }else {
                    imgFile = new File(Environment.getExternalStorageDirectory()+File.separator + makeUniqueName());
                    FileOutputStream fileOutputStream = null;
                    pictureFilePath = imgFile.getAbsolutePath();
                    Log.e("Path: ",pictureFilePath);

                    try {
                        imgFile.createNewFile();
                        fileOutputStream = new FileOutputStream(imgFile);
                        fileOutputStream.write(compressedImage.toByteArray());
                        fileOutputStream.flush();
                        uploadFiles = new UploadFiles(getApplicationContext(),URL,imgFile,resultTextView);
                        uploadFiles.execute();
                        fileOutputStream.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        
        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(60);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        try {
            camera.setPreviewDisplay(cameraSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();


    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
    private void startCapture() {
        camera.takePicture(null,null,jpegCallback);
    }

    private String makeUniqueName() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "SIGN_" + timeStamp + ".jpg";
        return pictureFile;
    }

    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true);

    }


}
