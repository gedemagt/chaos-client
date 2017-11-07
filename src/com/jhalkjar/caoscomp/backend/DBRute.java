package com.jhalkjar.caoscomp.backend;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.ui.Image;
import com.jhalkjar.caoscomp.database.RuteDatabase;
import com.jhalkjar.caoscomp.gui.Point;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jesper on 11/5/17.
 */
public class DBRute {

    public String getCreator() {
        return creator;
    }

    public String getImageUrl() {
        return image_url;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        if(image == null) {
            try {
             image = Image.createImage(FileSystemStorage.getInstance().openInputStream(image_url));
            } catch(Exception ex){
                Log.e(ex);
            }
        }

        return image;
    }

    public ArrayList<Point> getPoints() {
        return p;
    }

    public long getID() {
        return id;
    }

    public void save() {
        db.saveRute(this);
    }

    public void delete() {
        db.deleteRute(this);
    }

    private String creator, name, image_url;
    private ArrayList<Point> p;
    private long id;
    private RuteDatabase db;
    private Image image;

    public DBRute(String name, String image, ArrayList<Point> p, String creator, long id, RuteDatabase db) {
        this.creator = creator;
        this.name = name;
        this.p = p;
        this.id = id;
        this.image_url = image;
        this.db = db;
    }



}
