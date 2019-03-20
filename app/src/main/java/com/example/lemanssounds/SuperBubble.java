package com.example.lemanssounds;

import com.google.android.gms.maps.GoogleMap;
import java.util.ArrayList;
import java.util.List;
/**
 * complicated data storage class
 */
public class SuperBubble extends Bubble {
    /**
     * main feature is list of Bubbles in SuperBubble
     */
    private List<Bubble> listOfBubbles;
    private boolean playing = false;
    private int order;

    /**
     * overrided getters
     */
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
    public int getOrder()
    {
        return order;
    }
    /**
     * setters
     */
    public void setOrder(int r)
    {
        order = r;
    }

    public SuperBubble(){
        listOfBubbles = new ArrayList<>();
    }
    /**
     * add new Bubbles to SuperBubble
     *
     * @param newel exactly Bubble
     */
    public void addBubble(Bubble newel)
    {
        listOfBubbles.add(newel);
    }
    /**
     * when you want to draw SuperBubble you only need to draw one of its Bubble
     */
    public void draw_bubble (GoogleMap map)
    {
        listOfBubbles.get(0).draw_bubble(map);
    }
    /**
     * when you want to sign SuperBubble you only need to sign one of its Bubble
     */
    public void draw_polygon (GoogleMap map)
    {
        listOfBubbles.get(0).draw_polygon(map);
    }
    /**
     * when you want to play SuperBubble you only need to play one of its Bubble
     */
    public void play_go ()
    {
        if(!playing)
        {
            listOfBubbles.get(0).sound_play();
            if (listOfBubbles.size() > 1) listOfBubbles.get(1).sound_play();
            playing = true;
        }
    }
    /**
     * when you want to stop SuperBubble you  need to stop all of its Bubble - just in case
     */
    public void play_stop ()
    {
        if(playing) {
            for (Bubble b : listOfBubbles) {
                b.sound_pause();
            }
            playing = false;
        }
    }
}
