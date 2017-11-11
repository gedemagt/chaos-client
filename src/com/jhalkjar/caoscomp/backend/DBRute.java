package com.jhalkjar.caoscomp.backend;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.ui.Image;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.database.LocalDatabase;
import com.jhalkjar.caoscomp.gui.Point;

import java.util.Date;
import java.util.List;

/**
 * Created by jesper on 11/5/17.
 */
public class DBRute extends AbstractRute{

    public String getImageUrl() {
        return image_url;
    }

    public Image getImage() {
        if(image == null) {
            try {
             image = Image.createImage(FileSystemStorage.getInstance().openInputStream(image_url));
            } catch(Exception ex){
                Log.e(ex);
            }
        }

        return image;
    }

    public void save() {
        database.updateCoordinates(this);
        DB.getInstance().syncRutes();
    }

    public void delete() {
        FileSystemStorage.getInstance().delete(image_url);
        database.deleteRute(this);
    }

    @Override
    public boolean isLocal() {
        return true;
    }


    private String image_url;
    private Image image;
    private LocalDatabase database;

    public DBRute(long id, String uuid, Date date, String name, User author, Gym gym, List<Point> points, String image, LocalDatabase database) {
        super(id, uuid, date, name, author, gym, points);
        this.image_url = image;
        this.database = database;

    }


}
