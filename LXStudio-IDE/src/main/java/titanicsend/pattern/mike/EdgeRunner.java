package titanicsend.pattern.mike;

import java.util.*;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;

import titanicsend.app.TEVirtualColor;
import titanicsend.model.*;
import titanicsend.pattern.TEPattern;

@LXCategory("Testahedron")
public class EdgeRunner extends TEPattern {
  // Useful data related to LIT panels
  private class PanelData {
    int numEdgePixels; // Total number of pixels within the Edges of this panel
    int litEdgePixels; // Number that are lit up
    double maxRadius;  // Distance from any vertex to the centroid
    List<PanelPointData> pointData;

    PanelData(int numEdgePixels, double maxRadius) {
      assert numEdgePixels > 0;
      this.numEdgePixels = numEdgePixels;
      this.maxRadius = maxRadius;
      this.litEdgePixels = 0;
      this.pointData = new ArrayList<PanelPointData>();
    }
  }
  // Useful data for points inside LIT panels
  private class PanelPointData {
    LXPoint point;
    double radius;         // Distance from the centroid
    double radiusFraction; // ...expressed as a fraction <= 1.0

    PanelPointData(LXPoint point, double radius, double radiusFraction) {
      this.point = point;
      this.radius = radius;
      this.radiusFraction = radiusFraction;
    }
  }
  private HashMap<TEEdgeModel, Integer> edgeLastVisit;
  private HashMap<LXPoint, Integer> pointLastVisit;
  private HashMap<TEPanelModel, PanelData> panelData;
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
    for (TEVertex v : model.vertexesById.values()) {
      // Initialize all vertexes to gray
      v.virtualColor = new TEVirtualColor(50, 50, 50, 255);
    }
    this.panelData = new HashMap<>();
    for (TEPanelModel panel : model.panelsById.values()) {
      if (!panel.panelType.equals(TEPanelModel.LIT)) continue;
      int numEdgePixels = panel.e0.points.length + panel.e1.points.length + panel.e2.points.length;
      double maxRadius = panel.v0.distanceTo(panel.centroid);
      PanelData pd = new PanelData(numEdgePixels, maxRadius);
      for (LXPoint point : panel.points) {
        double radius = TEVertex.distance(point, panel.centroid);
        double radiusFraction = radius / maxRadius;
        pd.pointData.add(new PanelPointData(point, radius, radiusFraction));
      }
      this.panelData.put(panel, pd);
    }
  }

  // Select the edge least-recently visited (unless a subclass overrides this)
  public TEEdgeModel selectEdge(Set<TEEdgeModel> choices) {
    int oldestMove = this.moveNumber;
    TEEdgeModel winner = null;

    assert choices.size() > 0;
    for (TEEdgeModel e : choices) {
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
    if (this.pointLastVisit.getOrDefault(currentPoint, -1) < 0) {
      // First visit to this point. Increment neighbor Panels' lit-edge-pixel count.
      for (TEPanelModel panel : this.currentEdge.connectedPanels) {
        if (panel.panelType.equals(TEPanelModel.LIT)) {
          this.panelData.get(panel).litEdgePixels++;
        }
      }
    }
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

    reachedVertex.virtualColor = new TEVirtualColor(0, 100, 255, 255);

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

    for (LXPoint point : model.edgePoints) {
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
    for (Map.Entry<String, TEPanelModel> entry : model.panelsById.entrySet()) {
      TEPanelModel panel = entry.getValue();
      if (panel.panelType.equals(TEPanelModel.SOLID)) {
        assert panel.points.length == 1;
        LXPoint point = panel.points[0];
        int numVisitedEdges = 0;
        if (edgeLastVisit.getOrDefault(panel.e0, -1) >= 0) numVisitedEdges++;
        if (edgeLastVisit.getOrDefault(panel.e1, -1) >= 0) numVisitedEdges++;
        if (edgeLastVisit.getOrDefault(panel.e2, -1) >= 0) numVisitedEdges++;
        int color;
        if (numVisitedEdges == 3) color = LXColor.rgb(0, 180, 255);
        else if (numVisitedEdges == 2) color = LXColor.rgb(0, 0, 255);
        else if (numVisitedEdges == 1) color = LXColor.rgb(0, 0, 128);
        else color = LXColor.rgb(50, 50, 50);
        colors[point.index] = color;  // Used to control the real-life spotlight
        panel.virtualColor = new TEVirtualColor(color, 200); // Used to render a triangle in the virtual model
      } else if (panel.panelType.equals(TEPanelModel.LIT)) {
        PanelData panelData = this.panelData.get(panel);
        double litFraction = (double)panelData.litEdgePixels / panelData.numEdgePixels;
        for (PanelPointData pd : panelData.pointData) {
          int color;
          if (pd.radiusFraction <= litFraction) {
            color = LXColor.rgb(0, 180, 255);
          } else {
            color = LXColor.rgb(0,0,120);
          }
          colors[pd.point.index] = color;
        }
      }
    }
  }
}
