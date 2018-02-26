package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.PickerComponent;
import com.codename1.ui.layouts.GridLayout;
import com.jhalkjar.caoscomp.backend.Gym;
import com.jhalkjar.caoscomp.backend.Sector;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;

import java.util.List;

/**
 * Created by jesper on 11/13/17.
 */
public class GymPicker extends Container {

    List<Gym> gyms;
    PickerComponent pc;
    PickerComponent sector;

    public GymPicker(Form f, Sector s) {
        this(f, s, true, true);
    }

    public GymPicker(Form f, Sector s, boolean showGym, boolean showSector) {
        super(new GridLayout(1,2));
        gyms = DB.getInstance().getGyms();
        pc = PickerComponent.createStrings(gymToString(gyms));
        sector = PickerComponent.createStrings();

        pc.getPicker().setSelectedStringIndex(gyms.indexOf(DB.getInstance().getRememberedGym()));
        sector.getPicker().setStrings(sectorsToString(getGym()));
        if(s != null) sector.getPicker().setSelectedString(s.getName());
        else sector.getPicker().setSelectedStringIndex(0);

        pc.getPicker().addActionListener(evt -> {
            if(pc.getPicker().getSelectedStringIndex() == gyms.size()) {

                GymCreator creator = new GymCreator(null, f, gym-> {
                    gyms = DB.getInstance().getGyms();
                    pc.getPicker().setStrings(gymToString(gyms));
                    pc.getPicker().setSelectedStringIndex(gyms.indexOf(gym));
                    sector.getPicker().setStrings(sectorsToString(getGym()));
                });
                creator.show();
            }
            else sector.getPicker().setStrings(sectorsToString(getGym()));
        });
        if(showGym) add(pc);
        if(showSector) add(sector);
    }

    public Gym getGym() {
        return gyms.get(pc.getPicker().getSelectedStringIndex());
    }

    public Sector getSector() {
        return getGym().getSectors().get(sector.getPicker().getSelectedStringIndex());
    }

    private String[] gymToString(List<Gym> gyms) {
        String[] r = new String[gyms.size()+1];
        for(int i=0; i<gyms.size(); i++) {
            r[i] = gyms.get(i).getName();
        }
        r[gyms.size()] = "New gym..";
        return r;
    }

    private String[] sectorsToString(Gym g) {
        String[] r = new String[g.getSectors().size()];
        for(int i=0; i<g.getSectors().size(); i++) {
            r[i] = g.getSectors().get(i).getName();
        }
        return r;
    }
}
