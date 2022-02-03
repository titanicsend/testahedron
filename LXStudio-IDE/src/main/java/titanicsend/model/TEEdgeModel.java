package titanicsend.model;

import heronarts.lx.model.LXPoint;
import heronarts.lx.model.LXModel;
import java.util.*;

public class TEEdgeModel extends LXModel {
  public TEVertex v0, v1;
  public int numConnectedPanels; // -1 if we don't know

  // In microns, the same unit x,y,z coordinates use. 25k microns ~= 1 inch
  public static final int DISTANCE_BETWEEN_PIXELS = 25000;

  public boolean touches(TEEdgeModel other) {
    return this.v0.edges.contains(other) || this.v1.edges.contains(other);
  }

  public boolean touches(TEVertex v) {
    return this.v0 == v || this.v1 == v;
  }

  public TEEdgeModel(TEVertex v0, TEVertex v1, int numConnectedPanels) {
    super(makePoints(v0, v1));
    this.v0 = v0;
    this.v1 = v1;
    this.numConnectedPanels = numConnectedPanels;
  }

  public TEEdgeModel(TEVertex v0, TEVertex v1) {
    this(v0, v1, -1);
  }

  private static List<LXPoint> makePoints(TEVertex v0, TEVertex v1) {
    int numPixels = (int)(v0.distanceTo(v1) / DISTANCE_BETWEEN_PIXELS);
    assert numPixels > 0 : "Edge so short it has no pixels";

    float dx = v1.x - v0.x;
    float dy = v1.y - v0.y;
    float dz = v1.z - v0.z;

    List<LXPoint> points = new ArrayList<LXPoint>();
    for (int i = 0; i < numPixels; i++) {
      float fraction = (float)(i) / numPixels;
      LXPoint point = new LXPoint(
              v0.x + dx * fraction,
              v0.y + dy * fraction,
              v0.z + dz * fraction
      );
      points.add(point);
    }
    return points;
  }
}