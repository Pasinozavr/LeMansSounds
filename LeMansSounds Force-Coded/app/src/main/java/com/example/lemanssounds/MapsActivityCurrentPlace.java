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

public class MapsActivityCurrentPlace extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
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
    public boolean moveCamera = true, downloaded = true, same = false, notif = false;
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
    private static final String TAG_RADIUS= "radius";
    private JSONArray geoSoundTitle = null;

    private String downloadBubbleImage, downloadBubbleSound, downloadBubbleColor, downloadBubbleLatitude, downloadBubbleLongutide, downloadBubbleRadius;

    private Bubble tempBubble = new Bubble();
    private SuperBubble tempSuperBubble = new SuperBubble();
    private static final class Lock { }
    private final Object lock = new Lock();

    public SuperBubble testSuper = new SuperBubble(), superUniversity = new SuperBubble(), 	laMesse = new SuperBubble(), ambianceVignes = new SuperBubble(), erranceetRois = new SuperBubble(), laMuraille = new SuperBubble(), lAttente = new SuperBubble(), laGrandRue = new SuperBubble(), laMaisonDoisneau = new SuperBubble(), leCardinalGrente = new SuperBubble(), leMenhir = new SuperBubble(), lePilierRouge = new SuperBubble(), lePilierRougeAMB = new SuperBubble(), lesPansdeGorron = new SuperBubble(),lesVignes = new SuperBubble();

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
    for (Bubble b: badcodeBubble
    ) {
        b.setPlayer(this);
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


        Toast.makeText(getApplicationContext(), "Downloading data", Toast.LENGTH_LONG).show();

        //GOVNOKOD

        badcodeBubble[0] = new Bubble(122, 6, 118, 48.00672106, 0.19577265, 43, "Ambiance vignes", "Pierre Saint Julien", "https://soundways.eu/data/sounds/sound_a12940d9160d8b26b7a4372ca4631b76.mp3", "https://www.koshurmedicalmart.com/uploads/no_image_new.png");
        badcodeBubble[1] = new Bubble(168, 168, 168, 48.01001927, 0.19940123, 20, "L'attente", "Le duo de comiques troupiers", "", "https://soundways.eu/data/images/img_c3466670c83bb46b74a1916a59941d50.jpg");
        badcodeBubble[2] = new Bubble(209, 147, 53, 48.00741732, 0.19588888, 38, "La grande Rue", "Texte des historiens", "", "https://soundways.eu/data/images/img_53d30c4532bcc5f7cc573daa9e6a673d.jpg");
        badcodeBubble[3] = new Bubble( 235, 255, 0, 48.00806154, 0.19582048, 14, "La maison Doisneau", "Texte des historien", "https://soundways.eu/data/sounds/sound_2bb043177af4dd9da57b32245c32e7b2.mp3", "https://soundways.eu/data/images/img_413cefd2448263b39c93bd6ecc913e54.jpg");
        badcodeBubble[4] = new Bubble(255, 199, 0, 48.00930509, 0.19807220, 27, "La messe en latin", "Le choeur des moines bénédictins de l'abbaye de Pannonhalma", "https://soundways.eu/data/sounds/sound_eed0e4c7ca2da4dde9052f7a6f1aa4a1.mp3", "https://www.koshurmedicalmart.com/uploads/no_image_new.png");
        badcodeBubble[5] = new Bubble( 173, 255, 0, 48.01015205, 0.19871235, 29, "La messe en latin", "Le choeur des moines bénédictins de l'abbaye de Pannonhalma", "https://soundways.eu/data/sounds/sound_f87f14fffbe685111a4e82db05922f79.mp3", "https://www.koshurmedicalmart.com/uploads/no_image_new.png");
        badcodeBubble[6] = new Bubble( 235, 255, 0, 48.00995467, 0.19810617, 20, "La messe en latin", "Le choeur des moines bénédictins de l'abbaye de Pannonhalma", "https://soundways.eu/data/sounds/sound_e9d23c39997d238d9b981294c7629fd7.mp3", "https://www.koshurmedicalmart.com/uploads/no_image_new.png");
        badcodeBubble[7] = new Bubble( 255, 107, 0, 48.00995826, 0.19660100, 28, "La muraille", "texte des historien", "", "https://previews.dropbox.com/p/thumb/AAUL6fDcStDrD7hfCoZ9YF5xuCrdoJvlSCo4IhPQy2OwqyKRrbbnMoqP8QoWfV3eVjjDf2N9auLYmHmXUxzVt_h1lVLAFiiGUmf1LS2OjtRYYetNKzMKw474NlDb676MBj8H71qRauWMcWzLDonM_mtat411SlDM7cN59MjDg_ylGSsq1hWQnLq9FzNhy_HxiYYaj0QKChOkRdPsvu3Wgm0-iWUwyF5tvE7VHJTy6gk2Q6C3Ryjqq0MRb2wFyxL4BEqp60q_9XQU0J9OABujgJ-yUUxJ7GSFTYxxaqDMp3Cg4g/p.jpeg");
        badcodeBubble[8] = new Bubble(237, 34, 34, 48.00977523, 0.19824833, 10, "Le cardinal Grente", "Texte des historiens", "", "https://soundways.eu/data/images/img_c49399881ae58e85097ea291b94040f8.jpg");
        badcodeBubble[9] = new Bubble( 237, 34, 34, 48.00977523, 0.19824833, 10, "Le cardinal Grente", "Texte des historiens", "", "https://soundways.eu/data/images/img_c49399881ae58e85097ea291b94040f8.jpg");
        badcodeBubble[10] = new Bubble(190, 12, 235, 48.00955990, 0.19803911, 19, "Le menhir", "Texte des historien", "https://soundways.eu/data/sounds/sound_3da4392353b7ce32201d030a8c4b9e09.mp3", "https://previews.dropbox.com/p/thumb/AAVXxP8xu4CaamO4Kgr2kZUMFleDv_XNlDo-8761yCm13cbVAD7fXW9ON5Xb3eDVHcIgkVYwmZkIAgSoG0hH4Bbrtrssf4qKda2p8A4tPqR8XYnuWxRl7OEqQ04Czs6c4bJbx8pSO9Y6lIBCGD2GLencyahlGi3bummhLvxRDhpM5e0BySdOAHl0xK7hnX0tgowCoCaAM9er2v-vMF3MY0FcWywzzALBn_nCVDx4QcNSMgHNcDzbDe6mMfJhP0LRqujVCb3AYXvI2QTYoNNZg5wBZW0uG--a-MO6JG-kOQoFRQ/p.jpeg");
        badcodeBubble[11] = new Bubble( 212, 0, 63, 48.00845632, 0.19694969, 21, "Le pilier rouge", "Texte des historiens", "https://soundways.eu/data/sounds/sound_3da40050833baaaa1635a039f3b2aef9.mp3", "https://soundways.eu/data/images/img_8bfc863eef60154c150b65d132b65de5.jpeg");
        badcodeBubble[12] = new Bubble( 209, 61, 105, 48.00844735, 0.19695014, 53, "Le pilier rouge AMB", "Pas de texte", "https://soundways.eu/data/sounds/sound_2817a95c00ecf0142f624d90cb78709a.mp3", "https://soundways.eu/data/images/img_51975f50a0bede4a4bcad5cbabfd04b4.jpeg");
        badcodeBubble[13] = new Bubble( 255, 0, 230, 48.01004260, 0.19726083, 20, "Les Pans de Gorron - Maisons closes", "Texte des historiens", "", "https://soundways.eu/data/images/img_e358f8c94b3052ab7ad8609cb89293a6.jpg");
        badcodeBubble[14] = new Bubble( 140, 9, 9, 48.00684308, 0.19584462, 19, "Les vignes", "Texte des historien", "https://soundways.eu/data/sounds/sound_fb37b0735a8dfeece7d6d47586e6ed8a.mp3", "https://soundways.eu/data/images/img_4884fc0cd455c422ee277d1a39c89b4b.jpg");
        badcodeBubble[15] = new Bubble( 0, 255, 255, 48.0079041, 0.1963019, 20, "Errance et Rois (RFI LE MANS)", "De l’utilité de perdre Richard Coeur de Lion et Jean Sans Terre\n" +
                "Ballade sonore en compagnie de Richard Coeur de Lion et de Jean Sans Terre, son frère, dans la cité plantagenêt à travers les lieux emblématiques de la cité médiévale. Projet OIC", "", "https://soundways.eu//data//images//img_36797356da7392fb26cda77d0f2f1d0e.jpg");

        setPlayersAll();

        ambianceVignes.addBubble(badcodeBubble[0]);
        lAttente.addBubble(badcodeBubble[1]);
        laGrandRue.addBubble(badcodeBubble[2]);
        laMaisonDoisneau.addBubble(badcodeBubble[3]);
        laMesse.addBubble(badcodeBubble[4]);
        laMesse.addBubble(badcodeBubble[5]);
        laMesse.addBubble(badcodeBubble[6]);
        laMuraille.addBubble(badcodeBubble[7]);
        leCardinalGrente.addBubble(badcodeBubble[8]);
        leCardinalGrente.addBubble(badcodeBubble[9]);
        leMenhir.addBubble(badcodeBubble[10]);
        lePilierRouge.addBubble(badcodeBubble[11]);
        lePilierRougeAMB.addBubble(badcodeBubble[12]);
        lesPansdeGorron.addBubble(badcodeBubble[13]);
        lesVignes.addBubble(badcodeBubble[14]);
        erranceetRois.addBubble(badcodeBubble[15]);

        testUniverse.add(ambianceVignes);
        testUniverse.add(lAttente);
        testUniverse.add(laGrandRue);
        testUniverse.add(laMaisonDoisneau);
        testUniverse.add(laMesse);
        testUniverse.add(laMuraille);
        testUniverse.add(leCardinalGrente);
        testUniverse.add(leMenhir);
        testUniverse.add(lePilierRouge);
        testUniverse.add(lePilierRougeAMB);
        testUniverse.add(lesPansdeGorron);
        testUniverse.add(lesVignes);
        testUniverse.add(erranceetRois);

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

        cur = ambianceVignes;
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
