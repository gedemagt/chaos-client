package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.plaf.Style;

/**
 * Created by jesper on 11/28/17.
 */
public class Axis {

    private Canvas canvas;
    private float r2 = 0;
    private int iW, iH;

    public Axis(Canvas c) {
        this.canvas = c;
        updateSize();

    }

    public void updateSize() {
        if(canvas.getImage() == null) return;
        Style s = canvas.getStyle();
        int width = canvas.getWidth() - s.getHorizontalPadding();
        int height = canvas.getHeight() - s.getVerticalPadding();
        iW = canvas.getImage().getWidth();
        iH = canvas.getImage().getHeight();

        if(true){
            r2 = Math.min(((float)width) / ((float)iW), ((float)height) / ((float)iH));
        }else{
            r2 = Math.max(((float)width) / ((float)iW), ((float)height) / ((float)iH));
        }
    }

    public int xFloatToPixel(float x) {
        int imageDrawWidth = (int)(((float)iW) * r2 * canvas.getZoom());
        return (int) (x * ((float) (imageDrawWidth))) + canvas.getImageX() + canvas.getAbsoluteX();
    }

    public int yFloatToPixel(float y) {
        int imageDrawHeight = (int)(((float)iH) * r2 * canvas.getZoom());
        return (int) (y * ((float) (imageDrawHeight))) + canvas.getImageY() + canvas.getAbsoluteY();
    }

    public float xPixelToFloat(int x) {
        int imageDrawWidth = (int)(((float)iW) * r2 * canvas.getZoom());
        return (x - canvas.getImageX() - canvas.getAbsoluteX()) / (float) (imageDrawWidth);
    }

    public int wFloatToPixel(float x) {
        int imageDrawWidth = (int)(((float)iW) * r2 * canvas.getZoom());
        return (int) ((x) * (float) (imageDrawWidth));
    }

    public float yPixelToFloat(int y) {
        int imageDrawHeight = (int)(((float)iH) * r2 * canvas.getZoom());
        return (y - canvas.getImageY() - canvas.getAbsoluteY()) / (float) (imageDrawHeight);
    }


}
