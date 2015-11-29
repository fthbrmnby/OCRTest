package com.example.fatih.ocrtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final int MY_INTENT_CLICK = 302;
    String path = "";

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

    public void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), MY_INTENT_CLICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == MY_INTENT_CLICK) {
                if (null == data) return;

                String selectedImagePath;
                Uri selectedImageUri = data.getData();

                //MEDIA GALLERY
                selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
                path = selectedImagePath;
                Log.e("Image File Path", path);

                Intent i = new Intent(MainActivity.this, ImageConfirmation.class);
                i.putExtra("path", path);
                startActivity(i);
            }
        }
    }
}
