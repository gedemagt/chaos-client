package com.jhalkjar.caoscomp.gui.rutelist;

/**
 * Created by jesper on 11/5/17.
 */

import com.codename1.ui.*;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;

import com.jhalkjar.caoscomp.backend.*;
import com.jhalkjar.caoscomp.database.DB;
import com.jhalkjar.caoscomp.gui.*;
import com.jhalkjar.caoscomp.gui.competition.CompetitionList;
import com.jhalkjar.caoscomp.gui.misc.ToolbarSpacer;
import com.jhalkjar.caoscomp.gui.misc.WaitingDialog;

import java.util.Collections;
import java.util.List;


/**
 * Created by jesper on 2/8/17.
 */
public abstract class RuteList extends Form {

    Container centerContainer = new Container(new BorderLayout());

    public RuteList() {
        super(new BorderLayout());


//        DB.getInstance().addGymsSyncListener(new DB.RefreshListener() {
//            @Override
//            public void OnBeginRefresh() {
//
//            }
//
//            @Override
//            public void OnEndRefresh() {
//                populateToolbar();
//            }
//
//            @Override
//            public void OnRefreshError() {
//
//            }
//        });

        add(BorderLayout.CENTER, centerContainer);

    }


    void forceAndShow() {
        Dialog d = new WaitingDialog("Loading rutes");

        DB.getInstance().forceWebRefresh(new DB.RefreshListener() {
            @Override
            public void OnBeginRefresh() {

            }

            @Override
            public void OnEndRefresh() {
                d.dispose();
            }

            @Override
            public void OnRefreshError() {
                d.dispose();
            }
        });
        d.show();
    }


    protected void updateUI() {
        centerContainer.removeAll();
        List<Rute> rutes = getRutes().getAllRutes();
        if(rutes.size() == 0) {
            Label l = new Label("Got a problem?");
            centerContainer.add(BorderLayout.CENTER, l);
        }
        else {
            Container list = new Container(BoxLayout.y());
            list.setScrollableY(true);

            Collections.sort(rutes, (o1, o2) -> {
                long v1 = o1.getDate().getTime();
                long v2 = o2.getDate().getTime();
                if(v1==v2) return 0;
                else if(v1>v2) return -1;
                else return 1;
            });

            for(Rute r : rutes) {
                Container c = createElement(r);
                list.add(c);
            }

            list.addPullToRefresh(()  -> {
                onPull();
            });
            centerContainer.add(BorderLayout.CENTER, list);
        }
        revalidate();
    }

    protected abstract Container createElement(Rute r);

    protected abstract RuteCollection getRutes();
    protected void onPull() {}

    public List<Rute> getSelectedRutes() {
        return getRutes().getAllRutes();
    }

}

