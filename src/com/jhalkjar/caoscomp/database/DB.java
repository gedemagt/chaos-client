package com.jhalkjar.caoscomp.database;

import com.codename1.io.Log;
import com.jhalkjar.caoscomp.backend.DBRute;
import com.jhalkjar.caoscomp.backend.Gym;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.backend.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/9/17.
 */
public class DB {

    private static final DB INSTANCE = new DB();

    public static DB getInstance() {
        return INSTANCE;
    }

    private LocalDatabase local = new LocalDatabase();
    private WebDatabase web = new WebDatabase();

    private DB() {
        web.refresh();
        local.refresh();
    }

    public List<User> getUsers() {
        return web.getUsers();
    }

    public List<Gym> getGyms() {
        return web.getGyms();
    }

    private boolean contains(Rute r, List<Rute> rutes) {
        for(Rute rn : rutes) {
            if(r.getUUID().equals(rn.getUUID())) return true;
        }
        return false;
    }

    public List<Rute> getRutes() {
        List<Rute> l = new ArrayList<>();
        l.addAll(local.getRutes());
        for(Rute r : web.getRutes()) {
            if(!contains(r, l)) l.add(r);
        }
        return l;
    }

    public void download(Rute r) {
        if(!local.hasRute(r)) {
            Log.p("Downloads rute " + r.toString());
            local.addRute(r);
        }
        else {
            Log.p("Rute already exists rute!" + r.toString());
        }
        local.refresh();
    }

    public void syncRutes() {
        for(Rute r : local.getRutes()) {
            web.updateCoordinates(r);
        }
    }

    public Rute createRute(String name, String image_url, User author, Gym gym, Date date) {
        DBRute r = (DBRute) local.createRute(name, image_url, author, gym, date);
        web.uploadRute(r);
        return r;
    }

    public User createUser(String name, String email, String passwordHash, Gym gym, Date date) {
        User u = local.createUser(name, email, passwordHash, gym, date);
        web.uploadUser(u);
        return u;
    }

    public Gym createGym(String name, double lat, double lon, Date date) {
        Gym u = local.createGym(name, lat, lon, date);
        web.uploadGym(u);
        return u;
    }

    public void refresh() {
        web.refresh();
        local.refresh();
    }
}
