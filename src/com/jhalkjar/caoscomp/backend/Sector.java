package com.jhalkjar.caoscomp.backend;


import com.jhalkjar.caoscomp.database.DatabaseEntry;

/**
 * Created by jesper on 11/7/17.
 */
public interface Sector extends DatabaseEntry {
    String getName();
    Gym getGym();
}
