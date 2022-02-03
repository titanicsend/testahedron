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
    TEVertex v1 = new TEVertex(new LXPoint(5000, 5000, 5000));
    childList.add(new TEEdgeModel(v0, v1));

    return childList.toArray(new TEEdgeModel[0]);
  }
}
