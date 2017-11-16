package com.jhalkjar.caoscomp.database;

import com.jhalkjar.caoscomp.backend.ImageListener;
import com.jhalkjar.caoscomp.backend.Rute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jesper on 11/14/17.
 */
public abstract class ChaosDatabase {

    public abstract void save(Rute r);
    public abstract void delete(Rute r);
    public abstract void getImage(String uuid, ImageListener listener);

    protected List<DatabaseListener> listeners = new ArrayList<>();

    public void addDatabaseListener(DatabaseListener listener) {
        listeners.add(listener);
    }

}
