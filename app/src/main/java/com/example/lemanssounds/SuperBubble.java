package com.example.lemanssounds;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SuperBubble extends Bubble {
    private List<Bubble> listOfBubbles;
    private boolean playing = false;

    public SuperBubble(Bubble b)
    {
        listOfBubbles = new ArrayList<>();
        listOfBubbles.add(b);
    }
    public SuperBubble(){
        listOfBubbles = new ArrayList<>();
    }
    public void addBubble(Bubble newel)
    {
        listOfBubbles.add(newel);
    }
    public void draw_bubble (GoogleMap map)
    {
        listOfBubbles.get(0).draw_bubble(map);
    }
    public void play_go ()
    {
        if(!playing)
        {
            listOfBubbles.get(0).sound_play();
            if (listOfBubbles.size() > 1) listOfBubbles.get(1).sound_play();
            playing = true;
        }
    }
    public void play_stop ()
    {
        if(playing) {
            for (Bubble b : listOfBubbles) {
                b.sound_pause();
            }
            playing = false;
        }
    }
    @Override
    public double getLatitude()
    {
        return listOfBubbles.get(0).getLatitude();
    }
    @Override
    public double getLonguitude()
    {
        return listOfBubbles.get(0).getLonguitude();
    }
    @Override
    public int getRadius()
    {
        return listOfBubbles.get(0).getRadius();
    }
    public boolean getPlaying()
    {
        return playing;
    }
    public String getImageLink()
    {
        return listOfBubbles.get(0).getImageLink();
    }
    public String getName()
    {
        return listOfBubbles.get(0).getName();
    }
    public String getDescription()
    {
        return listOfBubbles.get(0).getDescription();
    }
    public List<Bubble> getAllBubble()
    {
        return listOfBubbles;
    }
    @Override
    public String getAudioLink() { return listOfBubbles.get(0).getAudioLink(); }
    @Override
    public String getAutor() { return listOfBubbles.get(0).getAutor(); }

}
