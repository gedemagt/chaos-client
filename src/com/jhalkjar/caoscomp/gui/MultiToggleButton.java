package com.jhalkjar.caoscomp.gui;

import com.codename1.ui.Button;
import com.codename1.ui.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jesper on 2/11/17.
 */
public class MultiToggleButton<State> extends Button {

    List<State> states = new ArrayList<State>();
    List<State> disabledStates = new ArrayList<State>();
    int current = 0;
    Map<State, Image> icons = new HashMap<State, Image>();


    public MultiToggleButton() {
        this.addActionListener(evt -> toggle());
    }

    public void addState(State state, Image icon) {
        states.add(state);
        if(icon != null) {
            icons.put(state, icon);
        }
    }

    public void toggle() {
        if (!isEnabled()) return;
        do {
            setCurrentState(current + 1);
        } while (disabledStates.contains(states.get(current)));
    }

    public void setCurrentState(State s) {
        setCurrentState(states.indexOf(s));
    }

    public void setCurrentState(int i) {
        current = i % states.size();
        Image fi = icons.get(states.get(current));
        if(fi == null) this.setText(states.get(current).toString().substring(0, 2));
        else this.setIcon(fi);
    }


    public State getCurrentState() {
        return states.get(current);
    }

    public void disableState(State s) {
        if(!disabledStates.contains(s)) disabledStates.add(s);
    }

    public void enableState(State s) {
        disabledStates.remove(s);
    }

}
