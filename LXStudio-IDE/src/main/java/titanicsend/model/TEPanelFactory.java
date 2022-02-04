package titanicsend.model;

import java.util.ArrayList;
import heronarts.lx.model.LXPoint;

public class TEPanelFactory {
  public static int MARGIN = 75000; // 75k microns ~= 3 inches
  public static int DISTANCE_BETWEEN_PIXELS = 25000; // 25k microns ~= 1 inch

  public static TEPanelModel build(TEVertex v0, TEVertex v1, TEVertex v2,
                            TEEdgeModel e0, TEEdgeModel e1, TEEdgeModel e2,
                            String panelType) {
    ArrayList<LXPoint> points = new ArrayList<LXPoint>();

    double centroidX = (v0.x + v1.x + v2.x) / 3.0;
    double centroidY = (v0.y + v1.y + v2.y) / 3.0;
    double centroidZ = (v0.z + v1.z + v2.z) / 3.0;

    LXPoint centroid = new LXPoint(centroidX, centroidY, centroidZ);

    if (panelType.equals(TEPanelModel.LIT)) {
      //stripe(points, v0, v1, v2, centroid);
    } else if (panelType.equals(TEPanelModel.SOLID)) {
      points.add(centroid);
    }

    return new TEPanelModel(points, v0, v1, v2, e0, e1, e2, panelType, centroid);
  }

/*
  private void stripe(ArrayList<LXPoint> points, TEVertex v0, TEVertex v1, TEVertex v2,
                      LXPoint centroid) {

  }
*/
}