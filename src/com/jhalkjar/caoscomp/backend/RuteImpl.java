package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.database.DB;
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
    public void setSector(Sector sector) {
        this.sector = sector;
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
    private String tag;

    public RuteImpl(long id, String uuid, String imageuuid, Date date, Date lastEdit, String name, User author, Sector sector, List<Point> points, Grade grade, int status, String tag) {
        super(id, uuid, date, name, author, sector, points, grade, status);
        this.lastEdit = lastEdit;
        this.imageuuid = imageuuid;
        this.status = status;
        this.tag = tag;
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

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
        DB.getInstance().save(this);
    }

}
