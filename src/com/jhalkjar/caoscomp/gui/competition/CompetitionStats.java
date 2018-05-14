package com.jhalkjar.caoscomp.gui.competition;

/**
 * Created by jesper on 11/5/17.
 */

import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.jhalkjar.caoscomp.backend.*;

import java.util.*;
import java.util.List;


public class CompetitionStats extends Form {

    Container centerContainer = new Container(new BorderLayout());
    List<Rute> rutes;
    Toolbar tb;

    Competition comp;

    Style s = UIManager.getInstance().getComponentStyle("Label");

    public CompetitionStats(Competition comp, Form back) {
        super(new BorderLayout());
        this.comp = comp;

        tb = getToolbar();

        tb.addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_EQUALIZER, getTitleStyle()), evt -> {
            updateUI();
        });
        tb.addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_HOME, getTitleStyle()), evt -> {
            createScoreBoard(addUp(comp.getStats()));
        });
        tb.addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, getTitleStyle()), evt -> {
            back.showBack();
        });
        tb.setTitle(comp.getName());

        rutes = comp.getRutes();
        add(BorderLayout.CENTER, centerContainer);
        updateUI();
    }

    private void updateUI() {
        centerContainer.removeAll();

        Map<Rute, List<Competition.Status>> stats = comp.getStats();

        Container list = new Container(BoxLayout.y());
        list.setScrollableY(true);

        for(Rute r : rutes) {
            Container c = createListElement(r, stats.getOrDefault(r, new ArrayList<>()));
            list.add(c);
        }
        centerContainer.add(BorderLayout.CENTER, list);
        revalidate();
    }

    private class CummulatedStats {
        public int tries;
        public int completed;
        public User user;
        public CummulatedStats(User user) {
            this.user =user;
            tries = 0;
            completed =0;
        }

        public void add(int tries, boolean completed) {
            this.tries += tries;
            if(completed) this.completed++;
        }

    }

    private List<Competition.Status> addUp(Map<Rute, List<Competition.Status>> all) {
        List<Competition.Status> r = new ArrayList<>();
        for(List<Competition.Status> st : all.values()) {
            r.addAll(st);
        }
        return r;
    }

    private Container createScoreBoard(List<Competition.Status> stats) {
        centerContainer.removeAll();
        Map<User, CummulatedStats> cummulated = new HashMap<>();
        for(Competition.Status stat : stats) {
            if(!cummulated.containsKey(stat.user)) cummulated.put(stat.user, new CummulatedStats(stat.user));
            cummulated.get(stat.user).add(stat.tries, stat.completed);
        }

        List<CummulatedStats> cumstats = new ArrayList<>(cummulated.values());
        Collections.sort(cumstats, (o1, o2) -> {
            if(o1.completed != o2.completed) {
                return o2.completed - o1.completed;
            }
            else{
                return -(o2.tries - o1.tries);
            }
        });

        Container list = new Container(BoxLayout.y());
        list.setScrollableY(true);

        for(CummulatedStats r : cumstats) {
            list.add(new Label(r.user.getName() + " " + r.completed + " " + r.tries));
        }

        centerContainer.add(BorderLayout.CENTER, list);
        revalidate();

        return list;
    }



    private Container createListElement(Rute r, List<Competition.Status> stats) {
        Label name = new Label(r.getName());

        name.setUIID("RuteName");

        int tri = 0;
        int flash = 0;
        int compl = 0;
        int max = 0;

        for(Competition.Status cs : stats) {
            tri++;
            if (cs.completed && cs.tries==1) flash++;
            if (cs.completed) compl++;
        }

        Label flashes = new Label(flash + "");
        Label avg = new Label(max+"");
        flashes.setUIID("DetailListElement");
        avg.setUIID("DetailListElement");

        Label completed = new Label(compl + "/"+tri);
        completed.setUIID("DateListElement");

        TableLayout tbl = new TableLayout(2, 4);
        Container cnt = new Container(tbl);
        cnt.setUIID("ListElement");

        cnt.add(tbl.createConstraint().widthPercentage(60).horizontalSpan(2), name);
        cnt.add(tbl.createConstraint().widthPercentage(40).horizontalSpan(2).horizontalAlign(RIGHT), completed);

        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_FLASH_ON, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), flashes);
        cnt.add(tbl.createConstraint().widthPercentage(10), new Label(FontImage.createMaterial(FontImage.MATERIAL_AUTORENEW, s)));
        cnt.add(tbl.createConstraint().widthPercentage(40), avg);

//        for(int i=0; i<cnt.getComponentCount(); i++) {
//            cnt.getComponentAt(i).addPointerReleasedListener(evt -> new Editor(rute, this).show());
//        }
//        cnt.addPointerReleasedListener(evt -> new Editor(rute, this).show());

        return BoxLayout.encloseY(cnt, new Spacer());
    }

    private class Spacer extends Container {

        public Spacer() {
            setUIID("Spacer");
        }

    }

}

