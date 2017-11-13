package com.jhalkjar.caoscomp.gui;

import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;

/**
 * Created by jesper on 11/13/17.
 */
public class Login extends Form {

    public Login() {
        super(new BorderLayout());
        TextField tfUsrnm = new TextField();
        tfUsrnm.setHint("username");
        TextField tfPassword = new TextField("password","Name of the Rute!", 20, TextArea.PASSWORD);
        Label error = new Label();
        error.setHidden(true);
        Button lgnBtn = new Button("Login");
        lgnBtn.addActionListener((ActionListener) (ActionEvent evt) -> {
            String username = tfUsrnm.getText();
            String hash = Util.createHash(tfPassword.getText());
            try {
                User loggedin = DB.getInstance().checkLogin(username, hash);
                Log.p("Logging in user: " + loggedin.getName() + "(" + loggedin.getUUID() + ")");
                Preferences.set("logged_in_user", loggedin.getUUID());
                new RuteList().show();
            } catch (IllegalArgumentException e) {
                error.setText(e.getMessage());
                error.setHidden(false);
                revalidate();
            }
        });
        add(BorderLayout.CENTER, BoxLayout.encloseY(tfUsrnm, tfPassword, error, lgnBtn));

        Button newUser = new Button("Register user!");
        newUser.addActionListener(evt -> {
            new UserCreator(this).show();
        });
        add(BorderLayout.SOUTH, newUser);

    }
}
