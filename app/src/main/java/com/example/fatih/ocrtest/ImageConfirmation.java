package com.example.fatih.ocrtest;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Fatih on 29.11.2015.
 */
public class ImageConfirmation extends AppCompatActivity{

    String path;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_confirmation);
        ImageView image = (ImageView) findViewById(R.id.show_image);
        Button confirmation = (Button) findViewById(R.id.confirmation_button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = extras.getString("path");
            setImage(path,image);
        }

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkNetwork(getApplicationContext())){
                    LongOperation op = new LongOperation();
                    op.execute(path);
                }
                else{
                    Context context = getApplicationContext();
                    CharSequence text = "Check Your Internet Connection";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }

    void setImage(String path, ImageView image){
        File imgFile = new  File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            image.setImageBitmap(myBitmap);
        }

    }

    boolean checkNetwork(Context ctx){
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        String parsedText="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ImageConfirmation.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                Log.e("PATH", path);
                String charset = "UTF-8";
                String apikey = "helloworld";

                String requestURL = "https://ocr.a9t9.com/api/Parse/Image";

                MultiPartUtility multipart = new MultiPartUtility(requestURL, charset);
                multipart.addFormField("apikey", apikey);
                multipart.addFormField("param_name_3", "param_value");
                multipart.addFilePart("file", new File(path));
                List<String> response = multipart.finish(); // response from server.
                String res="";
                JSONObject jObj;

                for (int i=0;i<response.size();i++){
                    Log.e("SONUC:::",response.get(i));
                    res += response.get(i);
                }
                jObj = new JSONObject(res);
                JSONArray aJsonArray = jObj.getJSONArray("ParsedResults");

                for (int i =0; i<aJsonArray.length(); i++){
                    JSONObject c = aJsonArray.getJSONObject(i);
                    parsedText = c.getString("ParsedText");
                }
                Log.i("PARSED TEXT",parsedText);
            }catch (Exception e){
                Log.e("ERROR ON BUILDING URL",e.toString());
            }
            return parsedText;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("###Arkaplan İşlemi##", "İşlem Bitti");
            if (pDialog.isShowing())
                pDialog.dismiss();

            final TextView resultView = (TextView) findViewById(R.id.result);
            resultView.setText(parsedText);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}


