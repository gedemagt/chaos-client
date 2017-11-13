package com.jhalkjar.caoscomp.backend;

import com.codename1.io.Log;
import com.jhalkjar.caoscomp.database.DatabaseEntryImpl;

import java.util.Date;

/**
 * Created by jesper on 11/7/17.
 */
public class UserImpl extends DatabaseEntryImpl implements User  {

    private String name, email, passwordHash;
    private Gym gym;

    public UserImpl(long id, String uuid, Date date, String name, String email, Gym gym, String passwordHash) {
        super(uuid, id, date);
        this.name = name;
        this.email = email;
        this.gym = gym;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public Gym getGym() {
        return gym;
    }

    @Override
    public String toString() {
        return name + "(" + uuid + ")";
    }

}
