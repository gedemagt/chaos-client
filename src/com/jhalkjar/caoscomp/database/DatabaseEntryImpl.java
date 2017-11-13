package com.jhalkjar.caoscomp.database;

import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.backend.UserImpl;

import java.util.Date;

/**
 * Created by jesper on 11/10/17.
 */
public class DatabaseEntryImpl implements DatabaseEntry {

    protected String uuid;
    protected long id;
    protected Date date;

    public DatabaseEntryImpl(String uuid, long id, Date date) {
        this.uuid = uuid;
        this.id = id;
        this.date = date;
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
    public boolean equals(Object u) {
        if(u == null) return false;
        if (!DatabaseEntry.class.isAssignableFrom(u.getClass())) {
            return false;
        }
        final DatabaseEntry other = (DatabaseEntry) u;

        return other.getUUID().equals(uuid);
    }
}
