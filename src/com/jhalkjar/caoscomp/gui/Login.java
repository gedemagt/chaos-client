package com.jhalkjar.caoscomp.gui;

import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.jhalkjar.caoscomp.Util;
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

        lgnBtn.addActionListener(evt -> {
            String user = username.getField().getText();
            String hash = password.getField().getText();

            try {
                Dialog d = new Dialog();
                d.add("Logging in...");
                DB.getInstance().checkLogin(user, hash, loggedin -> {
                    d.dispose();
                    if(loggedin != null) {
                        Log.p("Logging in user: " + loggedin.getName() + "(" + loggedin.getUUID() + ")");
                        Preferences.set("logged_in_user", loggedin.getUUID());
                        DB.getInstance().refreshLocal();
                        new RuteList().show();
                    }
                });
                d.show();

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
