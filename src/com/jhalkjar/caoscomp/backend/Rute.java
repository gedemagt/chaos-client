package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DatabaseEntry;
import com.jhalkjar.caoscomp.gui.Point;

import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/7/17.
 */
public interface Rute extends DatabaseEntry {

    String getName();
    User getAuthor();
    List<Point> getPoints();
    Gym getGym();
    void getImage(ImageListener callback);
    Date lastEdit();
    void setSaved(Date date);

    boolean isLocal();
    void setLocal(boolean b);

}
