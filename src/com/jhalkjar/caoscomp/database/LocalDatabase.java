package com.jhalkjar.caoscomp.database;

import ca.weblite.codename1.db.DAO;
import ca.weblite.codename1.db.DAOProvider;
import com.codename1.db.Database;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.ui.Image;
import com.jhalkjar.caoscomp.UUID;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.backend.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by jesper on 11/5/17.
 */
public class LocalDatabase extends ChaosDatabase{
    private static String configPath = "/setup.sql";

    private String dbname = "db";
    private static int VERSION = 4;

    private Map<String, Gym> gyms = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private Map<String, Rute> rutes = new HashMap<>();

    public final Gym unknownGym = new GymImpl(-1, "", Util.getNow(), "UnknowGym", 0,0, 0);
    public final User unknownUser = new UserImpl(-1, "", Util.getNow(), "UnknownUser", "", unknownGym, "",Role.USER, 0);

    public void refresh() {
        Log.p("[LocalDatabase] Refreshing..");
        loadGyms();
        loadUsers();
        loadRutes();
        Log.p("[LocalDatabase] Refreshing done!");
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


    public Rute createRute(String name, User author, Sector gym, Date date, String imageUUID, Grade grade) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO rutes = provider.get("rute");
            Map rute = (Map) rutes.newObject();
            String uuid = UUID.randomUUID().toString();
            if(imageUUID == null) imageUUID = UUID.randomUUID().toString();
            rute.put("uuid", uuid);
            rute.put("name", name);
            rute.put("coordinates", "[]");
            rute.put("author", author.getUUID());
            rute.put("gym", gym.getGym().getUUID());
            rute.put("sector", gym.getName());
            if(date == null) date = Util.getNow();
            rute.put("datetime", Util.format(date));
            rute.put("edit", Util.format(date));
            rute.put("image", imageUUID);
            rute.put("grade", grade.name());
            rutes.save(rute);
            db.close();

            loadRutes();
            Log.p("[LocalDatabase] Created rute with UUID=" + uuid + " and imageUUID=" + imageUUID);

            return getRute(uuid);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void syncGyms(Map<String, Gym> gyms) {
        for(Map.Entry<String, Gym> entry : gyms.entrySet()) {
            Gym r = entry.getValue();
            if(r.getStatus() == 1) delete(r);
            else if(has(r)) save(r);
        }
        refresh();
    }

    public void syncUsers(Map<String, User> users) {
        for(Map.Entry<String, User> entry : users.entrySet()) {
            User r = entry.getValue();
            if(r.getStatus() == 1) delete(r);
//            else if(has(r)) save(r);
        }
        refresh();
    }

    public void sync(Map<String, Rute> rutes) {
        for(Map.Entry<String, Rute> entry : rutes.entrySet()) {
            Rute r = entry.getValue();
            if(r.getStatus() == 1) delete(r);
            else if(has(r)) save(r);
            else {
                if(getUser(r.getAuthor().getUUID()) == null){
                    User u = r.getAuthor();
                    addUser(r.getUUID(), u.getName(), u.getEmail(), u.getPasswordHash(), u.getGym(), u.getDate(), u.getRole());
                }
                addRute(entry.getValue());
            }
        }
        refresh();
    }

    @Override
    public void getImage(String uuid, ImageListener listenr) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO games = provider.get("image");
            Map<String, Object> result = (Map<String, Object>) games.fetchOne(new String[]{"uuid", uuid});
            db.close();
            if(result == null) {
                listenr.onError();
                return;
            }
            String imageurl = (String) result.get("url");
            listenr.onImage(Image.createImage(FileSystemStorage.getInstance().openInputStream(imageurl)));
        } catch (IOException e) {
            e.printStackTrace();
            listenr.onError();
        }
    }

    public String getImageUrl(String uuid) throws NoImageException {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO games = provider.get("image");
            Map<String, Object> result = (Map<String, Object>) games.fetchOne(new String[]{"uuid", uuid});
            db.close();
            if(result == null) {
                throw new NoImageException("No image for uuid " + uuid + " found!");
            }
            String imageurl = (String) result.get("url");
            return imageurl;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setImage(String uuid, String imageUrl) {
        Log.p("[LocalDatabase] Sets image for " + uuid + ": " + imageUrl);
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
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
            Log.p("[LocalDatabase] Adding rute: " + r);
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO rutes = provider.get("rute");
            Map rute = (Map) rutes.newObject();
            rute.put("uuid", r.getUUID());
            rute.put("name", r.getName());
            rute.put("coordinates", Util.valsToString(r.getPoints()));
            rute.put("author", r.getAuthor().getUUID());
            rute.put("sector", r.getSector().getName());
            rute.put("gym", r.getSector().getGym().getUUID());
            rute.put("datetime", Util.format(r.getDate()));
            rute.put("image", r.getImageUUID());
            rute.put("grade", r.getGrade().name());
            rute.put("tag", r.getTag());
            rutes.save(rute);
            db.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean has(Rute r) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO games = provider.get("rute");
            Map result = (Map) games.fetchOne(new String[]{"uuid", r.getUUID()});
            db.close();
            return result != null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean has(Gym g) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO games = provider.get("gym");
            Map result = (Map) games.fetchOne(new String[]{"uuid", g.getUUID()});
            db.close();
            return result != null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean has(User g) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO games = provider.get("user");
            Map result = (Map) games.fetchOne(new String[]{"uuid", g.getUUID()});
            db.close();
            return result != null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public User createUser(String name, String email, String passwordHash, Gym gym, Date date, Role role) {
        return addUser(UUID.randomUUID().toString(), name, email, passwordHash, gym, date, role);
    }

    public User addUser(String uuid, String name, String email, String passwordHash, Gym gym, Date date, Role role) {
        Database db;
        try {
            db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO users = provider.get("user");
            Map user = (Map) users.newObject();
            user.put("uuid", uuid);
            user.put("name", name);
            user.put("email", email);
            user.put("password", passwordHash);
            user.put("gym", gym.getUUID());
            if(date == null) date = Util.getNow();
            user.put("datetime", Util.format(date));
            user.put("role", role.name());

            users.save(user);
            db.close();
            loadUsers();

            return getUser(uuid);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Gym createGym(String name, double lat, double lon, Date date, List<Sector> sectors) {
        return addGym(UUID.randomUUID().toString(), name, lat, lon, date, sectors);
    }

    public Gym addGym(String uuid, String name, double lat, double lon, Date date, List<Sector> sectors) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO gyms = provider.get("gym");
            Map gym = (Map) gyms.newObject();
            gym.put("uuid", uuid);
            gym.put("name", name);
            gym.put("lat", lat);
            gym.put("lon", lon);
            if(date == null) date = Util.getNow();
            gym.put("datetime", Util.format(date));
            gym.put("sectors", Util.sectorsToJSON(sectors));
            gyms.save(gym);
            db.close();

            loadGyms();

            return getGym(uuid);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void loadRutes() {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO games = provider.get("rute");
            List<Map> allRutes = games.fetchAll();
            rutes.clear();
            for(Map m : allRutes) {

                String name = (String) m.get("name");
                String points = (String) m.get("coordinates");
                String author = (String) m.get("author");
                Gym gym = DB.getInstance().getGym((String) m.get("gym"));
                String sector = (String) m.get("sector");
                Date date = getDate(m.get("datetime"));
                Date lastedit = getDate(m.get("edit"));
                String uuid = (String) m.get("uuid");
                String image = (String) m.get("image");
                long id = (Long) m.get("id");
                String grader = (String) (m.get("grade"));
                Grade grade;
                try {
                    grade = Grade.valueOf(grader);
                } catch (IllegalArgumentException|NullPointerException e) {
                    grade = Grade.NO_GRADE;
                }
                String tag = (String) (m.get("tag"));
                if(tag == null) tag = "";

                RuteImpl r = new RuteImpl(id, uuid, image, date, lastedit, name, DB.getInstance().getUser(author), gym.getSector(sector), Util.stringToVals(points), grade, 0, tag);
                rutes.put(uuid, r);
            }
            Log.p("[LocalDatabase] Loaded rutes " + getRutes().size() + " rutes.");
            db.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
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
                String roler = (String) m.get("role");
                Role role = roler != null ? Role.valueOf(roler) : Role.USER;


                users.put(uuid, new UserImpl(id, uuid, date, name, email, getGym(gym), pass, role, 0));
            }
            Log.p("[LocalDatabase] Loaded users: " + users.values());
            db.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGyms() {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
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
                gyms.put(uuid, new GymImpl(id, uuid, date, name, lat, lon, 0));

                String sectors = (String) m.get("sectors");
                if(sectors != null) {
                    JSONParser parser = new JSONParser();

                    List<Object> vals = (List<Object>) parser.parseJSON(new com.codename1.util.regex.StringReader(sectors)).get("root");
                    for (Object sec : vals) {
                        gyms.get(uuid).addSector(new Sector((String) sec, gyms.get(uuid)));
                    }
                }

            }
            Log.p("[LocalDatabase] Loaded gyms: " + gyms.values());
            db.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Date getDate(Object o) {
        if(o == null || o.toString().equals("null")) return Util.getNow();
        return Util.parse((String) o);
    }

    @Override
    public void delete(Rute r) {
        Log.p("[LocalDatabase] Deleting rute " + r.toString());
        try {

            Database db = Database.openOrCreate(dbname);
            db.execute("DELETE FROM rute WHERE uuid='" + r.getUUID() + "'");
            db.execute("DELETE FROM image WHERE uuid='" + r.getImageUUID()+"'");
            db.close();
            rutes.remove(r.getUUID());
            for(DatabaseListener l : listeners) l.OnDeletedRute(r);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileSystemStorage.getInstance().delete(getImageUrl(r.getImageUUID()));
        } catch (NoImageException e) {
            Log.p("[LocalDatabase] Could not find image to delete. Carry on..!");
        }

    }

    public void delete(Gym g) {
        Log.p("[LocalDatabase] Deleting gym " + g.toString());
        try {

            Database db = Database.openOrCreate(dbname);
            db.execute("DELETE FROM gym WHERE uuid='" + g.getUUID() + "'");
            db.close();
            gyms.remove(g.getUUID());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(User u) {
        Log.p("[LocalDatabase] Deleting user " + u.toString());
        try {

            Database db = Database.openOrCreate(dbname);
            db.execute("DELETE FROM user WHERE uuid='" + u.getUUID() + "'");
            db.close();
            users.remove(u.getUUID());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void save(Rute r) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO games = provider.get("rute");
            Map result = (Map) games.fetchOne(new String[]{"uuid", r.getUUID()});
            if(result != null) {
                result.put("coordinates", Util.valsToString(r.getPoints()));
                result.put("edit", Util.format(r.lastEdit()));
                result.put("name", r.getName());
                result.put("sector", r.getSector().getName());
                result.put("gym", r.getSector().getGym().getUUID());
                result.put("grade", r.getGrade());
                result.put("tag", r.getTag());
                games.update(result);
                for(DatabaseListener l : listeners) l.OnSaved(r);
            }
            db.close();
            Log.p("[LocalDatabase] Saved rute " + r);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(Gym g) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, VERSION);
            DAO games = provider.get("gym");
            Map result = (Map) games.fetchOne(new String[]{"uuid", g.getUUID()});
            if(result != null) {
                result.put("name", g.getName());
                result.put("sectors", Util.sectorsToJSON(g.getSectors()));
                games.update(result);
            }
            db.close();
            Log.p("[LocalDatabase] Saved gym " + g);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
