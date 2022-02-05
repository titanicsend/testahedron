package titanicsend.model;

import Jama.Matrix;
import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import titanicsend.app.TEVirtualColor;

import java.util.*;

public class TEVertex extends LXPoint {
  public static HashMap<Integer, TEVertex> vertexesById;

  public int id;
  public int numConnectedEdges;  // -1 if we don't know
  public Set<TEEdgeModel> edges;

  // Set to non-null and the virtual display will shade vertex's sphere
  public TEVirtualColor virtualColor;

  public TEVertex(LXPoint point, int id, int numConnectedEdges) {
    super(point);
    this.id = id;
    this.numConnectedEdges = numConnectedEdges;
    this.edges = new HashSet<TEEdgeModel>();
    this.virtualColor = new TEVirtualColor(255, 255, 255, 255);
  }

  public TEVertex(LXPoint point, int id) {
    this(point, id, -1);
  }

  public static double distance(LXPoint p0, LXPoint p1) {
    float dx = p0.x - p1.x;
    float dy = p0.y - p1.y;
    float dz = p0.z - p1.z;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  public double distanceTo(LXPoint p) {
    return TEVertex.distance(this, p);
  }

  public void addEdge(TEEdgeModel edge) {
    edges.add(edge);
  }
}