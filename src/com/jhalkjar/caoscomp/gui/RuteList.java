package com.jhalkjar.caoscomp.gui;

/**
 * Created by jesper on 11/5/17.
 */

import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;

import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.database.DB;
import java.util.List;


/**
 * Created by jesper on 2/8/17.
 */
public class RuteList extends Form {

    Container centerContainer = new Container(new BorderLayout());

    public RuteList() {
        super(new BorderLayout());

        refreshList();

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
                refreshList();
            }
        });

    }

    private void refreshList() {

        updateUI();
    }

    private void updateUI() {

        List<Rute> rutes = DB.getInstance().getRutes();
        centerContainer.removeAll();
        if(rutes.size() == 0) {
            Label l = new Label("Please add a rute!");
            centerContainer.add(BorderLayout.CENTER, l);
        }
        else {
            Container list = new Container(BoxLayout.y());
            list.setScrollableY(true);
            for(Rute r : rutes) {
                Container c = createListElement(r);
                list.add(c);
            }
            list.addPullToRefresh(() -> DB.getInstance().refresh());
            centerContainer.add(BorderLayout.CENTER, list);
        }
        revalidate();
    }

    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");


    private Container createListElement(Rute rute) {

        Style s = UIManager.getInstance().getComponentStyle("Title");
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

