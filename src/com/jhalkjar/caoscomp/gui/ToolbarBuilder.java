package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.*;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.backend.Role;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.competition.CompetitionForm;
import com.jhalkjar.caoscomp.gui.competition.CompetitionList;
import com.jhalkjar.caoscomp.gui.misc.ToolbarSpacer;
import com.jhalkjar.caoscomp.gui.rutelist.DefaultRuteList;

import java.util.ArrayList;
import java.util.List;

public class ToolbarBuilder {

    private List<Addable> cmds = new ArrayList<>();
    Style s = UIManager.getInstance().getComponentStyle("Title");

    public ToolbarBuilder() {

    }

    public ToolbarBuilder defaultGym() {
        cmds.add(new CommandAddable(Command.create(DB.getInstance().getRememberedGym().getName(), FontImage.createMaterial(FontImage.MATERIAL_HOME, s),
                (e) -> new DefaultRuteList().show())));
        return this;
    }

    public ToolbarBuilder gyms() {
        cmds.add(new CommandAddable(Command.create("Gyms", FontImage.createMaterial(FontImage.MATERIAL_HOME, s),
                (e) -> new GymList().show())));
        return this;
    }

    public ToolbarBuilder comps() {
         cmds.add(new CommandAddable(Command.create("Comps", FontImage.createMaterial(FontImage.MATERIAL_CAKE, s),
                (e) -> new CompetitionForm().show())));

        return this;
    }

    public ToolbarBuilder compsAll() {
        if(DB.getInstance().getLoggedInUser() != null && DB.getInstance().getLoggedInUser().getRole() == Role.ADMIN) {
            cmds.add(new CommandAddable(Command.create("Comps (ADMIN)", FontImage.createMaterial(FontImage.MATERIAL_PANORAMA, s),
                    (e) -> new CompetitionList().show())));
        }
        return this;
    }
//
//    public ToolbarBuilder currentComp() {
//
//        Competition currentComp = DB.getInstance().getCurrentCompetition();
//        if(currentComp != null) {
//            cmds.add(new CommandAddable(Command.create(currentComp.getName(), FontImage.createMaterial(FontImage.MATERIAL_HDR_STRONG, s),
//                    (e) -> new CompetitionRuteList(currentComp).show())));
//        }
//
//        return this;
//    }

    public ToolbarBuilder spacer() {
        cmds.add(new SpacerAddable());
        return this;
    }

    public ToolbarBuilder custom(String name, char icon, ActionListener al) {
        cmds.add(new CommandAddable(Command.create(name, FontImage.createMaterial(icon, s), al)));
        return this;
    }

    public void build(Toolbar tb) {
        tb.addComponentToSideMenu(new Label("ChaosCompanion"));
        tb.addComponentToSideMenu(new ToolbarSpacer());
        for(Addable a : cmds) {
            a.add(tb);
        }
        tb.addComponentToSideMenu(new ToolbarSpacer());
        tb.addCommandToSideMenu("Log out", FontImage.createMaterial(FontImage.MATERIAL_EXIT_TO_APP, s), (e) -> {
            DB.getInstance().logout();
        });
    }


    private interface Addable {
        void add(Toolbar tb);
    }

    private class CommandAddable implements Addable {

        private Command cmd;
        public CommandAddable(Command cmd) {
            this.cmd = cmd;
        }

        @Override
        public void add(Toolbar tb) {
            tb.addCommandToSideMenu(cmd);
        }
    }

    private class SpacerAddable implements Addable {

        @Override
        public void add(Toolbar tb) {
            tb.addComponentToSideMenu(new ToolbarSpacer());
        }
    }


}
