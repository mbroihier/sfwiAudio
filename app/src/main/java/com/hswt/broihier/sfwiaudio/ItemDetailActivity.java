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

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    private static String TAG = "ItemDetailActivity";
    private static AudioPlayer audioPlayer = null;
    private static ItemDetailActivity itemDetailActivity = null;

    public ItemDetailActivity () {
        if (itemDetailActivity == null) {
            itemDetailActivity = this;
        } else {
            Log.e(TAG,"itemDetailActivity is meant to be singular");
        }
    }

    public static ItemDetailActivity getItemDetailActivity() {
        return itemDetailActivity;
    }

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
        //PodCasts podInfo = new PodCasts(this.getApplicationContext());
        PodCasts podInfo = new PodCasts();
        podInfo.openDirectory();
        String filePath = podInfo.getItem(id);
        Log.d(TAG, "full path to audio file is: " + filePath);

        audioPlayer = new AudioPlayer();
        File file = new File(filePath);
        audioPlayer.play(this.getApplicationContext(), Uri.fromFile(file));


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ARG_ITEM_ID,
                    getIntent().getStringExtra(ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"Issuing stop command via back button press");
        audioPlayer.stop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Log.d(TAG, "Issuing stop command");
            audioPlayer.stop();
            navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        Log.d(TAG, "got here via " + id);
        return super.onOptionsItemSelected(item);
    }


    public void toggle() {
        audioPlayer.toggle();
    }
    public void slide(int location) {
        audioPlayer.seekPosition(location);
        Log.d(TAG, "current location is: " + location);
    }
    public int getRelativePosition () { return audioPlayer.getRelativeLocation();};
    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

}
