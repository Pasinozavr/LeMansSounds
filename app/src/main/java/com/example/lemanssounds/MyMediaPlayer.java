package com.example.lemanssounds;


import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MyMediaPlayer {
    public MediaPlayer mediaPlayer;
    public AudioManager am;
    public String cont;
    public MyMediaPlayer()
    {
        cont = Context.AUDIO_SERVICE;
        if (mediaPlayer != null)
            mediaPlayer.setLooping(false);

        mediaPlayer = new MediaPlayer();

    }
    public void initialize(Activity act, String link)
    {
            am = (AudioManager) act.getSystemService(cont);

            try {
                if(link != "") {
                    mediaPlayer.setDataSource(link);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepareAsync();
                }
                if (mediaPlayer == null) return;
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
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