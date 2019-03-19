package com.example.lemanssounds;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivityCurrentPlace extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private List<SuperBubble> testUniverse = new ArrayList<>();
    private LatLng currentPlace = mDefaultLocation;
    private Timer timer;
    private final Handler handler = new Handler();
    private TimerTask timerTask;
    private Toolbar toolbar;
    private boolean moveCamera = false, same = false, firstGo = true;
    private DialogFragment dlg;
    private SuperBubble cur;

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
    private static final String TAG_CHANNEL = "geosound_wormhole_channeldoi";
    private JSONArray geoSoundTitle = null;
    private static final String channel = "31fc06588831aa34e5879261bb189db9";
    private static final  String emptySound = "https://soundways.eu/data/sounds/sound_a3ca2bb33e0708c5c9bbd01d19370407.mp3", ringSound = "https://soundways.eu/data/sounds/sound_4b3235d35098974ad59ecbd6deb72b99.mp3";

    private List<String> downloadBubbleTitle = new ArrayList<>(), downloadBubbleDescription = new ArrayList<>(), downloadBubbleImage = new ArrayList<>(), downloadBubbleSound = new ArrayList<>(), downloadBubbleColor = new ArrayList<>(), downloadBubbleLatitude = new ArrayList<>(), downloadBubbleLongutide = new ArrayList<>(), downloadBubbleRadius = new ArrayList<>();

    private Bubble tempBubble = new Bubble(), ringBubble = new Bubble();
    private SuperBubble tempSuperBubble = new SuperBubble();
    private int numberOfBubbles = 0, currentOrder = 0;

    /**
     * return a string of rgb color in hex
     *
     * @param r
     * @param g
     * @param b
     * @param inverseOrder
     * @return
     */
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


    /**
     *
     */

    private class GetGeoSounds extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void ... params) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("http://soundways.eu/interface/geosound.php?_action=listGeoSound");

            Log.e(TAG, "Response from url: " + jsonStr);

                try {
                    JSONObject ob = new JSONObject(jsonStr);
                    geoSoundTitle = ob.getJSONArray(TAG_GEOSOUNDS);
                    for(int i = 0; i < geoSoundTitle.length(); i++)
                    {
                        JSONObject c = geoSoundTitle.getJSONObject(i);

                        if(c.getString(TAG_CHANNEL).equals(channel))
                        {
                            numberOfBubbles = numberOfBubbles + 1;
                            downloadBubbleImage.add(c.getString(TAG_IMAGE));
                            downloadBubbleSound.add(c.getString(TAG_SOUND));
                            downloadBubbleColor.add(c.getString(TAG_COLOR));
                            downloadBubbleLatitude.add(c.getString(TAG_LATITUDE));
                            downloadBubbleLongutide.add(c.getString(TAG_LONGITUDE));
                            downloadBubbleRadius.add(c.getString(TAG_RADIUS));
                            downloadBubbleDescription.add( c.getString(TAG_DESCRIPTION));
                            downloadBubbleTitle.add(c.getString(TAG_TITLE));
                        }
                        else
                        {
                            //break;
                        }

                    }

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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            for (int i = 0; i < numberOfBubbles; i++)
            {
                nulize();

                int r = 123, g = 123, b = 123;

                if(!downloadBubbleColor.get(i).equals("") && downloadBubbleColor != null) {
                    String [] rgb = downloadBubbleColor.get(i).split(",",3);
                    r = Integer.parseInt(rgb[0]);
                    g = Integer.parseInt(rgb[1]);
                    b = Integer.parseInt(rgb[2]);

                }

                tempBubble.setLatitude(Float.parseFloat(downloadBubbleLatitude.get(i)));
                tempBubble.setRadius(Integer.parseInt(downloadBubbleRadius.get(i)));
                tempBubble.setLonguitude(Float.parseFloat(downloadBubbleLongutide.get(i)));
                tempBubble.setAudioLink(downloadBubbleSound.get(i));
                tempBubble.setImageLink(downloadBubbleImage.get(i));
                tempBubble.setDescription(downloadBubbleDescription.get(i));
                tempBubble.setName(downloadBubbleTitle.get(i));
                tempBubble.setColor(getHexColor(r,g,b,true));


                if(downloadBubbleSound.get(i) == "" || downloadBubbleSound == null) tempBubble.setAudioLink(emptySound);

                tempBubble.setPlayer(MapsActivityCurrentPlace.this);

                tempSuperBubble.setOrder(i);
                tempSuperBubble.addBubble(tempBubble);
                tempSuperBubble.addBubble(ringBubble);

                testUniverse.add(tempSuperBubble);

                if(downloadBubbleSound.get(i) != "") cur = tempSuperBubble;
            }

            }
        }


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
                currentOrder = s.getOrder();
                cur = s;
                dlg = new GroupDialog(s.getImageLink(), s.getDescription(), s.getName(), cur);

                dlg.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
                dlg.show(getFragmentManager(), "groupdialog");
                break;
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
            if (s.getOrder() == currentOrder + 1 || (currentOrder == numberOfBubbles - 1 && s.getOrder() == 0)) s.draw_polygon(mMap);
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
                currentOrder = s.getOrder();
                if(s.getAudioLink()!= "")s.play_go();
                /*
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
            */
            }
            else {
                s.play_stop();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        return true;
    }

    private void setUpMap()
    {

        mMap.setOnMapClickListener(this);
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

        ringBubble.setAudioLink(ringSound);
        ringBubble.setPlayer(this);

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

        notification("--- Data is downloading currently ---", true);

        new GetGeoSounds().execute();
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
    public void nav_locate()
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
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.style);
        mMap.setMapStyle(mapStyleOptions);
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
