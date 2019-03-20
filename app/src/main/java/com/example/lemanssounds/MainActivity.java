package com.example.lemanssounds;

import android.Manifest;
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
import android.widget.TextView;

/**
 * Start page class
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    boolean langq = true;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private static final int INITIAL_REQUEST=1337;

    Intent intent_map_extra, intent_map, intent_help, intent_about, intent_tools, intent_json;
    /**
     * sets interface, check map permission
     */
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
                changeLanguage();
                return false;
            }
        });

        changeLanguage();
    }
    /**
     * change interface localisation language - takes data from string.xml
     */
    private void changeLanguage()
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Menu menu = navigationView.getMenu();
        MenuItem map = menu.findItem(R.id.menu_map), help = menu.findItem(R.id.menu_help), about = menu.findItem(R.id.menu_about), tools = menu.findItem(R.id.menu_tools), json = menu.findItem(R.id.menu_json), lang = menu.findItem(R.id.menu_switch_language), act = menu.findItem(R.id.menu_activities), settings = menu.findItem(R.id.menu_settings), for_dev = menu.findItem(R.id.menu_for_dev);
        if (langq)
        {
            map.setTitle(R.string.map_FR);
            help.setTitle(R.string.help_FR);
            about.setTitle(R.string.about_FR);
            tools.setTitle(R.string.tools_FR);
            json.setTitle(R.string.tests_FR);
            lang.setTitle(R.string.lang_FR);
            act.setTitle(R.string.act_FR);
            settings.setTitle(R.string.set_FR);
            for_dev.setTitle(R.string.dev_FR);
            ((TextView)findViewById(R.id.textView2)).setText(R.string.main_description_FR);
            ((TextView)findViewById(R.id.textView3)).setText(R.string.main_direction_FR);
            langq = false;
        }
        else
        {
            map.setTitle(R.string.map_EN);
            help.setTitle(R.string.help_EN);
            about.setTitle(R.string.about_EN);
            tools.setTitle(R.string.tools_EN);
            json.setTitle(R.string.tests_EN);
            lang.setTitle(R.string.lang_EN);
            act.setTitle(R.string.act_EN);
            settings.setTitle(R.string.set_EN);
            for_dev.setTitle(R.string.dev_EN);
            ((TextView)findViewById(R.id.textView2)).setText(R.string.main_description_ENG);
            ((TextView)findViewById(R.id.textView3)).setText(R.string.main_direction_ENG);
            langq = true;
        }
    }
    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean canAccessContacts() {
        return(hasPermission(Manifest.permission.READ_CONTACTS));
    }
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
    /**
     * nowhere to return to
     */
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
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
    /**
     * set actions when buttons are clicked - open other screens
     */
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
