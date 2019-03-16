package com.example.lemanssounds;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JSONTests extends AppCompatActivity {
    public static String link_canal1 = "http://soundways.eu/interface/geosound.php?_action=getGeoSound&geosounddoi=156c4db87bfb7325011fd85470b46419";
    private static final String TAG_GEOSOUNDS = "geosounds";
    private static final String TAG_IMAGE = "geosound_picture";
    private static final String TAG_SOUND = "geosound_soundfile";
    private static final String TAG_COLOR = "geosound_color";
    private static final String TAG_LATITUDE= "latitude";
    private static final String TAG_LONGITUDE= "longitude";
    JSONArray user = null;

    Button btnPushMe, button_stop;
    Toolbar toolbar;
    ImageView imgVw;

    String image_url="";
    String sound_url="";
    String color="";
    Float lat, lon;

    MyMediaPlayer mp;

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    private class RetrieveMessages extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            HttpClient client = new DefaultHttpClient();
            String json = "";
            try {
                String line = "";
                HttpGet request = new HttpGet(urls[0]);
                HttpResponse response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((line = rd.readLine()) != null) {
                    json += line + System.getProperty("line.separator");
                }
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            try {
                JSONObject ob = new JSONObject(json);
                user = ob.getJSONArray(TAG_GEOSOUNDS);
                JSONObject c = user.getJSONObject(0);
                image_url = c.getString(TAG_IMAGE);
                sound_url = c.getString(TAG_SOUND);}
             catch (JSONException c){}

            return json;
        }
        protected void onProgressUpdate(Void... progress) {
        }

        protected void onPostExecute(String result) {
            new DownloadImageTask(imgVw).execute(image_url);
            mp = new MyMediaPlayer();
            mp.initialize(JSONTests.this, sound_url);
            mp.play();
        }

    }
    private void toolBarSet() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle("JSON Page");
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsontests);

        imgVw = (ImageView) findViewById(R.id.imageView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolBarSet();
        btnPushMe = (Button) findViewById(R.id.buttonPshMe);
        button_stop = (Button) findViewById(R.id.button_stop);

        btnPushMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new RetrieveMessages().execute(link_canal1);
            }
        });

        button_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mp.mediaPlayer.isPlaying())
                {
                    mp.pause();
                    Toast.makeText(getApplicationContext(), "Stopped",  Toast.LENGTH_SHORT).show();
                    button_stop.setText("Play");
                }
                else
                {
                    mp.play();
                    Toast.makeText(getApplicationContext(), "Played",  Toast.LENGTH_SHORT).show();
                    button_stop.setText("Pause");

                }
            }
        });

    }
}
