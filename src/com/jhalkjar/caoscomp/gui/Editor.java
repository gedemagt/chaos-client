package com.jhalkjar.caoscomp.gui;

import com.codename1.components.InfiniteProgress;
import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.database.NoImageException;


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
        try {
            r.getImage(image->{

                canvas = new Canvas(r.getPoints(), edit);
                canvas.setImage(image);
                canvas.addClickListener((x, y) -> {
                    canvas.addPoint(x, y);
                    r.save();
                });
                canvas.addMoveListener(p -> {
                    if(p.getX()<0 || p.getX()>1.0 || p.getY()<0 || p.getY()>1.0) r.getPoints().remove(p);
                    r.save();
                });
                l.setHidden(true);
                add(BorderLayout.CENTER, canvas);
                revalidate();
            });
        } catch (NoImageException e) {
            e.printStackTrace();
        }
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
                r.delete();
                new RuteList().showBack();
            });
            cnt.add(b2);
        }
        cnt.add(multiToggleButton);
        getToolbar().add(BorderLayout.EAST, cnt);

    }

}
