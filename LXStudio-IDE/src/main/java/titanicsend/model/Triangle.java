package titanicsend.model;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;

public class Triangle extends LXModel {
    public Triangle(LXPoint p1, LXPoint p2, LXPoint p3, double spacing) {
        super(makePoints(p1, p2, p3, spacing));
    }

    public static List<LXPoint> makePoints(LXPoint p1, LXPoint p2, LXPoint p3, double spacing) {
        LXPoint[][] edges = {{p1, p2}, {p2, p3}, {p1, p3}};
        ArrayList<LXPoint> points = new ArrayList<LXPoint>();
        int index = 0;
        for (LXPoint[] edge : edges) {
            Spacing delta = new Spacing(edge[0], edge[1], spacing);
            LXPoint currentPoint = new LXPoint(edge[0]);

            while (currentPoint.x >= Math.min(edge[0].x, edge[1].x) && currentPoint.x <= Math.max(edge[0].x, edge[1].x) &&
                    currentPoint.y >= Math.min(edge[0].y, edge[1].y) && currentPoint.y <= Math.max(edge[0].y, edge[1].y) &&
                    currentPoint.z >= Math.min(edge[0].z, edge[1].z) && currentPoint.z <= Math.max(edge[0].z, edge[1].z)) {
                points.add(currentPoint);
                index += 1;
                currentPoint = new LXPoint(currentPoint);
                currentPoint.setX(currentPoint.x + (float) delta.xSpacing);
                currentPoint.setY(currentPoint.y + (float) delta.ySpacing);
                currentPoint.setZ(currentPoint.z + (float) delta.zSpacing);
                currentPoint.index = index;
            }
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
