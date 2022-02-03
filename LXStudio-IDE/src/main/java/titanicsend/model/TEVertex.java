package titanicsend.model;

import heronarts.lx.model.LXPoint;
import java.util.Set;

public class TEVertex extends LXPoint {
  public Set<TEEdgeModel> edges;

  public TEVertex(LXPoint point) {
    super(point);
  }

  public double distanceTo(TEVertex other) {
    float dx = this.x - other.x;
    float dy = this.y - other.y;
    float dz = this.z - other.z;
    return Math.sqrt(dx*dx + dy*dy + dz*dz);
  }

  public void addEdge(TEEdgeModel edge) {
    edges.add(edge);
  }
}