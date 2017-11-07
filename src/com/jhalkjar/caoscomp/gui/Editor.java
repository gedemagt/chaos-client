package com.jhalkjar.caoscomp.gui;

import com.codename1.io.Log;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.backend.DBRute;
import com.jhalkjar.caoscomp.database.RuteDatabase;

/**
 * Created by jesper on 11/5/17.
 */
public class Editor extends Form {

    RuteDatabase db = new RuteDatabase();
    Style s = UIManager.getInstance().getComponentStyle("Title");
    Canvas canvas;

    public Editor(DBRute r) {
        super(new BorderLayout());
        Log.p("Loading '" + r.getName() + "' with image url '" + r.getImageUrl() + "'!");

        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_UNDO, s), (e) -> {
            if(r.getPoints().size() > 0) r.getPoints().remove(r.getPoints().size() - 1);
            canvas.repaint();
            db.saveRute(r);
        });

        getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            new RuteList().showBack();
        });

        canvas = new Canvas(r.getImage(), r.getPoints());

        canvas.addPointerDraggedListener(evt -> {
            Log.p("Dragged");
        });

        canvas.addPointerReleasedListener(evt -> {
            if(canvas.wasDragged()) return;

            float xdiff = ((evt.getX() - canvas.getImageX() - canvas.getAbsoluteX())/canvas.getZoom());
            float ydiff = ((evt.getY() - canvas.getImageY() - canvas.getAbsoluteY())/canvas.getZoom());
            canvas.addPoint(xdiff, ydiff);
            canvas.repaint();
            db.saveRute(r);
        });

        add(BorderLayout.CENTER, canvas);
    }

}
