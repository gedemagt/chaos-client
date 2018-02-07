package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.spinner.Picker;
import com.jhalkjar.caoscomp.backend.Grade;
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

    private boolean edit, isLocal, editMode;

    private Axis axis;
    private State state = new IdleState();

    private Component cnt;
    private Component editorBar;
    private Button delete = new Button(FontImage.createMaterial(FontImage.MATERIAL_DELETE, s));

    private ActionListener pressListener = evt -> state = state.onPress(evt);
    private ActionListener dragListener = evt -> state = state.onDrag(evt);
    private ActionListener releaseListener = evt -> state = state.onRelease(evt);

    TextField title = new TextField();


    public Editor(Rute rute) {
        super(new BorderLayout());

        r = rute;
        for(Point p : r.getPoints()) p.setSelected(false);
        edit = r.getAuthor().equals(DB.getInstance().getLoggedInUser());
        isLocal = r.isLocal();

        canvas = new Canvas();
        axis = new Axis(canvas);


        title.setText(r.getName());
        title.setUIID("Title");
        populateToolbar(edit);
        add(BorderLayout.NORTH, l);
        if(edit) {
            editorBar = createEditorComponent();
            cnt = LayeredLayout.encloseIn(delete, editorBar);

            add(isVertical() ? BorderLayout.EAST : BorderLayout.SOUTH, cnt);
            addOrientationListener(evt -> {
                removeComponent(cnt);
                removeComponent(delete);
                removeComponent(editorBar);
                editorBar = createEditorComponent();
                cnt = LayeredLayout.encloseIn(delete, editorBar);
                add(isVertical() ? BorderLayout.EAST : BorderLayout.SOUTH, cnt);

                revalidate();
                axis.updateSize();
                repaint();
            });

            delete.setVisible(false);
            editorBar.setVisible(true);

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

        axis.updateSize();
        repaint();
        toggleEditMode(false);
    }

    void toggleEditMode(boolean editMode) {
        this.editMode = editMode;
        if(cnt != null) cnt.setHidden(!editMode);
        if(editMode) {
            addPointerPressedListener(pressListener);
            addPointerDraggedListener(dragListener);
            addPointerReleasedListener(releaseListener);
        }
        else {
            removePointerPressedListener(pressListener);
            removePointerDraggedListener(dragListener);
            removePointerReleasedListener(releaseListener);
        }
        revalidate();
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
        start.addActionListener(evt -> {
            state.selected.setType(Type.START);
            canvas.repaint();
            r.save();
        });
        RadioButton normal = new RadioButton(FontImage.createMaterial(FontImage.MATERIAL_PANORAMA_FISH_EYE, s));
        normal.addActionListener(evt -> {
            state.selected.setType(Type.NORMAL);
            canvas.repaint();
            r.save();
        });
        RadioButton end = new RadioButton(FontImage.createMaterial(FontImage.MATERIAL_BRIGHTNESS_1, s));
        end.addActionListener(evt -> {
            state.selected.setType(Type.END);
            canvas.repaint();
            r.save();
        });

        Picker gradePicker = new Picker();
        gradePicker.setType(Display.PICKER_TYPE_STRINGS);
        gradePicker.setStrings(
                Grade.GREEN.name(),
                Grade.YELLOW.name(),
                Grade.BLUE.name(),
                Grade.PURPLE.name(),
                Grade.RED.name(),
                Grade.BLACK.name(),
                Grade.WHITE.name());
        gradePicker.setText(r.getGrade().name());
        gradePicker.addActionListener( evt ->{
            r.setGrade(Grade.valueOf(gradePicker.getSelectedString()));
            r.save();
            populateToolbar(edit);


        });


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
        if(isVertical()) {
            Container c = new Container(new BorderLayout());
            c.add(BorderLayout.SOUTH, BoxLayout.encloseY(increase, decrease));
            c.add(BorderLayout.NORTH, BoxLayout.encloseY(start, normal, end));
            return c;
        }
        else {
            Container c = new Container(new BorderLayout());
            c.add(BorderLayout.EAST, BoxLayout.encloseX(decrease, increase));
            c.add(BorderLayout.WEST, BoxLayout.encloseX(start, normal, end));
            c.add(BorderLayout.CENTER, BoxLayout.encloseY(gradePicker));
            return c;
        }
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


    void populateToolbar(boolean canEdit) {
        tb = getToolbar();
        tb.getStyle().setBorder(Border.createEmpty());
        tb.getStyle().setBgColor(Grade.getColorInt(r.getGrade()));
        tb.addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            new RuteList().showBack();
        });

        if(canEdit) {
            char image = editMode ? FontImage.MATERIAL_VISIBILITY : FontImage.MATERIAL_EDIT;
            tb.addCommandToRightBar("", FontImage.createMaterial(image, s), evt -> {
                toggleEditMode(!editMode);
                populateToolbar(canEdit);
                if(!editMode) for(Point p : r.getPoints()) p.setSelected(false);
            });
        }

        char image = isLocal ? FontImage.MATERIAL_CLOUD : FontImage.MATERIAL_FILE_DOWNLOAD;
        String text = isLocal ? "Remove locally!" : "Download!";
        tb.addCommandToOverflowMenu(text, FontImage.createMaterial(image, s2), evt -> {

            r.setLocal(!isLocal);
            isLocal = !isLocal;

        });

        tb.addCommandToOverflowMenu("Copy", FontImage.createMaterial(FontImage.MATERIAL_CONTENT_COPY, s2), (e) -> {
            Dialog d = new Dialog();
            d.setUIID("Form");
            TextField name = new TextField("");
            Button ok = new Button("OK");
            CheckBox cb = new CheckBox("Copy points");
            cb.setSelected(false);
            ok.addActionListener(evt -> {
                Rute newR = DB.getInstance().createRute(name.getText(), null, DB.getInstance().getLoggedInUser(), r.getGym(), new Date(), r.getImageUUID(), r.getGrade());
                if(cb.isSelected()) {
                    for(Point p : r.getPoints()) newR.getPoints().add(new Point(p));
                    newR.save();
                }
                new Editor(newR).show();
            });
            d.add(BoxLayout.encloseY(new Label("Name"), name, cb, ok));
            d.show();
        });

        title.setEditable(editMode);
        title.addDataChangedListener((type, index) -> {
            r.setName(title.getText());
            r.save();
        });
        tb.setTitleComponent(title);

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
            super(new Point(0,0,0.05f));
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
