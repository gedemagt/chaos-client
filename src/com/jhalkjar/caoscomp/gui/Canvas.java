package com.jhalkjar.caoscomp.gui;

import com.codename1.components.ImageViewer;
import com.codename1.ui.Font;
import com.codename1.ui.Graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jesper on 11/5/17.
 */
public class Canvas extends ImageViewer {
    private List<Point> points = new ArrayList<>();
    private Point selected = null;
    boolean edit, wasDragged;
    private List<ClickListener> clickListeners = new ArrayList<>();
    private List<SelectionListener> selectionListeners = new ArrayList<>();
    private List<DeleteListener> deleteListeners = new ArrayList<>();

    int size = 10;

    public Canvas(List<Point> points, boolean edit) {
        this.edit = edit;
        this.points = points;
    }

    public void addPoint(float _x, float _y){
        points.add(new Point(_x, _y));
    }

    @Override
    public void pointerDragged(int x, int y) {
        if(selected == null){
            super.pointerDragged(x,y);
        }
        else {
            if(edit) selected.setPixel(x, y, this);
        }
        wasDragged = true;
    }

    @Override
    public void pointerPressed(int x, int y) {

        for(int i=0; i<points.size(); i++) {
            int xdiff = x - getAbsoluteX() - points.get(i).getXPixel(this);
            int ydiff = y - getAbsoluteY() - points.get(i).getYPixel(this);
            if((xdiff*xdiff + ydiff*ydiff) < size*size) {
                selected = points.get(i);
                if(edit) for(SelectionListener l : selectionListeners) l.OnSelect(selected);
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

            float xdiff = ((x - getImageX() - getAbsoluteX())/getZoom()) / (float) (getWidth());
            float ydiff = ((y - getImageY() - getAbsoluteY())/getZoom()) / (float) (getHeight());

            if(edit && !wasDragged) for(ClickListener cl : clickListeners) cl.OnClick(xdiff, ydiff);
        }
        else {
            if(y<getImageY() + getAbsoluteY()|| y>(getHeight()-getImageY() + getAbsoluteY())) {
                points.remove(selected);
                for(DeleteListener dl: deleteListeners) dl.OnDelete(selected);
            }
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

            if(p.getYPixel(this)<getImageY()|| p.getYPixel(this)>(getHeight()-getImageY() ))
                g.setColor(0xc09f2d);
            else
                g.setColor(0xFF3333);
            g.drawArc(p.getXPixel(this) - size/2, p.getYPixel(this) - size/2, size, size, 0, 360);
            g.drawArc(p.getXPixel(this) - size/2+1, p.getYPixel(this) - size/2+1, size-2, size-2, 0, 360);

            g.setColor(0x7caeff);
        }

    }

    public void addClickListener(ClickListener l) {
        clickListeners.add(l);
    }

    public void addSelectionListener(SelectionListener l) {
        selectionListeners.add(l);
    }

    public void addDeleteListener(DeleteListener l) {
        deleteListeners.add(l);
    }

    public interface ClickListener {
        void OnClick(float x, float y);
    }

    public interface SelectionListener {
        void OnSelect(Point p);
    }

    public interface DeleteListener {
        void OnDelete(Point p);
    }

}