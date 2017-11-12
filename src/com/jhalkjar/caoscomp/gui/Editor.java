package com.jhalkjar.caoscomp.gui;

import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.backend.Rute;

/**
 * Created by jesper on 11/5/17.
 */
public class Editor extends Form {

    Style s = UIManager.getInstance().getComponentStyle("Title");
    Canvas canvas;
    Label l = new Label("Retrieving image..");

    public Editor(Rute r) {
        super(new BorderLayout());

        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_UNDO, s), (e) -> {
            if(r.getPoints().size() > 0) r.getPoints().remove(r.getPoints().size() - 1);
            canvas.repaint();
            r.save();
        });

        getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            new RuteList().showBack();
        });
        add(BorderLayout.NORTH, l);
        l.setHidden(false);

        canvas = new Canvas(r.getPoints());
        canvas.setHidden(true);
        r.getImage(image->{
            canvas.setImage(image);
            canvas.setHidden(false);
            removeComponent(l);
            repaint();
        });
        canvas.addPointerDraggedListener(evt -> {
            Log.p("Dragged");
        });

        canvas.addPointerReleasedListener(evt -> {
            if(canvas.wasDragged()) return;

            float xdiff = ((evt.getX() - canvas.getImageX() - canvas.getAbsoluteX())/canvas.getZoom());
            float ydiff = ((evt.getY() - canvas.getImageY() - canvas.getAbsoluteY())/canvas.getZoom());
            canvas.addPoint(xdiff, ydiff);
            canvas.repaint();
            r.save();
        });

        add(BorderLayout.CENTER, canvas);
    }


}
