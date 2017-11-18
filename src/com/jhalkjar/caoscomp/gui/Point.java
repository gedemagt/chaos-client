package com.jhalkjar.caoscomp.gui;

/**
 * Created by jesper on 11/5/17.
 */
public class Point {

    private float x;
    private float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setPixel(int x, int y, Canvas c) {
        this.x = ((x - c.getImageX() - c.getAbsoluteX())/c.getZoom()) / (float) (c.getWidth());
        this.y = ((y - c.getImageY() - c.getAbsoluteY())/c.getZoom()) / (float) (c.getHeight());
    }

    public int getXPixel(Canvas c) {return (int) (x * c.getZoom() * ((float) (c.getWidth())) + c.getImageX());}
    public int getYPixel(Canvas c) {return (int) (y * c.getZoom() * ((float) (c.getHeight())) + c.getImageY());}

    public float getX() {return x;}
    public float getY() {return y;}

    public String toString() {
        return "[" + x + "," + y + "]";
    }

}
