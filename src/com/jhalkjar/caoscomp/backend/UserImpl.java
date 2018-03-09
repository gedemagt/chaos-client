package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DatabaseEntryImpl;

import java.util.Date;

/**
 * Created by jesper on 11/7/17.
 */
public class UserImpl extends DatabaseEntryImpl implements User  {

    private String name, email, passwordHash;
    private Gym gym;
    private Role role;

    public UserImpl(long id, String uuid, Date date, String name, String email, Gym gym, String passwordHash, Role role, int status) {
        super(uuid, id, date, status);
        this.name = name;
        this.email = email;
        this.gym = gym;
        this.role = role;
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
    public Role getRole(){return role;}

    @Override
    public String toString() {
        return name;
    }

}
