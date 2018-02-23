package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DatabaseEntryImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/7/17.
 */
public class GymImpl extends DatabaseEntryImpl implements Gym {

    private String name;
    private double lat, lon;
    private List<Sector> sectors = new ArrayList<>();

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
    public List<Sector> getSectors() {
        return sectors;
    }

    @Override
    public void addSector(Sector s) {

    }

    @Override
    public Sector getSector(String uuid) {
        for(Sector s : sectors) {
            if(uuid.equals(s.getUUID())) return s;
        }
        return null;
    }

    @Override
    public double getLat() {
        return lat;
    }

    @Override
    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "Gym<" + name + ">";
    }

}
