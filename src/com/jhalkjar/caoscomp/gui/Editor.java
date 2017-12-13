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

    private Style s = UIManager.getInstance().getComponentStyle("Title");
    private Style s2 = UIManager.getInstance().getComponentStyle("Label");
    private Canvas canvas;
    private Label l = new Label("Retrieving image..");
    private Rute r;
    private boolean edit, isLocal;

    private Axis axis;
    private State state = new IdleState();

    private Component cnt;
    private Component editorBar = createEditorComponent();
    private Button delete = new Button(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));

    public Editor(Rute rute) {
        super(new BorderLayout());

        r = rute;
        edit = r.getAuthor().equals(DB.getInstance().getLoggedInUser());
        isLocal = r.isLocal();

        canvas = new Canvas();
        axis = new Axis(canvas);

        delete.setVisible(false);
        editorBar.setVisible(false);

        populateToolbar();
        add(BorderLayout.NORTH, l);
        if(edit) {
            cnt = LayeredLayout.encloseIn(delete, editorBar);
            add(isVertical() ? BorderLayout.EAST : BorderLayout.SOUTH, cnt);
            addOrientationListener(evt -> {
                removeComponent(cnt);
                removeComponent(delete);
                removeComponent(editorBar);
                add(isVertical() ? BorderLayout.EAST : BorderLayout.SOUTH, cnt);

                revalidate();
                axis.updateSize();
                repaint();
            });

            addPointerPressedListener(evt -> state = state.onPress(evt));
            canvas.addPointerLongPressListener(evt -> state = state.onPress(evt));
            addPointerDraggedListener(evt -> state = state.onDrag(evt));
            addPointerReleasedListener(evt -> state = state.onRelease(evt));
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
                    g.clipRect(canvas.getX(), canvas.getY(), canvas.getWidth(), canvas.getHeight());
                });
            });
        } catch (NoImageException e) {
            e.printStackTrace();
        }

    }

    boolean isInDeleteRegion(ActionEvent evt) {
        return delete.getSelectedRect().contains(evt.getX(),evt.getY());
    }

    private boolean isVertical() {
        return Display.getInstance().getDisplayWidth() > Display.getInstance().getDisplayHeight();

    }

    Component createEditorComponent() {
        Button unzoom = new Button(FontImage.createMaterial(FontImage.MATERIAL_ZOOM_OUT, s));
        unzoom.addActionListener(evt -> {
            canvas.setZoom(1.0f);
            revalidate();
        });
        unzoom.setVisible(false);
        Button increase = new Button(FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_UP, s));
        increase.addActionListener(evt -> {
            state.selected.setSize(state.selected.getSize() + 0.05f);
            r.save();
            revalidate();
        });
        Button decrease = new Button(FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, s));
        decrease.addActionListener(evt -> {
            state.selected.setSize(state.selected.getSize() - 0.05f);
            r.save();
            revalidate();
        });

        RadioButton start = new RadioButton(FontImage.createMaterial(FontImage.MATERIAL_ADJUST, s));
        start.addActionListener(evt -> {state.selected.setType(Type.START); canvas.repaint();});
        RadioButton normal = new RadioButton(FontImage.createMaterial(FontImage.MATERIAL_PANORAMA_FISH_EYE, s));
        normal.addActionListener(evt -> {state.selected.setType(Type.NORMAL); canvas.repaint();});
        RadioButton end = new RadioButton(FontImage.createMaterial(FontImage.MATERIAL_BRIGHTNESS_1, s));
        end.addActionListener(evt -> {state.selected.setType(Type.END); canvas.repaint();});

        addPointerReleasedListener(evt -> {
            if(state.selected != null) {
                if(state.selected.getType() == Type.NORMAL) normal.setSelected(true);
                if(state.selected.getType() == Type.END) end.setSelected(true);
                if(state.selected.getType() == Type.START) start.setSelected(true);
            }
        });

        start.setToggle(true);
        normal.setToggle(true);
        end.setToggle(true);

        new ButtonGroup(start, normal, end);

        return BorderLayout.centerCenterEastWest(null, BoxLayout.encloseX(decrease, increase), BoxLayout.encloseX(start, normal, end));
    }


    private Point select(float x, float y) {
        for(int i=0; i<r.getPoints().size(); i++) {
            float size = r.getPoints().get(i).getSize()/2.f;

            if(Math.abs(x - r.getPoints().get(i).getX()) < size && Math.abs(y - r.getPoints().get(i).getY())<size){
                Point selected = r.getPoints().get(i);
                selected.setSelected(true);
                return selected;
            }
        }
        return null;
    }


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

    private abstract class State {

        protected Point selected;

        protected State(Point sel) {
            selected = sel;
        }

        abstract State onPress(ActionEvent evt);
        abstract State onDrag(ActionEvent evt);
        abstract State onRelease(ActionEvent evt);

    }

    private class IdleState extends State {

        protected IdleState(Point p) {
            super(p);
        }

        public IdleState() {
            super(new Point(0,0,0));
        }

        @Override
        public State onPress(ActionEvent evt) {
            if(canvas.wasMultiDragged()) return this;
            float x = axis.xPixelToFloat(evt.getX());
            float y = axis.yPixelToFloat(evt.getY());

            if(canvas.getSelectedRect().contains(evt.getX(),evt.getY())) {

                Point newSelected = select(x, y);
                if(selected != null) selected.setSelected(false);
                if(newSelected == null) return new CreateState(selected).onPress(evt);

                canvas.setImmediatelyDrag(true);
                newSelected.setSelected(true);
                if(evt.isLongEvent()) return new ScaleState(selected).onPress(evt);
                if(newSelected.equals(selected)) return new MoveState(selected).onPress(evt);
                selected = newSelected;
            }

            return this;
        }

        @Override
        public State onDrag(ActionEvent evt) {
            return this;
        }

        @Override
        public State onRelease(ActionEvent evt) {
            canvas.setImmediatelyDrag(false);
            editorBar.setVisible(selected != null);
            revalidate();
            return this;
        }
    }

    private class ScaleState extends State {
        protected  ScaleState(Point p) {super(p); initialSize = p.getSize();}

        private float initialX, initialSize;

        @Override
        State onPress(ActionEvent evt) {

            initialX = axis.xPixelToFloat(evt.getX());
            return this;
        }

        @Override
        State onDrag(ActionEvent evt) {

            float x = axis.xPixelToFloat(evt.getX()) - initialX;
            selected.setSize(x + initialSize);
            repaint();
            return this;
        }

        @Override
        State onRelease(ActionEvent evt) {
            return new IdleState(selected).onRelease(evt);
        }


    }

    private class MoveState extends State {

        protected MoveState(Point p) {
            super(p);
        }

        @Override
        public State onPress(ActionEvent evt) {
            delete.setVisible(true);
            editorBar.setVisible(false);
            revalidate();
            canvas.disablePointerDrag();
            return this;
        }

        @Override
        public State onDrag(ActionEvent evt) {

            float x = axis.xPixelToFloat(evt.getX());
            float y = axis.yPixelToFloat(evt.getY());
            selected.set(x, y);

            return this;
        }

        @Override
        public State onRelease(ActionEvent evt) {

            if(isInDeleteRegion(evt)) r.getPoints().remove(selected);
            else {
                float x = axis.xPixelToFloat(evt.getX());
                float y = axis.yPixelToFloat(evt.getY());
                selected.set(x, y);
            }
            r.save();
            delete.setVisible(false);
            editorBar.setVisible(true);
            revalidate();
            return new IdleState(selected).onRelease(evt);
        }
    }

    private class CreateState extends State {

        protected CreateState(Point p) {super(p);}

        @Override
        public State onPress(ActionEvent evt) {

            return this;
        }

        @Override
        public State onDrag(ActionEvent evt) {
            return new IdleState().onRelease(evt);
        }

        @Override
        public State onRelease(ActionEvent evt) {
            float x = axis.xPixelToFloat(evt.getX());
            float y = axis.yPixelToFloat(evt.getY());
            float size = selected == null ? 0.1f : selected.getSize();
            Type type = selected == null ? Type.NORMAL : selected.getType();
            selected = new Point(x,y, size);
            selected.setType(type);
            selected.setSelected(true);
            r.getPoints().add(selected);
            r.save();
            return new IdleState(selected).onRelease(evt);
        }
    }

}
