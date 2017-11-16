package com.jhalkjar.caoscomp.database;

import com.jhalkjar.caoscomp.backend.Rute;

/**
 * Created by jesper on 11/14/17.
 */
public interface DatabaseListener {

    void OnSaved(Rute r);
    void OnDeletedRute(Rute r);


}
