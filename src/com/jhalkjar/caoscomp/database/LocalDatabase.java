package com.jhalkjar.caoscomp.database;

import ca.weblite.codename1.db.DAO;
import ca.weblite.codename1.db.DAOProvider;
import com.codename1.db.Database;
import com.codename1.io.Log;
import com.codename1.l10n.ParseException;
import com.jhalkjar.caoscomp.*;
import com.jhalkjar.caoscomp.UUID;
import com.jhalkjar.caoscomp.backend.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by jesper on 11/5/17.
 */
public class LocalDatabase {
    private static String configPath = "/setup.sql";
    private String dbname = "o21k";

    private Map<String, Gym> gyms = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private Map<String, Rute> rutes = new HashMap<>();

    public void refresh() {
        Log.p("Refreshing LocalDatabase..");
        loadGyms();
        loadUsers();
        loadRutes();
        Log.p("Refreshing done!");
    }


    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }


    public List<Gym> getGyms() {
        return new ArrayList<>(gyms.values());
    }

    public User getUser(String uuid) {
        if(!users.containsKey(uuid)) return new UnknownUser();
        return users.get(uuid);
    }


    public Rute getRute(String uuid) {
        return rutes.get(uuid);
    }


    public Gym getGym(String uuid) {
        if(!gyms.containsKey(uuid)) return new UnknownGym();
        return gyms.get(uuid);
    }


    public List<Rute> getRutes() {
        return new ArrayList<>(rutes.values());
    }


    public Rute createRute(String name, String image_url, User author, Gym gym, Date date) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO rutes = provider.get("rute");
            Map rute = (Map) rutes.newObject();
            rute.put("uuid", UUID.randomUUID().toString());
            rute.put("name", name);
            rute.put("coordinates", "[]");
            rute.put("author", author.getID());
            rute.put("gym", gym.getID());
            rute.put("image_url", image_url);
            if(date == null) date = new Date();
            rute.put("datetime", Util.dateFormat.format(date));
            rutes.save(rute);
            db.close();

            loadRutes();

            return getRutes().get(getRutes().size()-1);

        } catch (IOException e) {
            Log.e(e);
        }
        return null;
    }

    public void addRute(Rute r) {
        try {
            Log.p("Adding new rute");
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO rutes = provider.get("rute");
            Map rute = (Map) rutes.newObject();
            rute.put("uuid", r.getUUID());
            rute.put("name", r.getName());
            rute.put("coordinates", Util.valsToString(r.getPoints()));
            rute.put("author", r.getAuthor().getUUID());
            rute.put("gym", r.getGym().getUUID());
            rute.put("image_url", r.getImageUrl());
            rute.put("datetime", Util.dateFormat.format(r.getDate()));
            rutes.save(rute);
            db.close();

            loadRutes();

        } catch (IOException e) {
            Log.e(e);
        }
    }

    public boolean hasRute(Rute r) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("rute");
            Object obj = games.getById(r.getID());
            return obj != null;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public User createUser(String name, String email, String passwordHash, Gym gym, Date date) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO users = provider.get("user");
            Map user = (Map) users.newObject();
            user.put("uuid", UUID.randomUUID().toString());
            user.put("name", name);
            user.put("email", email);
            user.put("password", passwordHash);
            user.put("gym", gym.getID());
            if(date == null) date = new Date();
            user.put("datetime", Util.dateFormat.format(date));
            users.save(user);
            db.close();

            loadUsers();

            return getUsers().get(getUsers().size()-1);

        } catch (IOException e) {
            Log.e(e);
        }
        return null;
    }

    public Gym createGym(String name, double lat, double lon, Date date) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO gyms = provider.get("user");
            Map gym = (Map) gyms.newObject();
            gym.put("uuid", UUID.randomUUID().toString());
            gym.put("name", name);
            gym.put("lat", lat);
            gym.put("lon", lon);
            if(date == null) date = new Date();
            gym.put("datetime", Util.dateFormat.format(date));
            gyms.save(gym);
            db.close();

            loadGyms();

            return getGyms().get(getGyms().size()-1);

        } catch (IOException e) {
            Log.e(e);
        }
        return null;
    }

    private void loadRutes() {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("rute");
            List<Map> allRutes = games.fetchAll();
            rutes.clear();
            for(Map m : allRutes) {

                String name = (String) m.get("name");
                String image_url = (String) m.get("image_url");
                String points = (String) m.get("coordinates");
                String author = (String) m.get("author");
                String gym = (String) m.get("gym");
                Date date = getDate(m.get("datetime"));
                String uuid = (String) m.get("uuid");
                long id = (Long) m.get("id");

                DBRute r = new DBRute(id, uuid, date, name, getUser(author), getGym(gym), Util.stringToVals(points), image_url, this);
                rutes.put(uuid, r);
            }
            db.close();

        } catch (IOException e) {
            Log.e(e);
        }
    }

    private void loadUsers() {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("user");
            List<Map> allRutes = games.fetchAll();
            users.clear();
            for(Map m : allRutes) {

                String name = (String) m.get("name");
                String email = (String) m.get("email");
                String gym = (String) m.get("gym");
                Date date = getDate(m.get("datetime"));
                String uuid = (String) m.get("uuid");
                long id = (Long) m.get("id");

                users.put(uuid, new UserImpl(id, uuid, date, name, email, getGym(gym)));
            }
            db.close();

        } catch (IOException e) {
            Log.e(e);
        }
    }

    private void loadGyms() {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("gym");
            List<Map> allRutes = games.fetchAll();
            gyms.clear();
            for(Map m : allRutes) {

                String name = (String) m.get("name");
                double lat = Double.parseDouble((String) m.get("lat"));
                double lon = Double.parseDouble((String) m.get("lon"));
                Date date = getDate(m.get("datetime"));
                String uuid = (String) m.get("uuid");
                long id = (Long) m.get("id");

                gyms.put(uuid, new GymImpl(id, uuid, date, name, lat, lon));
            }
            db.close();

        } catch (IOException e) {
            Log.e(e);
        }
    }


    private Date getDate(Object o) {
        if(o == null || o.toString().equals("null")) return new Date(0);
        try {
            return Util.dateFormat.parse((String) o);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(0);
        }
    }

    public void deleteRute(DBRute r) {

        try {
            Database db = Database.openOrCreate(dbname);
            db.execute("DELETE FROM rute WHERE id=" + r.getID());
            db.close();

        } catch (IOException e) {
            Log.e(e);
        }
        loadRutes();
    }

    public void updateCoordinates(DBRute r) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("rute");
            Map game = (Map) games.getById(r.getID(), true);
            game.put("coordinates", Util.valsToString(r.getPoints()));
            games.update(game);
            db.close();

        } catch (IOException e) {
            Log.e(e);
        }
    }

}
