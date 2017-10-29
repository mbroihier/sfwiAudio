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

    /**
     * Constructor for PodCasts
     */

    public PodCasts() {
    }

    /**
     * read the podcast directory and build necessary objects
     * <pre>
     *
     * {@code
     * Pseudo code:
     * attempt to open the podcast directory;
     * if there are files {
     *     put file names into an array
     *     if none of the files were podcasts {
     *         setup to display an error message
     *     }
     * } else {
     *     setup to display an error message
     * }
     * }
     * </pre>
     */
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

    /**
     * get the podcast name at an index
     * <pre>
     * @param item sting of the index into the podcast array
     * @return file name or empty
     * {@code
     * Pseudo code:
     * limit the size of the parsed index;
     * get the podcast name at the limited index or return "undefined";
     * }
     * </pre>
     */
    public String getItem (String item) {
        int index = Integer.parseInt(item);
        String fileName = "Undefined";
        if (index < podcastNames.size()) {
            fileName = podcastNames.get(index);
        }
        return(fileName);
    }

    /**
     * getter of playing
     * @return index of podcast that is playing
     * <pre>
     *
     * {@code
     * Pseudo code:
     * return private copy of playing;
     * }
     * </pre>
     */
    public int getPlaying() {
        return playing;
    }

    /**
     * setter of playing
     *
     * @param playing - index that is playing
     * <pre>
     *
     * {@code
     * Pseudo code:
     * set private copy of playing;
     * }
     * </pre>
     */
    public void setPlaying(int playing) {
        this.playing = playing;
    }

    /**
     * getter of progress
     * @return position of podcast
     * <pre>
     *
     * {@code
     * Pseudo code:
     * return private copy of progress;
     * }
     * </pre>
     */
    public int getProgress() {
        return progress;
    }

    /**
     * setter of progress
     *
     * @param progress - location of podcast
     * <pre>
     *
     * {@code
     * Pseudo code:
     * set private copy of playing;
     * }
     * </pre>
     */
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
