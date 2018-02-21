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

    public void getImage(String uuid, ImageListener l){
        if(images.containsKey(uuid)) l.onImage(images.get(uuid));
        else {

            ldb.getImage(uuid, new ImageListener() {
                @Override
                public void onImage(Image image) {
                    images.put(uuid, image);
                    l.onImage(image);
                }

                @Override
                public void onError() {
                    Log.p("Image with UUID=" + uuid + " was not in the local database. Trying webdatabase.");
                    wdb.getImage(uuid, new ImageListener() {
                        @Override
                        public void onImage(Image image) {
                            images.put(uuid, image);
                            l.onImage(image);
                        }

                        @Override
                        public void onError() {
                            Log.p("Image could not be found in either local or web database!");
                            l.onError();
                        }
                    });
                }
            });

        }
    }

}
