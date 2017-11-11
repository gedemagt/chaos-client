package com.jhalkjar.caoscomp.backend;


import com.jhalkjar.caoscomp.database.DatabaseEntry;

/**
 * Created by jesper on 11/7/17.
 */
public interface Gym extends DatabaseEntry {

    String getName();
    double getLat();
    double getLon();
}
