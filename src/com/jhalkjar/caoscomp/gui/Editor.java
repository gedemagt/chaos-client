package com.jhalkjar.caoscomp.gui;

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
    EditorTool tool = new EditorTool() {

    };

    Component editorBar;

    public Editor(Rute rute) {
        super(new BorderLayout());

        r = rute;
        edit = r.getAuthor().equals(DB.getInstance().getLoggedInUser());


        addOrientationListener(evt -> {
            removeComponent(editorBar);
            editorBar = createEditorBar();
            add(isVertical() ? BorderLayout.WEST : BorderLayout.NORTH, editorBar);
            revalidate();
        });

        populateToolbar();
        add(BorderLayout.NORTH, l);
        editorBar = createEditorBar();
        add(isVertical() ? BorderLayout.WEST : BorderLayout.NORTH, editorBar);
        l.setHidden(false);
        try {
            r.getImage(image->{

                canvas = new Canvas(r.getPoints(), edit);
                canvas.doSetImage(image);
                canvas.addClickListener((x, y) -> {
                    tool.OnClicked(x, y);
                    r.save();
                });
                canvas.addMoveListener(p -> {
                    tool.OnMoved(p);
                    r.save();
                });
                canvas.addSelectionListener(p -> tool.OnSelected(p));
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
        CheckBox delete = new CheckBox(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));
        Slider sl = new Slider();
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

        tool = new EditorTool() {

            public void OnClicked(float x, float y) {
                Point p = new Point(x,y, (float) sl.getProgress() / 100.0f);
                r.getPoints().add(p);
                if(selected != null) selected.setSelected(false);
                selected = p;
                p.setSelected(true);

            }

            public void OnSelected(Point p) {
                if(delete.isSelected()) r.getPoints().remove(p);
                else {
                    sl.setProgress((int) (p.getSize()*100.0));
                    if(selected != null) selected.setSelected(false);
                    selected = p;
                    p.setSelected(true);

                }
            }

        };
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

    void populateToolbar() {

        getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            new RuteList().showBack();
        });

//        MultiToggleButton<Boolean> multiToggleButton = new MultiToggleButton<>();
//        multiToggleButton.addState(true, FontImage.createMaterial(FontImage.MATERIAL_STAR, s));
//        multiToggleButton.addState(false, FontImage.createMaterial(FontImage.MATERIAL_STAR_BORDER, s));
//        multiToggleButton.setCurrentState(r.isLocal());
//        multiToggleButton.addActionListener(evt -> r.setLocal(multiToggleButton.getCurrentState()));
//
        Container cnt = new Container(BoxLayout.x());
//        if(edit) {
            Button b2 = new Button(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));
            b2.addActionListener(evt -> {
                r.delete();
                new RuteList().showBack();
            });
            cnt.add(b2);
//        }
//        cnt.add(multiToggleButton);
        getToolbar().add(BorderLayout.EAST, cnt);

    }

}
