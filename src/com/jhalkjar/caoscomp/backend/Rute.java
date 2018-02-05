package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DatabaseEntry;
import com.jhalkjar.caoscomp.database.NoImageException;
import com.jhalkjar.caoscomp.gui.Point;

import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/7/17.
 */
public interface Rute extends DatabaseEntry {

    String getName();
    void setName(String name);
    User getAuthor();
    List<Point> getPoints();
    Gym getGym();
    void setGym(Gym gym);
  
    Grade getGrade();
    void setGrade(Grade grade);
  
    void getImage(ImageListener callback) throws NoImageException;
    Date lastEdit();

    String getImageUUID();

    void save();
    void delete();

    boolean isLocal();
    void setLocal(boolean b);


}
