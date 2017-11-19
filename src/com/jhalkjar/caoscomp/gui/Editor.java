package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
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

    Slider sl = new Slider();
    CheckBox delete = new CheckBox(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));

    Component editorBar;

    public Editor(Rute rute) {
        super(new BorderLayout());

        r = rute;
        edit = r.getAuthor().equals(DB.getInstance().getLoggedInUser());
        isLocal = r.isLocal();

        populateToolbar();
        add(BorderLayout.NORTH, l);

        if(edit) {
            editorBar = createEditorBar();
            add(isVertical() ? BorderLayout.WEST : BorderLayout.NORTH, editorBar);
            addOrientationListener(evt -> {
                removeComponent(editorBar);
                editorBar = createEditorBar();
                add(isVertical() ? BorderLayout.WEST : BorderLayout.NORTH, editorBar);
                revalidate();
            });
        }
        l.setHidden(false);
        try {
            r.getImage(image->{

                canvas = new Canvas(r.getPoints(), edit);
                canvas.doSetImage(image);
                canvas.addClickListener((x, y) -> {
                    Point p = new Point(x,y, (float) sl.getProgress() / 100.0f);
                    r.getPoints().add(p);
                    if(selected != null) selected.setSelected(false);
                    selected = p;
                    p.setSelected(true);
                    r.save();
                });
                canvas.addMoveListener(p -> {
                    r.save();
                });
                canvas.addSelectionListener(p -> {
                    if(delete.isSelected()) r.getPoints().remove(p);
                    else {
                        sl.setProgress((int) (p.getSize()*100.0));
                        if(selected != null) selected.setSelected(false);
                        selected = p;
                        p.setSelected(true);
                    }
                });
                l.setHidden(true);
                add(BorderLayout.CENTER, canvas);
                revalidate();
            });
        } catch (NoImageException e) {
            e.printStackTrace();
        }
    }

    Point selected = null;

    private boolean isVertical() {
        return Display.getInstance().getDisplayWidth() > Display.getInstance().getDisplayHeight();

    }

    Component createEditorBar() {


        sl.setVertical(isVertical());
        sl.setMinValue(2);
        sl.setMaxValue(50);
        sl.setEditable(true);
        sl.setProgress(10);
        sl.addActionListener(evt -> {

            if(selected != null) {
                selected.setSize((float) sl.getProgress() / 100.0f);
                r.save();
                canvas.repaint();
            }

        });

        Container c = new Container(new BorderLayout());
        c.add(BorderLayout.CENTER, sl);
        if(isVertical()) {
            c.add(BorderLayout.SOUTH, delete);
        }
        else {
            c.add(BorderLayout.EAST, delete);
        }
        return c;
    }

    boolean isLocal;

    void populateToolbar() {
        Toolbar tb = new Toolbar();
        setToolbar(tb);

        tb.addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            new RuteList().showBack();
        });

        char image = isLocal ? FontImage.MATERIAL_STAR : FontImage.MATERIAL_STAR_BORDER;
        tb.addCommandToRightBar("", FontImage.createMaterial(image, s), evt -> {

            r.setLocal(!isLocal);
            isLocal = !isLocal;
            populateToolbar();

        });

        if(edit) {
            getToolbar().addCommandToOverflowMenu("Delete", null, evt -> {

                r.delete();
                new RuteList().showBack();
            });
        }
        revalidate();
    }

}
