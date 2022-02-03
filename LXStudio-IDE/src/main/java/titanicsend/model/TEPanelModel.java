package titanicsend.model;

import java.util.ArrayList;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

public class TEPanelModel extends LXModel {
  public final TEVertex v1, v2, v3;
  public final TEEdgeModel e1, e2, e3;

  // Given an Edge and three Vertexes, return the number of vertexes the edge touches
  private int count_touches(TEEdgeModel e, TEVertex v1, TEVertex v2, TEVertex v3) {
    int rv = 0;
    if (e.touches(v1)) rv++;
    if (e.touches(v2)) rv++;
    if (e.touches(v3)) rv++;
    return rv;
  }

  public TEPanelModel(ArrayList<LXPoint> points, TEVertex v1, TEVertex v2, TEVertex v3,
                      TEEdgeModel e1, TEEdgeModel e2, TEEdgeModel e3) {
    super(points);

    // Make sure we have three different edges
    assert e1 != e2;
    assert e1 != e3;
    assert e2 != e3;

    // ...and three different vertexes
    assert v1 != v2;
    assert v1 != v3;
    assert v2 != v3;

    // Make sure each edge touches the other two
    assert e1.touches(e2);
    assert e1.touches(e3);
    assert e2.touches(e3);

    // Make sure each edge touches exactly two of the three vertexes
    assert count_touches(e1, v1, v2, v3) == 2;
    assert count_touches(e2, v1, v2, v3) == 2;
    assert count_touches(e3, v1, v2, v3) == 2;

    this.e1 = e1;
    this.e2 = e2;
    this.e3 = e3;

    this.v1 = v1;
    this.v2 = v2;
    this.v3 = v3;
  }
}