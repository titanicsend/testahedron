package titanicsend.model;

import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXVector;

import java.util.ArrayList;
import java.util.List;

public class TELaserModel extends TEModel {
  public LXVector origin;
  public LXVector direction;
  public int color;
  public String id;

  // Angles represent the direction the laser is aimed and are in radians, of course.
  public TELaserModel(double x, double y, double z, LXVector direction) {
    super("Laser", makePoint(x, y, z));
    this.origin = new LXVector(this.points[0]);
    this.direction = direction;
    this.color = LXColor.rgb(255,0,0);
  }

  public String getId() {
    return this.id;
  }

  private static List<LXPoint> makePoint(double x, double y, double z) {
    List<LXPoint> points = new ArrayList<>();
    points.add(new LXPoint(x, y, z));
    return points;
  }
}