package com.jhalkjar.caoscomp.gui.rutelist;

import com.codename1.components.FloatingActionButton;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.*;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.*;
import com.jhalkjar.caoscomp.gui.misc.ColoredSquare;
import com.jhalkjar.caoscomp.gui.misc.Spacer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DefaultRuteList extends RuteList {


    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    Style s = UIManager.getInstance().getComponentStyle("Label");
    RuteCollection rc;

    Gym gymFilter = DB.getInstance().getRememberedGym();
    Sector sectorFilter = null;
    ArrayList<Grade> gradeFilter = new ArrayList<>();

    public DefaultRuteList() {
        super();

        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.setUIID("FaB");
        fab.addActionListener(evt -> {
            new RuteCreator(this, rute -> {}).show();
        });
        fab.bindFabToContainer(getContentPane());

        DB.getInstance().addRefreshListener(new DB.RefreshListener() {
            @Override
            public void OnBeginRefresh() {

            }

            @Override
            public void OnEndRefresh() {
                rc = new RuteCollection(DB.getInstance().getRutes());
            }

            @Override
            public void OnRefreshError() {

            }
        });
        populateToolbar();
        updateUI();
    }

    @Override
    public Container createElement(Rute rute) {

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
            cnt.getComponentAt(i).addPointerReleasedListener(evt -> new Editor(rute, this).show());
        }
        cnt.addPointerReleasedListener(evt -> new Editor(rute, this).show());

        return BoxLayout.encloseY(cnt, new Spacer());
    }

    @Override
    protected void onPull() {
        DB.getInstance().sync();
        populateToolbar();
        updateUI();
    }

    private void populateToolbar() {
        Toolbar tb = new Toolbar();
        setToolbar(tb);
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


        ToolbarBuilder builder = new ToolbarBuilder();
        builder.gyms().spacer().comps().compsAll().spacer().custom("Refresh", FontImage.MATERIAL_REFRESH, evt-> forceAndShow());

        if(DB.getInstance().getLoggedInUser() != null && DB.getInstance().getLoggedInUser().getRole() == Role.ADMIN) {

            builder.spacer().custom("Manage gym", FontImage.MATERIAL_SETTINGS, evt -> new GymCreator(DB.getInstance().getRememberedGym(), this, g -> {}).show());

        }
        builder.build(tb);
    }

    @Override
    protected RuteCollection getRutes() {
        if(rc == null) rc=new RuteCollection(DB.getInstance().getRutes());
        return rc.filter().sector(sectorFilter).gym(gymFilter).grade(gradeFilter).get();
    }

}
