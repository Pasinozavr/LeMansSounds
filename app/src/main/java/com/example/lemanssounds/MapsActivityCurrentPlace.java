package com.example.lemanssounds;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.widget.Toolbar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivityCurrentPlace extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private String allBubbleDois = "";
    private static final class Lock { }
    private final Object lock = new Lock();
    private PowerManager.WakeLock mWakeLock;
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
    private class GetContacts extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

            synchronized (lock)
            {
                while(downloaded)
                {
                    try{
                        lock.wait();
                    }
                    catch(InterruptedException e)
                    {

                    }
                }
            }
            synchronized (lock) {
                downloaded = false;
                lock.notifyAll();
            }
        }

        @Override
        protected Void doInBackground(String... arg) {

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(arg[0]);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject ob = new JSONObject(jsonStr);
                    geoSoundTitle = ob.getJSONArray(TAG_GEOSOUNDS);
                    JSONObject c = geoSoundTitle.getJSONObject(0);
                    downloadBubbleImage = c.getString(TAG_IMAGE);
                    downloadBubbleSound = c.getString(TAG_SOUND);
                    downloadBubbleColor = c.getString(TAG_COLOR);
                    downloadBubbleLatitude= c.getString(TAG_LATITUDE);
                    downloadBubbleLongutide= c.getString(TAG_LONGITUDE);
                    downloadBubbleRadius= c.getString(TAG_RADIUS);
                    downloadBubbleDescription = c.getString(TAG_DESCRIPTION);
                    downloadBubbleTitle = c.getString(TAG_TITLE);

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            nulize();

            int r = 123, g = 123, b = 123;

            if(!downloadBubbleColor.equals("") && downloadBubbleColor != null) {
                String [] rgb = downloadBubbleColor.split(",",3);
                r = Integer.parseInt(rgb[0]);
                g = Integer.parseInt(rgb[1]);
                b = Integer.parseInt(rgb[2]);
                tempBubble.setColor(getHexColor(r,g,b,true));
            }


            tempBubble.setLatitude(Float.parseFloat(downloadBubbleLatitude));
            tempBubble.setRadius(Integer.parseInt(downloadBubbleRadius));
            tempBubble.setLonguitude(Float.parseFloat(downloadBubbleLongutide));
            tempBubble.setAudioLink(downloadBubbleSound);
            tempBubble.setImageLink(downloadBubbleImage);
            tempBubble.setDescription(downloadBubbleDescription);
            tempBubble.setName(downloadBubbleTitle);

            tempBubble.player = new MyMediaPlayer();
            tempBubble.player.initialize(MapsActivityCurrentPlace.this, emptySound);


            if(downloadBubbleSound != "" && downloadBubbleSound != null)
            {
                if(downloadBubbleSound.length()<10)downloadBubbleSound = emptySound;
                tempBubble.player.initialize(MapsActivityCurrentPlace.this, downloadBubbleSound);
            }
            tempBubble.player.play();

            tempSuperBubble.addBubble(tempBubble);
            testUniverse.add(tempSuperBubble);

            if(downloadBubbleSound != "") cur = tempSuperBubble;

            synchronized (lock) {
                downloaded = true;
                lock.notifyAll();
            }
            }
        }


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
    public boolean moveCamera = false, downloaded = false, same = false, notif = false, firstGo = true;
    private DialogFragment dlg;
    private AlertDialog.Builder ad;
    private Context context;
    private SuperBubble cur;

    private Bubble [] badcodeBubble = new Bubble[16];

    private static final String TAG_GEOSOUNDS = "geosounds";
    private static final String TAG_IMAGE = "geosound_picture";
    private static final String TAG_SOUND = "geosound_soundfile";
    private static final String TAG_COLOR = "geosound_color";
    private static final String TAG_LATITUDE= "latitude";
    private static final String TAG_LONGITUDE= "longitude";
    private static final String TAG_RADIUS = "radius";
    private static final String TAG_DESCRIPTION = "geosound_description";
    private static final String TAG_TITLE = "geosound_title";
    private static final String TAG_AUTOR = "geosound_author";
    private JSONArray geoSoundTitle = null;

    private String emptySound = "https://soundways.eu/data/sounds/sound_a3ca2bb33e0708c5c9bbd01d19370407.mp3";
    private String downloadBubbleTitle, downloadBubbleDescription, downloadBubbleImage, downloadBubbleSound, downloadBubbleColor, downloadBubbleLatitude, downloadBubbleLongutide, downloadBubbleRadius;

    private Bubble tempBubble = new Bubble();
    private SuperBubble tempSuperBubble = new SuperBubble();




    @Override
    public void onMapClick(LatLng point) {
        Location point1_this = new Location("My point"), point2_center = new Location("Center point");

        point1_this.setLatitude(point.latitude);
        point1_this.setLongitude(point.longitude);

        for (final SuperBubble s: testUniverse) {
            point2_center.setLatitude(s.getLatitude());
            point2_center.setLongitude(s.getLonguitude());

            double distance = point2_center.distanceTo(point1_this);

            if (distance <= s.getRadius()) {
                cur = s;
                dlg = new GroupDialog(s.getImageLink(), s.getDescription(), s.getName(), cur);
                dlg.show(getFragmentManager(), "groupdialog");
                break;
            }
        }

    }

    private void createPlayer(final Bubble b)
    {
        b.setPlayer(this);
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

            if (distance <= s.getRadius()) {
                if (!notif)
                {
                    notif = true;
                    cur = s;

                context = this;
                String title = "You are in Bubble", message = "Would you like listen?", button1String = "Yes", button2String = "No";
                ad = new AlertDialog.Builder(context);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        Toast.makeText(context, "Go on", Toast.LENGTH_LONG).show();
                        if(s.getAudioLink()!= "")s.play_go();
                        else Toast.makeText(context, "No audio", Toast.LENGTH_SHORT).show();
                        notif = false;
                    }
                });
                ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        Toast.makeText(context, "Go off", Toast.LENGTH_LONG).show();
                        notif = false;
                    }
                });
                ad.setCancelable(true);
                ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(context, "No means no", Toast.LENGTH_LONG).show();
                        notif = false;
                    }
                });
                if (!s.getPlaying()) ad.show();
                //s.play_go();
                // notification("Checking # " + checked++ + " (" + distance + " m ) : " + "Listening", false);
                // toast = Toast.makeText(getApplicationContext(), "Checking # " + checked++ + " (" + distance + " m ) : " + "Listening", Toast.LENGTH_SHORT);
                break;
            }
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

    private void setUpMap() //If the setUpMapIfNeeded(); is needed then...
    {
        mMap.setOnMapClickListener(this);
    }
public void setPlayersAll()
{
    for (SuperBubble s: testUniverse
         ) {
        for (Bubble b: s.getAllBubble()
             ) {
            b.setPlayer(this);
        }

    }
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


        Toast.makeText(getApplicationContext(), "Data downloaded", Toast.LENGTH_LONG).show();

        timer = new Timer(true);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkForBubbleInteraction();
                    }
                });
            }
        };
        timer.schedule(timerTask, 2000, 10000);

        notification("Downloading data", false);



        downloadBubble("73d678cffb29c1cd4ff89a84b6cb6dec", false);
        downloadBubble("7515fb1ae1230c7a6d9effff64b1dc63", false);
        downloadBubble("bbd840d2acd86caee2203cfc06b1e2f5", false);
        downloadBubble("9e4a72fcf2a4bfa6ea1969069dd013d6", false);
        downloadBubble("4b39b3fafe2f48403baaf35bc7c699e2", false);
        downloadBubble("7cdcdd29a4c5d8b569c973b5b1fedb08", false);
        downloadBubble("f6b19ad4af41178b6f9d49969937f7fe", false);
        downloadBubble("37eebbcbdc6cc907756e60c3fefed54a", false);
        downloadBubble("2527fdff82e9cbc85de900610f66de14", false);
        downloadBubble("422e547db04c4f537ee84969dd9cc8d1", false);
        downloadBubble("af87fb49d89b747fd55f25daf43e8bfb", false);
        downloadBubble("e8ff7985994bc3181fb93712e2fa2b7f", false);
        downloadBubble("cdd7cc6927d0ed3e45e99b7032888bed", false);
        downloadBubble("98225669f6144fac873abb84b8a61e36", false);
        downloadBubble("dab3d375e24fb36e95fa0ea06c739223", false);
        downloadBubble("b8cd1bcf6e67232c0d89394cb6eeaaf4", false);
        downloadBubble("c62f5a14afd7a607e5e1384c4ab6ef00", false);
        downloadBubble("1fe77c7e24357f97be941291a689735d", false);
        downloadBubble("b1c6d09c2edf7bca0ff372594042a989", false);
        downloadBubble("f593150c958fa10a04797b6bbeb881a8", false);
        downloadBubble("482d63ff6ab63e72b4b655b25d63117d", false);
        downloadBubble("1123b8c7afb18d419a7d88325d24cd21", false);
        downloadBubble("5617ef41ee69a1dcab6af30631d0bd4b", false);
        downloadBubble("624cbbd2f9a911b65414c804930b0465", false);
        downloadBubble("6e6737f7c60cafc2cbace1940064cd03", false);
        downloadBubble("37f03abcc42c58eeb8c4a214a989a0d8", false);
        downloadBubble("148340132dce031b44a8f0b13ba55b47", false);
        downloadBubble("74b8a0cd9264d403ea5f949eeaca814e", false);
        downloadBubble("0d746d19e0edeed557a423c655ff2cca", false);
        downloadBubble("3c24259c9b84a8607e0c82ac58d2ce7d", false);
        downloadBubble("b4340aacaa7db7519671f29579457121", false);
        downloadBubble("e1704c3add3c69a24887a5f9a8b6a24d", false);
        downloadBubble("d27bb26e91c50ad53793130804a36e71", false);
        downloadBubble("d06679cb8fafa8a9876c03c33269b81a", false);
        downloadBubble("99369ec651267243c1d1250b9e00c874", false);
        downloadBubble("0604f98acf4e413adb318f16e1429a71", false);
        downloadBubble("e3e4ac7cab032100534dde912c6b19d1", false);
        downloadBubble("05f4b424b19c8fb3f63a31d9beba9c2e", false);
        downloadBubble("8839883438303d8f97b456e828ea6524", false);
        downloadBubble("b0b9af68a344cbdd66ce7495daf268ec", false);
        downloadBubble("2fbcf636069b2e44b4b41e7edb7e6cea", false);
        downloadBubble("ef51b4a8e7b261a2ec3be0087ac5f86b", false);
        downloadBubble("acf14624a8e1b815c17f74bd0f8bbad9", false);
        downloadBubble("bd1ce78289666b6331208d9efe9fab9b", false);
        downloadBubble("f7aa2d1c0576d8a8d4d8920e47ae8124", false);
        downloadBubble("2c85e1b1dbda067e3d93c5d81eb6d74d", false);
        downloadBubble("9815e1dbe76789c5f3e6c4639d1ea23e", false);
        downloadBubble("80cb7300c1921fd7cf79b4ad54d45a3b", false);
        downloadBubble("72ccf2966fdc7eb01c56d7481c6ef9bd", false);
        downloadBubble("b64912206ffec3ea07f4c715ad8840f5", false);
        downloadBubble("59acd52a90a3cd6e85937a146641e341", false);
        downloadBubble("d497c34f43c1e2329f9cc7cb14ee2842", false);

        //setPlayersAll();
        MediaPlayer tempMediaMpayer = MediaPlayer.create(MapsActivityCurrentPlace.this, R.raw.finished);
        tempMediaMpayer.start();
        notification("Players setted", false);
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

        //String json = new HttpHandler().makeServiceCall("http://soundways.eu/interface/geosound.php?_action=getGeoSound&geosounddoi=" + doi);

        if (downloaded) notification("downloading data", true);
        new GetContacts().execute("http://soundways.eu/interface/geosound.php?_action=getGeoSound&geosounddoi=" + doi);

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

        if (item.getItemId() == R.id.menu_zoom) {
            if (moveCamera) {
                notification("Zoom was turn off", false);
                item.setTitle("ZOOM - TURN ON");
                moveCamera = false;
            } else {
                notification("Zoom was turn on", false);
                item.setTitle("ZOOM - TURN OFF");
                moveCamera = true;
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

        setUpMap();
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
                            if(moveCamera || firstGo)
                            {mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            firstGo = false;}
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
