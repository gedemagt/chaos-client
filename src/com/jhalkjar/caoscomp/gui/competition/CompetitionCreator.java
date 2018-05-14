package com.jhalkjar.caoscomp.gui.competition;

import com.codename1.components.InteractionDialog;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.Util;
import com.jhalkjar.caoscomp.backend.Competition;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.Editor;
import com.jhalkjar.caoscomp.gui.RuteCreator;
import com.jhalkjar.caoscomp.gui.rutelist.CompetitionRuteList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by jesper on 11/5/17.
 */
public class CompetitionCreator extends Form {

    private Container centerContainer = new Container(new BorderLayout());
    private List<User> admins = new ArrayList();
    private Competition c;

    public CompetitionCreator(Form back, Competition comp) {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");

        String t = "Mysterious Competition";
        Date startD = Util.getNow();
        Date endD = Util.getNow();
        if(comp != null) {
            t = comp.getName();
            startD = comp.getStart();
            endD = comp.getStop();
            admins.addAll(comp.getAdmins());
        }
        else {
            admins.add(DB.getInstance().getLoggedInUser());
        }


        TextComponent name = new TextComponent().label("Name").text(t);

        PickerComponent start = PickerComponent.createDateTime(startD);
        PickerComponent end = PickerComponent.createDateTime(endD);

        c = comp;

        Button b = new Button("Admins");
        b.addActionListener(evt ->
                admins = chooseAdmins(admins));

        Button add = new Button("Add rute");
        add.addActionListener(evt -> {
            new RuteCreator(CompetitionCreator.this, r-> {
                c.addRute(r);
                updateRutes();
            }).show();
        });

        add(BorderLayout.NORTH,
                BoxLayout.encloseY(
                        name,
                        BoxLayout.encloseX(new Label("Start"), start),
                        BoxLayout.encloseX(new Label("Stop"), end),
                        b, add));
        add(BorderLayout.CENTER, centerContainer);
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_DONE, s), (e) -> {

            if(c == null) c = DB.getInstance().createCompetition(name.getText(), start.getPicker().getDate(), end.getPicker().getDate(), 0, admins);
            else {
                c.setName(name.getText());
                c.setStart(start.getPicker().getDate());
                c.setStop(end.getPicker().getDate());
                c.setAdmins(admins);
                c.save();
            }

            new CompetitionRuteList(c).show();
        });
        setBackCommand(getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            back.showBack();
        }));

        updateRutes();
    }

    private void updateRutes() {
        List<Rute> rutes = c.getRutes();
        centerContainer.removeAll();
        Collections.sort(rutes, (o1, o2) -> {
            return o1.getName().compareTo(o2.getName());
        });

        Container c = new Container(BoxLayout.y());
        for(Rute r : rutes) {
            Label l = new Label(r.getName());
            l.addPointerReleasedListener(evt -> new Editor(r, this).show());
            c.add(l);
        }
        c.setScrollableY(true);
        centerContainer.add(BorderLayout.CENTER, c);
        revalidate();
    }


    private List<User> chooseAdmins(List<User> checked) {

        List<User> u = DB.getInstance().getUsers();
        List<User> result = new ArrayList<>();
        result.addAll(checked);

        Collections.sort(u, (o1, o2) -> {
            if(checked.contains(o1) && !checked.contains(o2)) return -1;
            else if(checked.contains(o2) && !checked.contains(o1)) return 1;
            else return o1.getName().compareTo(o2.getName());
        });

        Container c = new Container(BoxLayout.y());
        for(User user : u) {
            CheckBox cb = new CheckBox();
            Label l = new Label(user.getName());
            c.add(BorderLayout.centerEastWest(l, cb, null));
            cb.setSelected(checked.contains(user));
            cb.addActionListener(evt -> {
                if(cb.isSelected()) result.add(user);
                if(!cb.isSelected()) result.remove(user);
            });
        }
        c.setScrollableY(true);

        InteractionDialog dlg = new InteractionDialog("Admins");

        dlg.setLayout(new BorderLayout());
        Button ok = new Button("OK");

        ok.addActionListener((ee) -> {
            dlg.dispose();

        });
        dlg.addComponent(BorderLayout.CENTER, c);
        dlg.addComponent(BorderLayout.SOUTH, ok);
        dlg.show(30, 30, 30, 30);
        return result;
    }

}
