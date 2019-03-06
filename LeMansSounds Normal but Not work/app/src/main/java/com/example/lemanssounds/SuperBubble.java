package com.example.lemanssounds;

import com.google.android.gms.maps.GoogleMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SuperBubble extends Bubble {
    private List<Bubble> listOfBubbles;
    private boolean playing = false;
    public String imageLink;

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
    public String getImageLink()
    {
        return imageLink;
    }
    public void play_go ()
    {
        if(!playing)
        {
            Random rnd = new Random();
            int size = listOfBubbles.size(), playFirst = rnd.nextInt(size), playSecond = rnd.nextInt(size), levelFirst = listOfBubbles.get(playFirst).getLevel(), levelSecond = listOfBubbles.get(playSecond).getLevel();
            listOfBubbles.get(playFirst).sound_play();

            do {
                playSecond = rnd.nextInt(size);
                levelSecond = listOfBubbles.get(playSecond).getLevel();
            } while ( levelSecond == levelFirst);

            listOfBubbles.get(playSecond).sound_play();
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
    public float getRadius()
    {
        return listOfBubbles.get(0).getRadius();
    }
    public boolean getPlaying()
    {
        return playing;
    }
}
