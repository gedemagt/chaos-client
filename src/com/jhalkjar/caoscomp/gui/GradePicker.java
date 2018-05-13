package com.jhalkjar.caoscomp.gui;

import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.Style;
import com.jhalkjar.caoscomp.backend.Grade;
import com.jhalkjar.caoscomp.gui.misc.CustomCheckButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jesper on 2/7/18.
 */
public class GradePicker {

    Style s = Display.getInstance().getCurrent().getTitleStyle();

    Grade picked = null;
    ArrayList<Grade> multiPicked = new ArrayList<>();

    private CustomCheckButton createMultiButton(Grade g) {
        CustomCheckButton b;
        if(g != Grade.NO_GRADE) b = new CustomCheckButton(Image.createImage(50,50,Grade.getColorInt(g)));
        else b = new CustomCheckButton(FontImage.createMaterial(FontImage.MATERIAL_BLOCK, s));
        b.addActionListener(evt -> {
            if(b.isSelected()) multiPicked.add(g);
            else multiPicked.remove(g);

            picked = g;
        });
        if(picked != null) b.setSelected(g == picked);
        else if (multiPicked.contains(g)) b.setSelected(true);
        return b;
    }


    public Grade getGrade(Grade g) {
        picked = g;

        Dialog dlg = new Dialog("Pick grade");
        dlg.setLayout(new BorderLayout());
        List<CustomCheckButton> customCheckButtonList = new ArrayList<>();

        Container cnt = new Container(new GridLayout(4,3));
        customCheckButtonList.add(createMultiButton(Grade.GREEN));
        customCheckButtonList.add(createMultiButton(Grade.YELLOW));
        customCheckButtonList.add(createMultiButton(Grade.BLUE));
        customCheckButtonList.add(createMultiButton(Grade.PURPLE));
        customCheckButtonList.add(createMultiButton(Grade.RED));
        customCheckButtonList.add(createMultiButton(Grade.BLACK));
        customCheckButtonList.add(createMultiButton(Grade.GRAY));

        for(CustomCheckButton cb : customCheckButtonList) {
            cb.addActionListener(evt -> {
                cb.setSelected(true);
                for (CustomCheckButton cbb: customCheckButtonList) {
                    if(cbb != cb) cbb.setSelected(false);
                }
            });
            cnt.add(cb);
        }


        dlg.add(BorderLayout.CENTER, cnt);


        Button ok = new Button("OK");
        ok.addActionListener(evt -> {
            for(CustomCheckButton cb : customCheckButtonList) {
                cnt.removeComponent(cb);
            }
            dlg.dispose();
        });
        dlg.add(BorderLayout.SOUTH, ok);
        dlg.show();

        Grade toReturn = picked;
        picked = null;

        return toReturn;
    }

    public ArrayList<Grade> getMultipleGrades(ArrayList<Grade> grades) {
        multiPicked = grades;
        Dialog dlg = new Dialog("Pick grades");
        dlg.setLayout(new BorderLayout());

        Container cnt = new Container(new GridLayout(4,3));
        cnt.add(createMultiButton(Grade.GREEN));
        cnt.add(createMultiButton(Grade.YELLOW));
        cnt.add(createMultiButton(Grade.BLUE));
        cnt.add(createMultiButton(Grade.PURPLE));
        cnt.add(createMultiButton(Grade.RED));
        cnt.add(createMultiButton(Grade.BLACK));
        cnt.add(createMultiButton(Grade.GRAY));


        dlg.add(BorderLayout.CENTER, cnt);


        Button ok = new Button("OK");
        ok.addActionListener(evt -> dlg.dispose());
        dlg.add(BorderLayout.SOUTH, ok);
        dlg.show();
        Log.p(multiPicked + "");

        return multiPicked;}
}
