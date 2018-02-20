package com.jhalkjar.caoscomp.gui;

import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.util.WeakHashMap;

import java.io.IOException;

/**
 * Created by jesper on 2/20/18.
 */
public class WaitingDialog extends Dialog {

    private String text;
    private Container cnt = new Container();
    private Image image;
    private int angle = 0;
    private int tick, lastTextTick = 0;
    private int tickCount = 5;
    private int angleIncrease = 16;
    private WeakHashMap<Integer, Image> cache = new WeakHashMap<>();
    private Label l = new Label();

    public WaitingDialog(String text) {
        super(new BorderLayout());
        l.setText(text+"         ");
        this.text = text;
        try {
            image  =Image.createImage("/climbing-shoes.png").scaled(80,80);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cnt = new Container() {

            @Override
            public void paint(Graphics g) {

                if (this.getComponentForm() != null && Display.getInstance().getCurrent() != this.getComponentForm()) {
                    return;
                }
                super.paint(g);
                if(image == null) {
                    return;
                }
                int v = angle % 360;
                Style s = getStyle();
                Image rotated;

                angle += angleIncrease;
                Integer angle = new Integer(v);
                rotated = cache.get(angle);
                if(rotated == null) {
                    rotated = image.rotate(v);
                    cache.put(v, rotated);
                }

                g.drawImage(rotated, getX() + s.getPaddingLeftNoRTL(), getY() + s.getPaddingTop());
            }

            @Override
            protected Dimension calcPreferredSize() {
                return new Dimension(image.getWidth(), image.getHeight());
            }

            @Override
            public boolean animate() {
                if (Display.getInstance().getCurrent() != this.getComponentForm()) {
                    return false;
                }
                // reduce repaint thrushing of the UI from the infinite progress
                boolean val = super.animate() || tick % tickCount == 0;
                tick++;
                if(angle - lastTextTick > 90) {
                    lastTextTick = angle;
                    String suffix = ".";
                    for(int i=0; i<(angle%360)/120; i++) {
                        suffix +=".";
                    }
                    l.setText(text + suffix);
                    l.repaint();
                }
                return val;
            }

            /**
             * {@inheritDoc}
             */
            protected void initComponent() {
                super.initComponent();
                Form f = getComponentForm();
                if(f != null) {
                    f.registerAnimated(this);
                }
            }

            /**
             * {@inheritDoc}
             */
            protected void deinitialize() {
                super.deinitialize();
                Form f = getComponentForm();
                if(f == null) {
                    f = Display.getInstance().getCurrent();
                }
                f.deregisterAnimated(this);
            }

        };
        Container cn2 = BorderLayout.centerEastWest(l, null, cnt);
        cn2.setUIID("WaitingDialog");
        add(BorderLayout.CENTER, cn2);
    }

}
