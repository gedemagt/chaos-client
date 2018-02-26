package com.jhalkjar.caoscomp.backend;

/**
 * Created by jesper on 11/7/17.
 */
public class Sector{

    private String name;
    private Gym gym;

    public Sector(String name, Gym gym) {
        this.name = name;
        this.gym = gym;
    }
    public Sector(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGym(Gym g) {
        this.gym = g;
    }

    public Gym getGym() {
        return gym;
    }


    @Override
    public String toString() {
        return "Sector<" + name + ">";
    }

    @Override
    public boolean equals(Object u) {
        if(u == null) return false;
        if (!Sector.class.isAssignableFrom(u.getClass())) {
            return false;
        }
        final Sector other = (Sector) u;

        return other.getName().equals(name) && other.getGym().equals(gym);
    }

}
