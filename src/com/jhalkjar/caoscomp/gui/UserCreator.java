package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;

import java.util.Date;


/**
 * Created by jesper on 11/5/17.
 */
public class UserCreator extends Form {

    public UserCreator(Form f) {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");

        TextField name = new TextField("username","Name of the Rute!", 20, TextArea.ANY);
        TextField email = new TextField("email","Name of the Rute!", 20, TextArea.ANY);
        TextField password = new TextField("password","Name of the Rute!", 20, TextArea.PASSWORD);

        GymPicker gym = new GymPicker(this);

        Label usernameError = new Label("Username already taken!");
        usernameError.setHidden(true);

        name.addDataChangedListener((type, index) -> {
            usernameError.setHidden(freeUsername(name.getText()));
        });

        add(BorderLayout.CENTER,
                BoxLayout.encloseY(
                        new Label("Name"),
                        name,
                        usernameError,
                        new Label("Email"),
                        email,
                        new Label("Gym"),
                        gym,
                        new Label("Password"),
                        password));
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_DONE, s), (e) -> {
            if(!freeUsername(name.getText())) Dialog.show("Invalid username", "Username already taken. Please pick a new one!", "OK", null);
            else {
                DB.getInstance().createUser(name.getText(), email.getText(), Util.createHash(password.getText()), gym.getGym(), new Date());
                f.showBack();
            }

        });
        getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            f.showBack();
        });
    }


    private boolean freeUsername(String s) {
        for(User u : DB.getInstance().getUsers()) {
            if(u.getName().equals(s)) {
                return false;
            }
        }
        return true;
    }

}
