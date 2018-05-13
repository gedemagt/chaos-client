package com.jhalkjar.caoscomp.database.RuteProvider;

import com.jhalkjar.caoscomp.backend.RuteCollection;

public interface RuteProvider {

    RuteCollection getRutes();

    void attach(RuteProviderListener listener);
    void detach(RuteProviderListener listener);

    interface RuteProviderListener {
        void onUpdatedRutes(RuteCollection rc);
    }
}

