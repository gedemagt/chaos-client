package com.jhalkjar.caoscomp.backend;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jesper on 11/7/17.
 */
public class WebGrabber {

    private String host;

    public WebGrabber(String host) {
        this.host = host;
    }

    public String getRutes() {
        ConnectionRequest r = new ConnectionRequest();
        r.setPost(false);
        r.setUrl(host + "/get_rutes");

        NetworkManager.getInstance().addToQueueAndWait(r);
        Map<String,Object> result = new HashMap<String, Object>();
        try {
            result = new JSONParser().parseJSON(new InputStreamReader(new ByteArrayInputStream(r.getResponseData()), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
