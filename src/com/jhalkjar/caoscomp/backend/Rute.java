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
    Sector getSector();
    void setSector(Sector sector);
  
    Grade getGrade();
    void setGrade(Grade grade);

    String getTag();
    void setTag(String tag);
  
    void getImage(ImageListener callback);
    Date lastEdit();

    String getImageUUID();

    void save();
    void delete();

}
