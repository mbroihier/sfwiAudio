package com.hswt.broihier.sfwiaudio;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ToggleButton;
import android.widget.TextView;

import com.hswt.broihier.sfwiaudio.sfwiaudio.audioFiles;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private ToggleButton pause;
    private static final String TAG="ItemDetailFragment";
    public static final String ARG_ITEM_ID = "item_id";

    private audioFiles.PodCastItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = audioFiles.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Log.d(TAG,"creating fragment");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Playing...");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"creating detailed view");
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        if (mItem != null) {
            Log.d(TAG,"mItem was not null: "+mItem.details);
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
        }
        pause = (ToggleButton) rootView.findViewById(R.id.toggleButton);
        if (pause == null) {
            Log.d(TAG,"pause button was not found");
        } else {
            Log.d(TAG, "pause button was detected and listener is being created");

            pause.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             Log.d(TAG, "button pressed");
                                             ItemDetailActivity activityReference = ItemDetailActivity.getItemDetailActivity();
                                             activityReference.toggle();
                                         }
                                     }

            );
        }
        rootView.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });



        return rootView;
    }

}