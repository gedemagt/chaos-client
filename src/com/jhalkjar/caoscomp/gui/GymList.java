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
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.*;
import com.jhalkjar.caoscomp.database.DB;

import java.util.ArrayList;
import java.util.Collections;
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
//            new GymCreator().show();
        });
        fab.bindFabToContainer(getContentPane());

        add(BorderLayout.CENTER, centerContainer);


        gyms = DB.getInstance().getGyms();
//        Collections.sort(rutes, (o1, o2) -> (int) (o2.getDate().getTime() - o1.getDate().getTime()));
        updateUI();
    }



    private void updateUI() {
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
    Style s = UIManager.getInstance().getComponentStyle("Label");


    private Container createListElement(Gym gym) {

        Label name = new Label(gym.getName());
        name.setUIID("RuteName");

        Label author = new Label(gym.getSectors().size()+"");
        author.setUIID("DetailListElement");

        Label date = new Label(dateFormat.format(gym.getDate()));
        date.setUIID("DateListElement");

        TableLayout tbl = new TableLayout(2, 4);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");

        cnt.add(tbl.createConstraint().widthPercentage(60).horizontalSpan(2), name);
        cnt.add(tbl.createConstraint().widthPercentage(30).horizontalAlign(LEFT), date);

        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_PERSON, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), author);

        for(int i=0; i<cnt.getComponentCount(); i++) {
            cnt.getComponentAt(i).addPointerReleasedListener(evt -> {
                DB.getInstance().setRememberedGym(gym);
                new RuteList().show();
            });
        }
        cnt.addPointerReleasedListener(evt -> {
            DB.getInstance().setRememberedGym(gym);
            new RuteList().show();
        });

        return BoxLayout.encloseY(cnt, new Spacer());
    }

    private class Spacer extends Container {

        public Spacer() {
            setUIID("Spacer");
        }

    }

}

