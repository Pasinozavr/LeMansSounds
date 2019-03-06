package com.example.lemanssounds;


import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MyMediaPlayer {
    public MediaPlayer mediaPlayer;
    public AudioManager am;

    public MyMediaPlayer(Activity act, String link)
    {
        if(link != "") {
            String cont = Context.AUDIO_SERVICE;
            am = (AudioManager) act.getSystemService(cont);
            if (mediaPlayer != null)
                mediaPlayer.setLooping(false);
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(link);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();

            if (mediaPlayer == null) return;
            mediaPlayer.start();
        }
        else
        {

        }
    }

    public void stop()
    {
        mediaPlayer.stop();
    }
    public void pause()
    {
        mediaPlayer.pause();
    }
    public void play()
    {
        mediaPlayer.start();
    }
}