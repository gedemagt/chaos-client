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
    private Sector uncategorized;

    public GymImpl(long id, String uuid, Date date, String name, double lat, double lon, int status) {
        super(uuid, id, date, status);
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        uncategorized = new Sector("Uncategorized", this);
        sectors.add(uncategorized);
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
        sectors.add(s);
    }

    @Override
    public void setSectors(List<Sector> s) {
        for(Sector ss : s) {
            ss.setGym(this);
        }
        sectors = s;
    }

    @Override
    public Sector getSector(String name) {
        for(Sector s : sectors) {
            if(s.getName().equals(name)) return s;
        }
        return uncategorized;
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
        String sec = "";
        for(Sector s : sectors) {
            sec += "," + s.getName();
        }
        return "Gym<" + name + " - " + sec + ">";
    }

}
