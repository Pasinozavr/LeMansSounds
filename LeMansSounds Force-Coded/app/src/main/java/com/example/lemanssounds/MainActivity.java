package com.example.lemanssounds;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean langq = false;//eng

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private static final String[] CAMERA_PERMS={
            Manifest.permission.CAMERA
    };
    private static final String[] CONTACTS_PERMS={
            Manifest.permission.READ_CONTACTS
    };
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST=1337;
    private static final int CAMERA_REQUEST=INITIAL_REQUEST+1;
    private static final int CONTACTS_REQUEST=INITIAL_REQUEST+2;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
    private Switch mySwitch;

    Intent intent_map_extra, intent_map, intent_help, intent_about, intent_tools, intent_json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "J'aime Le Mans!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                intent_map_extra = new Intent(MainActivity.this, MapsActivityCurrentPlace.class);
                startActivity(intent_map_extra);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Menu menu = navigationView.getMenu();

        if (!canAccessLocation() || !canAccessContacts()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }


        (menu.findItem(R.id.menu_switch_language)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MenuItem map = menu.findItem(R.id.menu_map), help = menu.findItem(R.id.menu_help), about = menu.findItem(R.id.menu_about), tools = menu.findItem(R.id.menu_tools), json = menu.findItem(R.id.menu_json), lang = menu.findItem(R.id.menu_switch_language), act = menu.findItem(R.id.menu_activities), settings = menu.findItem(R.id.menu_settings), for_dev = menu.findItem(R.id.menu_for_dev);
                if (langq)
                {
                    map.setTitle("Carte du Mans");
                    help.setTitle("Aidez-moi");
                    about.setTitle("Sujet");
                    tools.setTitle("Outiols");
                    json.setTitle("Tests JSON");
                    lang.setTitle("Changer de langue");
                    act.setTitle("Activités");
                    settings.setTitle("Réglages");
                    for_dev.setTitle("Pour les développeurs");
                    ((TextView)findViewById(R.id.textView2)).setText(R.string.main_description_FR);
                    ((TextView)findViewById(R.id.textView3)).setText(R.string.main_direction_FR);
                    langq = false;
                }
                else
                {
                    map.setTitle("Le Mans Map");
                    help.setTitle("Help");
                    about.setTitle("About");
                    tools.setTitle("Tools");
                    json.setTitle("JSON tests");
                    lang.setTitle("Language switch");
                    act.setTitle("Activities");
                    settings.setTitle("Settings");
                    for_dev.setTitle("For developers");
                    ((TextView)findViewById(R.id.textView2)).setText(R.string.main_description_ENG);
                    ((TextView)findViewById(R.id.textView3)).setText(R.string.main_direction_ENG);
                    langq = true;
                }

                return false;
            }
        });

    }

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean canAccessCamera() {
        return(hasPermission(Manifest.permission.CAMERA));
    }

    private boolean canAccessContacts() {
        return(hasPermission(Manifest.permission.READ_CONTACTS));
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.menu_map) {
            intent_map = new Intent(MainActivity.this, MapsActivityCurrentPlace.class);
            startActivity(intent_map);
        }
        if(item.getItemId() == R.id.menu_help)
        {
            intent_help = new Intent(MainActivity.this, Help.class);
            startActivity(intent_help);
        }
        if(item.getItemId() == R.id.menu_about)
        {
            intent_about = new Intent(MainActivity.this, About.class);
            startActivity(intent_about);
        }
        if(item.getItemId() == R.id.menu_tools)
        {
            intent_tools = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent_tools);
        }
        if(item.getItemId() == R.id.menu_json)
        {
            intent_json = new Intent(MainActivity.this, JSONTests.class);
            startActivity(intent_json);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
