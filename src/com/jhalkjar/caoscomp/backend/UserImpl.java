package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DatabaseEntryImpl;

import java.util.Date;

/**
 * Created by jesper on 11/7/17.
 */
public class UserImpl extends DatabaseEntryImpl implements User  {

    private String name, email;
    private Gym gym;
    private Role role;

    public UserImpl(long id, String uuid, Date date, String name, Gym gym, Role role, int status) {
        super(uuid, id, date, status);
        this.name = name;
        this.gym = gym;
        this.role = role;
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
    public Role getRole(){return role;}

    @Override
    public String toString() {
        return name;
    }

}
