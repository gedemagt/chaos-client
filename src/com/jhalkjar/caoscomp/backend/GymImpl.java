package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DatabaseEntryImpl;

import java.util.Date;

/**
 * Created by jesper on 11/7/17.
 */
public class GymImpl extends DatabaseEntryImpl implements Gym {

    private String name;
    private double lat, lon;

    public GymImpl(long id, String uuid, Date date, String name, double lat, double lon) {
        super(uuid, id, date);
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public double getLat() {
        return lat;
    }

    @Override
    public double getLon() {
        return lon;
    }

}
