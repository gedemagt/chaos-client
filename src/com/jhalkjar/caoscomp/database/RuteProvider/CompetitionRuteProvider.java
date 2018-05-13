package com.jhalkjar.caoscomp.database.RuteProvider;

import com.jhalkjar.caoscomp.backend.Competition;
import com.jhalkjar.caoscomp.backend.RuteCollection;
import com.jhalkjar.caoscomp.database.DB;

import java.util.ArrayList;
import java.util.List;

public class CompetitionRuteProvider implements RuteProvider{

    private List<RuteProviderListener> listeners = new ArrayList();

    private Competition comp;

    public CompetitionRuteProvider(Competition comp) {
        this.comp = comp;
    }

    @Override
    public RuteCollection getRutes() {
        return new RuteCollection(comp.getRutes());
    }

    @Override
    public void attach(RuteProviderListener listener) {
        listeners.add(listener);
    }

    @Override
    public void detach(RuteProviderListener listener) {
        listeners.remove(listener);
    }
}
