package com.jhalkjar.caoscomp.gui.misc;

import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.Style;

public class ToolbarSpacer extends Label {

    public ToolbarSpacer() {
        super(" ");
        Style separatorStyle = getAllStyles();
        separatorStyle.setBgImage(Image.createImage(30, 2, 0x7f000000));
        separatorStyle.setBackgroundType(Style.BACKGROUND_IMAGE_TILE_HORIZONTAL_ALIGN_CENTER);
        separatorStyle.setMargin(0, 0, 0, 0);
        separatorStyle.setPadding(0, 0, 0, 0);
    }

}
