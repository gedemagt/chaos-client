package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DatabaseEntryImpl;

import java.util.Date;

/**
 * Created by jesper on 11/7/17.
 */
public class SectorImpl extends DatabaseEntryImpl implements Sector {

    private String name;
    private Gym gym;

    public SectorImpl(long id, String uuid, Date date, String name, Gym gym) {
        super(uuid, id, date);
        this.name = name;
        this.gym = gym;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Gym getGym() {
        return gym;
    }


    @Override
    public String toString() {
        return "Sector<" + name + ">";
    }

}
