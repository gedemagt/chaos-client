package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.database.NoImageException;

import java.util.Date;


/**
 * Created by jesper on 11/5/17.
 */
public class Editor extends Form {

    Style s = UIManager.getInstance().getComponentStyle("Title");
    Style s2 = UIManager.getInstance().getComponentStyle("Label");
    Canvas canvas;
    Label l = new Label("Retrieving image..");
    Rute r;
    boolean edit;


    Component editorBar;
    Slider sl;
    CheckBox delete;

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
                canvas.updateSize();
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
                canvas.updateSize();
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

        sl = createStarRankSlider(isVertical());
        delete = new CheckBox(FontImage.createMaterial(FontImage.MATERIAL_REMOVE_CIRCLE_OUTLINE, s2));
        sl.setMinValue(2);
        sl.setMaxValue(50);
        sl.setEditable(true);
        sl.setProgress(10);
        sl.addDataChangedListener((k,u) -> {

            if(selected != null) {
                selected.setSize((float) sl.getProgress() / 100.0f);
                canvas.repaint();
            }
        });
        sl.addActionListener(evt -> {

            if(selected != null) {
                selected.setSize((float) sl.getProgress() / 100.0f);
                r.save();
                canvas.repaint();
            }
        });

        Container c = new Container(new BorderLayout());
        c.add(BorderLayout.CENTER, FlowLayout.encloseCenter(sl));
        if(isVertical()) {
            c.add(BorderLayout.NORTH, delete);
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

        tb.addCommandToOverflowMenu("Copy", FontImage.createMaterial(FontImage.MATERIAL_CONTENT_COPY, s2), (e) -> {
            Dialog d = new Dialog();
            d.setUIID("Form");
            TextField name = new TextField("");
            Button ok = new Button("OK");
            CheckBox cb = new CheckBox("Copy points");
            cb.setSelected(false);
            ok.addActionListener(evt -> {
                Rute newR = DB.getInstance().createRute(name.getText(), null, DB.getInstance().getLoggedInUser(), r.getGym(), new Date(), r.getImageUUID());
                if(cb.isSelected()) {
                    for(Point p : r.getPoints()) newR.getPoints().add(new Point(p));
                    newR.save();
                }
                new Editor(newR).show();
            });
            d.add(BoxLayout.encloseY(new Label("Name"), name, cb, ok));
            d.show();
        });

        if(edit) {
            getToolbar().addCommandToOverflowMenu("Delete", FontImage.createMaterial(FontImage.MATERIAL_DELETE, s2), evt -> {

                r.delete();
                new RuteList().showBack();
            });
        }

        tb.setTitle(r.getName());
        revalidate();
    }


    private Slider createStarRankSlider(boolean vertical) {
        Slider starRank = new Slider();
        starRank.setEditable(true);
        starRank.setMinValue(2);
        starRank.setMaxValue(50);
        starRank.setEditable(true);
        String uuid = "Slider";
        if(vertical) uuid += "V";
        starRank.setUIID(uuid);
        starRank.setVertical(vertical);
//        Style s = new Style(0xffff33, 0, getAllStyles().getFont(), (byte)0);
//        Image fullStar = FontImage.createMaterial(FontImage.MATERIAL_LENS, s).toImage();
//        s.setOpacity(100);
//        s.setFgColor(0);
//        Image emptyStar = FontImage.createMaterial(FontImage.MATERIAL_LENS, s).toImage();
//        initStarRankStyle(starRank.getSliderEmptySelectedStyle(), emptyStar);
//        initStarRankStyle(starRank.getSliderEmptyUnselectedStyle(), emptyStar);
//        initStarRankStyle(starRank.getSliderFullSelectedStyle(), fullStar);
//        initStarRankStyle(starRank.getSliderFullUnselectedStyle(), fullStar);
//        Dimension d;
//        if(vertical) {
//            d = new Dimension(fullStar.getWidth(), fullStar.getHeight() * 5);
//        }
//        else {
//            d = new Dimension(fullStar.getWidth() * 5, fullStar.getHeight());
//        }
//        starRank.setPreferredSize(d);
        return starRank;
    }

}
