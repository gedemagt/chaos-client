package com.jhalkjar.caoscomp.gui;

/**
 * Created by jesper on 11/5/17.
 */

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

    List<Rute> rutes;

    public RuteList() {
        super(new BorderLayout());

        refreshList();
        updateUI();
        Style s = UIManager.getInstance().getComponentStyle("Title");
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_ADD, s), (e) -> {
            new RuteCreator().show();
        });

    }

    private void refreshList() {
        rutes = DB.getInstance().getRutes(false);
        updateUI();

    }

    private void updateUI() {

        removeAll();
        if(rutes.size() == 0) {
            Label l = new Label("Please add a rute!");
            add(BorderLayout.CENTER, l);
        }
        else {
            Container list = new Container(BoxLayout.y());
            list.addPullToRefresh(() -> {
                DB.getInstance().refresh();
                refreshList();
                updateUI();
            });
            list.setScrollableY(true);
            for(Rute r : rutes) {
                Container c = createListElement(r);
                list.add(c);
            }
            add(BorderLayout.CENTER, list);
        }
        revalidate();
    }

    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");


    private Container createListElement(Rute rute) {

        Style s = UIManager.getInstance().getComponentStyle("Title");
        Label name = new Label(rute.getName());
        Label author = new Label(rute.getAuthor().getName());
        Label gym = new Label(rute.getGym().getName());
        Label date = new Label(dateFormat.format(rute.getDate()));

        TableLayout layout = new TableLayout(3, 3);
        Container cnt = new Container(layout);
        Button delete = new Button(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));
        delete.addActionListener(evt -> {
            rute.delete();
            refreshList();
        });
        Button download = new Button(FontImage.createMaterial(FontImage.MATERIAL_FILE_DOWNLOAD, s));
        download.addActionListener(evt -> {
            DB.getInstance().download(rute);
            refreshList();
        });
        cnt.add(layout.createConstraint().widthPercentage(50).horizontalAlign(Component.LEFT), name);
        cnt.add(layout.createConstraint().widthPercentage(50).horizontalAlign(Component.LEFT), date);

        cnt.add(layout.createConstraint().widthPercentage(50).horizontalAlign(Component.LEFT), author);
        cnt.add(layout.createConstraint().widthPercentage(50).horizontalAlign(Component.LEFT), gym);
        if(rute.isLocal()) {
            cnt.add(layout.createConstraint().widthPercentage(50).horizontalAlign(Component.LEFT), delete);
        }
        else {
            cnt.add(layout.createConstraint().widthPercentage(50).horizontalAlign(Component.LEFT), download);
        }

        name.addPointerReleasedListener(evt -> new Editor(rute).show());

        return cnt;
    }

}

