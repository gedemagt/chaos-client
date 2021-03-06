package com.jhalkjar.caoscomp.gui;

import com.codename1.components.ImageViewer;

/**
 * Created by jesper on 11/5/17.
 */
public class Canvas extends ImageViewer {

    private boolean wasMultiDragged = false;
    private boolean immedateDrag = false;
    private boolean diablePointerDrag = false;

    @Override
    public void pointerPressed(int[] x, int[] y) {
        wasMultiDragged = x.length > 1;
        super.pointerPressed(x,y);
    }

    @Override
    public void pointerDragged(int[] x, int[] y) {
        wasMultiDragged = x.length > 1;
        if(!diablePointerDrag) super.pointerDragged(x,y);
    }

    void setImmediatelyDrag(boolean b) {
        immedateDrag = b;
    }

    void disablePointerDrag() {diablePointerDrag = true;}

    public boolean wasMultiDragged() {
        return wasMultiDragged;
    }

    @Override
    public int getDragRegionStatus(int x, int y) {
        if(immedateDrag) {
            return DRAG_REGION_IMMEDIATELY_DRAG_XY;
        }
        else{
            return super.getDragRegionStatus(x,y);
        }
    }

    @Override
    public void pointerReleased(int x, int y) {
        wasMultiDragged = false;
        diablePointerDrag = false;
        super.pointerReleased(x,y);
    }

    @Override
    public void pointerReleased(int[] x, int[] y) {
        wasMultiDragged = false;
        diablePointerDrag = false;
        super.pointerReleased(x,y);
    }

}