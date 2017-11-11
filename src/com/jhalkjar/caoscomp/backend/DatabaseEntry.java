package com.jhalkjar.caoscomp.backend;

import java.util.Date;

/**
 * Created by jesper on 11/10/17.
 */
public interface DatabaseEntry {

    String getUUID();
    long getID();
    Date getDate();

}
