package com.jhalkjar.caoscomp.gui;

import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.validation.Constraint;
import com.codename1.ui.validation.Validator;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;

/**
 * Created by jesper on 11/13/17.
 */
public class Login extends Form {

    public Login() {
        super(new BorderLayout());
        TextComponent username = new TextComponent().label("Username");
        TextComponent password = new TextComponent().label("Password");
        password.getField().setConstraint(TextArea.PASSWORD);

        Button lgnBtn = new Button("Login");
        Validator val = new Validator();
        val.addConstraint(username, new Constraint() {
            @Override
            public boolean isValid(Object value) {
                for(User u : DB.getInstance().getUsers()) {
                    if(u.getName().equals(value)) return true;
                }
                return false;
            }

            @Override
            public String getDefaultFailMessage() {
                return "Unknown username";
            }
        });
        val.addConstraint(password, new Constraint() {
            @Override
            public boolean isValid(Object value) {
                try {
                    String user = username.getField().getText();
                    String hash = Util.createHash(password.getField().getText());
                    User loggedin = DB.getInstance().checkLogin(user, hash);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            @Override
            public String getDefaultFailMessage() {
                return "Wrong password!";
            }
        });
//        val.addSubmitButtons(lgnBtn);

        lgnBtn.addActionListener(evt -> {
            String user = username.getField().getText();
            String hash = Util.createHash(password.getField().getText());

            try {
                User loggedin = DB.getInstance().checkLogin(user, hash);
                Log.p("Logging in user: " + loggedin.getName() + "(" + loggedin.getUUID() + ")");
                Preferences.set("logged_in_user", loggedin.getUUID());
                new RuteList().show();
            } catch (IllegalArgumentException e) {
                revalidate();
            }
        });
        add(BorderLayout.CENTER, BoxLayout.encloseY(username, password, lgnBtn));

        Button newUser = new Button("Register user!");
        newUser.addActionListener(evt -> {
            new UserCreator(this).show();
        });
        add(BorderLayout.SOUTH, newUser);

    }
}
