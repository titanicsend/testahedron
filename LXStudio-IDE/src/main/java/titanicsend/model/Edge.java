package titanicsend.model;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;

public class Edge extends LXModel {
    public Edge(LXPoint p1, LXPoint p2, double spacing) {
        super(makePoints(p1, p2, spacing));
    }

    public static List<LXPoint> makePoints(LXPoint p1, LXPoint p2, double pixelSpacing) {
        ArrayList<LXPoint> points = new ArrayList<LXPoint>();
        int index = 0;
        Spacing spacing = new Spacing(p1, p2, pixelSpacing);
        LXPoint currentPoint = new LXPoint(p1);

        while (currentPoint.x >= Math.min(p1.x, p2.x) && currentPoint.x <= Math.max(p1.x, p2.x) &&
                currentPoint.y >= Math.min(p1.y, p2.y) && currentPoint.y <= Math.max(p1.y, p2.y) &&
                currentPoint.z >= Math.min(p1.z, p2.z) && currentPoint.z <= Math.max(p1.z, p2.z)) {
            points.add(currentPoint);
            index += 1;

            currentPoint = new LXPoint(currentPoint);
            currentPoint.setX(currentPoint.x + (float) spacing.xSpacing);
            currentPoint.setY(currentPoint.y + (float) spacing.ySpacing);
            currentPoint.setZ(currentPoint.z + (float) spacing.zSpacing);
            currentPoint.index = index;
        }

        return points;
    }

    public static class Spacing {
        public double xSpacing, ySpacing, zSpacing;
        public Spacing(LXPoint p1, LXPoint p2, double spacing) {
            double distance = Util.distance(p1, p2);
            xSpacing = (p2.x - p1.x) / distance;
            ySpacing = (p2.y - p1.y) / distance;
            zSpacing = (p2.z - p1.z) / distance;
        }
    }
}
