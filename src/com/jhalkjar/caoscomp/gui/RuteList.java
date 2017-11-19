package com.jhalkjar.caoscomp.gui;

/**
 * Created by jesper on 11/5/17.
 */

import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;

import com.codename1.ui.spinner.Picker;
import com.codename1.ui.table.Table;
import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.Gym;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.backend.User;
import com.jhalkjar.caoscomp.database.DB;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by jesper on 2/8/17.
 */
public class RuteList extends Form {

    Container centerContainer = new Container(new BorderLayout());
    List<Rute> rutes;

    public RuteList() {
        super(new BorderLayout());

        Style s = UIManager.getInstance().getComponentStyle("Title");
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_ADD, s), (e) -> {
            new RuteCreator().show();
        });
        getToolbar().addCommandToOverflowMenu("Log out", null, (e)->{
            Preferences.set("logged_in_user", "");
            new Login().show();
        });

        Label l =  new Label("Refreshing...");
        add(BorderLayout.NORTH, l);
        add(BorderLayout.CENTER, centerContainer);

        l.setHidden(!DB.getInstance().isRefreshing());
        DB.getInstance().addRefreshListener(new DB.RefreshListener() {
            @Override
            public void OnBeginRefresh() {
                l.setHidden(false);
                revalidate();
            }

            @Override
            public void OnEndRefresh() {
                l.setHidden(true);
                rutes = DB.getInstance().getRutes();
                Collections.sort(rutes, (o1, o2) -> (int) (o2.getDate().getTime() - o1.getDate().getTime()));
                updateUI();
            }
        });
        rutes = DB.getInstance().getRutes();
        Collections.sort(rutes, (o1, o2) -> (int) (o2.getDate().getTime() - o1.getDate().getTime()));
        updateUI();
    }

    Gym gymFilter = null;
    User userFilter = null;

    Container createSelectionContainer() {
        TableLayout tbl = new TableLayout(1,4);
        Container cnt = new Container(tbl);

        Picker gyms = new Picker();
        gyms.setType(Display.PICKER_TYPE_STRINGS);
        List<Gym> gymList = DB.getInstance().getGyms();
        String[] gymStrings = new String[gymList.size()+1];
        gymStrings[0]="All";
        for(int i=0; i<gymList.size(); i++) gymStrings[i+1] = gymList.get(i).getName();
        gyms.setStrings(gymStrings);
        int selected = gymList.indexOf(gymFilter);
        if(selected == -1) selected = 0;
        else selected +=1;
        gyms.setSelectedStringIndex(selected);

        Picker users = new Picker();
        users.setType(Display.PICKER_TYPE_STRINGS);
        List<User> userList = DB.getInstance().getUsers();
        String[] userStrings = new String[userList.size()+1];
        userStrings[0]="All";
        for(int i=0; i<userList.size(); i++) userStrings[i+1] = userList.get(i).getName();
        users.setStrings(userStrings);
        selected = userList.indexOf(userFilter);
        if(selected == -1) selected = 0;
        else selected +=1;
        users.setSelectedStringIndex(selected);

        gyms.addActionListener(evt -> {
            int selectedGym = gyms.getSelectedStringIndex()-1;
            gymFilter = selectedGym>=0 ? gymList.get(selectedGym) : null;
            updateUI();
        });
        users.addActionListener(evt -> {
            int selectedUser = users.getSelectedStringIndex()-1;
            userFilter = selectedUser>=0 ? userList.get(selectedUser) : null;
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
            Label l = new Label("Please add a rute!");
            centerContainer.add(BorderLayout.CENTER, l);
        }
        else {
            Container list = new Container(BoxLayout.y());
            list.setScrollableY(true);
            for(Rute r : rutes) {
                if(userFilter != null && !r.getAuthor().equals(userFilter)) continue;
                if(gymFilter != null && !r.getGym().equals(gymFilter)) continue;
                Container c = createListElement(r);
                list.add(c);
            }
            list.addPullToRefresh(() -> DB.getInstance().sync());
            centerContainer.add(BorderLayout.NORTH, createSelectionContainer());
            centerContainer.add(BorderLayout.CENTER, list);
        }
        revalidate();
    }

    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    Style s = UIManager.getInstance().getComponentStyle("Title");


    private Container createListElement(Rute rute) {

        Label name = new Label(rute.getName());
        name.setUIID("RuteName");

        Label author = new Label(rute.getAuthor().getName());
        Label gym = new Label(rute.getGym().getName());
        author.setUIID("AuthorListElement");
        gym.setUIID("AuthorListElement");

        Label date = new Label(dateFormat.format(rute.getDate()));
        date.setUIID("DateListElement");

        TableLayout tbl = new TableLayout(2, 4);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");

        cnt.add(tbl.createConstraint().widthPercentage(60).horizontalSpan(3), name);
        cnt.add(tbl.createConstraint().widthPercentage(40).horizontalAlign(LEFT), date);


        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_HOME, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), gym);
        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_PERSON, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), author);

        name.addPointerReleasedListener(evt -> new Editor(rute).show());

        return cnt;
    }

}

