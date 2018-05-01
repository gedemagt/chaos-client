package com.jhalkjar.caoscomp.database;

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.*;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.jhalkjar.caoscomp.UUID;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.backend.*;
import com.jhalkjar.caoscomp.web.Response;
import com.jhalkjar.caoscomp.web.Rest;

import java.io.*;
import java.util.*;

/**
 * Created by jesper on 11/8/17.
 */
public class WebDatabase extends ChaosDatabase {


//    private static final String host = "https://jeshj.pythonanywhere.com";
    private static String host = "http://localhost:5000";

    private static String LAST_WEB_CONNECTION = "last_sync";

    public User getUser(String id) {
        if(id.length() == 0) return null;
        Map<String, Object> result = Rest.get(host + "/get_user/" + id).acceptJson().getAsJsonMap(false).getResponseData();
        Map<String, Object> vals = (Map<String,Object>) result.values().iterator().next();
        String name = (String) vals.get("name");
        String email = (String) vals.get("email");
        String password = (String) vals.get("password");
        Gym gym = getGym((String) vals.get("gym"));
        Date date = Util.parse((String) vals.get("date"));
        String uuid = (String) vals.get("uuid");
        String roler = (String) vals.get("role");
        Role role = roler != null ? Role.valueOf(roler) : Role.USER;

        User s = new UserImpl(-1, uuid, date, name, email, gym, password, role, 0);
        Log.p("[WebDatabase] Loaded users: " + s.toString());
        return s;
    }

    public Gym getGym(String id) {
        if(id.length() == 0) return null;
        Map<String, Object> result = Rest.get(host + "/get_gym/" + id).acceptJson().getAsJsonMap(false).getResponseData();
        Log.p(result+"");
        Map<String, Object> vals = (Map<String,Object>) result.values().iterator().next();
        String name = (String) vals.get("name");
        double lon = Double.parseDouble((String) vals.get("lon"));
        double lat = Double.parseDouble((String) vals.get("lat"));
        Date date = Util.parse((String) vals.get("date"));
        String uuid = (String) vals.get("uuid");
        Gym g = new GymImpl(-1, uuid, date, name, lat, lon, 0);
        Object sectors = vals.get("sectors");
        if(sectors != null) {
            for(String sector : Util.jsonToSectors((String) sectors)) {
                g.addSector(new Sector(sector, g));
            }
        }
        Log.p("[WebDatabase] Loaded gym: " + g);
        return g;
    }

    public List<Gym> getGyms() {
        Map<String, Object> result = Rest.get(host + "/get_gyms").acceptJson().getAsJsonMap(false).getResponseData();
        List<Gym> re = new ArrayList<>();
        for(String key : result.keySet()) {
            Map<String,Object> vals = (Map<String, Object>) result.get(key);
            String name = (String) vals.get("name");
            double lon = Double.parseDouble((String) vals.get("lon"));
            double lat = Double.parseDouble((String) vals.get("lat"));
            Date date = Util.parse((String) vals.get("date"));
            String uuid = (String) vals.get("uuid");
            Gym g = new GymImpl(-1, uuid, date, name, lat, lon, 0);
            Object sectors = vals.get("sectors");
            if(sectors != null) {
                for(String sector : Util.jsonToSectors((String) sectors)) {
                    g.addSector(new Sector(sector, g));
                }
            }
            re.add(g);
        }

        return re;
    }

    public void uploadRute(Rute r, String imageUrl) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", r.getName());
            object.put("author", r.getAuthor().getUUID());
            object.put("gym", r.getSector().getGym().getUUID());
            object.put("sector", r.getSector().getName());
            object.put("date", Util.format(r.getDate()));
            object.put("edit", Util.format(r.lastEdit()));
            object.put("uuid", r.getUUID());
            object.put("image", r.getImageUUID());
            object.put("grade", r.getGrade().name());
            object.put("tag", r.getTag());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Rest.post(host + "/add_rute").acceptJson().jsonContent().body(object.toString())
                .getAsStringAsync(
                        response -> {
                            Log.p("[WebDatabase] Uploaded rute: " + r);
                            if(imageUrl != null) {uploadImage(r.getImageUUID(), imageUrl);}
                        },
                        (sender, err, errCode, errorMessage) -> {

                        }
                );
    }

    @Override
    public void delete(Rute r) {
        Rest.post(host + "/delete/" + r.getUUID()).getAsStringAsync(
                response -> {
                    Log.p("[WebDatabase] Deleted rute: " + r);
                },
                (sender, err, errCode, errorMessage) -> {

                }
        );
    }

    private void uploadImage(String uuid, String url) {
        MultipartRequest request = new MultipartRequest();
        request.setPost(true);
        request.setUrl(host + "/add_image/" + uuid);
        try {
            Log.p("[WebDatabase] Uploading image '" + url + "'");
            request.addData("file", url, "image/jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        NetworkManager.getInstance().addToQueueAndWait(request);
    }

    public void uploadUser(User u) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", u.getName());
            object.put("password", u.getPasswordHash());
            object.put("email", u.getEmail());
            object.put("gym", u.getGym().getUUID());
            object.put("date", Util.format(u.getDate()));
            object.put("uuid", u.getUUID());
            object.put("role", u.getRole().name());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Rest.post(host + "/add_user").jsonContent().acceptJson().body(object.toString()).getAsStringAsync(
                response -> {
                    Log.p("[WebDatabase] Uploading user: " + u.toString());
                },
                (sender, err, errCode, errorMessage) -> {
                    Log.p("[WebDatabase] Failed to upload user: " + u.toString()+ " - " + errorMessage);
                    //err.printStackTrace();
                }
        );
    }

    public void uploadGym(Gym g) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", g.getName());
            object.put("lat", g.getLat());
            object.put("lon", g.getLon());
            object.put("date", Util.format(g.getDate()));
            object.put("uuid", g.getUUID());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Rest.post(host + "/add_gym").jsonContent().acceptJson().body(object.toString()).getAsStringAsync(
                response -> {
                    Log.p("[WebDatabase] Uploading gym: " + g.toString());
                },
                (sender, err, errCode, errorMessage) -> {
                    err.printStackTrace();
                }
        );
    }


    public List<Competition> getCompetitions() {
        Map<String, Object> result = Rest.get(host + "/get_comps").acceptJson().getAsJsonMap(false).getResponseData();
        List<Competition> comps = new ArrayList<>();
        for(Object key : (List<Object>) result.get("root")) {
            Map<String, Object> vals = (Map<String, Object>) key;
            String name = (String) vals.get("name");
            String uuid = (String) vals.get("uuid");
            Date date = Util.parse((String) vals.get("date"));
            Date start = Util.parse((String) vals.get("start"));
            Date end = Util.parse((String) vals.get("stop"));
            int pin = (int) (double) vals.get("pin");
            int type = (int) (double) vals.get("type");

            List<Rute> rutes = new ArrayList<>();
            for(Object r : (List<Object>) vals.get("rutes")) {
                String ruteUUID = (String) r;
                rutes.add(DB.getInstance().getRute(ruteUUID));
            }

            List<User> admins = new ArrayList<>();
            for(Object r : (List<Object>) vals.get("admins")) {
                String userUUID = (String) r;
                admins.add(DB.getInstance().getUser(userUUID));
            }

            comps.add(new CompetitionImpl(uuid, date, 0, name, start, end, type, admins, rutes, pin));
        }
        return comps;
    }

    public Competition createCompetition(String name, Date start, Date stop, int type, List<User> admins) {
        JSONObject object = new JSONObject();
        Date date = Util.getNow();
        String uuid = UUID.randomUUID().toString();
        try {
            object.put("uuid", uuid);
            object.put("name", name);
            object.put("date", Util.format(date));
            object.put("edit", Util.format(date));
            object.put("start", Util.format(start));
            object.put("stop", Util.format(stop));
            object.put("type", type);
            String adminsS = "";
            for(int i=0; i<admins.size(); i++) {
                adminsS += admins.get(i).getUUID();
                if(i<admins.size()-1) adminsS += ",";
            }
            object.put("admins", adminsS);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        int pin =  Integer.parseInt(Rest.post(host + "/update_comp").jsonContent().acceptJson().body(object.toString()).getAsString(true).getResponseData());

        return new CompetitionImpl(uuid, date, 0, name, start, stop, type, admins, new ArrayList<>(), pin);
    }

    public void saveCompetition(Competition c) {
        JSONObject object = new JSONObject();
        try {
            object.put("uuid", c.getUUID());
            object.put("name", c.getName());
            object.put("edit", Util.format(Util.getNow()));
            object.put("start", Util.format(c.getStart()));
            object.put("stop", Util.format(c.getStop()));
            object.put("type", c.getType());
            String adminsS = "";
            for(int i=0; i<c.getAdmins().size(); i++) {
                adminsS += c.getAdmins().get(i).getUUID();
                if(i<c.getAdmins().size()-1) adminsS += ",";
            }
            object.put("admins", adminsS);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Rest.post(host + "/update_comp").jsonContent().acceptJson().body(object.toString()).getAsString(true).getResponseData();
    }

    public void addRute(Competition c, Rute r) {
        JSONObject object = new JSONObject();
        try {
            object.put("comp", c.getUUID());
            object.put("rute", r.getUUID());
            object.put("date", Util.format(Util.getNow()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Rest.post(host + "/add_rute_comp").jsonContent().acceptJson().body(object.toString()).getAsString(true).getResponseData();
    }




    @Override
    public void save(Rute r) {
        JSONObject object = new JSONObject();
        try {
            object.put("uuid", r.getUUID());
            object.put("coordinates", Util.valsToString(r.getPoints()));
            object.put("edit", Util.format(r.lastEdit()));
            object.put("name", r.getName());
            object.put("gym", r.getSector().getGym().getUUID());
            object.put("sector", r.getSector().getName());
            object.put("grade", r.getGrade());
            object.put("tag", r.getTag());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Rest.post(host + "/update_coordinates").jsonContent().acceptJson().body(object.toString()).getAsStringAsync(
                response -> {
                    Log.p("[WebDatabase] Saving rute: " + r.toString());
                },
                (sender, err, errCode, errorMessage) -> {
                    err.printStackTrace();
                }
        );
    }

    public void login(String username, String password, WebDatabase.Result<String> onLogin, Runnable onError) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", username);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Rest.post(host + "/login").acceptJson().jsonContent().body(object.toString()).getAsStringAsync(
                response -> {
                    Log.p("Hansi");
                    onLogin.OnResult(response.getResponseData());
                },
                (sender, err, errCode, errorMessage) -> {
                    onError.run();
                }
        );
    }

    private String getLastSync() {
        String lastSync = Preferences.get(LAST_WEB_CONNECTION, "");
        Log.p("[WebDatabase] " + "Last synced: " + lastSync);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(LAST_WEB_CONNECTION, lastSync);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private Map<String, Rute> parseRutes(Map<String,Object> result) {

        Map<String, Rute> list = new HashMap<>();
        for(String key : result.keySet()) {
            Map<String,Object> vals = (Map<String, Object>) result.get(key);
            String name = (String) vals.get("name");
            String coordinates = (String) vals.get("coordinates");
            Gym gym = DB.getInstance().getGym((String) vals.get("gym"));
            String sector = (String) vals.get("sector");
            Date date = Util.parse((String) vals.get("date"));
            Date last_edit = Util.parse((String) vals.get("edit"));
            User author = DB.getInstance().getUser((String) vals.get("author"));
            String uuid = (String) vals.get("uuid");
            String image = (String) vals.get("image");
            String grader = (String) (vals.get("grade"));
            int status = (int) ((double) vals.get("status"));
            Grade grade = grader != null ? Grade.valueOf(grader) : Grade.NO_GRADE;
            String tag = (String) vals.get("tag");
            if(tag == null) tag = "";

            list.put(uuid, new RuteImpl(-1, uuid, image, date, last_edit, name, author, gym.getSector(sector), Util.stringToVals(coordinates), grade, status, tag));
        }
        Log.p("[WebDatabase] Loaded " + list.size() + " rutes.");
        Preferences.set(LAST_WEB_CONNECTION, Util.format(Util.getNow()));
        Log.p("[WebDatabase] " + "Last synced: " + Preferences.get(LAST_WEB_CONNECTION, ""));
        return list;
    }


    public void getRutes(Result<Map<String, Rute>> runnable) {
        Rest.post(host + "/get_rutes").jsonContent().acceptJson().body(getLastSync()).getAsJsonMap(value -> {
            Map<String,Object> result = value.getResponseData();
            Map<String,Rute> list = parseRutes(result);
            runnable.OnResult(list);
        },
        (sender, err, errCode, errorMessage) -> {
            err.printStackTrace();
        });
    }

    public Map<String,Rute> getRutes() {
        Map<String,Object> result = Rest.post(host + "/get_rutes").jsonContent().acceptJson().body(getLastSync()).getAsJsonMap(false).getResponseData();
        Map<String,Rute> list = parseRutes(result);
        return list;
    }


    public void downloadImage(String uuid, String path, Runnable callback) {
        Log.p("[WebDatabase] Downloading picture for rute " + uuid + " @ " + path);
        if(FileSystemStorage.getInstance().exists(path)) {
            Log.p("[WebDatabase] " + path + " already exists. Deleting..");
            FileSystemStorage.getInstance().delete(path);
        }

        MultipartRequest request = new MultipartRequest();
        request.setPost(false);
        request.setUrl(host + "/download/" + uuid);

        request.downloadImageToFileSystem(path, value-> callback.run());
        NetworkManager.getInstance().addToQueue(request);

    }

    public void getImage(String uuid, ImageListener image) {
        MultipartRequest request = new MultipartRequest();
        request.setPost(true);
        request.setUrl(host + "/download/" + uuid);
        request.setFailSilently(true);
        request.addResponseListener(evt -> {
            if(evt.getResponseCode() != 200) {
                image.onError();
            }
            else {
                Image img = EncodedImage.create(request.getResponseData());
                Log.p("[WebDatabase] Download of image " + uuid);
                image.onImage(img);
            }
        });


        NetworkManager.getInstance().addToQueue(request);

    }


    public boolean checkUserName(String username) {
        Response<String> c = Rest.post(host + "/check_username/" + username).getAsString(true);
        return c.getResponseCode() == 200;
    }

    public boolean checkGymName(String gymname) {
        Response<String> c = Rest.post(host + "/check_gymname/" + gymname).getAsString(true);
        return c.getResponseCode() == 200;
    }

    public void resetLastVisit() {
        Preferences.set(LAST_WEB_CONNECTION, "1900-01-01 00:00:00");
    }

    public void logout() {
        Rest.get(host + "/logout").getAsString(true);
    }

    public void save(Gym g) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", g.getName());
            object.put("lat", g.getLat());
            object.put("lon", g.getLon());
            object.put("uuid", g.getUUID());
            object.put("sectors", Util.sectorsToJSON(g.getSectors()));
            object.put("tags", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.p("[WebDatabase] Saving gym: " + g.toString());
        Rest.post(host + "/save_gym").jsonContent().acceptJson().body(object.toString()).getAsString(false);

    }

    public interface Result<T> {
        void OnResult(T t);
    }

}
