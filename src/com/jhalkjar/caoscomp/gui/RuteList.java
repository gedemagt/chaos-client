package com.jhalkjar.caoscomp.gui;

/**
 * Created by jesper on 11/5/17.
 */

import com.codename1.components.FloatingActionButton;
import com.codename1.io.Log;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
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
public class RuteList extends Form {

    Container centerContainer = new Container(new BorderLayout());
    List<Rute> rutes;
    Gym gymFilter = DB.getInstance().getRememberedGym();
    Sector sectorFilter = null;
    ArrayList<Grade> gradeFilter = new ArrayList<>();
    Toolbar tb;

    ArrayList<Rute> selectedRutes = new ArrayList<>();


    public RuteList() {
        super(new BorderLayout());

        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.setUIID("FaB");
        fab.addActionListener(evt -> {
            new RuteCreator(this).show();
        });
        fab.bindFabToContainer(getContentPane());

        populateToolbar();
        DB.getInstance().addGymsSyncListener(new DB.RefreshListener() {
            @Override
            public void OnBeginRefresh() {

            }

            @Override
            public void OnEndRefresh() {
                populateToolbar();
            }

            @Override
            public void OnRefreshError() {

            }
        });

        Label l = new Label("Network error");
        add(BorderLayout.NORTH, l);
        add(BorderLayout.CENTER, centerContainer);

        l.setHidden(true);

        DB.getInstance().addRefreshListener(new DB.RefreshListener() {
            @Override
            public void OnBeginRefresh() {
            }

            @Override
            public void OnEndRefresh() {
                l.setHidden(true);
                rutes = DB.getInstance().getRutes();
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
        updateUI();
        if(rutes.size()==0) {
            forceAndShow();
        }
    }

    void forceAndShow() {
        Dialog d = new WaitingDialog("Loading rutes");

        DB.getInstance().forceWebRefresh(new DB.RefreshListener() {
            @Override
            public void OnBeginRefresh() {

            }

            @Override
            public void OnEndRefresh() {
                d.dispose();
            }

            @Override
            public void OnRefreshError() {
                d.dispose();
            }
        });
        d.show();
    }

    void populateToolbar() {
        tb = new Toolbar();
        setToolbar(tb);
        tb.addCommandToOverflowMenu("Log out", null, (e) -> {
            DB.getInstance().logout();
        });

//        tb.addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_FILTER_LIST, s), evt -> {
//                    selectionContainer.setHidden(!selectionContainer.isHidden());
//                    selectionContainer.animateLayout(2);
//        });

        List<Sector> sectors = DB.getInstance().getRememberedGym().getSectors();
        String[] sectorStrings = new String[sectors.size() + 1];
        sectorStrings[0] = "All";
        for (int i = 0; i < sectors.size(); i++) sectorStrings[i + 1] = sectors.get(i).getName();
        PickerComponent sectorPicker = PickerComponent.createStrings(sectorStrings);
        int selected = sectors.indexOf(sectorFilter);
        if (selected == -1) selected = 0;
        else selected += 1;
        sectorPicker.getPicker().setSelectedStringIndex(selected);

        sectorPicker.getPicker().addActionListener(evt -> {
            int selectedSector = sectorPicker.getPicker().getSelectedStringIndex() - 1;
            sectorFilter = selectedSector >= 0 ? sectors.get(selectedSector) : null;
            updateUI();
        });

        Button gradePicker = new Button(FontImage.createMaterial(FontImage.MATERIAL_GRADE, getTitleStyle()));
        gradePicker.addActionListener(evt -> {
            gradeFilter = new GradePicker().getMultipleGrades(gradeFilter);
            updateUI();

        });
        TableLayout tbl = new TableLayout(1, 2);
        Container cnt = new Container(tbl);
        cnt.add(tbl.createConstraint().widthPercentage(80), sectorPicker);
        cnt.add(tbl.createConstraint().widthPercentage(20), gradePicker);
        tb.setTitleComponent(cnt);

        tb.addCommandToOverflowMenu("Force refresh", null, evt -> {
            forceAndShow();
        });
        tb.addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_HOME, getTitleStyle()), evt -> {
            new GymList().showBack();
        });
        if(DB.getInstance().getLoggedInUser() != null && DB.getInstance().getLoggedInUser().getRole() == Role.ADMIN) {
            tb.addCommandToOverflowMenu("Manage gym", null, evt -> {
                new GymCreator(DB.getInstance().getRememberedGym(), this, g -> {}).show();
            });
        }
    }


    private void updateUI() {
        centerContainer.removeAll();
        if(rutes.size() == 0) {
            Label l = new Label("Got a problem?");
            centerContainer.add(BorderLayout.CENTER, l);
        }
        else {
            Container list = new Container(BoxLayout.y());
            list.setScrollableY(true);
            selectedRutes = new ArrayList<>();
            for(Rute r : rutes) {
                if(sectorFilter != null && !r.getSector().equals(sectorFilter)) continue;
                if(gymFilter != null && !r.getSector().getGym().equals(gymFilter)) continue;
                if(gradeFilter.size() != 0 && !gradeFilter.contains(r.getGrade())) continue;
                selectedRutes.add(r);
            }

            Collections.sort(selectedRutes, (o1, o2) -> {
                long v1 = o1.getDate().getTime();
                long v2 = o2.getDate().getTime();
                if(v1==v2) return 0;
                else if(v1>v2) return -1;
                else return 1;
            });

            for(Rute r : selectedRutes) {
                Container c = createListElement(r);
                list.add(c);
            }

            list.addPullToRefresh(()  -> {
                DB.getInstance().sync();
                populateToolbar();
                revalidate();
            });
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
        Label gym = new Label(rute.getSector().getName());
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

    public ArrayList<Rute> getSelectedRutes() {
        return selectedRutes;
    }

    private class Spacer extends Container {

        public Spacer() {
            setUIID("Spacer");
        }

    }

}

