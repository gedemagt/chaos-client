package com.jhalkjar.caoscomp.database;


import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.events.ActionListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jesper on 2/20/18.
 */
public class WebUtil {

    public static ConnectionRequest sendJson(String url, String json) {
        ConnectionRequest r = new ConnectionRequest(){
            @Override
            protected void buildRequestBody(OutputStream os) throws IOException {
                os.write(json.getBytes("UTF-8"));
            }
        };
        r.setPost(true);
        r.setUrl(url);
        r.setContentType("application/json");
        NetworkManager.getInstance().addToQueueAndWait(r);
        return r;
    }

    public static ConnectionRequest sendJson(String url, String json, ActionListener<NetworkEvent> listener) {
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

    public static void getJSON(String url, WebDatabase.Result<Map<String, Object>> evt) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(false);
        r.setUrl(url);
        r.addResponseListener(evt1 -> evt.OnResult(getJsonData(evt1.getConnectionRequest())));
        NetworkManager.getInstance().addToQueue(r);
    }

    public static Map<String, Object> getJSON(String url) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(false);
        r.setUrl(url);
        NetworkManager.getInstance().addToQueueAndWait(r);
        return getJsonData(r);
    }

    public static Map<String, Object> getJsonData(ConnectionRequest evt) {
        try {
            return new JSONParser().parseJSON(
                    new InputStreamReader(
                            new ByteArrayInputStream(evt.getResponseData()), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public static ConnectionRequest post(String url, ActionListener<NetworkEvent> evt) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(true);
        r.setUrl(url);
        r.addResponseListener(evt);
        NetworkManager.getInstance().addToQueue(r);
        return r;
    }

    public static ConnectionRequest post(String url) {
        return post(url, false);
    }

    public static ConnectionRequest post(String url, boolean ignorefail) {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(true);
        r.setUrl(url);
        r.setFailSilently(ignorefail);
        NetworkManager.getInstance().addToQueueAndWait(r);
        return r;
    }
}
