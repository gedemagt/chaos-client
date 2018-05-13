package com.jhalkjar.caoscomp.gui.rutelist;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.Grade;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.gui.Editor;
import com.jhalkjar.caoscomp.gui.misc.ColoredSquare;
import com.jhalkjar.caoscomp.gui.misc.Spacer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class DefaultElementDrawer implements RuteListElementDrawer {

    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    Style s = UIManager.getInstance().getComponentStyle("Label");

    @Override
    public Container createElement(RuteList list, Rute rute) {

        Label name = new Label(rute.getName());
        name.setUIID("RuteName");

        Label author = new Label(rute.getAuthor().getName());
        Label gym = new Label(rute.getSector().getName());
        author.setUIID("DetailListElement");
        gym.setUIID("DetailListElement");

        Label date = new Label(dateFormat.format(rute.getDate()));
        date.setUIID("DateListElement");

        TableLayout tbl = new TableLayout(2, 4);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");

        cnt.add(tbl.createConstraint().widthPercentage(60).horizontalSpan(2), name);
        cnt.add(tbl.createConstraint().widthPercentage(30).horizontalAlign(Component.LEFT), date);
        cnt.add(tbl.createConstraint().widthPercentage(10).horizontalAlign(Component.RIGHT).verticalAlign(Component.CENTER),
                BorderLayout.center(new ColoredSquare(Grade.getColorInt(rute.getGrade()), name.getStyle().getFont().getHeight())));

        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_HOME, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), gym);
        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_PERSON, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), author);

        for(int i=0; i<cnt.getComponentCount(); i++) {
            cnt.getComponentAt(i).addPointerReleasedListener(evt -> new Editor(rute, list).show());
        }
        cnt.addPointerReleasedListener(evt -> new Editor(rute, list).show());

        return BoxLayout.encloseY(cnt, new Spacer());

    }
}
