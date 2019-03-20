package com.example.lemanssounds;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import android.app.Activity;
import android.graphics.Color;

import static com.example.lemanssounds.MapsActivityCurrentPlace.getHexColor;

/**
 * minimal data element class - bubble
 */
public class Bubble {
    private float volume;
    private int radius;
    private double longuitude, latitude;
    private String audioLink, color, name, description, imageLink, autor;
    private int level;
    private MyMediaPlayer player;
    private boolean playing = false;
    /**
     * public call of new bubble creation
     */
    public Bubble() {
        level = 1;
        radius = 50;
        audioLink = "";
        color= "FF0000";
        player = new MyMediaPlayer();
    }
    /**
     * setters
     */
    public void setPlayer(Activity act){
        player.initialize(act, audioLink);
        player.play();
    }
    public void setLonguitude (double tmp)
    {
        longuitude = tmp;
    }
    public void setLatitude (double tmp)
    {
        latitude = tmp;
    }
    public void setRadius (int tmp)
    {
        if (tmp > 0 && tmp < 500) radius = tmp;
    }
    public void setVolume (float tmp)
    {
        if (tmp >= 0 && tmp <=1) volume = tmp;
    }
    public void setAudioLink (String tmp) { audioLink = tmp; }
    public void setColor (String tmp) { color = tmp;}
    public void setLevel (int tmp) { level = tmp; }
    public void setPlaying (boolean tmp) { playing = tmp;}
    public void setName(String nm)
    {
        name = nm;
    }
    public void setDescription(String des)
    {
        description = des;
    }
    public void setImageLink(String lnk)
    {
        imageLink = lnk;
    }
    public void setAutor(String aut)
    {
        autor = aut;
    }
    /**
     * getters
     */
    public double getLonguitude () { return longuitude;}
    public double getLatitude () { return latitude;}
    public int getRadius () { return radius;}
    public float getVolume () { return volume;}
    public String getAudioLink () { return audioLink;}
    public String getColor () { return color;}
    public int getLevel() { return level;}
    public boolean getPlaying() {return playing;}
    public String getName()
    {
        return name;
    }
    public String getDescription()
    {
        return description;
    }
    public String getImageLink()
    {
        return imageLink;
    }
    public String getAutor() { return autor;}
    /**
     * draws bubble circle
     */
    public void draw_bubble(GoogleMap map) {
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitude, longuitude)).radius(radius)
                .fillColor(Color.parseColor("#22" + color ))
                .strokeWidth(1);
        map.addCircle(circleOptions);
    }
    /**
     * draws bubble square
     */
    public void draw_polygon(GoogleMap map) {
        map.addPolygon(new PolygonOptions()
                .add(new LatLng(latitude - 0.0001, longuitude + 0.0001), new LatLng(latitude + 0.0001, longuitude + 0.0001),  new LatLng(latitude + 0.0001, longuitude - 0.0001),  new LatLng(latitude - 0.0001, longuitude - 0.0001))
                .strokeColor(Color.RED));
    }
    /**
     * pause sound in bubble's player
     */
    public void sound_pause()
    {
        if(audioLink != "")player.pause();
        playing = false;
    }
    /**
     * stop sound in bubble's player
     */
    public void sound_stop()
    {
        if(audioLink != "")player.stop();
        playing = false;
    }
    /**
     * start / resume playing sound in bubble's player
     */
    public void sound_play()
    {
        if(audioLink != "")player.play();
        playing = true;
    }




}

