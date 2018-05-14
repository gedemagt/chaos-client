package com.jhalkjar.caoscomp.gui.competition;

import com.codename1.components.FloatingActionButton;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.*;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.ToolbarBuilder;
import com.jhalkjar.caoscomp.gui.misc.Spacer;
import com.jhalkjar.caoscomp.gui.rutelist.CompetitionRuteList;

import java.util.Collections;
import java.util.List;


/**
 * Created by jesper on 2/8/17.
 */
public class CompetitionList extends Form {

    Container centerContainer = new Container(new BorderLayout());

    List<Competition> comps;


    public CompetitionList() {
        super(new BorderLayout());

        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.setUIID("FaB");
        fab.addActionListener(evt -> {
            new CompetitionCreator(this, null).show();
        });
        fab.bindFabToContainer(getContentPane());
        comps = DB.getInstance().getCompetitions();
        add(BorderLayout.CENTER, centerContainer);

        new ToolbarBuilder().gyms().defaultGym().build(getToolbar());
        updateUI();
    }


    private void updateUI() {
        centerContainer.removeAll();
        if(comps.size() == 0) {
            Label l = new Label("Got a competition?");
            centerContainer.add(BorderLayout.CENTER, l);
        }
        else {
            Container list = new Container(BoxLayout.y());
            list.setScrollableY(true);

            Collections.sort(comps, (o1, o2) -> {
                long v1 = o1.getDate().getTime();
                long v2 = o2.getDate().getTime();
                if(v1==v2) return 0;
                else if(v1>v2) return -1;
                else return 1;
            });

            for(Competition r : comps) {
                Container c = createListElement(r);
                list.add(c);
            }

            centerContainer.add(BorderLayout.CENTER, list);
        }
        revalidate();
    }

    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    Style s = UIManager.getInstance().getComponentStyle("Label");


    private Container createListElement(Competition comp) {

        Label name = new Label(comp.getName());
        name.setUIID("RuteName");


        TableLayout tbl = new TableLayout(2, 4);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");

        cnt.add(tbl.createConstraint().widthPercentage(100).horizontalSpan(4), name);

        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_ALARM, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), new Label(dateFormat.format(comp.getStart())));
        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_ALARM_OFF, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), new Label(dateFormat.format(comp.getStop())));

        for(int i=0; i<cnt.getComponentCount(); i++) {
            cnt.getComponentAt(i).addPointerReleasedListener(evt -> new CompetitionRuteList(comp).show());
        }
        cnt.addPointerReleasedListener(evt -> new CompetitionRuteList(comp).show());

        return BoxLayout.encloseY(cnt, new Spacer());
    }


}

