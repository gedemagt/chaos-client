package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.PickerComponent;
import com.codename1.ui.layouts.BorderLayout;
import com.jhalkjar.caoscomp.backend.Gym;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;

import java.util.List;

/**
 * Created by jesper on 11/13/17.
 */
public class GymPicker extends Container {

    List<Gym> gyms;
    PickerComponent pc;

    public GymPicker(Form f) {
        super(new BorderLayout());
        gyms = DB.getInstance().getGyms();
        pc = PickerComponent.createStrings(gymToString(gyms));

        int index = 0;
        User loggedIn = DB.getInstance().getLoggedInUser();
        if(loggedIn != null && gyms.indexOf(loggedIn.getGym()) != -1) {
            index = gyms.indexOf(loggedIn.getGym());
        }


        pc.getPicker().setSelectedStringIndex(index);

        pc.getPicker().addActionListener(evt -> {
            if(pc.getPicker().getSelectedStringIndex() == gyms.size()) {

                GymCreator creator = new GymCreator(f, gym-> {
                    gyms = DB.getInstance().getGyms();
                    pc.getPicker().setStrings(gymToString(gyms));

                    pc.getPicker().setSelectedStringIndex(gyms.indexOf(gym));
                });
                creator.show();
            }
        });
        add(BorderLayout.CENTER, pc);
    }

    public Gym getGym() {
        return gyms.get(pc.getPicker().getSelectedStringIndex());
    }

    private String[] gymToString(List<Gym> gyms) {
        String[] r = new String[gyms.size()+1];
        for(int i=0; i<gyms.size(); i++) {
            r[i] = gyms.get(i).getName();
        }
        r[gyms.size()] = "New gym..";
        return r;
    }
}
