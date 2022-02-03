package titanicsend.model;

import java.util.*;

import heronarts.lx.model.StripModel;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXModelBuilder;
import heronarts.lx.model.LXPoint;

public class TEVehicleModel extends LXModel {
  private LXModelBuilder modelBuilder;

  // Should only be externally constructable by calling .build()
  private TEVehicleModel(TEVehicleBuilder builder) {
    super(builder.children);
  }

  public static TEVehicleModel build() {
    TEVehicleBuilder builder = new TEVehicleBuilder();
    return new TEVehicleModel(builder);
  }

  private static class TEVehicleBuilder extends LXModelBuilder {
    private final LXModel[] children;

    public TEVehicleBuilder() {
      setTags("Titanic's End Vehicle");
      List<LXModel> childList = new ArrayList<LXModel>();

      // For the first version of this, I just hardcoded one strip:
      // childList.add(new StripModel(10));

      // That worked, but for some reason, I can't replace it with a TEEdgeModel:
      TEVertex v0 = new TEVertex(new LXPoint(1,1,1));
      TEVertex v1 = new TEVertex(new LXPoint(5000,5000,5000));
      childList.add(new TEEdgeModel(v0, v1));

      this.children = childList.toArray(new LXModel[0]);
    }
  }
}
