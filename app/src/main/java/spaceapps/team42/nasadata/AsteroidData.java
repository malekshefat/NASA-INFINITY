package spaceapps.team42.nasadata;

public class AsteroidData {

    private String name;
    private double max_diam, min_diam, h, rel_vel, distance;
    private boolean dangerous;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMax_diam() {
        return max_diam;
    }

    public void setMax_diam(double max_diam) {
        this.max_diam = max_diam;
    }

    public double getMin_diam() {
        return min_diam;
    }

    public void setMin_diam(double min_diam) {
        this.min_diam = min_diam;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getRel_vel() {
        return rel_vel;
    }

    public void setRel_vel(double rel_vel) {
        this.rel_vel = rel_vel;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isDangerous() {
        return dangerous;
    }

    public void setDangerous(boolean dangerous) {
        this.dangerous = dangerous;
    }
}
