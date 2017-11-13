package com.jhalkjar.caoscomp.backend;

import java.util.Date;

/**
 * Created by jesper on 11/7/17.
 */
public class UnknownUser implements User {


    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public String getEmail() {
        return "noone@none.kein";
    }

    @Override
    public String getPasswordHash() {
        return "";
    }

    @Override
    public Gym getGym() {
        return new UnknownGym();
    }

    @Override
    public Date getDate() {
        return new Date(0);
    }

    @Override
    public String getUUID() {
        return "";
    }

    @Override
    public long getID() {
        return -1;
    }
}
