package com.example.fatih.ocrtest;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_INTENT_CLICK=302;
    String path="Deneme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button selectPhotoFromGallery = (Button) findViewById(R.id.select_photo_from_gallery);
        selectPhotoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
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
                Log.e("Image File Path", path);

                new LongOperation().execute();
            }
        }
    }
    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try{
                Log.e("PATH",path);
                String charset = "UTF-8";
                String apikey = "helloworld";

                String requestURL = "https://ocr.a9t9.com/api/Parse/Image";

                MultiPartUtility multipart = new MultiPartUtility(requestURL, charset);
                multipart.addFormField("apikey", apikey);
                multipart.addFormField("param_name_3", "param_value");
                multipart.addFilePart("file", new File(path));
                List<String> response = multipart.finish(); // response from server.
                for (int i=0;i<response.size();i++){
                    Log.e("SONUC:::",response.get(i));
                }
            }catch (Exception e){
                Log.e("ERROR ON BUILDING URL",e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("###Arkaplan İşlemi##","İşlem Bitti");
        }

        @Override
        protected void onPreExecute() {
            Log.e("###Arkaplan İşlemi##","İşlem Başladı");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
