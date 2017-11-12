package com.jhalkjar.caoscomp.backend;

import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.jhalkjar.caoscomp.database.WebDatabase;
import com.jhalkjar.caoscomp.gui.Point;

import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/5/17.
 */
public class WebRute extends AbstractRute{

    public void getImage(ImageListener imageListener) {
        if(image == null) database.getImage(uuid, img-> {
            imageListener.onImage(img);
            this.image = img;
        });
        else imageListener.onImage(image);
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

    private Image image;
    private WebDatabase database;

    public WebRute(long id, String uuid, String name, User author, Gym gym, List<Point> points, Date date, WebDatabase database) {
        super(id, uuid, date, name, author, gym, points);
        this.database = database;
    }

}
