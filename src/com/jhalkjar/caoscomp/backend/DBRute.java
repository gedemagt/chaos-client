package com.jhalkjar.caoscomp.backend;

import com.codename1.ui.Image;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.database.LocalDatabase;
import com.jhalkjar.caoscomp.gui.Point;

import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/5/17.
 */
public class DBRute extends AbstractRute{

    public Image getImage() {
        if(image == null) image = database.getImage(uuid);
        return image;
    }

    public void save() {
        database.updateCoordinates(this);
        DB.getInstance().syncRutes();
    }

    public void delete() {
        database.deleteRute(this);
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    private Image image;
    private LocalDatabase database;

    public DBRute(long id, String uuid, Date date, String name, User author, Gym gym, List<Point> points, LocalDatabase database) {
        super(id, uuid, date, name, author, gym, points);
        this.database = database;

    }


}
