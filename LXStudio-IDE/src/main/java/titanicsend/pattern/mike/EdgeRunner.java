package titanicsend.pattern.mike;

import java.util.*;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;

import titanicsend.model.*;
import titanicsend.pattern.TEPattern;

@LXCategory("Testahedron")
public class EdgeRunner extends TEPattern {
  private HashMap<TEEdgeModel, Integer> edgeLastVisit;
  private HashMap<LXPoint, Integer> pointLastVisit;
  private TEEdgeModel currentEdge;
  private int moveNumber;
  private int currentPoint;
  private boolean fwd;
  private double accumulatedMsec;
  private static final double MOVE_PERIOD_MSEC = 2.5;

  public EdgeRunner(LX lx) {
    super(lx);
    this.edgeLastVisit = new HashMap<TEEdgeModel, Integer>();
    this.pointLastVisit = new HashMap<LXPoint, Integer>();
    this.currentEdge = model.edgesById.values().iterator().next(); // Get any value
    this.currentPoint = 0;
    this.fwd = true;
    this.moveNumber = 0;
  }

  // Select the edge least-recently visited (unless a subclass overrides this)
  public TEEdgeModel selectEdge(Set<TEEdgeModel> choices) {
    int oldestMove = this.moveNumber;
    TEEdgeModel winner = null;

    assert choices.size() > 0;
    for(TEEdgeModel e : choices) {
      int lastVisit = this.edgeLastVisit.getOrDefault(e, -1);
      if (lastVisit < oldestMove) {
        oldestMove = lastVisit;
        winner = e;
      }
    }

    assert winner != null;
    return winner;
  }

  // This is a hook for subclasses to do something with the current point
  // in between moves. By default, we just note when it was last visited.
  public void mark() {
    LXPoint currentPoint = this.currentEdge.points[this.currentPoint];
    assert currentPoint != null;
    this.pointLastVisit.put(currentPoint, this.moveNumber);
  }

  // Move along the current edge until we reach the end. Use selectEdge()
  // to pick a new one at that point; unless overridden, it picks the
  // path least recently visited.
  public void move() {
    this.edgeLastVisit.put(this.currentEdge, ++this.moveNumber);
    TEVertex reachedVertex = null;

    if (this.fwd) {
      if (++this.currentPoint >= currentEdge.points.length) {
        reachedVertex = currentEdge.v1;
      }
    } else {
      if (--this.currentPoint < 0) {
        reachedVertex = currentEdge.v0;
      }
    }

    // We're still in the middle of an Edge
    if (reachedVertex == null) return;

    // We've reached a Vertex
    Set<TEEdgeModel> connectedEdges = reachedVertex.edges;

    TEEdgeModel newEdge = selectEdge(connectedEdges);
    this.currentEdge = newEdge;
    if (newEdge.v0 == reachedVertex) {
      this.fwd = true;
      this.currentPoint = 0;
    } else {
      this.fwd = false;
      this.currentPoint = newEdge.points.length - 1;
    }
  }

  public void run(double deltaMs) {
    this.accumulatedMsec += deltaMs;
    while (this.accumulatedMsec >= MOVE_PERIOD_MSEC) {
      this.accumulatedMsec -= MOVE_PERIOD_MSEC;
      this.mark();
      this.move();
    }

    for (LXPoint point : model.points) {
      int lastVisit = this.pointLastVisit.getOrDefault(point, -1);
      int color;
      if (lastVisit == -1) {
        color = LXColor.rgb(50, 50, 50);
      } else {
        int age = this.moveNumber - lastVisit;
        if (age <= 3) color = LXColor.WHITE;
        else if (age <= 10) color = LXColor.rgb(0, 180, 255);
        else if (age <= 50) color = LXColor.rgb(0, 0, 255);
        else color = LXColor.rgb(0, 0, 100);
      }
      colors[point.index] = color;
    }
  }
}
