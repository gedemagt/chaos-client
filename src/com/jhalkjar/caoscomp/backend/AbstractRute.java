package com.jhalkjar.caoscomp.backend;
import com.jhalkjar.caoscomp.database.DatabaseEntryImpl;
import com.jhalkjar.caoscomp.gui.Point;
import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/9/17.
 */
public abstract class AbstractRute extends DatabaseEntryImpl implements Rute {

    protected String name;
    protected User author;
    protected Gym gym;
    protected List<Point> p;

    public AbstractRute(long id, String uuid, Date date, String name, User author, Gym gym, List<Point> points) {
        super(uuid, id, date);
        this.author = author;
        this.gym = gym;
        this.name = name;
        this.p = points;
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


}
