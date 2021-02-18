package com.punuo.sys.app.agedcare.model;


import androidx.annotation.NonNull;

/**
 * Created by asus on 2017/9/12.
 */

public class Cluster implements Comparable<Cluster> {
    private String name;

    public Cluster() {
    }

    public Cluster(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == Cluster.class) {
            Cluster device = (Cluster) o;
            if (this.getName().equals(device.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull Cluster another)throws ClassCastException, NullPointerException {
        if (another != null) {
            Cluster dev = another;
                return this.name.compareTo(dev.getName());
        } else {
            return 0;
        }
    }
}
