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
    public float getX() {return x;}
    public float getY() {return y;}

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "[" + x + "," + y + "]";
    }

}
