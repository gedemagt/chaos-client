package com.jhalkjar.caoscomp.gui;

import com.codename1.components.ImageViewer;
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

    int size = 10;

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
            if(edit) selected.set(xPixelToFloat(x), yPixelToFloat(y));
        }
        wasDragged = true;
    }

    @Override
    public void pointerPressed(int x, int y) {
        for(int i=0; i<points.size(); i++) {
            int xdiff = x - getAbsoluteX() - xFloatToPixel(points.get(i).getX());
            int ydiff = y - getAbsoluteY() - yFloatToPixel(points.get(i).getY());
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
        return (x - getImageX() - getAbsoluteX()) / (float) (imageDrawWidth);
    }

    float yPixelToFloat(int y) {
        int imageDrawHeight = (int)(((float)iH) * r2 * getZoom());
        return (y - getImageY() - getAbsoluteY()) / (float) (imageDrawHeight);
    }

    @Override
    public void pointerReleased(int x, int y) {
        if(selected == null) {
            super.pointerReleased(x,y);
            if(edit && !wasDragged) for(ClickListener cl : clickListeners) cl.OnClick(xPixelToFloat(x), yPixelToFloat(y));
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
            Style s = getStyle();
            int width = getWidth() - s.getHorizontalPadding();
            int height = getHeight() - s.getVerticalPadding();
            iW = getImage().getWidth();
            iH = getImage().getHeight();

            r2 = Math.min(((float)width) / ((float)iW), ((float)height) / ((float)iH));
        }

        Font f = g.getFont();
        size = (int) (f.getHeight()*1.5 * getZoom());
        for(int i=0; i<points.size(); i++) {
            Point p = points.get(i);

            g.setColor(0xFFFFFF);
            g.setAlpha(100);
            int x = xFloatToPixel(p.getX());
            int y = yFloatToPixel(p.getY());
            g.fillArc(x - size/2, y - size/2, size, size, 0, 360);

            g.setAlpha(255);

            g.setColor(0xFF3333);
            g.drawArc(x - size/2, y - size/2, size, size, 0, 360);
            g.drawArc(x - size/2+1, y - size/2+1, size-2, size-2, 0, 360);

            g.setColor(0x7caeff);

            if(i>0) {
                g.setColor(0xFF3333);
                Point pBefore = points.get(i-1);
                int xDir = x - xFloatToPixel(pBefore.getX());
                int yDir = y - yFloatToPixel(pBefore.getY());
                double xDiff = (double) xDir / Math.sqrt((double) xDir*xDir + yDir*yDir);
                double yDiff = (double) yDir / Math.sqrt((double) xDir*xDir + yDir*yDir);
                int xx = (int) Math.floor(size/2 * xDiff);
                int yy = (int) Math.floor(size/2 * yDiff);
                g.drawLine(x-xx,y-yy, xFloatToPixel(pBefore.getX()) + xx, yFloatToPixel(pBefore.getY()) + yy);
            }

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