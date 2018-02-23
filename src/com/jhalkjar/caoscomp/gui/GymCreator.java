package com.jhalkjar.caoscomp.gui;

import com.codename1.maps.MapComponent;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.backend.Gym;
import com.jhalkjar.caoscomp.database.DB;


/**
 * Created by jesper on 11/5/17.
 */
public class GymCreator extends Form {

//    MapComponent mapComponent = new MapComponent();
    double lat, lon;

    public GymCreator(Form back, GymCreationListener l) {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");

        TextComponent name = new TextComponent().label("Name");
//        mapComponent.addMapListener((source, zoom, center) -> {
//            lat = center.getLatitude();
//            lon = center.getLongitude();
//        });

        add(BorderLayout.NORTH,
                BoxLayout.encloseY(
                        new Label("Name"),
                        name));
//        add(BorderLayout.CENTER, mapComponent);
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_DONE, s), (e) -> {
            if(!DB.getInstance().checkGymname(name.getField().getText())) Dialog.show("Invalid name", "Name already taken. Please pick a new one!", "OK", null);
            else {
                Gym g = DB.getInstance().createGym(name.getField().getText(), lat, lon, Util.getNow());
                g.addSector(DB.getInstance().createSector("1", g, Util.getNow()));
                back.showBack();
                l.onNewGym(g);
            }
        });
        setBackCommand(getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            back.showBack();
        }));
    }

    public interface GymCreationListener {
        void onNewGym(Gym g);
    }

}
