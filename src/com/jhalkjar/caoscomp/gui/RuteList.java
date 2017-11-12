package com.jhalkjar.caoscomp.gui;

/**
 * Created by jesper on 11/5/17.
 */

import com.codename1.components.InfiniteProgress;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;

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

        Style s = UIManager.getInstance().getComponentStyle("Title");
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_ADD, s), (e) -> {
            new RuteCreator().show();
        });
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_REFRESH, s), (e) -> {
            DB.getInstance().refresh();
            refreshList();
        });

    }

    private void refreshList() {
        rutes = DB.getInstance().getRutes();
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

        Container cnt = new Container(new BorderLayout());
//        Button delete = new Button(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));
//        delete.addActionListener(evt -> {
//            rute.delete();
//            refreshList();
//        });
//        Button download = new Button(FontImage.createMaterial(FontImage.MATERIAL_FILE_DOWNLOAD, s));
//        download.addActionListener(evt -> {
//            Dialog ip = new InfiniteProgress().showInifiniteBlocking();
//            DB.getInstance().download(rute, () -> {
//                refreshList();
//                ip.dispose();
//            });
//        });

        cnt.add(BorderLayout.NORTH, BoxLayout.encloseX(name, date));


        cnt.add(BorderLayout.CENTER, BoxLayout.encloseX(new Label(FontImage.createMaterial(FontImage.MATERIAL_HOME, s)),
                gym,
                new Label(FontImage.createMaterial(FontImage.MATERIAL_PERSON, s)), author));


        name.addPointerReleasedListener(evt -> new Editor(rute).show());

        return cnt;
    }

}

