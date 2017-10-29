package com.hswt.broihier.sfwiaudio;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.hswt.broihier.sfwiaudio.ItemDetailFragment.ARG_ITEM_ID;
import static android.os.Environment.DIRECTORY_PODCASTS;
import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by Mark Broihier
 */
public class ItemDetailActivity extends AppCompatActivity {

    private static String TAG = "ItemDetailActivity";
    private static AudioPlayer audioPlayer = null;
    private static ItemDetailActivity itemDetailActivity = null;
    private FileOutputStream output = null;
    private FileInputStream input = null;
    private static PodCasts podInfo = new PodCasts();

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

        try {
            input = new FileInputStream(getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS + "/state.bin");
            PodCasts oldPodInfo;
            ObjectInputStream in = new ObjectInputStream(input);
            oldPodInfo = (PodCasts) in.readObject();
            Log.d(TAG, oldPodInfo.toString()+"\n"+podInfo.toString());

            if (oldPodInfo.getPlaying() == Integer.parseInt(id) && podInfo.getItem(""+podInfo.getPlaying()).equals(oldPodInfo.getItem(""+oldPodInfo.getPlaying()))) {
                position = oldPodInfo.getProgress();
            }
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "file read for state.bin failed: " + e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "error in class definition: " + e);
        }

        try {
            output = new FileOutputStream(getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS + "/state.bin");
            ObjectOutputStream out = new ObjectOutputStream(output);
            podInfo.setProgress(position);
            out.writeObject(podInfo);
            out.close();
            Log.d(TAG,"podInfo updated after looking at old: "+podInfo.toString());
        } catch (IOException e) {
            Log.e(TAG, "file write failed: " + e);
        }
        String filePath = podInfo.getItem(id);
        Log.d(TAG, "full path to audio file is: " + filePath);

        audioPlayer = new AudioPlayer();
        File file = new File(filePath);
        audioPlayer.play(this.getApplicationContext(), Uri.fromFile(file), position);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Log.i(TAG, "Starting detailed item fragment with fragment manager");
            Bundle arguments = new Bundle();
            arguments.putString(ARG_ITEM_ID,
                    getIntent().getStringExtra(ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .addToBackStack("Detail")
                    .commit();
        } else {
            Log.e(TAG, "savedInstanceState should have been null in this design");
        }
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
            Log.d(TAG, "Issuing stop command via back button press");
            audioPlayer.stop();
        }
        navigateUpTo(new Intent(this, ItemListActivity.class));
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
        popUp(); //navigateUpTo(new Intent(this, ItemListActivity.class));
    }

    /**
     * respond to onOptionsItemSelected (respond to home key press)
     *
     * <pre>
     *
     * {@code
     * Pseudo code:
     * if home key {
     *     popUP;
     * }
     * }
     * </pre>
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            popUp(); //navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        Log.d(TAG, "got here via " + id);
        return super.onOptionsItemSelected(item);
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
        try { // store current location
            output = new FileOutputStream(getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS + "/state.bin");
            podInfo.setProgress(audioPlayer.getRelativeLocation());
            ObjectOutputStream out = new ObjectOutputStream(output);
            out.writeObject(podInfo);
            out.close();
            Log.d(TAG,"podInfo being updated in toggle: "+podInfo.toString());
        } catch (IOException e) {
            Log.e(TAG, "file write failed: " + e);
        }
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
        itemDetailActivity = null;
        Log.d(TAG, "onStop");
    }

}
