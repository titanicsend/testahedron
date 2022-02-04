package titanicsend.model;

import heronarts.lx.model.LXPoint;
import java.util.*;

public class TEVertex extends LXPoint {
  public static HashMap<Integer, TEVertex> vertexesById;

  public int id;
  public int numConnectedEdges;  // -1 if we don't know
  public Set<TEEdgeModel> edges;

  public TEVertex(LXPoint point, int id, int numConnectedEdges) {
    super(point);
    this.id = id;
    this.numConnectedEdges = numConnectedEdges;
    this.edges = new HashSet<TEEdgeModel>();
  }

  public TEVertex(LXPoint point, int id) {
    this(point, id, -1);
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