package com.jhalkjar.caoscomp.database;

import ca.weblite.codename1.db.DAO;
import ca.weblite.codename1.db.DAOProvider;
import com.codename1.db.Database;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.jhalkjar.caoscomp.backend.Rute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.jhalkjar.caoscomp.gui.Point;

/**
 * Created by jesper on 11/5/17.
 */
public class RuteDatabase {
    private static String configPath = "/setup.sql";
    private String dbname = "erik";

    public RuteDatabase() {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            db.close();
        }
        catch (IOException e) {

        }
    }

    public Rute createRute(String name, String image_url, String creator) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO rutes = provider.get("rutes");
            Map rute = (Map) rutes.newObject();
            rute.put("name", name);
            rute.put("image_url", image_url);
            rute.put("creator", creator);
            rute.put("x", "");
            rute.put("y", "");
            rutes.save(rute);
            db.close();

            List<Rute> loaded = loadRutes();
            return loaded.get(loaded.size()-1);

        } catch (IOException e) {

        }
        return null;
    }

    public ArrayList<Rute> loadRutes() {
        ArrayList<Rute> rutes = new ArrayList<>();
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("rutes");
            List<Map> allRutes = games.fetchAll();
            for(Map m : allRutes) {

                String name = (String) m.get("name");
                String image_url = (String) m.get("image_url");
                String points = (String) m.get("points");
                String creater = (String) m.get("creator");
                long id = (Long) m.get("id");

                Rute r = new Rute(name, image_url, stringToVals(points), creater, id, this);
                rutes.add(r);
            }
            db.close();

        } catch (IOException e) {

        }

        return rutes;
    }

    private ArrayList<Point> stringToVals(String string) {
        ArrayList<Point> result = new ArrayList<>();
        if(string.equals("null") || string.length() == 0) return result;
        JSONParser parser = new JSONParser();

        try {
            Map<String, Object> parsed = parser.parseJSON(new com.codename1.util.regex.StringReader(string));
            List<Object> points = (List<Object>) parsed.get("root");
            for(int i=0; i<points.size(); i++) {
                List<Object> xy = (List<Object>) points.get(i);
                double x = (Double) xy.get(0);
                double y = (Double) xy.get(1);
                result.add(new Point((float) x, (float) y));
            }

        } catch (IOException e) {
            Log.e(e);
        }
        return result;
    }


    private String valsToString(List<Point> vals) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0; i<vals.size(); i++) {
            sb.append(vals.get(i));
            sb.append(",");
        }
        if(vals.size()>0) sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

    public void saveRute(Rute r) {
        try {
            Database db = Database.openOrCreate(dbname);
            DAOProvider provider = new DAOProvider(db, configPath, 1);
            DAO games = provider.get("rutes");
            Map game = (Map) games.getById(r.getID(), true);
            game.put("name", r.getName());
            game.put("image_url", r.getImageUrl());
            game.put("points", valsToString(r.getPoints()));
            game.put("creator", r.getCreator());
            games.update(game);
            db.close();

        } catch (IOException e) {

        }
    }

    public void deleteRute(Rute r) {

        try {
            Database db = Database.openOrCreate(dbname);
            db.execute("DELETE FROM rutes WHERE id=" + r.getID());
            db.close();

        } catch (IOException e) {

        }
    }

}
