package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.spinner.Picker;
import com.jhalkjar.caoscomp.backend.Gym;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;

import java.util.List;

/**
 * Created by jesper on 11/13/17.
 */
public class GymPicker extends Picker {

    List<Gym> gyms;

    public GymPicker(Form f) {
        setType(Display.PICKER_TYPE_STRINGS);
        gyms = DB.getInstance().getGyms();

        setStrings(gymToString(gyms));
        int index = 0;
        User loggedIn = DB.getInstance().getLoggedInUser();
        if(loggedIn != null && gyms.indexOf(loggedIn.getGym()) != -1) {
            index = gyms.indexOf(loggedIn.getGym());
        }


        setSelectedStringIndex(index);

        addActionListener(evt -> {
            if(getSelectedStringIndex() == gyms.size()) {

                GymCreator creator = new GymCreator(f, gym-> {
                    gyms = DB.getInstance().getGyms();
                    setStrings(gymToString(gyms));

                    setSelectedStringIndex(gyms.indexOf(gym));
                });
                creator.show();
            }
        });
    }

    public Gym getGym() {
        return gyms.get(getSelectedStringIndex());
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
