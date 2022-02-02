package titanicsend.model;

import heronarts.lx.model.LXPoint;

public class Util {
    static double distance(LXPoint p1, LXPoint p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2) + Math.pow(p2.z - p1.z, 2));
    }
}
