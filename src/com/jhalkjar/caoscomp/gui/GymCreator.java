package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.backend.Gym;
import com.jhalkjar.caoscomp.backend.Sector;
import com.jhalkjar.caoscomp.database.DB;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jesper on 11/5/17.
 */
public class GymCreator extends Form {

    double lat, lon;
    private Container sectors;
    private List<SectorContainer> scs = new ArrayList<>();
    private Gym g;

    public GymCreator(Gym g, Form back, GymCreationListener l) {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");
        this.g = g;

        TextComponent name = new TextComponent().label("Name");
        Button addSector = new Button(FontImage.createMaterial(FontImage.MATERIAL_ADD, getAllStyles()));
        addSector.addActionListener(evt -> {
            SectorContainer sec = new SectorContainer();
            sectors.add(sec);
            scs.add(sec);
            revalidate();
        });
        sectors = new Container(BoxLayout.y());
        sectors.setScrollableY(true);
        Container sectorHeadline = BorderLayout.centerEastWest(new Label("Sectors"), addSector, null);
        add(BorderLayout.NORTH,
                BoxLayout.encloseY(
                        new Label("Name"),
                        name));
        add(BorderLayout.CENTER, BoxLayout.encloseY(sectorHeadline, sectors));
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_DONE, s), (e) -> {

            if(g == null && !DB.getInstance().checkGymname(name.getField().getText())) Dialog.show("Invalid name", "Name already taken. Please pick a new one!", "OK", null);

            else {
                List<Sector> sectors = new ArrayList<>();
                for (SectorContainer sc : scs) {
                    sectors.add(new Sector(sc.getSectorName()));
                }
                Gym gg;
                if (g != null) {

                    g.setSectors(sectors);
                    DB.getInstance().save(g);
                    gg = g;
                } else {
                    gg = DB.getInstance().createGym(name.getField().getText(), lat, lon, Util.getNow(), sectors);
                }


                DB.getInstance().syncGyms();
                back.showBack();
                l.onNewGym(gg);
            }
        });
        setBackCommand(getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            back.showBack();
        }));

        if(g != null) {
            name.getField().setText(g.getName());
            for(Sector sec : g.getSectors()) {
                SectorContainer secC = new SectorContainer(sec.getName());
                sectors.add(secC);
                scs.add(secC);
                revalidate();
            }
        }
    }

    class SectorContainer extends Container{
        TextField tf = new TextField(20);
        Button b = new Button(FontImage.createMaterial(FontImage.MATERIAL_DELETE, getAllStyles()));

        public SectorContainer() {
            this("");
        }

        public SectorContainer(String s) {
            super(new BorderLayout());
            b.addActionListener(evt -> {
                sectors.removeComponent(this);
                scs.remove(this);
                revalidate();});
            add(BorderLayout.CENTER, tf);
            add(BorderLayout.EAST, b);
            tf.setText(s);
            if(s.equals("Uncategorized")) {
                tf.setEnabled(false);
                b.setEnabled(false);
            }
            revalidate();
        }

        public String getSectorName() {
            return tf.getText();
        }
    }

    public interface GymCreationListener {
        void onNewGym(Gym g);
    }

}
