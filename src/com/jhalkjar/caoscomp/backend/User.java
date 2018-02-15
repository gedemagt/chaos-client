package com.jhalkjar.caoscomp.backend;


import com.jhalkjar.caoscomp.database.DatabaseEntry;

/**
 * Created by jesper on 11/7/17.
 */
public interface User extends DatabaseEntry {

    String getName();
    String getEmail();
    String getPasswordHash();
    Gym getGym();
    Role getRole();

}
