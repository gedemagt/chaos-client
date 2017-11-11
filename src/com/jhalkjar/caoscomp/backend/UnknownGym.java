package com.jhalkjar.caoscomp.backend;

import java.util.Date;

/**
 * Created by jesper on 11/8/17.
 */
public class UnknownGym implements Gym {
    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public String getUUID() {
        return "";
    }

    @Override
    public long getID() {
        return -1;
    }

    @Override
    public double getLat() {
        return 0;
    }

    @Override
    public double getLon() {
        return 0;
    }

    @Override
    public Date getDate() {
        return new Date(0);
    }
}
