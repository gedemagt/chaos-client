package com.jhalkjar.caoscomp.gui.rutelist;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.Competition;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.Editor;
import com.jhalkjar.caoscomp.gui.misc.Spacer;



public class CompetitionElementDrawer implements RuteListElementDrawer {

    Style s = UIManager.getInstance().getComponentStyle("Label");

    private Competition comp;

    public CompetitionElementDrawer(Competition comp) {
        this.comp = comp;
    }

    @Override
    public Container createElement(RuteList list, Rute rute) {

        Label name = new Label(rute.getName());
        name.setUIID("RuteName");

        Label tries = new Label(rute.getAuthor().getName());
        Label gym = new Label(rute.getSector().getName());
        tries.setUIID("DetailListElement");
        gym.setUIID("DetailListElement");

        TableLayout tbl = new TableLayout(2, 4);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");

        Competition.Status status = comp.getStatus(DB.getInstance().getLoggedInUser(), rute);
        tries.setText(status.tries +" ");

        Button up = new Button(FontImage.createMaterial(FontImage.MATERIAL_ARROW_UPWARD, tries.getStyle()));
        Button down = new Button(FontImage.createMaterial(FontImage.MATERIAL_ARROW_DOWNWARD, tries.getStyle()));


        CheckBox completed = new CheckBox();

        completed.setSelected(status.completed);
        completed.addActionListener(evt -> {
            comp.setStatus(DB.getInstance().getLoggedInUser(), rute, new Competition.Status(status.tries, completed.isSelected(), rute, DB.getInstance().getLoggedInUser()));
        });
        up.addActionListener(evt -> {
            int current = Integer.parseInt(tries.getText().trim());
            current++;
            tries.setText(current + "");
            comp.setStatus(DB.getInstance().getLoggedInUser(), rute, new Competition.Status(current, completed.isSelected(), rute, DB.getInstance().getLoggedInUser()));
            cnt.revalidate();
        });
        down.addActionListener(evt -> {
            int current = Integer.parseInt(tries.getText().trim());
            if(current == 0) return;
            current--;
            tries.setText(current + "");
            comp.setStatus(DB.getInstance().getLoggedInUser(), rute, new Competition.Status(current, completed.isSelected(), rute, DB.getInstance().getLoggedInUser()));
            cnt.revalidate();
        });

        cnt.add(tbl.createConstraint().widthPercentage(60).horizontalSpan(3), name);
        cnt.add(tbl.createConstraint().widthPercentage(30).horizontalAlign(Component.RIGHT).verticalAlign(Component.CENTER),
                completed);

        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_HOME, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), gym);
        cnt.add(tbl.createConstraint().widthPercentage(0), new Label(FontImage.createMaterial(FontImage.MATERIAL_AUTORENEW, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), BorderLayout.centerEastWest(tries, up, down));

        for(int i=0; i<cnt.getComponentCount(); i++) {
            if(cnt.getComponentAt(i) == completed) continue;
            if(cnt.getComponentAt(i) == up) continue;
            if(cnt.getComponentAt(i) == down) continue;
            cnt.getComponentAt(i).addPointerReleasedListener(evt -> new Editor(rute, list).show());
        }
        cnt.addPointerReleasedListener(evt -> new Editor(rute, list).show());

        return BoxLayout.encloseY(cnt, new Spacer());

    }
}
