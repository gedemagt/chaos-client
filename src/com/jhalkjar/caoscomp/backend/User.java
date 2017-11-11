package com.jhalkjar.caoscomp.backend;


/**
 * Created by jesper on 11/7/17.
 */
public interface User extends DatabaseEntry {

    String getName();
    String getEmail();
    Gym getGym();

}
