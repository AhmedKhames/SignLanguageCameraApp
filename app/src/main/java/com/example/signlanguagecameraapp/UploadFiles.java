package com.example.signlanguagecameraapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.MediaPlayer;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private int  index;
    private String[] classList = {"أنا","يغلق","يستمر","يأكل","فين(أين)","يسمع","يفتح","قصير","طويل","يشاهد"};
    private MediaPlayer fen,open,close,eat,continueVerb,listen,shortWord,tall,watch,ana,value;
    //private  ArrayList<MediaPlayer> predictionVoice;
    private Map<Integer,MediaPlayer> predictionVoice;


    UploadFiles(Context context,String address,File in,TextView tv){
        serverUrl = address;
        myFile = in;
        resTextView = tv;

        fen = MediaPlayer.create(context,R.raw.fen);
        ana = MediaPlayer.create(context,R.raw.ana);
        eat = MediaPlayer.create(context,R.raw.eat);
        close = MediaPlayer.create(context,R.raw.close);
        open = MediaPlayer.create(context,R.raw.open);
        continueVerb = MediaPlayer.create(context,R.raw.continue_verb);
        listen = MediaPlayer.create(context,R.raw.listen);
        shortWord = MediaPlayer.create(context,R.raw.short_word);
        tall = MediaPlayer.create(context,R.raw.tall);
        watch = MediaPlayer.create(context,R.raw.watch);

        predictionVoice = new HashMap<Integer, MediaPlayer>();

        predictionVoice.put(0,ana);
        predictionVoice.put(1,close);
        predictionVoice.put(2,continueVerb);
        predictionVoice.put(3,eat);
        predictionVoice.put(4,fen);
        predictionVoice.put(5,listen);
        predictionVoice.put(6,open);
        predictionVoice.put(7,shortWord);
        predictionVoice.put(8,tall);
        predictionVoice.put(9,watch);

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
            index = Integer.parseInt(result);
            resTextView.setText(classList[index]);
            predictionVoice.get(index).start();
        }
    }
}
