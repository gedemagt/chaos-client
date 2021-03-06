package com.jhalkjar.caoscomp;

import com.codename1.ui.*;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.GymList;
import com.jhalkjar.caoscomp.gui.Login;
import com.jhalkjar.caoscomp.gui.RuteList;
import com.jhalkjar.caoscomp.gui.WaitingDialog;


/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.
 */
public class CaosCompanion {

    private Form current;
    private Resources theme;

    public void init(Object context) {
        theme = UIManager.initFirstTheme("/theme");
//        theme = UIManager.initNamedTheme("/theme", "Theme 1")

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature, uncomment if you have a pro subscription
        // Log.bindCrashProtection(true);
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }

        Display.getInstance().lockOrientation(true);

//        DB.getInstance().sync();

        DB.getInstance().refreshLocal();
        if(DB.getInstance().getLoggedInUser() == null) new Login().show();
        else{
            DB.getInstance().syncGyms();
            if(DB.getInstance().getRememberedGym() == null) new GymList().show();
            else new RuteList().show();
        }
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = Display.getInstance().getCurrent();
        }
    }
    
    public void destroy() {
    }

}
