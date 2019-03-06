package com.example.lemanssounds;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Toolbar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivityCurrentPlace extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    public GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    public List<SuperBubble> testUniverse = new ArrayList<>();
    public LatLng currentPlace = mDefaultLocation;
    public Timer timer;
    public final Handler handler = new Handler();
    public TimerTask timerTask;
    private Toolbar toolbar;
    public boolean moveCamera = true, downloaded = true, same = false;
    private DialogFragment dlg;
    private AlertDialog.Builder ad;
    private Context context;
    private SuperBubble cur;

    private static final String TAG_GEOSOUNDS = "geosounds";
    private static final String TAG_IMAGE = "geosound_picture";
    private static final String TAG_SOUND = "geosound_soundfile";
    private static final String TAG_COLOR = "geosound_color";
    private static final String TAG_LATITUDE= "latitude";
    private static final String TAG_LONGITUDE= "longitude";
    private static final String TAG_RADIUS= "radius";
    private JSONArray geoSoundTitle = null;

    private String downloadBubbleImage, downloadBubbleSound, downloadBubbleColor, downloadBubbleLatitude, downloadBubbleLongutide, downloadBubbleRadius;

    private Bubble tempBubble = new Bubble();
    private SuperBubble tempSuperBubble = new SuperBubble();
    private static final class Lock { }
    private final Object lock = new Lock();

    public static String getHexColor(int r, int g, int b,
                                     boolean inverseOrder) {
        String red, green, blue;
        String val = Integer.toHexString(r).toUpperCase();
        red = val.length() == 1 ? "0" + val : val; // add leading zero
        val = Integer.toHexString(g).toUpperCase();
        green = val.length() == 1 ? "0" + val : val; // add leading zero
        val = Integer.toHexString(b).toUpperCase();
        blue = val.length() == 1 ? "0" + val : val; // add leading zero
        if (!inverseOrder) {
            return blue + green + red;
        } else {
            return red + green + blue;
        }
    }


    private class RetrieveMessages extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            downloaded = false;
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
            } catch (IOException e2) {
            }

            try {
                JSONObject ob = new JSONObject(json);
                geoSoundTitle = ob.getJSONArray(TAG_GEOSOUNDS);
                JSONObject c = geoSoundTitle.getJSONObject(0);
                downloadBubbleImage = c.getString(TAG_IMAGE);
                downloadBubbleSound = c.getString(TAG_SOUND);
                downloadBubbleColor = c.getString(TAG_COLOR);
                downloadBubbleLatitude= c.getString(TAG_LATITUDE);
                downloadBubbleLongutide= c.getString(TAG_LONGITUDE);
                downloadBubbleRadius= c.getString(TAG_RADIUS);
            }
            catch (JSONException c){}

            return json;
        }
        protected void onProgressUpdate(Void... progress) {
        }

        protected void onPostExecute(String result) {
            String [] rgb = downloadBubbleColor.split(",",3);
            int r = Integer.parseInt(rgb[0]), g = Integer.parseInt(rgb[1]), b = Integer.parseInt(rgb[2]);

            nulize();

            tempBubble.setColor(getHexColor(r,g,b,true));
            tempBubble.setRadius(Float.parseFloat(downloadBubbleRadius));
            tempBubble.setLatitude(Float.parseFloat(downloadBubbleLatitude));
            tempBubble.setLonguitude(Float.parseFloat(downloadBubbleLongutide));
            tempBubble.setAudioLink(downloadBubbleSound);

            tempSuperBubble.imageLink = downloadBubbleImage;
            tempSuperBubble.addBubble(tempBubble);
            testUniverse.add(tempSuperBubble);

            if(downloadBubbleSound!="") cur = tempSuperBubble;

            synchronized (lock) {
                downloaded = true;
                lock.notifyAll();
            }

        }

    }
    public void notification(String text, boolean isLong)
    {
        Toast.makeText(getApplicationContext(), text,  isLong  ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }
    private void drawAll()
    {
        for (SuperBubble s: testUniverse) {
            s.draw_bubble(mMap);
        }
    }
    public void nulize()
    {
        tempBubble = new Bubble();
        tempBubble.setPlayer(this);
        if(!same) tempSuperBubble = new SuperBubble();
    }
    public void checkForBubbleInteraction()
    {
        //float[] results = new float[5];
        updateLocationUI();
        getDeviceLocation();

        currentPlace = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());

        Location point1_this = new Location("My point"), point2_center = new Location("Center point");
        point1_this.setLatitude(currentPlace.latitude);
        point1_this.setLongitude(currentPlace.longitude);

        for (final SuperBubble s: testUniverse) {
            point2_center.setLatitude(s.getLatitude());
            point2_center.setLongitude(s.getLonguitude());

            double distance = point2_center.distanceTo(point1_this);

            if (distance <= s.getRadius())
            {
                cur = s;

                context = this;
                String title = "You are in Bubble", message = "Would you like listen?", button1String = "Yes", button2String = "No";
                ad = new AlertDialog.Builder(context);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        Toast.makeText(context, "Go on",Toast.LENGTH_LONG).show();
                        s.play_go();
                    }
                });
                ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        Toast.makeText(context, "Go off", Toast.LENGTH_LONG).show();
                    }
                });
                ad.setCancelable(true);
                ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(context, "No means no", Toast.LENGTH_LONG).show();
                    }
                });
                if(!s.getPlaying()) ad.show();
                //s.play_go();
                // notification("Checking # " + checked++ + " (" + distance + " m ) : " + "Listening", false);
                // toast = Toast.makeText(getApplicationContext(), "Checking # " + checked++ + " (" + distance + " m ) : " + "Listening", Toast.LENGTH_SHORT);
                break;
            }
            else {
                if(s.getPlaying())
                {
                    //notification("Checking # " + checked++ + " (" + distance + " m ) : " + "Stop", false);
                    //toast = Toast.makeText(getApplicationContext(), "Checking # " + checked++ + " (" + distance + " m ) : " + "Stop", Toast.LENGTH_SHORT);
                }
                else
                {
                    //notification("Checking # " + checked++ + " (" + distance + " m ) : " + "Didn't", false);
                    //toast = Toast.makeText(getApplicationContext(), "Checking # " + checked++ + " (" + distance + " m ) : " + "Didn't", Toast.LENGTH_SHORT);
                }
                s.play_stop();
            }
        }
        //toast.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setContentView(R.layout.activity_maps_current_place);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        Toast.makeText(getApplicationContext(), "Downloading data", Toast.LENGTH_LONG).show();
        downloadBubble("72ccf2966fdc7eb01c56d7481c6ef9bd", false);
        downloadBubble("59acd52a90a3cd6e85937a146641e341", false);
        downloadBubble("ef51b4a8e7b261a2ec3be0087ac5f86b", false);
        downloadBubble("2fbcf636069b2e44b4b41e7edb7e6cea", false);
        downloadBubble("80cb7300c1921fd7cf79b4ad54d45a3b", false);
        downloadBubble("fa59738b5d6378c5a9cba14e9c326c99", true);
        downloadBubble("b0b9af68a344cbdd66ce7495daf268ec", false);
        downloadBubble("2c85e1b1dbda067e3d93c5d81eb6d74d", false);
        downloadBubble("73d678cffb29c1cd4ff89a84b6cb6dec", true);
        downloadBubble("9815e1dbe76789c5f3e6c4639d1ea23e", false);
        downloadBubble("eb67ef9a0d58910cb0d70c5921787585", true);
        downloadBubble("b64912206ffec3ea07f4c715ad8840f5", false);
        Toast.makeText(getApplicationContext(), "Data downloaded", Toast.LENGTH_LONG).show();

        timer = new Timer(true);

        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        checkForBubbleInteraction();
                    }
                });
            }
        };
        timer.schedule(timerTask, 2000, 10000);

    }
    private void downloadBubble(String doi, boolean twiced)
    {
        if(twiced)
        {
            same = true;
        }
        else
        {
            same = false;
        }
        new RetrieveMessages().execute("http://soundways.eu/interface/geosound.php?_action=getGeoSound&geosounddoi=" + doi);
        synchronized (lock) {
            while (!downloaded) {
                try {
                    lock.wait();
                }
                catch (InterruptedException e)
                {
                    Toast.makeText(getApplicationContext(), "WTF", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }
    public void nav_lovate()
    {
        updateLocationUI();
        getDeviceLocation();

        notification("Location = " + currentPlace.latitude + " , " + currentPlace.longitude, false);
    }
    //menu buttoms click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_zoom)
        {
            if(moveCamera) {
                notification("Zoom was turn off", false);
                item.setTitle("ZON");
                moveCamera = false;
            }
            else
            {
                notification("Zoom was turn on", false);
                item.setTitle("ZOFF");
                moveCamera = true;
            }
        }
        if(item.getItemId() == R.id.menu_info)
        {
            dlg = new GroupDialog(cur.getImageLink(), "There may be description");
            dlg.show(getFragmentManager(), "groupdialog");
        }
        if(item.getItemId() == R.id.menu_sound)
        {
            if (cur.getAudioLink()!= "")
            {
                if(cur.getPlaying())cur.play_stop();
                else
                {
                    cur.play_go();
                }
            }
            else
            {
                nulize();
                tempBubble.setAudioLink("https://soundways.eu//data//sounds//sound_2bb043177af4dd9da57b32245c32e7b2.wav");
                tempBubble.sound_play();
                //notification("No audio file", false);
            }


        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);
                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());
                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }
        });

        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mMap.clear();
                /*MarkerOptions mp = new MarkerOptions();
                currentPlace = new LatLng(location.getLatitude(), location.getLongitude());
                mp.position(currentPlace);
                mp.title("Moi");
                mMap.addMarker(mp).setAlpha(1);*/
                drawAll();
            }
        });
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }
    public void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if(moveCamera)
                            {mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));}
                            currentPlace = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            if(moveCamera){mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));}
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            currentPlace = new LatLng(mDefaultLocation.latitude, mDefaultLocation.longitude);
                        }
                    }
                });
                //!

            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    public void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }

}
