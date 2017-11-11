package com.jhalkjar.caoscomp.backend;

import com.codename1.ui.Image;
import com.jhalkjar.caoscomp.database.WebDatabase;
import com.jhalkjar.caoscomp.gui.Point;

import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/5/17.
 */
public class WebRute extends AbstractRute{

    public String getImageUrl() {
        if(image == null) {
            image_url = database.getFile(this, value -> {
                image = value;
            });
        }
        return image_url;
    }

    public Image getImage() {
        if(image == null) {
            image_url = database.getFile(this, value -> {
                image = value;
            });
        }
        return image;
    }

    public void save() {
        database.updateCoordinates(this);
    }

    public void delete() {
        // Nothing
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    private String image_url;
    private Image image;
    private WebDatabase database;

    public WebRute(long id, String uuid, String name, User author, Gym gym, List<Point> points, Date date, WebDatabase database) {
        super(id, uuid, date, name, author, gym, points);
        this.database = database;
    }

}
