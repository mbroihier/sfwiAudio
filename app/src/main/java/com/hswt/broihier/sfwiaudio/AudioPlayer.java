package com.hswt.broihier.sfwiaudio;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.net.URI;

/**
 * Created by broihier on 1/25/17.
 */

public class AudioPlayer {

    private MediaPlayer player = null;
    private boolean running = false;

    private final String TAG = "AudioPlayer";
    /**
     * <pre>
     * {@code
     * Stop a running player
     *
     * Pseudo code:
     *
     * if player exits {
     *     release the player;
     *     cleanup;
     * }
     * } </pre>
     */
    public void stop () {
        Log.d(TAG,"stop was called");
        if (player != null) {
            player.release();
            player = null;
            running = false;
        }
    }

    /**
     * <pre>
     * toggle the player's run state
     *
     * Pseudo code:
     * {@code
     * if player exists {
     *     if player running {
     *         pause the player;
     *     } else {
     *         run the player;
     *     }
     * }
     * }
     * </pre>
     */
    public void toggle () {
        if (player != null) {
            if (running) {
                player.pause();
                Log.d(TAG,"pausing");
            } else {
                player.start();
                Log.d(TAG,"continuing");
            }
            running = !running;
        }
    }

    /**
     *
     * @param c Context player will run in
     * @param uri File player will play
     *
     * <pre>
     * {@code
     * create player;
     * setup listener that will clean up when player is complete;
     * }
     * </pre>
     */
    public void play (Context c, Uri uri) {
        stop();
        player = MediaPlayer.create(c, uri);
        running = true;
        if (player != null) {
            Log.d(TAG, "player was created and a start was attempted");
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop();
                }
            });
        } else {
            Log.d(TAG,"player is still null");
        }
    }

    public int getLocation (){
        return player.getCurrentPosition();
    }

    public int getRelativeLocation () { return player.getCurrentPosition() * 100 / player.getDuration();}

    public void seekPosition (int relativePosition) {
        int newPosition = relativePosition * player.getDuration() / 100;
        player.seekTo(newPosition);
    }

}
