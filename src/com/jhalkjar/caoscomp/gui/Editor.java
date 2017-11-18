package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.database.DB;


/**
 * Created by jesper on 11/5/17.
 */
public class Editor extends Form {

    Style s = UIManager.getInstance().getComponentStyle("Title");
    Canvas canvas;
    Label l = new Label("Retrieving image..");
    Rute r;
    boolean edit;

    public Editor(Rute rute) {
        super(new BorderLayout());

        r = rute;
        edit = r.getAuthor().equals(DB.getInstance().getLoggedInUser());

        populateToolbar();


        add(BorderLayout.NORTH, l);
        l.setHidden(false);

        r.getImage(image->{

            canvas = new Canvas(r.getPoints(), edit);
            canvas.setImage(image);
            canvas.addClickListener((x, y) -> {
                canvas.addPoint(x, y);
                DB.getInstance().save(r);
            });
            canvas.addSelectionListener(p -> {

            });
            l.setHidden(true);
            add(BorderLayout.CENTER, canvas);
            revalidate();
        });
    }

    void populateToolbar() {

        getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            new RuteList().showBack();
        });

        MultiToggleButton<Boolean> multiToggleButton = new MultiToggleButton<>();
        multiToggleButton.addState(true, FontImage.createMaterial(FontImage.MATERIAL_STAR, s));
        multiToggleButton.addState(false, FontImage.createMaterial(FontImage.MATERIAL_STAR_BORDER, s));
        multiToggleButton.setCurrentState(r.isLocal());
        multiToggleButton.addActionListener(evt -> r.setLocal(multiToggleButton.getCurrentState()));

        Container cnt = new Container(BoxLayout.x());
        if(edit) {
            Button b2 = new Button(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));
            b2.addActionListener(evt -> {
                DB.getInstance().delete(r);
                new RuteList().showBack();
            });
            cnt.add(b2);
        }
        cnt.add(multiToggleButton);
        getToolbar().add(BorderLayout.EAST, cnt);

    }

}
