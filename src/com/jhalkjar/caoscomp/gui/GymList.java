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
//        if(rutes.size()==0) {
//            forceAndShow();
//        }
    }

//    void forceAndShow() {
//        Dialog d = new WaitingDialog("Loading rutes");
//        DB.getInstance().forceWebRefresh(new DB.RefreshListener() {
//            @Override
//            public void OnBeginRefresh() {
//
//            }
//
//            @Override
//            public void OnEndRefresh() {
//                d.dispose();
//            }
//
//            @Override
//            public void OnRefreshError() {
//                d.dispose();
//            }
//        });
//        d.show();
//    }

//    void populateToolbar() {
//        tb = new Toolbar();
//        setToolbar(tb);
////        tb.setTitle("Problems");
//        tb.addCommandToOverflowMenu("Log out", null, (e) -> {
//            DB.getInstance().logout();
//        });
//
////        tb.addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_FILTER_LIST, s), evt -> {
////                    selectionContainer.setHidden(!selectionContainer.isHidden());
////                    selectionContainer.animateLayout(2);
////        });
//
////        List<Gym> gymList = DB.getInstance().getGyms();
////        String[] gymStrings = new String[gymList.size() + 1];
////        gymStrings[0] = "All";
////        for (int i = 0; i < gymList.size(); i++) gymStrings[i + 1] = gymList.get(i).getName();
////
////        PickerComponent gyms = PickerComponent.createStrings(gymStrings);
////        int selected = gymList.indexOf(gymFilter);
////        if (selected == -1) selected = 0;
////        else selected += 1;
////        gyms.getPicker().setSelectedStringIndex(selected);
//
//        Button gradePicker = new Button(FontImage.createMaterial(FontImage.MATERIAL_GRADE, s));
//        gradePicker.addActionListener(evt -> {
//            gradeFilter = new GradePicker().getMultipleGrades();
//            updateUI();
//
//        });
//        selectionContainer = createSelectionContainer();
//        tb.setTitleComponent(selectionContainer);
//
////        tb.add(BorderLayout.WEST, BoxLayout.encloseX(gyms, gradePicker));
////        gyms.getPicker().addActionListener(evt -> {
////            int selectedGym = gyms.getPicker().getSelectedStringIndex() - 1;
////            gymFilter = selectedGym >= 0 ? gymList.get(selectedGym) : null;
////            updateUI();
////        });
//
//
//        tb.addCommandToOverflowMenu("Force refresh", null, evt -> {
//            forceAndShow();
//        });
//    }

//        Container createSelectionContainer(){
//            TableLayout tbl = new TableLayout(1, 4);
//            Container cnt = new Container(tbl);
//            cnt.setUIID("SelectionContainer ");
//
//
//            List<Sector> sectors = DB.getInstance().getRememberedGym().getSectors();
//            String[] gymStrings = new String[sectors.size() + 1];
//            gymStrings[0] = "All";
//            for (int i = 0; i < sectors.size(); i++) gymStrings[i + 1] = sectors.get(i).getName();
//            PickerComponent gyms = PickerComponent.createStrings(gymStrings);
//            int selected = sectors.indexOf(sectorFilter);
//            if (selected == -1) selected = 0;
//            else selected += 1;
//            gyms.getPicker().setSelectedStringIndex(selected);
//
//            gyms.getPicker().addActionListener(evt -> {
//                int selectedGym = gyms.getPicker().getSelectedStringIndex() - 1;
//                sectorFilter = selectedGym >= 0 ? sectors.get(selectedGym) : null;
//                updateUI();
//            });
//
//
////            cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_HOME, s)));
//            cnt.add(tbl.createConstraint().widthPercentage(40), gyms);
////            cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_PERSON, s)));
////            cnt.add(tbl.createConstraint().widthPercentage(40), users);
//
//            return cnt;
//        }




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
//            list.addPullToRefresh(()  -> {
//                DB.getInstance().sync();
//                populateToolbar();
//                revalidate();
//            });
//            centerContainer.add(BorderLayout.NORTH, selectionContainer);
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
//        Label gym = new Label(rute.getSector().getName());
        author.setUIID("DetailListElement");
//        gym.setUIID("DetailListElement");

        Label date = new Label(dateFormat.format(gym.getDate()));
        date.setUIID("DateListElement");

        TableLayout tbl = new TableLayout(2, 4);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");

        cnt.add(tbl.createConstraint().widthPercentage(60).horizontalSpan(2), name);
        cnt.add(tbl.createConstraint().widthPercentage(30).horizontalAlign(LEFT), date);

//        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_HOME, s)));
//        cnt.add(tbl.createConstraint().widthPercentage(40), gym);
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

