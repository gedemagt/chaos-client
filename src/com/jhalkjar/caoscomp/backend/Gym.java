package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DatabaseEntry;

import java.util.List;

/**
 * Created by jesper on 11/7/17.
 */
public interface Gym extends DatabaseEntry {

    String getName();
    List<Sector> getSectors();
    void addSector(Sector s);
    void setSectors(List<Sector> s);
    Sector getSector(String name);
    double getLat();
    double getLon();
}
