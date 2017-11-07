package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.Font;
import com.codename1.ui.Graphics;

/**
 * Created by jesper on 11/5/17.
 */
public class Hold {

    private int x,y;
    String label;
    int size = 30;

    public Hold(String label, int x, int y) {
        this.label = label;
        this.x = x;
        this.y = y;
    }

    public void paint(Graphics g) {
        Font f = g.getFont();
        g.drawArc(x - size/2, y - size/2, size, size, 0, 360);
        g.drawString(label, x-f.stringWidth(label)/2, y-f.getHeight()/2);
    }

}
