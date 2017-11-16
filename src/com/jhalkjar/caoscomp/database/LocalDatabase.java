package com.jhalkjar.caoscomp.database;

import ca.weblite.codename1.db.DAO;
import ca.weblite.codename1.db.DAOProvider;
import com.codename1.db.Database;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.l10n.ParseException;
import com.codename1.ui.Image;
import com.jhalkjar.caoscomp.*;
import com.jhalkjar.caoscomp.UUID;
import com.jhalkjar.caoscomp.backend.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by jesper on 11/5/17.
 */
public class LocalDatabase extends ChaosDatabase{
    private static String configPath = "/setup.sql";
    private String dbname = "1sdsaddsss";

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
        return users.get(uuid);
    }


    public Rute getRute(String uuid) {
        return rutes.get(uuid);
    }


    public Gym getGym(String uuid) {
        return gyms.get(uuid);
    }


    public List<Rute> getRutes() {
        return new ArrayList<>(rutes.values());
    }


    public Rute createRute(String name, User author, Gym gym, Date date) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO rutes = provider.get("rute");
            Map rute = (Map) rutes.newObject();
            String uuid = UUID.randomUUID().toString();
            rute.put("uuid", uuid);
            rute.put("name", name);
            rute.put("coordinates", "[]");
            rute.put("author", author.getUUID());
            rute.put("gym", gym.getUUID());
            if(date == null) date = new Date();
            rute.put("datetime", Util.dateFormat.format(date));
            rutes.save(rute);
            db.close();

            loadRutes();

            return getRute(uuid);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void getImage(String uuid, ImageListener listenr) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("image");
            Map<String, Object> result = (Map<String, Object>) games.fetchOne(new String[]{"uuid", uuid});
            String imageurl = (String) result.get("url");
            db.close();
            listenr.onImage(Image.createImage(FileSystemStorage.getInstance().openInputStream(imageurl)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getImageUrl(String uuid) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("image");
            Map<String, Object> result = (Map<String, Object>) games.fetchOne(new String[]{"uuid", uuid});
            String imageurl = (String) result.get("url");
            db.close();
            return imageurl;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User checkLogin(String username, String password) throws IllegalArgumentException {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("user");
            Map<String, Object> result = (Map<String, Object>) games.fetchOne(new String[]{"name", username});
            db.close();
            if(result == null)
                throw new IllegalArgumentException(username + " is not a user!");
            String dbPass = (String) result.get("password");
            if(!password.equals(dbPass))
                throw new IllegalArgumentException("Wrong password!");
            return getUser((String) result.get("uuid"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setImage(String uuid, String imageUrl) {
        Log.p("Sets image for " + uuid + ": " + imageUrl);
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("image");
            Map result = (Map) games.fetchOne(new String[]{"uuid", uuid});
            if(result == null) {
                result = (Map) games.newObject();
                result.put("uuid", uuid);
                result.put("url", imageUrl);
                games.save(result);
            }
            else {
                result.put("url", imageUrl);
                games.update(result);
            }
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            rute.put("datetime", Util.dateFormat.format(r.getDate()));
            rutes.save(rute);
            db.close();

            loadRutes();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasRute(Rute r) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("rute");
            Object obj = games.getById(r.getID());
            db.close();
            return obj != null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public User createUser(String name, String email, String passwordHash, Gym gym, Date date) {
        return addUser(UUID.randomUUID().toString(), name, email, passwordHash, gym, date);
    }

    public User addUser(String uuid, String name, String email, String passwordHash, Gym gym, Date date) {
        Database db;
        try {
            db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO users = provider.get("user");
            Map user = (Map) users.newObject();
            user.put("uuid", uuid);
            user.put("name", name);
            user.put("email", email);
            user.put("password", passwordHash);
            user.put("gym", gym.getUUID());
            if(date == null) date = new Date();
            user.put("datetime", Util.dateFormat.format(date));
            users.save(user);

            db.close();

            loadUsers();

            return getUsers().get(getUsers().size()-1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Gym createGym(String name, double lat, double lon, Date date) {
        return addGym(UUID.randomUUID().toString(), name, lat, lon, date);
    }

    public Gym addGym(String uuid, String name, double lat, double lon, Date date) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO gyms = provider.get("gym");
            Map gym = (Map) gyms.newObject();
            gym.put("uuid", uuid);
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
            e.printStackTrace();
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
                String points = (String) m.get("coordinates");
                String author = (String) m.get("author");
                String gym = (String) m.get("gym");
                Date date = getDate(m.get("datetime"));
                String uuid = (String) m.get("uuid");
                long id = (Long) m.get("id");

                RuteImpl r = new RuteImpl(id, uuid, date, name, getUser(author), getGym(gym), Util.stringToVals(points), this);
                rutes.put(uuid, r);
            }
            Log.p("Local rutes: " + getRutes().toString());
            db.close();

        } catch (IOException e) {
            e.printStackTrace();
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
                String pass = (String) m.get("password");
                Date date = getDate(m.get("datetime"));
                String uuid = (String) m.get("uuid");
                long id = (Long) m.get("id");
                users.put(uuid, new UserImpl(id, uuid, date, name, email, getGym(gym), pass));
            }
            Log.p("Loaded users");
            db.close();

        } catch (IOException e) {
            e.printStackTrace();
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
            Log.p("Loaded gyms!");
            db.close();

        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void delete(Rute r) {

        try {
            Database db = Database.openOrCreate(dbname);
            db.execute("DELETE FROM rute WHERE id=" + r.getID());
            db.close();
            FileSystemStorage.getInstance().delete(getImageUrl(r.getUUID()));
            rutes.remove(r.getUUID());
            for(DatabaseListener l : listeners) l.OnDeletedRute(r);

        } catch (IOException e) {
            Log.e(e);
        }
        loadRutes();
    }

    @Override
    public void save(Rute r) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("rute");
            Map game = (Map) games.getById(r.getID(), true);
            game.put("coordinates", Util.valsToString(r.getPoints()));
            games.update(game);
            db.close();
            for(DatabaseListener l : listeners) l.OnSaved(r);

        } catch (IOException e) {
            Log.e(e);
        }
    }

}
