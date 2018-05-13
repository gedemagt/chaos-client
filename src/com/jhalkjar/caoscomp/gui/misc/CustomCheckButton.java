package com.jhalkjar.caoscomp.gui.misc;

import com.codename1.ui.*;
import com.codename1.ui.plaf.Border;


/**
 * Created by jesper on 3/9/18.
 */
public class CustomCheckButton extends CheckBox {

    Image selected, unselected;

    public CustomCheckButton(Image image) {
        setUIID("Button");
        setToggle(true);
        selected = image;
        unselected = image.modifyAlpha((byte) 128);
        setIcon(unselected);
        getAllStyles().setBorder(Border.createEmpty());
        addActionListener(evt -> {
            update();
        });
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        update();
    }

    private void update() {
        if(isSelected()) setIcon(selected);
        else setIcon(unselected);
    }


}
