package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.Graphics;

/**
 * Created by jesper on 11/5/17.
 */
public class Point {

    private float size;
    private float x;
    private float y;
    private boolean selected;

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.size = p.size;
        this.selected = false;
    }

    public Point(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }
    public float getX() {return x;}
    public float getY() {return y;}
    public float getSize() {return size;}

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getJSON() {
        return "{\"x\":" + x + "f, \"y\": " + y + "f, \"size\": " + size + "f, \"type\": 1" + "}";
    }

    public String toString() {
        return "Point<[" + x + "," + y + "], " + size + ">";
    }

    public void render(Graphics g, int x, int y, int size) {
        g.setColor(0xFFFFFF);
        g.setAlpha(100);
        g.fillArc(x - size/2, y - size/2, size, size, 0, 360);

        g.setAlpha(255);
        if(selected) {
            g.setColor(0x2BCD31);
        }
        else {
            g.setColor(0xFF3333);
        }

        g.drawArc(x - size/2, y - size/2, size, size, 0, 360);
        g.drawArc(x - size/2+1, y - size/2+1, size-2, size-2, 0, 360);

    }

}
