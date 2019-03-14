package com.example.lemanssounds;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import android.app.Activity;
import android.graphics.Color;

import static com.example.lemanssounds.MapsActivityCurrentPlace.getHexColor;


public class Bubble {
    private float radius, volume;
    private double longuitude, latitude;
    private String audioLink, color, name, description, imageLink;
    private int level;
    private MyMediaPlayer player;
    private boolean playing = false;

    //private Activity act;
    public Bubble() {
        level = 1;
        radius = 50;
        audioLink = "";
        color= "FF0000";
    }
    public Bubble(int r, int g, int b, double lat, double lon, int rad, String textName, String textDescription, String textAudioLink, String textImageLink)
    {
        longuitude = lon;
        latitude = lat;
        radius = rad;
        audioLink = textAudioLink;
        volume = 1;
        color = getHexColor(r,g,b,true);
        description = textDescription;
        name = textName;
        imageLink = textImageLink;
        level = 1;
    }
    public void setPlayer(Activity act){
       // this.act = act;
        player = new MyMediaPlayer(act, audioLink);
    }
    public void setLonguitude (double tmp)
    {
        longuitude = tmp;
    }
    public void setLatitude (double tmp)
    {
        latitude = tmp;
    }
    public void setRadius (float tmp)
    {
        if (tmp > 0 && tmp < 500) radius = tmp;
    }
    public void setVolume (float tmp)
    {
        if (tmp >= 0 && tmp <=1) volume = tmp;
    }
    public void setAudioLink (String tmp)
    {
        audioLink = tmp;
       // player = new MyMediaPlayer(act, audioLink);
    }
    public void setColor (String tmp) { color = tmp;}
    public void setLevel (int tmp) { level = tmp; }
    public void setPlaying (boolean tmp) { playing = tmp;}

    public double getLonguitude () { return longuitude;}
    public double getLatitude () { return latitude;}
    public float getRadius () { return radius;}
    public float getVolume () { return volume;}
    public String getAudioLink () { return audioLink;}
    public String getColor () { return color;}
    public int getLevel() { return level;}
    public boolean getPlaying() {return playing;}

    public void draw_bubble(GoogleMap map) {
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitude, longuitude)).radius(radius)
                .fillColor(Color.parseColor("#22" + color ))
                .strokeWidth(1);
        map.addCircle(circleOptions);
    }

    public void sound_pause()
    {
        if(audioLink!="")player.pause();
        playing = false;
    }
    public void sound_stop()
    {
        if(audioLink!="")player.stop();
        playing = false;
    }
    public void sound_play()
    {
        if(audioLink!="")player.play();
        playing = true;
    }
    public void setName(String nm)
    {
        name = nm;
    }
    public void setDescription(String des)
    {
        description = des;
    }
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
    public void setImageLink(String lnk)
    {
        imageLink = lnk;
    }
}

