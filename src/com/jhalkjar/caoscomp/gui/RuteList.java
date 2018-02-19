package com.jhalkjar.caoscomp.gui;

/**
 * Created by jesper on 11/5/17.
 */

import com.codename1.components.FloatingActionButton;
import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;

import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.Grade;
import com.jhalkjar.caoscomp.backend.Gym;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.GymPicker.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by jesper on 2/8/17.
 */
public class RuteList extends Form {

    Container centerContainer = new Container(new BorderLayout());
    List<Rute> rutes;
    Container selectionContainer;
    Gym gymFilter = DB.getInstance().getLoggedInUser().getGym();
    User userFilter = null;
    ArrayList<Grade> gradeFilter = new ArrayList<>();
    Toolbar tb;


    public RuteList() {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");

        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.setUIID("FaB");
        fab.addActionListener(evt -> {
            new RuteCreator().show();
        });
        fab.bindFabToContainer(getContentPane());

        populateToolbar();

        Label l = new Label("Network error");
        selectionContainer = createSelectionContainer();
        selectionContainer.setHidden(true);
        add(BorderLayout.NORTH, l);
        add(BorderLayout.CENTER, centerContainer);

//        l.setHidden(!DB.getInstance().isRefreshing());
        l.setHidden(true);
        DB.getInstance().addRefreshListener(new DB.RefreshListener() {
            @Override
            public void OnBeginRefresh() {
            }

            @Override
            public void OnEndRefresh() {
                l.setHidden(true);
                rutes = DB.getInstance().getRutes();
                Collections.sort(rutes, (o1, o2) -> (int) (o2.getDate().getTime() - o1.getDate().getTime()));
                populateToolbar();
                updateUI();
            }

            @Override
            public void OnRefreshError() {
                l.setHidden(false);
                revalidate();
            }
        });
        rutes = DB.getInstance().getRutes();
        Collections.sort(rutes, (o1, o2) -> (int) (o2.getDate().getTime() - o1.getDate().getTime()));
        updateUI();
    }


    void populateToolbar() {
        tb = new Toolbar();
        setToolbar(tb);
        tb.setTitle("Problems");
        tb.addCommandToOverflowMenu("Log out", null, (e) -> {
            Preferences.set("logged_in_user", "");
            new Login().show();
        });

//        tb.addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_FILTER_LIST, s), evt -> {
//                    selectionContainer.setHidden(!selectionContainer.isHidden());
//                    selectionContainer.animateLayout(2);
//        });

        List<Gym> gymList = DB.getInstance().getGyms();
        String[] gymStrings = new String[gymList.size() + 1];
        gymStrings[0] = "All";
        for (int i = 0; i < gymList.size(); i++) gymStrings[i + 1] = gymList.get(i).getName();

        PickerComponent gyms = PickerComponent.createStrings(gymStrings);
        int selected = gymList.indexOf(gymFilter);
        if (selected == -1) selected = 0;
        else selected += 1;
        gyms.getPicker().setSelectedStringIndex(selected);

        Button gradePicker = new Button(FontImage.createMaterial(FontImage.MATERIAL_GRADE, s));
        gradePicker.addActionListener(evt -> {
            gradeFilter = new GradePicker().getMultipleGrades();
            updateUI();

        });

        tb.add(BorderLayout.WEST, BoxLayout.encloseX(gyms, gradePicker));
        gyms.getPicker().addActionListener(evt -> {
        int selectedGym = gyms.getPicker().getSelectedStringIndex() - 1;
        gymFilter = selectedGym >= 0 ? gymList.get(selectedGym) : null;
        updateUI();
        });


        tb.addCommandToOverflowMenu("Force refresh", null, evt -> {
            DB.getInstance().forceWebRefresh();
            populateToolbar();
            revalidate();
        });
    }

        Container createSelectionContainer(){
            TableLayout tbl = new TableLayout(1, 4);
            Container cnt = new Container(tbl);
            cnt.setUIID("SelectionContainer ");


            List<Gym> gymList = DB.getInstance().getGyms();
            String[] gymStrings = new String[gymList.size() + 1];
            gymStrings[0] = "All";
            for (int i = 0; i < gymList.size(); i++) gymStrings[i + 1] = gymList.get(i).getName();
            PickerComponent gyms = PickerComponent.createStrings(gymStrings);
            int selected = gymList.indexOf(gymFilter);
            if (selected == -1) selected = 0;
            else selected += 1;
            gyms.getPicker().setSelectedStringIndex(selected);

            List<User> userList = DB.getInstance().getUsers();
            String[] userStrings = new String[userList.size() + 1];
            userStrings[0] = "All";
            for (int i = 0; i < userList.size(); i++) userStrings[i + 1] = userList.get(i).getName();
            PickerComponent users = PickerComponent.createStrings(userStrings);
            selected = userList.indexOf(userFilter);
            if (selected == -1) selected = 0;
            else selected += 1;
            users.getPicker().setSelectedStringIndex(selected);

            gyms.getPicker().addActionListener(evt -> {
                int selectedGym = gyms.getPicker().getSelectedStringIndex() - 1;
                gymFilter = selectedGym >= 0 ? gymList.get(selectedGym) : null;
                updateUI();
            });
            users.getPicker().addActionListener(evt -> {
                int selectedUser = users.getPicker().getSelectedStringIndex() - 1;
                userFilter = selectedUser >= 0 ? userList.get(selectedUser) : null;
                updateUI();
            });

            cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_HOME, s)));
            cnt.add(tbl.createConstraint().widthPercentage(40), gyms);
            cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_PERSON, s)));
            cnt.add(tbl.createConstraint().widthPercentage(40), users);

            return cnt;
        }




    private void updateUI() {

        centerContainer.removeAll();
        if(rutes.size() == 0) {
            DB.getInstance().forceWebRefresh();
            Label l = new Label("Got a problem?");
            Container cnt = new Container(new BorderLayout());
            cnt.add(BorderLayout.CENTER, l);
            centerContainer.add(BorderLayout.CENTER, cnt);
            centerContainer.addPullToRefresh(()  -> {
                DB.getInstance().sync();
                populateToolbar();
                revalidate();
            });
        }
        else {
            Container list = new Container(BoxLayout.y());
            list.setScrollableY(true);
            for(Rute r : rutes) {
                if(userFilter != null && !r.getAuthor().equals(userFilter)) continue;
                if(gymFilter != null && !r.getGym().equals(gymFilter)) continue;
                if(gradeFilter.size() != 0 && !gradeFilter.contains(r.getGrade())) continue;
                Container c = createListElement(r);
                list.add(c);
            }
            list.addPullToRefresh(()  -> {
                DB.getInstance().sync();
                populateToolbar();
                revalidate();
            });
            centerContainer.add(BorderLayout.NORTH, selectionContainer);
            centerContainer.add(BorderLayout.CENTER, list);
        }
        revalidate();
    }

    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    Style s = UIManager.getInstance().getComponentStyle("Label");


    private Container createListElement(Rute rute) {

        Label name = new Label(rute.getName());
        name.setUIID("RuteName");

        Label author = new Label(rute.getAuthor().getName());
        Label gym = new Label(rute.getGym().getName());
        author.setUIID("DetailListElement");
        gym.setUIID("DetailListElement");

        Label date = new Label(dateFormat.format(rute.getDate()));
        date.setUIID("DateListElement");

        TableLayout tbl = new TableLayout(2, 4);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");

        cnt.add(tbl.createConstraint().widthPercentage(60).horizontalSpan(2), name);
        cnt.add(tbl.createConstraint().widthPercentage(30).horizontalAlign(LEFT), date);
        cnt.add(tbl.createConstraint().widthPercentage(10).horizontalAlign(RIGHT).verticalAlign(CENTER),
                BorderLayout.center(new ColoredSquare(Grade.getColorInt(rute.getGrade()), name.getStyle().getFont().getHeight())));

        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_HOME, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), gym);
        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_PERSON, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), author);

        for(int i=0; i<cnt.getComponentCount(); i++) {
            cnt.getComponentAt(i).addPointerReleasedListener(evt -> new Editor(rute, this).show());
        }
        cnt.addPointerReleasedListener(evt -> new Editor(rute, this).show());

        return BoxLayout.encloseY(cnt, new Spacer());
    }

    private class Spacer extends Container {

        public Spacer() {
            setUIID("Spacer");
        }

    }

}

