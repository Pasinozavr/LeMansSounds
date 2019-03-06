package com.example.lemanssounds;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import android.app.Activity;
import android.graphics.Color;


public class Bubble {
    private float radius, volume;
    private double longuitude, latitude;
    private String type, audioLink;
    private String color;
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
    public Bubble(double templong, double templat, float temprad, String templink, String tempcolor, String temptype, int templvl)
    {
        longuitude = templong;
        latitude = templat;
        radius = temprad;
        audioLink = templink;
        volume = 1;
        color = tempcolor;
        type = temptype;
        level = templvl;
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
    public void setType (String tmp)
    {
        type = tmp;
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
    public String getType () { return type;}
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
}
