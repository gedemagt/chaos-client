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


//    private static final String host = "https://jeshj.pythonanywhere.com";
    private static String host = "http://localhost:5000";

    private static String LAST_WEB_CONNECTION = "last_sync";
    public WebDatabase() {

    }

    public User getUser(String id) {
        if(id.length() == 0) return null;
        ConnectionRequest r = getAndWait(host + "/get_user/" + id);
        try {
            Map<String, Object> result = getJsonData(r);
            for(String key : result.keySet()) {
                Map<String,Object> vals = (Map<String, Object>) result.get(key);
                String name = (String) vals.get("name");
                String email = (String) vals.get("email");
                String password = (String) vals.get("password");
                Gym gym = getGym((String) vals.get("gym"));
                Date date = Util.parse((String) vals.get("date"));
                String uuid = (String) vals.get("uuid");
                String roler = (String) vals.get("role");
//                Log.p("roler: " + roler);
                Role role = roler != null ? Role.valueOf(roler) : Role.USER;

                User s = new UserImpl(-1, uuid, date, name, email, gym, password, role);
                Log.p("[WebDatabase] Loaded users: " + s.toString());
                return s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Gym getGym(String id) {
        if(id.length() == 0) return null;
        ConnectionRequest r = getAndWait(host + "/get_gym/" + id);
        try {
            Map<String, Object> result = getJsonData(r);
            for(String key : result.keySet()) {
                Map<String,Object> vals = (Map<String, Object>) result.get(key);
                String name = (String) vals.get("name");
                double lon = Double.parseDouble((String) vals.get("lon"));
                double lat = Double.parseDouble((String) vals.get("lat"));
                Date date = Util.parse((String) vals.get("date"));
                String uuid = (String) vals.get("uuid");
                Gym g = new GymImpl(-1, uuid, date, name, lat, lon);
                Log.p("[WebDatabase] Loaded gym: " + g);
                return g;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void uploadRute(Rute r, String imageUrl) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", r.getName());
            object.put("author", r.getAuthor().getUUID());
            object.put("gym", r.getGym().getUUID());
            object.put("date", Util.format(r.getDate()));
            object.put("edit", Util.format(r.lastEdit()));
            object.put("uuid", r.getUUID());
            object.put("image", r.getImageUUID());
            object.put("grade", r.getGrade().name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendJson(host + "/add_rute", object.toString(), evt -> {
            Log.p("[WebDatabase] Uploaded rute: " + r);
            if(imageUrl != null) {uploadImage(r.getImageUUID(), imageUrl);}
        });

    }

    @Override
    public void delete(Rute r) {
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
            object.put("date", Util.format(u.getDate()));
            object.put("uuid", u.getUUID());
            object.put("role", u.getRole().name());

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
            object.put("date", Util.format(g.getDate()));
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
            object.put("edit", Util.format(r.lastEdit()));
            object.put("name", r.getName());
            object.put("gym", r.getGym().getUUID());
            object.put("grade", r.getGrade());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.p("[WebDatabase] Saving rute: " + r.toString());
        sendJson(host + "/update_coordinates", object.toString());
    }

    public String login(String username, String password) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", username);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ConnectionRequest r = sendJsonWait(host + "/login", object.toString(), evt -> {

        });
        try {
            return new String(r.getResponseData(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
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


    public void getRutes(Result<Map<String, Rute>> runnable) {
        sendJson(host + "/get_rutes", getLastSync(), evt -> {
            try {
                Map<String,Object> result = getJsonData(evt.getConnectionRequest());
                Map<String, Rute> list = new HashMap<>();
                for(String key : result.keySet()) {
                    Map<String,Object> vals = (Map<String, Object>) result.get(key);
                    String name = (String) vals.get("name");
                    String coordinates = (String) vals.get("coordinates");
                    Gym gym = DB.getInstance().getGym((String) vals.get("gym"));
                    Date date = Util.parse((String) vals.get("date"));
                    Date last_edit = Util.parse((String) vals.get("edit"));
                    User author = DB.getInstance().getUser((String) vals.get("author"));
                    String uuid = (String) vals.get("uuid");
                    String image = (String) vals.get("image");
                    String grader = (String) (vals.get("grade"));
                    int status = (int) ((double) vals.get("status"));
                    Grade grade = grader != null ? Grade.valueOf(grader) : Grade.NO_GRADE;
                    list.put(uuid, new RuteImpl(-1, uuid, image, date, last_edit, name, author, gym, Util.stringToVals(coordinates), grade, status));
                }
                Log.p("[WebDatabase] Loaded rutes: " + list.values());
                Preferences.set(LAST_WEB_CONNECTION, Util.format(Util.getNow()));
                Log.p("[WebDatabase] " + "Last synced: " + Preferences.get(LAST_WEB_CONNECTION, ""));
                runnable.OnResult(list);

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

    public ConnectionRequest getAndWait(String url) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(false);
        r.setUrl(url);
        NetworkManager.getInstance().addToQueueAndWait(r);
        return r;
    }

    public ConnectionRequest postAndWait(String url, ActionListener<NetworkEvent> evt) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(true);
        r.setUrl(url);
        r.addResponseListener(evt);
        NetworkManager.getInstance().addToQueueAndWait(r);
        return r;
    }

    public ConnectionRequest postAndWaitAndFail(String url, ActionListener<NetworkEvent> evt) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(true);
        r.setUrl(url);
        r.setFailSilently(true);
        r.addResponseListener(evt);
        NetworkManager.getInstance().addToQueueAndWait(r);
        return r;
    }

    public void post(String url, ActionListener<NetworkEvent> evt) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(true);
        r.setUrl(url);
        r.addResponseListener(evt);
        NetworkManager.getInstance().addToQueue(r);
    }

    Map<String, Object> getJsonData(ConnectionRequest evt) throws IOException {
        return new JSONParser().parseJSON(
                new InputStreamReader(
                        new ByteArrayInputStream(evt.getResponseData()), "UTF-8"));
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
        r.addExceptionListener(evt -> Log.p("Hansi"));
        NetworkManager.getInstance().addToQueue(r);
        return r;
    }

    private ConnectionRequest sendJsonWait(String url, String json, ActionListener<NetworkEvent> listener) {
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
        NetworkManager.getInstance().addToQueueAndWait(r);
        return r;
    }

    private ConnectionRequest sendJson(String url, String json) {
        return sendJson(url, json, evt -> {});
    }

    public boolean checkUserName(String username) {
        ConnectionRequest c = postAndWaitAndFail(host + "/check_username/" + username, evt -> {});
        return c.getResponseCode() == 200;
    }

    public void resetLastVisit() {
        Preferences.set(LAST_WEB_CONNECTION, "1900-01-01 00:00:00");
    }


    public interface Result<T> {
        void OnResult(T t);
    }

}
