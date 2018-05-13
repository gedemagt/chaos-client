package com.jhalkjar.caoscomp.database.RuteProvider;

import com.jhalkjar.caoscomp.backend.RuteCollection;
import com.jhalkjar.caoscomp.database.DB;

import java.util.ArrayList;
import java.util.List;

public class DBRuteProvider implements RuteProvider{

    private List<RuteProvider.RuteProviderListener> listeners = new ArrayList();

    private RuteCollection rc;

    public DBRuteProvider() {
        DB.getInstance().addRefreshListener(new DB.RefreshListener() {
            @Override
            public void OnBeginRefresh() {

            }

            @Override
            public void OnEndRefresh() {
                rc = new RuteCollection(DB.getInstance().getRutes());
                for(RuteProviderListener l : listeners) {
                    l.onUpdatedRutes(rc);
                }
            }

            @Override
            public void OnRefreshError() {

            }
        });
    }

    @Override
    public RuteCollection getRutes() {
        if(rc == null) rc = new RuteCollection(DB.getInstance().getRutes());
        return rc;
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
