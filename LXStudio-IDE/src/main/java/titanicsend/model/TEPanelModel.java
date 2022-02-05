package titanicsend.model;

import java.util.ArrayList;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import titanicsend.app.TEVirtualColor;

public class TEPanelModel extends LXModel {
  public final static String NONEXISTENT = "nonexistent";
  public final static String SOLID = "solid";
  public final static String LIT = "lit";

  public final TEVertex v0, v1, v2;
  public final TEEdgeModel e0, e1, e2;
  public final LXPoint centroid;
  public String panelType;

  // Set to non-null and the virtual display will shade the panel's triangle
  public TEVirtualColor virtualColor;

  // Given an Edge and three Vertexes, return the number of vertexes the edge touches
  private int count_touches(TEEdgeModel e, TEVertex v0, TEVertex v1, TEVertex v2) {
    int rv = 0;
    if (e.touches(v0)) rv++;
    if (e.touches(v1)) rv++;
    if (e.touches(v2)) rv++;
    return rv;
  }

  public TEPanelModel(ArrayList<LXPoint> points, TEVertex v0, TEVertex v1, TEVertex v2,
                      TEEdgeModel e0, TEEdgeModel e1, TEEdgeModel e2, String panelType,
                      LXPoint centroid) {
    super(points);

    this.panelType = panelType;
    this.centroid = centroid;

    switch (panelType) {
      case NONEXISTENT:
        // Display nonexistent panels as wispy pink
        this.virtualColor = new TEVirtualColor(255, 0, 255, 100);
        break;
      case LIT:
        // Display lit panels as semi-transparent gold
        //this.virtualColor = new TEVirtualColor(255, 255, 0, 200);

        // Don't display lit panels
        this.virtualColor = null;
        break;
      case SOLID:
        // Display solid panels as semi-transparent blue, unless repainted by a pattern
        this.virtualColor = new TEVirtualColor(0, 0, 255, 200);
        break;
      default:
        throw new Error("Unknown panel type: " + panelType);
    }

    // Make sure we have three different edges
    assert e0 != e1;
    assert e0 != e2;
    assert e1 != e2;

    // ...and three different vertexes
    assert v0 != v1;
    assert v0 != v2;
    assert v1 != v2;

    // Make sure each edge touches the other two
    assert e0.touches(e1);
    assert e0.touches(e2);
    assert e1.touches(e2);

    // Make sure each edge touches exactly two of the three vertexes
    assert count_touches(e0, v0, v1, v2) == 2;
    assert count_touches(e1, v0, v1, v2) == 2;
    assert count_touches(e2, v0, v1, v2) == 2;

    this.e0 = e0;
    this.e1 = e1;
    this.e2 = e2;

    this.v0 = v0;
    this.v1 = v1;
    this.v2 = v2;
  }
}