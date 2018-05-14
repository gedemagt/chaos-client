package com.jhalkjar.caoscomp.gui;

/**
 * Created by jesper on 11/5/17.
 */

import com.codename1.components.FloatingActionButton;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.*;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.misc.Spacer;
import com.jhalkjar.caoscomp.gui.rutelist.DefaultRuteList;

import java.util.List;


/**
 * Created by jesper on 2/8/17.
 */
public class GymList extends Form {

    Container centerContainer = new Container(new BorderLayout());
    List<Gym> gyms;

    public GymList() {
        super(new BorderLayout());

        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.setUIID("FaB");
        fab.addActionListener(evt -> {
            new GymCreator(null, this, g->{updateUI();}).show();
        });
        fab.bindFabToContainer(getContentPane());

        add(BorderLayout.CENTER, centerContainer);

        new ToolbarBuilder().comps().compsAll().build(getToolbar());

        updateUI();
    }



    private void updateUI() {
        gyms = DB.getInstance().getGyms();
        centerContainer.removeAll();
        if(gyms.size() == 0) {
            Label l = new Label("Please add a gym?");
            centerContainer.add(BorderLayout.CENTER, l);
        }
        else {
            Container list = new Container(BoxLayout.y());
            list.setScrollableY(true);
            for(Gym r : gyms) {
                Container c = createListElement(r);
                list.add(c);
            }
            list.addPullToRefresh(()  -> {
//                DB.getInstance().sync();
//                revalidate();
            });
            centerContainer.add(BorderLayout.CENTER, list);
        }
        revalidate();
    }

    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");


    private Container createListElement(Gym gym) {

        Label name = new Label(gym.getName());
        name.setUIID("RuteName");

        Label author = new Label(gym.getSectors().size()+"");
        author.setUIID("DetailListElement");

        Label date = new Label(dateFormat.format(gym.getDate()));
        date.setUIID("DateListElement");

        TableLayout tbl = new TableLayout(2, 2);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");

        cnt.add(tbl.createConstraint().widthPercentage(50), name);
        cnt.add(tbl.createConstraint().widthPercentage(50).horizontalAlign(LEFT), date);

        cnt.add(tbl.createConstraint().widthPercentage(50).horizontalAlign(LEFT), new Label("Rutes: " + countRutes(DB.getInstance().getRutes(), gym)));
        cnt.add(tbl.createConstraint().widthPercentage(50).horizontalAlign(LEFT), new Label("Sectors: " + gym.getSectors().size()));

        for(int i=0; i<cnt.getComponentCount(); i++) {
            cnt.getComponentAt(i).addPointerReleasedListener(evt -> {
                DB.getInstance().setRememberedGym(gym);
                new DefaultRuteList().show();
            });
        }
        cnt.addPointerReleasedListener(evt -> {
            DB.getInstance().setRememberedGym(gym);
            new DefaultRuteList().show();
        });

        return BoxLayout.encloseY(cnt, new Spacer());
    }


    private int countRutes(List<Rute> rutes, Gym g) {
        int i=0;
        for(Rute r : rutes) {
            if(r.getSector().getGym().equals(g)) i++;
        }
        return i;
    }

}

