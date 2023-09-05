package com.example.openeyes.recorder;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

public class AndroidAudioPlayer implements AudioPlayer {

    private Context context;
    private MediaPlayer player;

    public AndroidAudioPlayer(Context context) {
        this.context = context;
        this.player = null;
    }

    @Override
    public void playFile(File file) {
        player = MediaPlayer.create(this.context, Uri.fromFile(file));
        player.start();

    }

    @Override
    public void playUrl(String url) {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(url);
            player.prepare();
            player.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void stop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;

        }
    }
}
