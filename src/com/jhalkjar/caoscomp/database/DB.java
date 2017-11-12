package com.jhalkjar.caoscomp.database;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.codename1.ui.util.ImageIO;
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

    private DB() {
        local.refresh();
        web.refresh();
    }

    public User getUser(String uuid) {

        return local.getUser(uuid);
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
        return web.getGyms();
    }

    private boolean contains(Rute r, List<Rute> rutes) {
        for(Rute rn : rutes) {
            if(r.getUUID().equals(rn.getUUID())) return true;
        }
        return false;
    }

    public List<Rute> getRutes(boolean useLocal) {
        List<Rute> l = new ArrayList<>();
        l.addAll(local.getRutes());
        if(!useLocal) {
            for(Rute r : web.getRutes()) {
                if(!contains(r, l)) l.add(r);
            }
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

    public void syncRutes() {
        for(Rute r : local.getRutes()) {
            web.updateCoordinates(r);
        }
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
        web.refresh();
        local.refresh();
    }
}
