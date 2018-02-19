package com.jhalkjar.caoscomp.database;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.NetworkManager;
import com.codename1.io.Preferences;
import com.codename1.util.SuccessCallback;
import com.jhalkjar.caoscomp.backend.*;

import java.util.*;

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
    private WebDatabase web;

    private List<RefreshListener> refreshListeners = new ArrayList<>();
    private ImageProvider imgProvider;

    public User getLoggedInUser() {
        String username = Preferences.get("logged_in_user", "");
        User loggedin = local.getUser(username);
        return loggedin;
    }

    public boolean checkUsername(String username) {
        if(username.equals("")) return false;
        return web.checkUserName(username);
    }

    public void forceWebRefresh() {
        web.resetLastVisit();
        sync();
    }

    public void syncGyms() {
        for(Gym g : web.getGyms()) {
            if(local.getGym(g.getUUID()) == null) local.addGym(g.getUUID(), g.getName(), g.getLat(), g.getLon(), g.getDate());
        }
    }

    private DB() {
        web = new WebDatabase();
        imgProvider = new ImageProvider(local, web);
//        NetworkManager.getInstance().addErrorListener(evt -> {
//            for(RefreshListener l : refreshListeners) l.OnEndRefresh();
//            evt.consume();
//        });

    }

    public void delete(Rute r) {
        local.delete(r);
        web.delete(r);
    }

    public void save(Rute r) {
        local.save(r);
        web.save(r);
    }

    public Gym getGym(String uuid) {
        Gym g = local.getGym(uuid);
        if(g == null) {
            g = web.getGym(uuid);
            if(g == null) g = local.unknownGym;
            else g = local.addGym(g.getUUID(), g.getName(), g.getLat(), g.getLon(), g.getDate());
        }

        return g;
    }

    public User getUser(String uuid) {
        User g = local.getUser(uuid);
        if(g == null) {
            g = web.getUser(uuid);
            if(g == null) g = local.unknownUser;
            else {
                g = local.addUser(g.getUUID(), g.getName(), g.getEmail(), g.getPasswordHash(), g.getGym(), g.getDate(), g.getRole());
            }
        }
        return g;
    }

    public List<Gym> getGyms() {
        List<Gym> l = new ArrayList<>();
        l.addAll(local.getGyms());
        return l;
    }

    public List<User> getUsers() {
        List<User> l = new ArrayList<>();
        l.addAll(local.getUsers());
        return l;
    }

    public ImageProvider getImageProvider() {
        return imgProvider;
    }

    public List<Rute> getRutes() {
        List<Rute> l = new ArrayList<>();
        l.addAll(local.getRutes());

        Collections.sort(l, (o1, o2) -> o1.getName().compareTo(o2.getName()));

        return l;
    }
    
    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void downloadImage(Rute r, SuccessCallback<Rute> onSucces) {
        if(!local.hasRute(r)) {
            Log.p("[DB] Downloads rute " + r.toString());

            String path = FileSystemStorage.getInstance().getAppHomePath() + r.getImageUUID() + ".jpg";
            web.downloadImage(r.getImageUUID(), path, () -> {
                local.setImage(r.getImageUUID(), path);
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
        web.getRutes(ruteMap -> {
            for(Map.Entry<String, Rute> entry : ruteMap.entrySet()) {
                Rute r = entry.getValue();
                if(r.getStatus() == 1) local.delete(r);
                else if(local.hasRute(r)) local.save(r);
                else {
                    if(local.getGym(r.getGym().getUUID()) == null){
                        Gym g = r.getGym();
                        local.addGym(g.getUUID(), g.getName(), g.getLat(), g.getLon(), g.getDate());
                    }
                    if(local.getUser(r.getAuthor().getUUID()) == null){
                        User u = r.getAuthor();
                        local.addUser(r.getUUID(), u.getName(), u.getEmail(), u.getPasswordHash(), r.getGym(), u.getDate(), u.getRole());
                    }
                    local.addRute(entry.getValue());
                }
            }
            local.refresh();
            for(RefreshListener l : refreshListeners) l.OnEndRefresh();
        });
    }

    public Rute createRute(String name, String image_url, User author, Gym gym, Date date, String imageUUID, Grade grade) {
        Rute r = local.createRute(name, author, gym, date, imageUUID, grade);
        String uploadURL = null;
        if(image_url != null) {
            String new_url = r.getImageUUID() + ".jpg";
            String long_new_url = FileSystemStorage.getInstance().getAppHomePath() + new_url;
            FileSystemStorage.getInstance().rename(image_url, new_url);
            local.setImage(r.getImageUUID(), long_new_url);
            uploadURL = long_new_url;
        }
        web.uploadRute(r, uploadURL);
        return r;
    }

    public User createUser(String name, String email, String passwordHash, Gym gym, Date date, Role role) {
        User u = local.createUser(name, email, passwordHash, gym, date, role);
        web.uploadUser(u);
        return u;
    }

    public Gym createGym(String name, double lat, double lon, Date date) {
        Gym u = local.createGym(name, lat, lon, date);
        web.uploadGym(u);
        return u;
    }

    public User checkLogin(String username, String password) {
        String uuid = web.login(username, password);
        if(local.getUser(uuid) == null) {
            User u = web.getUser(uuid);
            local.addUser(u.getUUID(), u.getName(), u.getEmail(), u.getPasswordHash(), u.getGym(), u.getDate(), u.getRole());
        }
        return local.getUser(uuid);
    }

    public void addRefreshListener(RefreshListener l) {
        refreshListeners.add(l);
    }

    public boolean checkGymname(String text) {
        return web.checkGymName(text);
    }

//    public void setLocal(Rute rute, boolean b,  SuccessCallback<Rute> onSucces) {
//        Log.p("[DB] Sets rute " + rute.toString() + " local");
//        if(b) downloadImage(rute, onSucces);
//    }

    public interface RefreshListener {
        void OnBeginRefresh();
        void OnEndRefresh();
        void OnRefreshError();
    }

}
