package com.jhalkjar.caoscomp.backend;

import com.jhalkjar.caoscomp.database.DatabaseEntry;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Competition extends DatabaseEntry {

    String getName();
    Date getStart();
    Date getStop();
    int getType();
    List<User> getAdmins();
    List<Rute> getRutes();
    int getPin();
    void save();

    Status getStatus(User u, Rute r);
    void setStatus(User u, Rute r, Status s);

    void setName(String name);
    void setStart(Date start);
    void setStop(Date stop);
    void setType(int type);
    void addRute(Rute r);
    void addAdmin(User r);
    void removeAdmin(User r);

    Map<Rute, List<Competition.Status>> getStats();


    public class Status {
        public final Rute rute;
        public final User user;
        public final int tries;
        public final boolean completed;

        public Status(int tries, boolean completed, Rute r, User u) {
            this.tries = tries;
            this.completed = completed;
            this.rute = r;
            this.user = u;
        }

    }

}
