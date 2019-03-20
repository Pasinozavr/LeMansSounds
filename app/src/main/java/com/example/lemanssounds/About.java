package com.example.lemanssounds;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 * About page class
 */
public class About extends AppCompatActivity  {
    private Toolbar toolbar;
    /**
     * sets back-arrow and title
     */
    private void toolBarSet() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle("About Page");

    }
    /**
     * set button to return to main screen
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    /**
     * set interface from appropriate xml file
     *
     * @param savedInstanceState saved Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolBarSet();
    }
}
