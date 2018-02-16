package com.jhalkjar.caoscomp.gui;

import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.validation.Constraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.ui.validation.Validator;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.database.DB;

import java.util.Date;


/**
 * Created by jesper on 11/5/17.
 */
public class UserCreator extends Form {

    public UserCreator(Form f) {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");

        TextComponent name = new TextComponent().label("Username");
        TextComponent email = new TextComponent().label("Email");
        TextComponent password = new TextComponent().label("Password");
        password.getField().setConstraint(TextArea.PASSWORD);

        GymPicker gym = new GymPicker(this);

        Validator val = new Validator();
        val.addConstraint(name, new Constraint() {
            @Override
            public boolean isValid(Object value) {
                return freeUsername((String) value);
            }

            @Override
            public String getDefaultFailMessage() {
                return "Username already taken!";
            }
        });
        val.addConstraint(email, RegexConstraint.validEmail());

        add(BorderLayout.CENTER,
                BoxLayout.encloseY(
                        name,
                        email,
                        password,
                        gym));
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_DONE, s), (e) -> {
            if(!freeUsername(name.getField().getText())) Dialog.show("Invalid username", "Username already taken. Please pick a new one!", "OK", null);
            else if(password.getField().getText().length()==0) Dialog.show("No password", "Please choose a password!", "OK", null);
            else {
                DB.getInstance().createUser(name.getField().getText(), email.getField().getText(), Util.createHash(password.getField().getText()), gym.getGym(), Util.getNow());
                f.showBack();
            }

        });
        setBackCommand(setBackCommand("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            f.showBack();
        }));
    }


    private boolean freeUsername(String s) {
        return DB.getInstance().checkUsername(s);
    }

}
