package com.jhalkjar.caoscomp.backend;

import com.codename1.io.Log;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.database.NoImageException;
import com.jhalkjar.caoscomp.gui.Point;

import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/5/17.
 */
public class RuteImpl extends AbstractRute{

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public void getImage(ImageListener callback) throws NoImageException {
        DB.getInstance().getImageProvider().getImage(imageuuid, callback);
    }

    @Override
    public Date lastEdit() {
        return lastEdit;
    }

    @Override
    public String getImageUUID() {
        return imageuuid;
    }

    @Override
    public void save() {
        lastEdit = new Date();
        DB.getInstance().save(this);
    }

    @Override
    public void delete() {
        DB.getInstance().delete(this);
    }

    @Override
    public void setLocal(boolean b) {
        DB.getInstance().setLocal(this, b, value -> {
            this.id = value.getID();
        });
    }

    @Override
    public boolean isLocal() {
        return DB.getInstance().isLocal(this);
    }

    private Date lastEdit;
    private String imageuuid;

    public RuteImpl(long id, String uuid, String imageuuid, Date date, Date lastEdit, String name, User author, Gym gym, List<Point> points) {
        super(id, uuid, date, name, author, gym, points);
        this.lastEdit = lastEdit;
        this.imageuuid = imageuuid;
    }

    @Override
    public String toString() {
        return "Rute<" + name + "@" + uuid +">";
    }

}
