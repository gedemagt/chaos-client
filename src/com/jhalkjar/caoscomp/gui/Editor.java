package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.LayeredLayout;
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
    boolean edit, wasDragged, notSlider;

    Axis axis;

    Component editorBar;
    Slider sl;
    Button delete = new Button(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));
    Point selected = null;
    private boolean sliderPressed;

    public Editor(Rute rute) {
        super(new BorderLayout());

        r = rute;
        edit = r.getAuthor().equals(DB.getInstance().getLoggedInUser());
        isLocal = r.isLocal();

        canvas = new Canvas();
        axis = new Axis(canvas);

        populateToolbar();
        add(BorderLayout.NORTH, l);

        if(edit) {
            editorBar = createEditorBar();
            add(isVertical() ? BorderLayout.WEST : BorderLayout.NORTH, editorBar);
            addOrientationListener(evt -> {
                removeComponent(sl.getParent());
                removeComponent(sl);
                removeComponent(delete);
                removeComponent(editorBar);
                editorBar = createEditorBar();
                add(isVertical() ? BorderLayout.WEST : BorderLayout.NORTH, editorBar);
                axis.updateSize();
                revalidate();
            });
        }
        l.setHidden(false);
        try {
            r.getImage(image->{

                canvas.setImage(image);
                l.setHidden(true);
                add(BorderLayout.CENTER, canvas);
                revalidate();
                axis.updateSize();
                setGlassPane((g, rect) -> {
                    g.setAntiAliased(true);
                    for(int i=0; i<r.getPoints().size(); i++) {
                        Point p = r.getPoints().get(i);
                        int xPix = axis.xFloatToPixel(p.getX());
                        int yPix = axis.yFloatToPixel(p.getY());
                        if(getContentPane().contains(xPix, yPix)) p.render(g, xPix, yPix, axis.wFloatToPixel(p.getSize()));
                    }
                });
            });
        } catch (NoImageException e) {
            e.printStackTrace();
        }


        addPointerPressedListener(evt -> {
            if(selected != null && !isInDeleteRegion(evt))  {
                selected.setSelected(false);
                selected = null;
            }
            float x = axis.xPixelToFloat(evt.getX());
            float y = axis.yPixelToFloat(evt.getY());
            for(int i=0; i<r.getPoints().size(); i++) {
                float size = r.getPoints().get(i).getSize()/2.f;

                if(Math.abs(x - r.getPoints().get(i).getX()) < size && Math.abs(y - r.getPoints().get(i).getY())<size){
                    selected = r.getPoints().get(i);
                    selected.setSelected(true);
                    canvas.setImmediatelyDrag(true);
                    sl.setProgress((int) (selected.getSize() * 100.0f));
                    notSlider = true;
                    break;
                }
            }
        });

        addPointerDraggedListener(evt -> {
            if(sliderPressed) return;
            if(!wasDragged) {
                wasDragged = true;
                showSlider(selected == null);
            }

            if(selected != null && notSlider) {

                float x = axis.xPixelToFloat(evt.getX());
                float y = axis.yPixelToFloat(evt.getY());
                selected.set(x,y);
            }
        });

        addPointerReleasedListener(evt -> {
            if(selected != null && wasDragged && isInDeleteRegion(evt)) {
                r.getPoints().remove(selected);
                r.save();
            }
            else if(selected == null && !isInDeleteRegion(evt) && !wasDragged) {
                float x = axis.xPixelToFloat(evt.getX());
                float y = axis.yPixelToFloat(evt.getY());
                Point p = new Point(x,y, (float) sl.getProgress() / 100.0f);
                r.getPoints().add(p);
                if(selected != null) selected.setSelected(false);
                selected = p;
                p.setSelected(true);
                r.save();
            }
            if(wasDragged) r.save();
            canvas.setImmediatelyDrag(false);
            showSlider(true);
            wasDragged = false;
            notSlider = true;
            sliderPressed = false;
        });
    }

    boolean isInDeleteRegion(ActionEvent evt) {
        return sl.getSelectedRect().contains(evt.getX(),evt.getY());
    }

    private boolean isVertical() {
        return Display.getInstance().getDisplayWidth() > Display.getInstance().getDisplayHeight();

    }

    Component createEditorBar() {

        sl = createSlider(isVertical());
        sl.setMinValue(2);
        sl.setMaxValue(50);
        sl.setProgress(10);
        sl.setEditable(true);
        sl.addDataChangedListener((k,u) -> {
            notSlider = false;
            if(selected != null) {
                selected.setSize((float) u / 100.0f);
            }
        });
        sl.addPointerPressedListener(evt -> sliderPressed = true);

        Container c = new Container(new BorderLayout());
        c.add(BorderLayout.CENTER, LayeredLayout.encloseIn(sl, delete));
        c.setLeadComponent(sl);
        showSlider(true);
        return c;
    }

    void showSlider(boolean b) {
        delete.setVisible(!b);
        sl.setVisible(b);
        revalidate();
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


    private Slider createSlider(boolean vertical) {
        Slider slider = new Slider();
        slider.setEditable(true);
        slider.setMinValue(2);
        slider.setMaxValue(50);
        slider.setEditable(true);
        String uuid = "Slider";
        if(vertical) uuid += "V";
        slider.setUIID(uuid);
        slider.setVertical(vertical);
        return slider;
    }

}
