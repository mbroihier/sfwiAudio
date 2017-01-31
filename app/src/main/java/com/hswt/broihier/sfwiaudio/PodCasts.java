package com.hswt.broihier.sfwiaudio;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_PODCASTS;
import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by broihier on 12/11/16.
 */

public class PodCasts {

    final String TAG="PodCasts";

    private static Context mContext;
    private List<String> podcastNames = new ArrayList<String>();

    PodCasts (Context context) {
        mContext = context;
    }

    public void openDirectory () {
        String path = getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS;
        Log.d(TAG,"attempted path: "+path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            Log.d(TAG, "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                podcastNames.add(i, path + "/" + files[i].getName());
                Log.d(TAG, "File " + i + ":" + files[i].getName());
            }
        } else {
            podcastNames.add(0,"There are no podcast files in the podcast directory");
        }

    }

    public String getItem (String item) {
        int index = Integer.parseInt(item);
        return(podcastNames.get(index));
    }

    public static Context getContext() {
        return mContext;
    }
}
