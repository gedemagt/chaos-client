package com.jhalkjar.caoscomp.gui.competition;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.backend.Competition;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.database.RuteProvider.CompetitionRuteProvider;
import com.jhalkjar.caoscomp.gui.rutelist.CompetitionElementDrawer;
import com.jhalkjar.caoscomp.gui.rutelist.RuteList;

import java.util.ArrayList;


/**
 * Created by jesper on 11/5/17.
 */
public class CompetitionCreator extends Form {


    public CompetitionCreator(Form back) {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");

        TextComponent name = new TextComponent().label("Name").text("Mysterious Problem");

        PickerComponent start = PickerComponent.createDateTime(com.jhalkjar.caoscomp.Util.getNow());
        PickerComponent end = PickerComponent.createDateTime(com.jhalkjar.caoscomp.Util.getNow());


        add(BorderLayout.NORTH,
                BoxLayout.encloseY(
                        name,
                        BoxLayout.encloseX(new Label("Start"), start),
                        BoxLayout.encloseX(new Label("Stop"), end)));
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_DONE, s), (e) -> {
            ArrayList<User> admins = new ArrayList();
            admins.add(DB.getInstance().getLoggedInUser());
            Competition comp = DB.getInstance().createCompetition(name.getText(), start.getPicker().getDate(), end.getPicker().getDate(), 0, admins);
            new RuteList(new CompetitionRuteProvider(comp), new CompetitionElementDrawer(comp), false).show();
        });
        setBackCommand(getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            back.showBack();
        }));
    }

}
