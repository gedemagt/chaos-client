package com.jhalkjar.caoscomp;

import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.jhalkjar.caoscomp.gui.Point;
import com.jhalkjar.caoscomp.gui.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jesper on 11/7/17.
 */
public class Util {

    public static ArrayList<Point> stringToVals(String string) {
        ArrayList<Point> result = new ArrayList<>();
        if(string == null || string.equals("null") || string.length() == 0) return result;
        JSONParser parser = new JSONParser();

        try {
            Map<String, Object> parsed = parser.parseJSON(new com.codename1.util.regex.StringReader(string));
            List<Object> points = (List<Object>) parsed.get("root");
            for(int i=0; i<points.size(); i++) {
                Map<String, Object> xy = (Map<String, Object>) points.get(i);
                double x = (Double) xy.get("x");
                double y = (Double) xy.get("y");
                double size = (Double) xy.get("size");
                String type;
                Object typeParsed = xy.get("type");
                if(!(typeParsed instanceof String)) type = "NORMAL";
                else type = (String) typeParsed;
                Type t = Type.valueOf(type);
                Point p = new Point((float) x, (float) y, (float) size);
                p.setType(t);
                result.add(p);
            }

        } catch (IOException e) {
            Log.e(e);
        }
        return result;
    }


    public static String valsToString(List<Point> vals) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0; i<vals.size(); i++) {
            sb.append(vals.get(i).getJSON());
        }
        if(vals.size()>0) sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static String createHash(String data) {
        return data;
//        SHA1Digest sha1 = new SHA1Digest();
//        try {
//            byte[] b = data.getBytes("UTF-8");
//            sha1.update(b, 0, b.length);
//            byte[] hash = new byte[sha1.getDigestSize()];
//            sha1.doFinal(hash, 0);
//            return new String(hash, "UTF-8");
//        } catch (Exception ex) {
//            Log.e(ex);
//        }
//        return null;
    }
}
