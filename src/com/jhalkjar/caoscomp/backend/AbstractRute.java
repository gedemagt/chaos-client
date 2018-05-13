package com.jhalkjar.caoscomp.backend;
import com.jhalkjar.caoscomp.database.DatabaseEntryImpl;
import com.jhalkjar.caoscomp.gui.misc.Point;
import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/9/17.
 */
public abstract class AbstractRute extends DatabaseEntryImpl implements Rute {

    protected String name;
    protected User author;
    protected Sector sector;
    protected List<Point> p;
    protected Grade grade;

    public AbstractRute(long id, String uuid, Date date, String name, User author, Sector sector, List<Point> points, Grade grade, int status) {
        super(uuid, id, date, status);
        this.author = author;
        this.sector = sector;
        this.name = name;
        this.p = points;
        this.grade = grade;
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
    public Sector getSector() {
        return sector;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public Grade getGrade() {   return grade; }

    @Override
    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}

