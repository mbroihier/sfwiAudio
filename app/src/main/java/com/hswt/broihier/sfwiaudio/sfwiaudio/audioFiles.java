package com.hswt.broihier.sfwiaudio.sfwiaudio;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_PODCASTS;
import static android.os.Environment.getExternalStorageDirectory;

public class audioFiles {

    public static final List<PodCastItem> ITEMS = new ArrayList<PodCastItem>();

    public static final Map<String, PodCastItem> ITEM_MAP = new HashMap<String, PodCastItem>();

    private static final String TAG="audioFiles";
    static {
        String path = getExternalStorageDirectory() + "/" + DIRECTORY_PODCASTS;
        Log.d(TAG,"attempted path: "+path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d(TAG,"Size: "+files.length);
        for (int i=0; i < files.length; i++){
            Log.d(TAG,"File "+i+":"+files[i].getName());
            addItem (i,files[i].getName());
        }

    }

    private static void addItem(int id, String item){
        PodCastItem fullitem = new PodCastItem(id, item,"");
        ITEMS.add(fullitem);
        ITEM_MAP.put(fullitem.id, fullitem);
    }


    public static class PodCastItem {
        public final String id;
        public final String content;
        public final String details;

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
