package com.hswt.broihier.sfwiaudio;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;

import static android.os.Environment.DIRECTORY_PODCASTS;
import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by broihier on 1/25/17.
 */

public class AudioPlayer {

    private MediaPlayer player = null;
    private boolean running = false;

    private static AudioPlayer audioPlayerReference = new AudioPlayer();

    private final String TAG = "AudioPlayer";

    private PodCasts podcastInfo;

    private AudioPlayer() {
        if (audioPlayerReference == null) {
            audioPlayerReference = this;
        }
        PodCasts podcastInfo = new PodCasts();
    }

    public static AudioPlayer getAudioPlayerReference() {
        return audioPlayerReference; // implement a singleton
    }

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
    public void play (Context c, Uri uri, int position) {
        stop();
        player = MediaPlayer.create(c, uri);
        running = true;
        if (player != null) {
            Log.d(TAG, "player was created and a start was attempted");
            player.start();
            seekPosition(position);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG,"media player completed");
                    stop();
                }
            });
        } else {
            Log.d(TAG,"player is still null");
        }
    }

    /**
     *     @return position within the podcast
     * <pre>
     *
     * {@code
     * Pseudo code:
     *
     * get the position of the player and return it;
     * }
     * </pre>
     */
    public int getLocation (){
        return player.getCurrentPosition();
    }

    /**
     *     @return relative position within the podcast (0-100%)
     * <pre>
     *
     * {@code
     * Pseudo code:
     *
     * get the position of the player divide it by the total length of the podcast and return the percentage;
     * }
     * </pre>
     */
    public int getRelativeLocation () { return player.getCurrentPosition() * 100 / player.getDuration();}

    /**
     * <pre>
     *
     * {@code
     * Pseudo code:
     *
     * using the relative position of the progress bar, seek to the same position within the podcast;
     * }
     * </pre>
     */
    public void seekPosition (int relativePosition) {
        relativePosition = Math.min(98, relativePosition);
        int newPosition = relativePosition * player.getDuration() / 100;
        player.seekTo(newPosition);
    }

    /**
     *     @return true if the podcast is still playing
     * <pre>
     *
     * {@code
     * Pseudo code:
     *
     * if player is not null, then it is still playing;
     * }
     * </pre>
     */
    public boolean playerStatus() {
        return player != null;
    }

    /**
     * Read the podcast status file
     * <pre>
     *
     * {@code
     * Pseudo code:
     *
     * read the old podcast status and return it to the caller;
     * }
     * </pre>
     */
    public PodCasts readPodcastInfo(int IDOfPodcastToPlay) {
        PodCasts oldPodcastInfo = new PodCasts();
        try {
            FileInputStream input = new FileInputStream(getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS + "/state.bin");
            ObjectInputStream in = new ObjectInputStream(input);
            oldPodcastInfo = (PodCasts) in.readObject();
            Log.d(TAG, oldPodcastInfo.toString());
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "file read for state.bin failed: " + e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "error in class definition: " + e);
        }
        return oldPodcastInfo;
    }

    /**
     * Write the podcast status file
     *
     * @param podcastInfo what to update file to
     * <pre>
     *
     * {@code
     * Pseudo code:
     *
     * write the podcast status;
     * }
     * </pre>
     */
    public void writePodcastInfo(PodCasts podcastInfo) {
        try {
            FileOutputStream output = new FileOutputStream(getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS + "/state.bin");
            ObjectOutputStream out = new ObjectOutputStream(output);
            out.writeObject(podcastInfo);
            out.close();
            Log.d(TAG,"podInfo updated in file: "+podcastInfo.toString());
        } catch (IOException e) {
            Log.e(TAG, "file write failed: " + e);
        }
    }
    /**
     * Write the podcast status file using the internal status
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     *
     * write the podcast status;
     * }
     * </pre>
     */
    public void writePodcastInfo() {
        try {
            FileOutputStream output = new FileOutputStream(getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS + "/state.bin");
            ObjectOutputStream out = new ObjectOutputStream(output);
            out.writeObject(podcastInfo);
            out.close();
            Log.d(TAG,"podInfo updated in file: "+podcastInfo.toString());
        } catch (IOException e) {
            Log.e(TAG, "file write failed: " + e);
        }
    }
    /**
     * Update the contents of the podcastInfo object from an external copy
     *
     * @param podcastInfo new internal satte
     * <pre>
     *
     * {@code
     * Pseudo code:
     *
     * update private copy;
     * }
     * </pre>
     */
    public void updatePodcastInfo(PodCasts podcastInfo) {
        this.podcastInfo = podcastInfo;
    }
}
