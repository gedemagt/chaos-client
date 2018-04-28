package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DB;

import java.util.ArrayList;
import java.util.List;

public class CompetitionImpl implements Competition{

    private List<Rute> rutes;

    public CompetitionImpl() {
        rutes = new ArrayList();
        List<String> uuids = new ArrayList<String>();
        uuids.add("3d4cb3f8-a00d-4c77-8b4e-24a605715d6b");
        uuids.add("fc4528d5-bfd8-481b-8952-20e4c3f755d4");
        uuids.add("c8b045d0-e67a-496f-a407-218745abe7a7");
        uuids.add("eaea0901-ed71-4428-8c56-1b0a4bb1315d");
        uuids.add("bd73668c-cfce-4c61-966d-8354405bcd13");

        for(Rute r : DB.getInstance().getRutes()) {
            if(uuids.contains(r.getUUID())) rutes.add(r);
        }
    }

    @Override
    public List<Rute> getRutes() {
        return rutes;
    }

    @Override
    public int getPin() {
        return 0;
    }
}
