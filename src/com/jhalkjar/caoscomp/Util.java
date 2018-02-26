package com.jhalkjar.caoscomp;

import ca.weblite.codename1.json.JSONArray;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.ParseException;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.properties.InstantUI;
import com.codename1.util.regex.StringReader;
import com.jhalkjar.caoscomp.backend.Sector;
import com.jhalkjar.caoscomp.gui.Point;
import com.jhalkjar.caoscomp.gui.Type;

import java.io.IOException;
import java.util.*;

/**
 * Created by jesper on 11/7/17.
 */
public class Util {

    public static String sectorsToJSON(List<Sector> s) {
        JSONArray arr = new JSONArray();
        for(Sector sec: s) {
            if(!sec.getName().equals("Uncategorized"))arr.put(sec.getName());
        }
        return arr.toString();
    }

    public static List<String> jsonToSectors(String json) {
        List<String> re = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            List<Object> points = (List<Object>) parser.parseJSON(new StringReader(json)).get("root");
            if(points == null) return re;
            for(Object o : points) {
                re.add((String) o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return re;
    }

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

    public static Date getNow() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        cal.setTime(new Date(System.currentTimeMillis() - tz.getRawOffset()));
        return cal.getTime();
    }

    public static String format(Date date) {
        DateFormat dtfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dtfmt.format(date);
    }

    public static Date parse(String date) {
        DateFormat dtfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dtfmt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Util.getNow();
    }

}
