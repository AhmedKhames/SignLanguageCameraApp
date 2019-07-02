package com.example.signlanguagecameraapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.audiofx.DynamicsProcessing;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;



public class UploadFiles extends AsyncTask<String, Void, String> {



    private Context context;
    private String serverUrl;
    private TextView resTextView;
    private HttpURLConnection urlConnection = null;
    private OutputStream outputStream;
    private URL url;
    private File myFile;
    private BufferedWriter httpRequestBodyWrite;
    private FileInputStream inputStreamToImageFile;
    private int bytesRead;
    private byte[] dataBuffer = new byte[1024*1024];
    private BufferedReader httpResponseReader;
    private String lineRead,res,arabicResponse;
    private TextToSpeech textToSpeech;
    private int  speech;

    UploadFiles(String address,File in,TextView tv){
        serverUrl = address;
        myFile = in;
        resTextView = tv;

    }

    UploadFiles(Context context,String address,File in,TextView tv){
        serverUrl = address;
        myFile = in;
        resTextView = tv;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            url = new URL(serverUrl);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type","image/jpeg");

            outputStream = urlConnection.getOutputStream();
            httpRequestBodyWrite = new BufferedWriter(new OutputStreamWriter(outputStream));

            httpRequestBodyWrite.flush();

            inputStreamToImageFile = new FileInputStream(myFile);


            while ((bytesRead = inputStreamToImageFile.read(dataBuffer)) != -1){
                outputStream.write(dataBuffer,0,bytesRead);
            }
            outputStream.flush();


            httpResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            while((lineRead = httpResponseReader.readLine()) != null) {
                res = lineRead;
            }

            outputStream.close();
            httpRequestBodyWrite.close();

        } catch (Exception e) {
            e.toString();
        }
        return res;
    }
    @Override
    protected void onPostExecute(String result) {

        Log.d("Response",result);

        if (result == null){
            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
        } else {
            resTextView.setText(arabicWord(result));
        }
    }


    private String arabicWord(String responseMessage){
        arabicResponse = null;
    switch (responseMessage){
            case "tall":
                arabicResponse = "طويل";
                break;
            case "close":
                arabicResponse = "يغلق";
                break;
            case "open":
                arabicResponse = "يفتح";
                break;
            case "fen":
                arabicResponse = "فين(أين)";
                break;
            case "short":
                arabicResponse = "قصير";
                break;
            case "watch":
                arabicResponse = "يشاهد";
                break;
            case "listen":
                arabicResponse = "يسمع";
                break;
            case "continue":
                arabicResponse = "يستمر";
                break;
            case "eat":
                arabicResponse = "يأكل";
                break;
        }
        return arabicResponse;
    }

    private void textToSpeech(Context appContext){
        textToSpeech = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS){
                    speech = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });


    }

}
