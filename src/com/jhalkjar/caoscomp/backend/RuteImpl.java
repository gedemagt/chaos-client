package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.Util;
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

    public void getImage(ImageListener callback){
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
        lastEdit = Util.getNow();
        DB.getInstance().save(this);
    }

    @Override
    public void delete() {
        DB.getInstance().delete(this);
    }

    private Date lastEdit;
    private String imageuuid;
    private int status;

    public RuteImpl(long id, String uuid, String imageuuid, Date date, Date lastEdit, String name, User author, Gym gym, List<Point> points, Grade grade, int status) {
        super(id, uuid, date, name, author, gym, points, grade);
        this.lastEdit = lastEdit;
        this.imageuuid = imageuuid;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Rute<" + name + "@" + uuid +">";
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

}
