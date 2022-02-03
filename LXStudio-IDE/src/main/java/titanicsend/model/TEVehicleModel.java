package titanicsend.model;

import java.util.*;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

public class TEVehicleModel extends LXModel {
  public TEEdgeModel[] edges;

  public TEVehicleModel() {
    this(makeEdges());
  }

  private TEVehicleModel(TEEdgeModel[] edges) {
    super(edges);
    this.edges = edges;
    reindexPoints();
  }

  private static TEEdgeModel[] makeEdges() {
    List<LXModel> childList = new ArrayList<LXModel>();

    TEVertex v0 = new TEVertex(new LXPoint(0, 0, 0));
    TEVertex v1 = new TEVertex(new LXPoint(50000, 0, 0));
    childList.add(new TEEdgeModel(v0, v1));

    TEVertex v2 = new TEVertex(new LXPoint(0, 50000, 0));
    childList.add(new TEEdgeModel(v1, v2));

    childList.add(new TEEdgeModel(v2, v0));

    return childList.toArray(new TEEdgeModel[0]);
  }
}
