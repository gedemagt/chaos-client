package com.jhalkjar.caoscomp.backend;

import com.codename1.io.Log;
import com.codename1.ui.Image;
import com.jhalkjar.caoscomp.database.ChaosDatabase;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.Point;

import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/5/17.
 */
public class RuteImpl extends AbstractRute{

    public void getImage(ImageListener callback) {
        if(image == null) database.getImage(uuid, image1 -> {
            image = image1;
            callback.onImage(image);
        });
        else callback.onImage(image);

    }

    @Override
    public Date lastEdit() {
        return lastEdit;
    }

    @Override
    public void setSaved(Date date) {
        lastEdit = date;
    }

    @Override
    public void setLocal(boolean b) {
        if(isLocal() && !b) {
            database.delete(this);
            List<Rute> r = DB.getInstance().getRutes();
            this.database = ((RuteImpl) r.get(r.indexOf(this))).database;
            this.id = -1;
            Log.p("Unlocalize " + toString());
        }
        else if(!isLocal() && b) DB.getInstance().download(this, newRute -> {
            Log.p("Localize " + toString());
            this.id = newRute.getID();
            this.database = ((RuteImpl) newRute).database;
        });
    }

    private Image image;
    private ChaosDatabase database;
    private Date lastEdit;

    public RuteImpl(long id, String uuid, Date date, Date lastEdit, String name, User author, Gym gym, List<Point> points, ChaosDatabase database) {
        super(id, uuid, date, name, author, gym, points);
        this.database = database;
        this.lastEdit = lastEdit;

    }

    @Override
    public String toString() {
        return "Rute<" + name + "@" + uuid + " - " + isLocal() +">";
    }

}
