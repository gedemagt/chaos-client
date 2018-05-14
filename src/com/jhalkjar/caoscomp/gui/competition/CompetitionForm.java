package com.jhalkjar.caoscomp.gui.competition;


import com.codename1.components.InteractionDialog;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.Competition;
import com.jhalkjar.caoscomp.backend.Role;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.ToolbarBuilder;
import com.jhalkjar.caoscomp.gui.misc.Spacer;
import com.jhalkjar.caoscomp.gui.rutelist.CompetitionRuteList;

import javax.swing.*;


public class CompetitionForm extends Form {


    Container centerContainer = new Container(new BorderLayout());

    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    Style s = UIManager.getInstance().getComponentStyle("Label");

    public CompetitionForm() {
        setLayout(new BorderLayout());
        Button joinCompetition = new Button("Join");
        Button createCompetition = new Button("Create");
        add(BorderLayout.NORTH, BoxLayout.encloseY(joinCompetition, createCompetition));

        add(BorderLayout.CENTER, centerContainer);

        joinCompetition.addActionListener(evt -> {

            InteractionDialog dlg = new InteractionDialog("Hello");

            dlg.setLayout(new BorderLayout());
            TextField pin = new TextField(10);
            Label unknown = new Label("Unknown competition");
            unknown.setHidden(true);
            Button ok = new Button("OK");
            Button close = new Button("Close");
            close.addActionListener((ee) -> dlg.dispose());
            ok.addActionListener((ee) -> {
                for(Competition c : DB.getInstance().getCompetitions()) {
                    if(c.getPin() == Integer.parseInt(pin.getText())) {
                        dlg.dispose();
                        DB.getInstance().setCurrentCompetition(c);
                        new CompetitionRuteList(c).show();
                    }
                }
                unknown.setHidden(false);
                dlg.revalidate();

            });
            dlg.addComponent(BorderLayout.SOUTH, BoxLayout.encloseX(ok,close));
            dlg.addComponent(BorderLayout.CENTER, BoxLayout.encloseY(pin, unknown));
            dlg.show(30, 30, 30, 30);

        });

        createCompetition.addActionListener(evt -> {
            new CompetitionCreator(this, null).show();
        });

        new ToolbarBuilder().gyms().defaultGym().spacer().compsAll().build(getToolbar());

        updateUI();
    }

    private void updateUI() {
        centerContainer.removeAll();

        Container list = new Container(BoxLayout.y());
        list.setScrollableY(true);



        for(Competition r : DB.getInstance().getParticipated(DB.getInstance().getLoggedInUser())) {
            Container c = createListElement(r);
            list.add(c);
        }

        centerContainer.add(BorderLayout.CENTER, list);

        revalidate();
    }

    private Container createListElement(Competition comp) {

        Label name = new Label(comp.getName());
        name.setUIID("RuteName");


        Button stats = new Button(FontImage.createMaterial(FontImage.MATERIAL_FORMAT_ALIGN_JUSTIFY, s));
        Button edit = new Button(FontImage.createMaterial(FontImage.MATERIAL_SETTINGS, s));

        stats.addActionListener(evt -> {
            CompetitionStats s = new CompetitionStats(comp, CompetitionForm.this);
            s.show();
        });

        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new CompetitionCreator(CompetitionForm.this, comp).show();
            }
        });


        TableLayout tbl = new TableLayout(2, 4);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");
        if(comp.getAdmins().contains(DB.getInstance().getLoggedInUser()) || DB.getInstance().getLoggedInUser().getRole()== Role.ADMIN) {
            cnt.add(tbl.createConstraint().widthPercentage(75).horizontalSpan(3), name);
            cnt.add(tbl.createConstraint().widthPercentage(25).horizontalSpan(1), BoxLayout.encloseX(stats, edit));
        }
        else {

            cnt.add(tbl.createConstraint().widthPercentage(100).horizontalSpan(4), name);
        }

        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_ALARM, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), new Label(dateFormat.format(comp.getStart())));
        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_ALARM_OFF, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), new Label(dateFormat.format(comp.getStop())));

        for(int i=0; i<cnt.getComponentCount(); i++) {
            if(cnt.getComponentAt(i) == stats) continue;
            if(cnt.getComponentAt(i) == edit) continue;
            cnt.getComponentAt(i).addPointerReleasedListener(evt -> new CompetitionRuteList(comp).show());
        }
        cnt.addPointerReleasedListener(evt -> new CompetitionRuteList(comp).show());

        return BoxLayout.encloseY(cnt, new Spacer());
    }


}
