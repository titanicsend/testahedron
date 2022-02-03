package titanicsend.model;

import java.util.ArrayList;
import heronarts.lx.model.LXPoint;

public class TEPanelFactory {
  public static TEPanelModel build(TEVertex v0, TEVertex v1, TEVertex v2,
                            TEEdgeModel e0, TEEdgeModel e1, TEEdgeModel e2) {
    ArrayList<LXPoint> points = new ArrayList<LXPoint>();

    double centroidX = (v0.x + v1.x + v2.x) / 3.0;
    double centroidY = (v0.y + v1.y + v2.y) / 3.0;
    double centroidZ = (v0.z + v1.z + v2.z) / 3.0;

    // FIXME: Actually add all the points, not just the centroid
    LXPoint p = new LXPoint(centroidX, centroidY, centroidZ);
    points.add(p);

    return new TEPanelModel(points, v0, v1, v2, e0, e1, e2);
  }
}