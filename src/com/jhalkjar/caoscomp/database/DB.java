package com.jhalkjar.caoscomp.database;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.jhalkjar.caoscomp.backend.*;
import com.jhalkjar.caoscomp.gui.Login;

import java.util.*;

/**
 * Created by jesper on 11/9/17.
 */
public class DB {

    private static final DB INSTANCE = new DB();

    public static DB getInstance() {
        return INSTANCE;
    }

    private LocalDatabase local = new LocalDatabase();
    private WebDatabase web;

    private List<RefreshListener> refreshListeners = new ArrayList<>();
    private List<RefreshListener> syncRefreshListeners = new ArrayList<>();
    private ImageProvider imgProvider;

    public User getLoggedInUser() {
        String username = Preferences.get("logged_in_user", "");
        User loggedin = local.getUser(username);
        if(loggedin == local.unknownUser) return null;
        return loggedin;
    }

    public boolean checkUsername(String username) {
        if(username.equals("")) return false;
        return web.checkUserName(username);
    }

    public void forceWebRefresh(RefreshListener rf) {
        web.resetLastVisit();
        syncGyms();
        sync(rf);
    }

    public void refreshLocal() {
        for(RefreshListener rl : refreshListeners) rl.OnBeginRefresh();
        local.refresh();
        for(RefreshListener rl : refreshListeners) rl.OnEndRefresh();
    }

    public void syncGyms() {
        Log.p("[DB] Syncing gyms");
        for(RefreshListener rl : syncRefreshListeners) rl.OnBeginRefresh();
        for(Gym g : web.getGyms()) {
            if(local.getGym(g.getUUID()) == null) {
                local.addGym(g.getUUID(), g.getName(), g.getLat(), g.getLon(), g.getDate(), g.getSectors());
            }
            else {
                local.save(g);
            }
        }
        refreshLocal();
        for(RefreshListener rl : syncRefreshListeners) rl.OnEndRefresh();
    }

    private DB() {
        web = new WebDatabase();
        imgProvider = new ImageProvider(local, web);
    }

    public void delete(Rute r) {
        local.delete(r);
        web.delete(r);
    }

    public void save(Rute r) {
        local.save(r);
        web.save(r);
        refreshLocal();
    }

    public void save(Gym g) {
        local.save(g);
        web.save(g);
        refreshLocal();
    }

    public Gym getGym(String uuid) {
        Gym g = local.getGym(uuid);
        if(g == null) {
            g = web.getGym(uuid);
            if(g == null) g = local.unknownGym;
            else g = local.addGym(g.getUUID(), g.getName(), g.getLat(), g.getLon(), g.getDate(), g.getSectors());
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
    
//    public boolean isRefreshing() {
//        return isRefreshing;
//    }

//    public void downloadImage(Rute r, SuccessCallback<Rute> onSucces) {
//        if(!local.hasRute(r)) {
//            Log.p("[DB] Downloads rute " + r.toString());
//
//            String path = FileSystemStorage.getInstance().getAppHomePath() + r.getImageUUID() + ".jpg";
//            web.downloadImage(r.getImageUUID(), path, () -> {
//                local.setImage(r.getImageUUID(), path);
//                local.refresh();
//                onSucces.onSucess(local.getRute(r.getUUID()));
//            });
//        }
//        else {
//            Log.p("[DB] Rute already exists rute!" + r.toString());
//        }
//
//    }


    public void sync(RefreshListener rf) {
        web.getRutes( ruteMap->{
            local.sync(ruteMap);
            for(RefreshListener l : refreshListeners) l.OnEndRefresh();
            rf.OnEndRefresh();
        });
        for(RefreshListener l : refreshListeners) l.OnBeginRefresh();
        rf.OnBeginRefresh();
    }

    public void sync() {
        for(RefreshListener l : refreshListeners) l.OnBeginRefresh();
        local.sync(web.getRutes());
        for(RefreshListener l : refreshListeners) l.OnEndRefresh();

    }

    public Rute createRute(String name, String image_url, User author, Sector sector, Date date, String imageUUID, Grade grade) {
        Rute r = local.createRute(name, author, sector, date, imageUUID, grade);
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

    public Gym createGym(String name, double lat, double lon, Date date, List<Sector> sectors) {
        Gym u = local.createGym(name, lat, lon, date, sectors);
        web.uploadGym(u);
        return u;
    }

    public void checkLogin(String username, String password, WebDatabase.Result<User> onLogin) {
        web.login(username, password, uuid -> {
            if(uuid=="") onLogin.OnResult(null);
            if(local.getUser(uuid) == null) {
                User u = web.getUser(uuid);
                local.addUser(u.getUUID(), u.getName(), u.getEmail(), u.getPasswordHash(), u.getGym(), u.getDate(), u.getRole());
            }
            onLogin.OnResult(local.getUser(uuid));
        });

    }

    public void addRefreshListener(RefreshListener l) {
        refreshListeners.add(l);
    }

    public void addGymsSyncListener(RefreshListener l) {
        syncRefreshListeners.add(l);
    }

    public boolean checkGymname(String text) {
        return web.checkGymName(text);
    }

    public void logout() {
        web.logout();
        Preferences.set("logged_in_user", "");
        new Login().show();
    }

    public Gym getRememberedGym() {
        String g = Preferences.get("last_gym", "NO_NO_NO");
        if(g.equals("NO_NO_NO")) return null;
        return getGym(g);
    }
    public void setRememberedGym(Gym g) {
        Preferences.set("last_gym", g.getUUID());
    }

    public interface RefreshListener {
        void OnBeginRefresh();
        void OnEndRefresh();
        void OnRefreshError();
    }

}
