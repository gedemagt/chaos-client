package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.Component;
import com.codename1.ui.Graphics;
import com.codename1.ui.geom.Dimension;

/**
 * Created by jesper on 2/7/18.
 */
public class ColoredSquare extends Component {

    private int color;
    private int size;

    public ColoredSquare(int color, int size) {
        this.color = color;
        this.size = size;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillRect(0, 0, size, size);
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    protected Dimension calcPreferredSize() {
        return new Dimension(size, size);
    }
}
