package com.jhalkjar.caoscomp.database;

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.*;
import com.codename1.l10n.ParseException;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionListener;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.backend.*;

import java.io.*;
import java.util.*;

/**
 * Created by jesper on 11/8/17.
 */
public class WebDatabase extends ChaosDatabase {

    private static final String host = "https://jeshj.pythonanywhere.com";
//    private static String host = "http://localhost:5000";


    private Map<String, Gym> gyms = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private Map<String, Rute> rutes = new HashMap<>();

    public WebDatabase() {

    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Gym> getGyms() {
        return new ArrayList<>(gyms.values());
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public Rute getRute(String id) {
        return rutes.get(id);
    }

    public Gym getGym(String id) {
        return gyms.get(id);
    }


    public List<Rute> getRutes() {
        return new ArrayList<>(rutes.values());
    }


    public void uploadRute(Rute r, String imageUrl) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", r.getName());
            object.put("author", r.getAuthor().getUUID());
            object.put("gym", r.getGym().getUUID());
            object.put("date", Util.dateFormat.format(r.getDate()));
            object.put("edit", Util.dateFormat.format(r.lastEdit()));
            object.put("uuid", r.getUUID());
            object.put("image", r.getImageUUID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendJson(host + "/add_rute", object.toString(), evt -> {
            Log.p("[WebDatabase] Uploaded rute: " + r);
            if(imageUrl != null) {uploadImage(r.getImageUUID(), imageUrl);}
            rutes.put(r.getUUID(), r);
        });

    }

    @Override
    public void delete(Rute r) {
        rutes.remove(r.getUUID());
        post(host + "/delete/" + r.getUUID(), evt -> {
            Log.p("[WebDatabase] Deleted rute: " + r);
        });
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
            object.put("date", Util.dateFormat.format(u.getDate()));
            object.put("uuid", u.getUUID());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.p("[WebDatabase] Uploading user: " + u.toString());
        sendJson(host + "/add_user", object.toString());

    }

    public void uploadGym(Gym g) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", g.getName());
            object.put("lat", g.getLat());
            object.put("lon", g.getLon());
            object.put("date", Util.dateFormat.format(g.getDate()));
            object.put("uuid", g.getUUID());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.p("[WebDatabase] Uploading gym: " + g.toString());
        sendJson(host + "/add_gym", object.toString());
    }


    @Override
    public void save(Rute r) {
        JSONObject object = new JSONObject();
        try {
            object.put("uuid", r.getUUID());
            object.put("coordinates", Util.valsToString(r.getPoints()));
            object.put("edit", Util.dateFormat.format(r.lastEdit()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.p("[WebDatabase] Saving rute: " + r.toString());
        sendJson(host + "/update_coordinates", object.toString());
    }

    public void refresh(Runnable done) {
        Log.p("[WebDatabase] Refreshing..");
        updateGyms(() -> updateUsers(() -> updateRutes(() -> {done.run();})));
        Log.p("[WebDatabase] Refreshing done!");
    }


    private void updateRutes(Runnable runnable) {
        get(host + "/get_rutes", evt -> {
            try {
                Map<String,Object> result = getJsonData(evt);
                Map<String, Rute> list = new HashMap<>();
                for(String key : result.keySet()) {
                    Map<String,Object> vals = (Map<String, Object>) result.get(key);
                    String name = (String) vals.get("name");
                    String coordinates = (String) vals.get("coordinates");
                    Gym gym = getGym((String) vals.get("gym"));
                    Date date = Util.dateFormat.parse((String) vals.get("date"));
                    Date last_edit = Util.dateFormat.parse((String) vals.get("edit"));
                    User author = getUser((String) vals.get("author"));
                    String uuid = (String) vals.get("uuid");
                    String image = (String) vals.get("image");
                    list.put(uuid, new RuteImpl(-1, uuid, image, date, last_edit, name, author, gym, Util.stringToVals(coordinates)));
                }
                rutes = list;
                Log.p("[WebDatabase] Loaded rutes: " + rutes.toString());
                runnable.run();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

    }

    private void updateGyms(Runnable runnable) {
        get(host + "/get_gyms", evt -> {
            try {
                Map<String,Object> result = getJsonData(evt);
                Map<String, Gym> gyms = new HashMap<>();
                for(String key : result.keySet()) {
                    Map<String,Object> vals = (Map<String, Object>) result.get(key);
                    String name = (String) vals.get("name");
                    double lon = Double.parseDouble((String) vals.get("lon"));
                    double lat = Double.parseDouble((String) vals.get("lat"));
                    Date date = Util.dateFormat.parse((String) vals.get("date"));
                    String uuid = (String) vals.get("uuid");
                    gyms.put(uuid, new GymImpl(-1, uuid, date, name, lat, lon));
                }
                this.gyms = gyms;
                Log.p("[WebDatabase] Loaded gyms: " + this.gyms.toString());
                runnable.run();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

    }


    private void updateUsers(Runnable runnable) {
        get(host + "/get_users", evt -> {
            try {
                Map<String, User> users = new HashMap<>();
                Map<String,Object> result = getJsonData(evt);
                for(String key : result.keySet()) {
                    Map<String,Object> vals = (Map<String, Object>) result.get(key);
                    String name = (String) vals.get("name");
                    String email = (String) vals.get("email");
                    String password = (String) vals.get("password");
                    Gym gym = getGym((String) vals.get("gym"));
                    Date date = Util.dateFormat.parse((String) vals.get("date"));
                    String uuid = (String) vals.get("uuid");

                    users.put(uuid, new UserImpl(-1, uuid, date, name, email, gym, password));
                }
                this.users = users;
                Log.p("[WebDatabase] Loaded users: " + this.users.toString());
                runnable.run();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    public void get(String url, ActionListener<NetworkEvent> evt) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(false);
        r.setUrl(url);
        r.addResponseListener(evt);
        NetworkManager.getInstance().addToQueue(r);
    }

    public void post(String url, ActionListener<NetworkEvent> evt) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(true);
        r.setUrl(url);
        r.addResponseListener(evt);
        NetworkManager.getInstance().addToQueue(r);
    }

    Map<String, Object> getJsonData(NetworkEvent evt) throws IOException {
        return new JSONParser().parseJSON(
                new InputStreamReader(
                        new ByteArrayInputStream(evt.getConnectionRequest().getResponseData()), "UTF-8"));
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

    public void getImage(String uuid, ImageListener image) throws NoImageException {
        MultipartRequest request = new MultipartRequest();
        request.setPost(true);
        request.setUrl(host + "/download/" + uuid);
        request.addResponseListener(evt -> {
            Image img = EncodedImage.create(request.getResponseData());
            image.onImage(img);
            Log.p("[WebDatabase] Download of image " + uuid);
        });
        NetworkManager.getInstance().addToQueue(request);

    }

    private ConnectionRequest sendJson(String url, String json, ActionListener<NetworkEvent> listener) {
        ConnectionRequest r = new ConnectionRequest(){
            @Override
            protected void buildRequestBody(OutputStream os) throws IOException {
                os.write(json.getBytes("UTF-8"));
            }
        };
        r.setPost(true);
        r.setUrl(url);
        r.setContentType("application/json");
        r.addResponseListener(listener);
        NetworkManager.getInstance().addToQueue(r);
        return r;
    }

    private ConnectionRequest sendJson(String url, String json) {
        return sendJson(url, json, evt -> {});
    }


}
