package com.jhalkjar.caoscomp.database;
import java.util.Date;

/**
 * Created by jesper on 11/10/17.
 */
public class DatabaseEntryImpl implements DatabaseEntry {

    protected String uuid;
    protected long id;
    protected Date date;
    protected int status;

    public DatabaseEntryImpl(String uuid, long id, Date date, int status) {
        this.uuid = uuid;
        this.id = id;
        this.date = date;
        this.status = status;
    }



    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object u) {
        if(u == null) return false;
        if (!DatabaseEntry.class.isAssignableFrom(u.getClass())) {
            return false;
        }
        final DatabaseEntry other = (DatabaseEntry) u;

        return other.getUUID().equals(uuid);
    }
}
