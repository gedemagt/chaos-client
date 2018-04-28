package com.jhalkjar.caoscomp.backend;

import java.util.ArrayList;
import java.util.List;

public class RuteCollection {


    private List<Rute> rutes = new ArrayList<>();

    public RuteCollection(List<Rute> r) {
        for(Rute rr : r) rutes.add(rr);
    }

    public List<Rute> getAllRutes() {
        return rutes;
    }

    public RuteFilter filter() {
        return new RuteFilter(rutes);
    }

    public int size() {
        return rutes.size();
    }



}

