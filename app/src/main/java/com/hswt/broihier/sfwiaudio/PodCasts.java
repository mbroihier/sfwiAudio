package com.hswt.broihier.sfwiaudio;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_PODCASTS;
import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by broihier on 12/11/16.
 */

public class PodCasts implements Serializable {

    final String TAG="PodCasts";

    private List<String> podcastNames = new ArrayList<String>();
    private int playing = 0;
    private int progress = 0;

    public PodCasts() {
    }

    public void openDirectory () {
        String path = getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS;
        Log.d(TAG,"attempted path: "+path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            Log.d(TAG, "Size: " + files.length);
            podcastNames.removeAll(podcastNames);
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals("state.bin")) continue;
                podcastNames.add(path + "/" + files[i].getName());
                Log.d(TAG, "File " + i + ":" + files[i].getName());
            }
            if (podcastNames.size() == 0) {
                podcastNames.add(0,"There are no podcast files in the podcast directory");
            }
        } else {
            podcastNames.add(0,"There are no podcast files in the podcast directory");
        }

    }

    public String getItem (String item) {
        int index = Integer.parseInt(item);
        String fileName = "Undefined";
        if (index < podcastNames.size()) {
            fileName = podcastNames.get(index);
        }
        return(fileName);
    }

    public int getPlaying() {
        return playing;
    }

    public void setPlaying(int playing) {
        this.playing = playing;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PodCasts podCasts = (PodCasts) o;

        if (playing != podCasts.playing) return false;
        return podcastNames.equals(podCasts.podcastNames);

    }

    @Override
    public int hashCode() {
        int result = podcastNames.hashCode();
        result = 31 * result + playing;
        return result;
    }

    @Override
    public String toString() {
        return "PodCasts{" +
                "podcastNames=" + podcastNames +
                ", playing=" + playing +
                ", progress=" + progress +
                '}';
    }
}
