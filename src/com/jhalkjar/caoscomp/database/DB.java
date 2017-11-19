package com.jhalkjar.caoscomp.database;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.codename1.util.SuccessCallback;
import com.jhalkjar.caoscomp.backend.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/9/17.
 */
public class DB {

    private static final DB INSTANCE = new DB();
    private boolean isRefreshing;

    public static DB getInstance() {
        return INSTANCE;
    }

    private LocalDatabase local = new LocalDatabase();
    private WebDatabase web = new WebDatabase();

    private List<RefreshListener> refreshListeners = new ArrayList<>();
    private ImageProvider imgProvider;

    public User getLoggedInUser() {
        String username = Preferences.get("logged_in_user", "");
        User loggedin = local.getUser(username);
        return loggedin;
    }

    private DB() {
        imgProvider = new ImageProvider(local, web);
    }

    public boolean isLocal(Rute r) {
        return local.hasRute(r);
    }

    public void delete(Rute r) {
        if(r.isLocal()) local.delete(r);
        web.delete(r);
    }

    public void save(Rute r) {
        if(r.isLocal()) local.save(r);
        web.save(r);
    }

    public Gym getGym(String uuid) {
        return local.getGym(uuid);
    }

    public List<Gym> getGyms() {
        List<Gym> l = new ArrayList<>();
        l.addAll(local.getGyms());
        for(Gym r : web.getGyms()) {
            if(!l.contains(r)) l.add(r);
        }
        return l;
    }

    public List<User> getUsers() {
        List<User> l = new ArrayList<>();
        l.addAll(local.getUsers());
        for(User r : web.getUsers()) {
            if(!l.contains(r)) l.add(r);
        }
        return l;
    }

    public ImageProvider getImageProvider() {
        return imgProvider;
    }

    public List<Rute> getRutes() {
        List<Rute> l = new ArrayList<>();
        l.addAll(local.getRutes());
        for(Rute r : web.getRutes()) {
            if(!l.contains(r)) l.add(r);
        }

        Collections.sort(l, (o1, o2) -> o1.getName().compareTo(o2.getName()));

        return l;
    }
    
    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void download(Rute r, SuccessCallback<Rute> onSucces) {
        if(!local.hasRute(r)) {
            Log.p("[DB] Downloads rute " + r.toString());

            String path = FileSystemStorage.getInstance().getAppHomePath() + r.getUUID() + ".jpg";
            web.downloadImage(r.getUUID(), path, () -> {
                local.addRute(r);
                local.setImage(r.getUUID(), path);
                local.refresh();
                onSucces.onSucess(local.getRute(r.getUUID()));
            });
        }
        else {
            Log.p("[DB] Rute already exists rute!" + r.toString());
        }

    }

    public void sync() {
        isRefreshing = true;
        for(RefreshListener l : refreshListeners) l.OnBeginRefresh();
        local.refresh();
        web.refresh(()-> {
            for(Gym g : web.getGyms()) {
                if(!local.getGyms().contains(g)) local.addGym(g.getUUID(), g.getName(), g.getLat(), g.getLon(), g.getDate());
            }
            for(Gym g : local.getGyms()) {
                if(!web.getGyms().contains(g)) web.uploadGym(g);
            }

            for(User u : web.getUsers()) {
                if(!local.getUsers().contains(u)) local.addUser(u.getUUID(), u.getName(), u.getEmail(), u.getPasswordHash(), u.getGym(), u.getDate());
            }
            for(User u : local.getUsers()) {
                if(!web.getUsers().contains(u)) web.uploadUser(u);
            }

            for(Rute u : local.getRutes()) {
                if(!web.getRutes().contains(u)) {
                    try {
                        web.uploadRute(u, local.getImageUrl(u.getUUID()));
                    } catch (NoImageException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Rute webRute = web.getRute(u.getUUID());
                    Log.p("local: " + u.lastEdit());
                    Log.p("web: " + webRute.lastEdit());
                    if(webRute.lastEdit().getTime() < u.lastEdit().getTime()) {
                        web.save(u);
                    }
                    else local.save(webRute);
                }
            }
            local.refresh();
            web.refresh(()-> {
                isRefreshing = false;
                for(RefreshListener l : refreshListeners) l.OnEndRefresh();
            });
        });
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

    public User checkLogin(String username, String password) throws IllegalArgumentException {
        return local.checkLogin(username, password);
    }

    public void addRefreshListener(RefreshListener l) {
        refreshListeners.add(l);
    }

    public void setLocal(Rute rute, boolean b,  SuccessCallback<Rute> onSucces) {
        if(b && !local.hasRute(rute)) {
            Log.p("[DB] Sets rute " + rute.toString() + " local");
            download(rute, onSucces);
        }
        else if(!b) {
            Log.p("[DB] Sets rute " + rute.toString() + " unlocal");
            local.delete(rute);
            onSucces.onSucess(web.getRute(rute.getUUID()));
        }
    }

    public interface RefreshListener {
        void OnBeginRefresh();
        void OnEndRefresh();
    }

}
