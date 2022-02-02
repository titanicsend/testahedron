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
        for (LXPoint[] edge : edges) {
            Spacing delta = new Spacing(edge[0], edge[1], spacing);
            LXPoint currentPoint = new LXPoint(edge[0]);

            int xDirection = edge[1].x > edge[0].x ? 1 : -1;
            int yDirection = edge[1].y > edge[0].y ? 1 : -1;
            int zDirection = edge[1].z > edge[0].z ? 1 : -1;

            while (currentPoint.x >= Math.min(edge[0].x, edge[1].x) && currentPoint.x <= Math.max(edge[0].x, edge[1].x) &&
                    currentPoint.y >= Math.min(edge[0].y, edge[1].y) && currentPoint.y <= Math.max(edge[0].y, edge[1].y) &&
                    currentPoint.z >= Math.min(edge[0].z, edge[1].z) && currentPoint.z <= Math.max(edge[0].z, edge[1].z)) {
                points.add(currentPoint);
                currentPoint = new LXPoint(currentPoint);
                currentPoint.setX(currentPoint.x + (float) (xDirection * delta.xSpacing));
                currentPoint.setY(currentPoint.y + (float) (yDirection * delta.ySpacing));
                currentPoint.setZ(currentPoint.z + (float) (zDirection * delta.zSpacing));
            }
        }
        return points;
    }

    public static class Spacing {
        public double xSpacing, ySpacing, zSpacing;
        public Spacing(LXPoint p1, LXPoint p2, double spacing) {
            double distance = Util.distance(p1, p2);
            xSpacing = Math.abs((p2.x - p1.x) / distance);
            ySpacing = Math.abs((p2.y - p1.y) / distance);
            zSpacing = Math.abs((p2.z - p1.z) / distance);
        }
    }

}
