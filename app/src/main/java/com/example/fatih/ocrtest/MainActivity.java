package com.example.fatih.ocrtest;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final int MY_INTENT_CLICK=302;
    String path;
    SendRequest req = new SendRequest();
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button selectPhotoFromGallery = (Button) findViewById(R.id.select_photo_from_gallery);
        selectPhotoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
/*                try {
                    res = req.execute(path).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("REPONSE GET FROM MAIN",res);*/
            }
        });
    }

    public void selectImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), MY_INTENT_CLICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == MY_INTENT_CLICK)
            {
                if (null == data) return;

                String selectedImagePath;
                Uri selectedImageUri = data.getData();

                //MEDIA GALLERY
                selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
                path = selectedImagePath;
                Log.i("Image File Path", ""+selectedImagePath);
            }
        }

        try {
            res = req.execute(path).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("REPONSE GET FROM MAIN",res);
    }
}

    class SendRequest extends AsyncTask<String, Void, String>{

        String finalResponse;

        @Override
        protected String doInBackground(String... params) {
        try{
            Log.i("PATH",params[0]);
            String url = "https://ocr.a9t9.com/api/Parse/Image";
            String charset = "UTF-8";
            String apikey = "helloworld";
            String file = params[0]; // Path'ten file oluşturup dene!!!

            String query = String.format("apikey=%s&file=%s",
                    URLEncoder.encode(apikey, charset),
                    URLEncoder.encode(file, charset));

            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            OutputStream output = connection.getOutputStream();
                output.write(query.getBytes(charset));


            InputStream response = connection.getInputStream();
            java.util.Scanner s = new java.util.Scanner(response).useDelimiter("\\A");
            finalResponse = s.hasNext() ? s.next() : "";

        }catch (Exception e){
            Log.e("ERROR ON BUILDING URL",e.toString());
        }

        return finalResponse;
        }
    }

