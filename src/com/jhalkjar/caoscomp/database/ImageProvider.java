package com.jhalkjar.caoscomp.database;

import com.codename1.io.Log;
import com.codename1.ui.Image;
import com.jhalkjar.caoscomp.backend.ImageListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jesper on 11/18/17.
 */
public class ImageProvider {

    Map<String, Image> images = new HashMap<>();
    WebDatabase wdb;
    LocalDatabase ldb;

    public ImageProvider(LocalDatabase l, WebDatabase w) {
        this.ldb = l;
        this.wdb = w;
    }

    public void getImage(String uuid, ImageListener l) throws NoImageException {
        if(images.containsKey(uuid)) l.onImage(images.get(uuid));
        else {
            try {
                ldb.getImage(uuid, image -> {
                    images.put(uuid, image);
                    l.onImage(image);
                });
            } catch (NoImageException e) {
                Log.p("Image with UUID=" + uuid + " was not in the local database. Trying webdatabase.");
                try {
                    wdb.getImage(uuid, image -> {
                        images.put(uuid, image);
                        l.onImage(image);
                    });
                } catch (NoImageException e1) {
                    throw new NoImageException("Image could not be found in either local or web database!");
                }
            }
        }
    }

}
