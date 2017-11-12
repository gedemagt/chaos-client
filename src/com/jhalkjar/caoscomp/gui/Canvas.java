package com.jhalkjar.caoscomp.gui;

import com.codename1.components.ImageViewer;
import com.codename1.io.Log;
import com.codename1.ui.Font;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jesper on 11/5/17.
 */
public class Canvas extends ImageViewer {
    private List<Point> points = new ArrayList<>();
    private boolean wasDragged = false;
    private Point selected = null;

    int size = 10;

    public Canvas(List<Point> points) {
        this.points = points;
    }

    public void addPoint(float _x, float _y){
        points.add(new Point(_x, _y));
    }

    public boolean wasDragged() {return this.wasDragged;}

    @Override
    public void pointerDragged(int x, int y) {
        if(selected == null){
            super.pointerDragged(x,y);
            wasDragged = true;
        }
        else {
            selected.setPixel(x, y, this);
        }
    }

    @Override
    public void pointerPressed(int x, int y) {


        for(int i=0; i<points.size(); i++) {
            int xdiff = x - getAbsoluteX() - points.get(i).getXPixel(this);
            int ydiff = y - getAbsoluteY() - points.get(i).getYPixel(this);
            if((xdiff*xdiff + ydiff*ydiff) < size*size) {
                selected = points.get(i);
                break;
            }
        }
        if(selected == null) super.pointerPressed(x,y);
    }

    @Override
    public int getDragRegionStatus(int x, int y) {
        if(selected != null) {
            return DRAG_REGION_IMMEDIATELY_DRAG_XY;
        }
        else{
            return super.getDragRegionStatus(x,y);
        }

    }

    @Override
    public void pointerReleased(int x, int y) {
        if(selected == null) {
            super.pointerReleased(x,y);
        }
        else {
            selected = null;
        }
        wasDragged = false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setAntiAliased(true);

        Font f = g.getFont();
        size = (int) (f.getHeight()*1.5 * getZoom());
        for(int i=0; i<points.size(); i++) {
            Point p = points.get(i);

            g.setColor(0xFFFFFF);
            g.setAlpha(100);
            g.fillArc(p.getXPixel(this) - size/2, p.getYPixel(this) - size/2, size, size, 0, 360);

            g.setAlpha(255);
            g.setColor(0xFF3333);
            g.drawArc(p.getXPixel(this) - size/2, p.getYPixel(this) - size/2, size, size, 0, 360);
            g.drawArc(p.getXPixel(this) - size/2+1, p.getYPixel(this) - size/2+1, size-2, size-2, 0, 360);

            g.setColor(0x7caeff);

//            String label = Integer.toString(i);

            //g.drawString(label, p.getXPixel(this) - f.stringWidth(label)/2, p.getYPixel(this) - f.getHeight()/2);
        }

    }

}