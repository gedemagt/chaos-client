package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.database.DatabaseEntryImpl;

import java.util.Date;
import java.util.List;

public class CompetitionImpl extends DatabaseEntryImpl implements Competition{

    private List<Rute> rutes;
    private List<User> users;
    private Date start, end;
    private String name;
    private int pin, type;



    public CompetitionImpl(String uuid, Date date, int status, String name, Date start, Date end, int type, List<User> admins, List<Rute> rutes, int pin) {
        super(uuid, 0, date, status);
        this.start = start;
        this.end = end;
        this.type = type;
        this.name = name;
        this.users = admins;
        this.rutes = rutes;
        this.pin = pin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Date getStart() {
        return start;
    }

    @Override
    public Date getStop() {
        return end;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public List<User> getAdmins() {
        return users;
    }

    @Override
    public List<Rute> getRutes() {
        return rutes;
    }

    @Override
    public int getPin() {
        return pin;
    }

    @Override
    public void save() {
        DB.getInstance().saveCompetition(this);
    }

    @Override
    public Status getStatus(User u, Rute r) {
        return DB.getInstance().getStatus(this, r, u);
    }

    @Override
    public void setStatus(User u, Rute r, Status s) {
        DB.getInstance().setStatus(this, r, u, s);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setStart(Date start) {
        this.start = start;
    }

    @Override
    public void setStop(Date stop) {
        this.end = stop;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void addRute(Rute r) {
        this.rutes.add(r);
        DB.getInstance().addRute(this, r);
    }

    @Override
    public void addAdmin(User r) {
        this.users.add(r);
    }

    @Override
    public void removeAdmin(User r) {
        this.users.remove(r);
    }
}
