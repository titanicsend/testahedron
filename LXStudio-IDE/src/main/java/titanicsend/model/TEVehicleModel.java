package titanicsend.model;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.StripModel;

public class TEVehicleModel extends LXModel {
  public TEVehicleModel() {
    // Gotta start somewhere. Our first model of the vehicle is just a strip of lights.
    super(new LXModel[] {new StripModel(10)});
  }
}
