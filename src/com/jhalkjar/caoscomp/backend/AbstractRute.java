package com.jhalkjar.caoscomp.backend;
import com.codename1.io.Log;
import com.jhalkjar.caoscomp.gui.Point;
import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/9/17.
 */
public abstract class AbstractRute implements Rute {

    protected String name;
    protected User author;
    protected Gym gym;
    protected List<Point> p;
    protected long id;
    protected Date date;
    protected String uuid;

    public AbstractRute(long id, String uuid, Date date, String name, User author, Gym gym, List<Point> points) {
        this.author = author;
        this.gym = gym;
        this.name = name;
        this.p = points;
        this.id = id;
        this.date = date;
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public User getAuthor() {
        return author;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public List<Point> getPoints() {
        return p;
    }

    @Override
    public Gym getGym() {
        return gym;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

}
