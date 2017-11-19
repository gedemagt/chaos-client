package com.jhalkjar.caoscomp.gui;

import com.codename1.components.ImageViewer;
import com.codename1.io.Log;
import com.codename1.ui.Font;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.plaf.Style;

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
    private List<MovedListener> movedListeners = new ArrayList<>();
    Style s = getStyle();

    float r2 = 0;
    int iW, iH;

    public Canvas(List<Point> points, boolean edit) {
        this.edit = edit;
        this.points = points;
    }

    public void doSetImage(Image image) {


        super.setImage(image);
    }

    @Override
    public void pointerDragged(int x, int y) {
        if(selected == null){
            super.pointerDragged(x,y);
        }
        else {
            if(edit) selected.set(xPixelToFloat(x -  getAbsoluteX()), yPixelToFloat(y -  getAbsoluteY()));
        }
        wasDragged = true;
    }

    @Override
    public void pointerPressed(int x, int y) {
        for(int i=0; i<points.size(); i++) {
            int xdiff = x - xFloatToPixel(points.get(i).getX()) -  getAbsoluteX();
            int ydiff = y - yFloatToPixel(points.get(i).getY()) - getAbsoluteY();
            if((xdiff*xdiff + ydiff*ydiff) < wFloatToPixel(points.get(i).getSize()) * wFloatToPixel(points.get(i).getSize())) {
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

    int xFloatToPixel(float x) {
        int imageDrawWidth = (int)(((float)iW) * r2 * getZoom());
        return (int) (x * ((float) (imageDrawWidth)) + getImageX());
    }

    int yFloatToPixel(float y) {
        int imageDrawHeight = (int)(((float)iH) * r2 * getZoom());
        return (int) (y * ((float) (imageDrawHeight)) + getImageY());
    }

    float xPixelToFloat(int x) {
        int imageDrawWidth = (int)(((float)iW) * r2 * getZoom());
        return (x - getImageX()) / (float) (imageDrawWidth);
    }

    int wFloatToPixel(float x) {
        int imageDrawWidth = (int)(((float)iW) * r2 * getZoom());
        return (int) ((x) * (float) (imageDrawWidth));
    }

    float yPixelToFloat(int y) {
        int imageDrawHeight = (int)(((float)iH) * r2 * getZoom());
        return (y - getImageY()) / (float) (imageDrawHeight);
    }

    @Override
    public void pointerReleased(int x, int y) {
        if(selected == null) {
            super.pointerReleased(x,y);
            if(edit && !wasDragged) for(ClickListener cl : clickListeners) cl.OnClick(xPixelToFloat(x-getAbsoluteX()), yPixelToFloat(y - getAbsoluteY()));
        }
        else {
            if(edit) for(MovedListener l : movedListeners) l.OnMove(selected);
            selected = null;
        }
        wasDragged = false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setAntiAliased(true);


        if(r2 == 0) {
            int width = getWidth() - s.getHorizontalPadding();
            int height = getHeight() - s.getVerticalPadding();
            iW = getImage().getWidth();
            iH = getImage().getHeight();

            r2 = Math.min(((float)width) / ((float)iW), ((float)height) / ((float)iH));
        }


        for(int i=0; i<points.size(); i++) {
            Point p = points.get(i);
            p.render(g, xFloatToPixel(p.getX()), yFloatToPixel(p.getY()) + getY(), wFloatToPixel(p.getSize()));
        }

    }

    public void addClickListener(ClickListener l) {
        clickListeners.add(l);
    }

    public void addSelectionListener(SelectionListener l) {
        selectionListeners.add(l);
    }

    public void addMoveListener(MovedListener l) {
        movedListeners.add(l);
    }

    public interface ClickListener {
        void OnClick(float x, float y);
    }

    public interface SelectionListener {
        void OnSelect(Point p);
    }

    public interface MovedListener {
        void OnMove(Point p);
    }

}