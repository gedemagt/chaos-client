package com.jhalkjar.caoscomp.database;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.jhalkjar.caoscomp.backend.*;

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

    public User getLoggedInUser() {
        String username = Preferences.get("logged_in_user", "");
        User loggedin = local.getUser(username);
        return loggedin;
    }

    private DB() {
        local.refresh();
        web.refresh(()-> {
            for(Gym g : web.getGyms()) {
                if(!local.getGyms().contains(g)) local.addGym(g.getUUID(), g.getName(), g.getLat(), g.getLon(), g.getDate());
            }
            for(User u : web.getUsers()) {
                if(!local.getUsers().contains(u)) local.addUser(u.getUUID(), u.getName(), u.getEmail(), u.getPasswordHash(), u.getGym(), u.getDate());
            }

        });

    }

    public User getUser(String uuid) {
        User u = local.getUser(uuid);
        if(u == null) u = web.getUser(uuid);
        if(u == null) {
            throw new IllegalArgumentException("No such user: " + uuid);
        }
        return u;
    }


    public Rute getRute(String uuid) {
        return local.getRute(uuid);
    }


    public Gym getGym(String uuid) {
        return local.getGym(uuid);
    }

    public List<User> getUsers() {
        return web.getUsers();
    }

    public List<Gym> getGyms() {
        List<Gym> l = new ArrayList<>();
        l.addAll(local.getGyms());
        for(Gym r : web.getGyms()) {
            if(!l.contains(r)) l.add(r);
        }
        return l;
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

    public void download(Rute r, Runnable onSucces) {
        if(!local.hasRute(r)) {
            Log.p("Downloads rute " + r.toString());

            String path = FileSystemStorage.getInstance().getAppHomePath() + r.getUUID() + ".jpg";
            web.downloadImage(r.getUUID(), path, () -> {
                local.addRute(r);
                local.setImage(r.getUUID(), path);
                local.refresh();
                onSucces.run();
            });
        }
        else {
            Log.p("Rute already exists rute!" + r.toString());
        }

    }

    public void syncRute(Rute r) {
        web.updateCoordinates(r);
    }

    public Rute createRute(String name, String image_url, User author, Gym gym, Date date) {
        Rute r = local.createRute(name, author, gym, date);
        local.setImage(r.getUUID(), image_url);
        web.uploadRute(r, image_url);
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
        web.refresh(()->{});
        local.refresh();
    }

    public User checkLogin(String username, String password) throws IllegalArgumentException {
        return local.checkLogin(username, password);
    }


}
