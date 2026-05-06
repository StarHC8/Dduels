package org.starhc.dduels.models;

public class Spawn {
    private double x;
    private double y;
    private double z;
    private float yaw;

    public Spawn(double x, double y, double  z, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = (float) yaw;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }
}
