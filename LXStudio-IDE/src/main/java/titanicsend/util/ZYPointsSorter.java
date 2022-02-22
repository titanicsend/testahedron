package titanicsend.util;

import heronarts.lx.model.LXPoint;

import java.util.Comparator;

// ZYPointsSorter is used to sort LXPoints first by z value and then by y value.
public class ZYPointsSorter implements Comparator<LXPoint> {
    public int compare(LXPoint a, LXPoint b) {
        if (a.z != b.z) {
            return (int)(a.z - b.z);
        } else {
            return (int)(a.y - b.y);
        }
    }
}