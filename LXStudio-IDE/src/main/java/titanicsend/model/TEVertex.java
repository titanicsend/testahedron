package titanicsend.model;

import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXVector;
import titanicsend.app.TEVirtualColor;

import java.util.*;

public class TEVertex extends LXVector {
  public static HashMap<Integer, TEVertex> vertexesById;

  public int id;
  public int numConnectedEdges;  // -1 if we don't know
  public Set<TEEdgeModel> edges;

  // Set to non-null and the virtual display will shade vertex's sphere
  public TEVirtualColor virtualColor;

  public TEVertex(LXVector vector, int id, int numConnectedEdges) {
    super(vector);
    this.id = id;
    this.numConnectedEdges = numConnectedEdges;
    this.edges = new HashSet<TEEdgeModel>();
    this.virtualColor = new TEVirtualColor(255, 255, 255, 255);
  }

  public TEVertex(LXVector vector, int id) {
    this(vector, id, -1);
  }

  public static double distance(LXVector v, float x, float y, float z) {
    float dx = v.x - x;
    float dy = v.y - y;
    float dz = v.z - z;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }


  public static double distance(LXVector v, LXPoint p) {
    return distance(v, p.x, p.y, p.z);
  }

  public double distanceTo(LXVector v) {
    return distance(this, v.x, v.y, v.z);
  }

  public void addEdge(TEEdgeModel edge) {
    edges.add(edge);
  }

  public void nudgeToward(LXPoint other, float distance) {
    LXVector thisVector = new LXVector(this);
    LXVector otherVector = new LXVector(other);
    LXVector newPosition = thisVector.lerp(otherVector, distance);
    this.x = newPosition.x;
    this.y = newPosition.y;
    this.z = newPosition.z;
  }
}