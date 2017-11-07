package com.jhalkjar.caoscomp.gui;

/**
 * Created by jesper on 11/5/17.
 */

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;

import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.database.RuteDatabase;

import java.util.List;


/**
 * Created by jesper on 2/8/17.
 */
public class RuteList extends Form {


    public RuteList() {
        super(new BorderLayout());

        updateUI();
        Style s = UIManager.getInstance().getComponentStyle("Title");
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_ADD, s), (e) -> {
            new RuteCreator().show();
        });

    }

    private void updateUI() {
        RuteDatabase db = new RuteDatabase();
        List<Rute> rutes = db.loadRutes();
        removeAll();
        if(rutes.size() == 0) {
            Label l = new Label("Please add a rute!");
            add(BorderLayout.CENTER, l);
        }
        else {
            Container list = new Container(BoxLayout.y());
            list.setScrollableY(true);
            for(Rute r : rutes) {
                Container c = createListElement(r);
                list.add(c);
            }
            add(BorderLayout.CENTER, list);
        }
        revalidate();
    }


    private Container createListElement(Rute rute) {

        Style s = UIManager.getInstance().getComponentStyle("Title");
        Label name = new Label(rute.getName());

        TableLayout layout = new TableLayout(1, 2);
        Container cnt = new Container(layout);
        Button delete = new Button(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));
        delete.addActionListener(evt -> {
            rute.delete();
            updateUI();
        });
        cnt.add(layout.createConstraint().widthPercentage(80).horizontalAlign(Component.LEFT), name);
        cnt.add(layout.createConstraint().widthPercentage(20).horizontalAlign(Component.RIGHT), delete);

        name.addPointerReleasedListener(evt -> new Editor(rute).show());

        return cnt;
    }

}

