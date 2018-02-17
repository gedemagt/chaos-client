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
    private Type type;

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.size = p.size;
        this.selected = false;
        this.type = p.type;
    }

    public Point(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = Type.NORMAL;
    }
    public float getX() {return x;}
    public float getY() {return y;}
    public float getSize() {return size;}

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setType(Type t) {
        this.type = t;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getJSON() {
        return "{\"x\":" + x + "f, \"y\": " + y + "f, \"size\": " + size + "f, \"type\": \"" + type.name() + "\"}";
    }

    public String toString() {
        return "Point<[" + x + "," + y + "], " + size + "," + type + ">";
    }

    public void render(Graphics g, int x, int y, int size) {
        if(size < 2) return;
        if(selected) {
            g.setColor(0x2BCD31);
        }
        else {
            g.setColor(0xFFFFFF);
        }
        g.setAlpha(70);
        g.fillArc(x - size/2, y - size/2, size, size, 0, 360);

        g.setAlpha(255);
        if(type == Type.NORMAL) g.setColor(0x0000ff);
        if(type == Type.START) g.setColor(0x006400);
        if(type == Type.END) g.setColor(0xFF0000);


        g.drawArc(x - size/2, y - size/2, size, size, 0, 360);
        g.drawArc(x - size/2+1, y - size/2+1, size-2, size-2, 0, 360);


    }

    public Type getType() {
        return type;
    }
}
