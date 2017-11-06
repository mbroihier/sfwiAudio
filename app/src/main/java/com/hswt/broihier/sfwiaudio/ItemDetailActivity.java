package com.hswt.broihier.sfwiaudio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;


import java.io.File;

import static com.hswt.broihier.sfwiaudio.ItemDetailFragment.ARG_ITEM_ID;
import static com.hswt.broihier.sfwiaudio.ItemDetailFragment.ARG_SCREEN_WIDTH;

/**
 * Created by Mark Broihier
 */
public class ItemDetailActivity extends AppCompatActivity {

    private static String TAG = "ItemDetailActivity";
    private static AudioPlayer audioPlayer = null;
    private static ItemDetailActivity itemDetailActivity = null;
    private static PodCasts podInfo = new PodCasts();
    private int screenWidth = 0;

    /**
     * constructor
     */
    public ItemDetailActivity() {
        if (itemDetailActivity == null) {
            itemDetailActivity = this;
        } else {
            Log.e(TAG, "itemDetailActivity is meant to be singular");
            for (StackTraceElement s : Thread.currentThread().getStackTrace()) {
                Log.e(TAG, "" + s);
            }
            itemDetailActivity = this;
        }
    }

    /**
     * getter activity reference
     *
     * @return detailed information
     */
    public static ItemDetailActivity getItemDetailActivity() {
        return itemDetailActivity;
    }

    /**
     * in response to OnCreate
     * @param savedInstanceState
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     * when activity is created, get the argument item ID and process it by ....
     * creating a podcast information object and setting the current playing index and position;
     * attempting to open the last playing state file;
     * if that file exists and it is playing the same podcast {
     *     set the play position to the one recorded in the file - this is where the last pause occurred;
     * }
     * write out a new playing state file;
     * start the detailed fragment that handles the user interface for playing the selected podcast;
     * }
     * </pre>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent myIntent = getIntent();
        String id = myIntent.getStringExtra(ARG_ITEM_ID);

        Log.d(TAG, "Detail Activity Received: " + id);
        podInfo.openDirectory();
        int position = 0;
        int index = Integer.parseInt(id);
        podInfo.setPlaying(index);
        podInfo.setProgress(position);

        Log.d(TAG,"updated podInfo: "+podInfo.toString());

        audioPlayer = audioPlayer.getAudioPlayerReference();
        PodCasts oldPodInfo = audioPlayer.readPodcastInfo(Integer.parseInt(id));
        if (oldPodInfo.getPlaying() == Integer.parseInt(id) && podInfo.getItem(""+podInfo.getPlaying()).equals(oldPodInfo.getItem(""+oldPodInfo.getPlaying()))) {
            position = oldPodInfo.getProgress();
        } else {
            audioPlayer.writePodcastInfo(podInfo);
        }

        String filePath = podInfo.getItem(id);
        Log.d(TAG, "full path to audio file is: " + filePath);

        File file = new File(filePath);
        if (audioPlayer.playerStatus()) { // if something is playing, stop it
            audioPlayer.stop();
        }
        audioPlayer.play(this.getApplicationContext(), Uri.fromFile(file), position);

        screenWidth = ItemListActivity.screenWidth;

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Log.i(TAG, "Starting detailed item fragment with fragment manager");
            Bundle arguments = new Bundle();
            arguments.putString(ARG_ITEM_ID,
                    getIntent().getStringExtra(ARG_ITEM_ID));
            arguments.putInt(ARG_SCREEN_WIDTH, screenWidth);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Log.e(TAG, "savedInstanceState should have been null in this design");
        }
    }

    /**
     * respond to up arrow on title bar
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     * call popUP;
     * }
     * </pre>
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "up arrow/back - terminating play of podcast");
        popUp();
        return false;
    }

    /**
     * pop back to the list view that started this activity
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     * if the player is playing {
     *     stop the player;
     * }
     * go back to previous menu in the hierarchy
     * }
     * </pre>
     */
    public void popUp() {
        Log.d(TAG, "fragment wants to navigate up");
        if (audioPlayer.playerStatus()) {
            Log.d(TAG, "calling audio player stop");
            recordPosition();
            audioPlayer.stop();
        }
    }

    /**
     * pop back to the list view that started this activity, but don't stop audio
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     * go back to previous menu in the hierarchy
     * }
     * </pre>
     */
    public void popUpButContinueAudio() {
        Log.d(TAG, "fragment wants to navigate up");
    }

    /**
     * respond to onBackPressed
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     * call popUP;
     * }
     * </pre>
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "Issuing stop command via back button press");
        popUp();
    }


    /**
     * PAUSE/RESUME was hit within the fragment - control the audio player running in this activity
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     * toggle the audio player state;
     * update the podcast location information;
     * }
     * </pre>
     */
    public void toggle() {
        audioPlayer.toggle();
        recordPosition();
    }

    /**
     * record position
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     * update the podcast location information;
     * }
     * </pre>
     */
    public void recordPosition() {

        podInfo.setProgress(audioPlayer.getRelativeLocation());
        audioPlayer.writePodcastInfo(podInfo);

    }

    /**
     * Proccess slider motion
     *
     * @param location location to move podcast to
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     * move the audio player to the new location;
     * }
     * </pre>
     */
    public void slide(int location) {
        audioPlayer.seekPosition(location);
        Log.d(TAG, "current location is: " + location);
        recordPosition();
    }

    /**
     * getter for relative position
     * @return relative position of the podcast (0-100%)
     *
     */

    public int getRelativePosition() {
        return audioPlayer.getRelativeLocation();
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        Log.d(TAG, "in this path, attempting to retain fragment - audio is still playing");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        Log.d(TAG, "onContentChanged");
    }

    @Override
    public void onStop() {
        super.onStop();
        //itemDetailActivity = null;
        Log.d(TAG, "onStop");
    }

}
