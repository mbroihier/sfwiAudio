package com.hswt.broihier.sfwiaudio.sfwiaudio;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_PODCASTS;
import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by Mark Broihier 1/25/17
 */
public class audioFiles {

    public static final List<PodCastItem> ITEMS = new ArrayList<PodCastItem>();

    public static final Map<String, PodCastItem> ITEM_MAP = new HashMap<String, PodCastItem>();

    private static final String TAG="audioFiles";
    static {
        refreshPodcastList();
    }
    /**
     * refresh podcast list
     * <pre>
     *
     * {@code
     * Pseudo code:
     * open the podcast directory and read it
     * if there are files {
     *     build the list podcasts to display
     *     if the list is empty {
     *         put an error message into the display list;
     *     }
     * } else {
     *     put an error message into the display list
     * }
     * }
     * </pre>
     */
    public static void refreshPodcastList() {
        String path = getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS;
        Log.d(TAG,"from within refreshPodcastList - attempted path: "+path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        emptyItems();
        if (files != null) {
            Log.d(TAG,"Size: "+files.length);
            int storageIndex = 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals("state.bin")) continue;
                Log.d(TAG, "File " + i + ":" + files[i].getName());
                addItem(storageIndex++, files[i].getName());
            }
            if (storageIndex == 0) {
                addItem(0,"There are no podcasts in the podcast directory");
            }
        } else {
            addItem(0,"There are no podcasts in the podcast directory or you have not given the app permission to access external storage");
        }
    }
    /**
     * add item to the display list
     * <pre>
     *
     * {@code
     * Pseudo code:
     * create a PodCastItem object;
     * add it to the list;
     * add it to the map;
     * }
     * </pre>
     */
    private static void addItem(int id, String item){
        PodCastItem fullitem = new PodCastItem(id, item,"");
        ITEMS.add(fullitem);
        ITEM_MAP.put(fullitem.id, fullitem);
    }

    /**
     * empty display items
     * <pre>
     *
     * {@code
     * Pseudo code:
     * clear list;
     * clear map;
     * }
     * </pre>
     */
    private static void emptyItems(){
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static class PodCastItem {
        public final String id;
        public final String content;
        public final String details;

        /**
         * Constructor
         * @param id of list
         * @param content file name
         * @param details if necessary - detail information
         */

        public PodCastItem(int id, String content, String details) {
            this.id = ""+id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
