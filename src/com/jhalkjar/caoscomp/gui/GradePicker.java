package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.jhalkjar.caoscomp.backend.Grade;

/**
 * Created by jesper on 2/7/18.
 */
public class GradePicker {

    Style s = Display.getInstance().getCurrent().getTitleStyle();

    Grade picked = Grade.NO_GRADE;
    ButtonGroup gp = new ButtonGroup();

    public GradePicker() {
    }

    private Button createButton(Grade g) {

        RadioButton b;
        if(g != Grade.NO_GRADE) b = new RadioButton(Image.createImage(50,50, Grade.getColorInt(g)));
        else b = new RadioButton(FontImage.createMaterial(FontImage.MATERIAL_BLOCK, s));
        b.getAllStyles().setBorder(Border.createEmpty());
        b.addActionListener(evt -> picked = g);
        gp.add(b);
        return b;
    }

    public Grade getGrade() {
        Dialog dlg = new Dialog("Pick grade!");
        dlg.setLayout(new BorderLayout());

        Container cnt = new Container(new GridLayout(4,3));
        cnt.add(createButton(Grade.GREEN));
        cnt.add(createButton(Grade.YELLOW));
        cnt.add(createButton(Grade.BLUE));
        cnt.add(createButton(Grade.PURPLE));
        cnt.add(createButton(Grade.BLACK));
        cnt.add(createButton(Grade.GRAY));
        cnt.add(createButton(Grade.NO_GRADE));

        dlg.add(BorderLayout.CENTER, cnt);


        Button ok = new Button("OK");
        ok.addActionListener(evt -> dlg.dispose());
        dlg.add(BorderLayout.SOUTH, ok);
        dlg.show();

        return picked;
    }

}
