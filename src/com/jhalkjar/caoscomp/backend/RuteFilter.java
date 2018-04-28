package com.jhalkjar.caoscomp.backend;

import java.util.ArrayList;
import java.util.List;

public class RuteFilter {

    private List<User> u = new ArrayList<>();
    private List<Gym> g = new ArrayList<>();
    private List<Grade> gr = new ArrayList<>();
    private List<Sector> s = new ArrayList<>();

    private List<Rute> rute = new ArrayList();

    public RuteFilter(List<Rute> rutes) {
        for(Rute r : rutes) rute.add(r);
    }


    public RuteFilter user(User u) {
        if(u != null) this.u.add(u);
        return this;
    }
    public RuteFilter user(List<User> u) {
        for(User uu : u) user(uu);
        return this;
    }


    public RuteFilter gym(Gym g) {
        if(g != null) this.g.add(g);
        return this;
    }
    public RuteFilter gym(List<Gym> g) {
        for(Gym gg : g) gym(gg);
        return this;
    }


    public RuteFilter grade(Grade gr) {
        if(gr != null) this.gr.add(gr);
        return this;
    }
    public RuteFilter grade(List<Grade> gr) {
        for(Grade grgr : gr) grade(grgr);
        return this;
    }


    public RuteFilter sector(Sector s) {
        if(s != null) this.s.add(s);
        return this;
    }
    public RuteFilter sector(List<Sector> s) {
        for(Sector ss : s) sector(ss);
        return this;
    }

    public RuteCollection get() {

        List<Rute> re = new ArrayList();

        for(Rute r : rute) {
            boolean discard = false;
            if(u.size() >0 && !u.contains(r.getAuthor()))           discard = true;
            if(g.size() >0 && !g.contains(r.getSector().getGym()))  discard = true;
            if(gr.size()>0 && !gr.contains(r.getGrade()))           discard = true;
            if(s.size() >0 && !s.contains(r.getSector()))           discard = true;

            if(!discard) re.add(r);
        }

        return new RuteCollection(re);
    }

}
