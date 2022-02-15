package titanicsend.model;

import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXVector;

import java.util.ArrayList;
import java.util.List;

public class TELaserModel extends TEModel {
  public static double MSEC_PER_REVOLUTION = 3000;
  public static double RADIANS_PER_MSEC = (2.0 * Math.PI) / MSEC_PER_REVOLUTION;
  public static float SPIN_RADIUS_RATIO = 0.3F; // Slope of the cone

  public LXVector origin;
  public LXVector homeDirection;
  public LXVector direction;
  public int color;
  public String id;

  private LXVector perpendicular;
  private float theta;

  // Angles represent the direction the laser is aimed and are in radians, of course.
  public TELaserModel(double x, double y, double z, LXVector homeDirection) {
    super("Laser", makePoint(x, y, z));
    this.origin = new LXVector(this.points[0]);

    this.homeDirection = homeDirection;
    this.homeDirection.normalize();

    this.perpendicular = new LXVector(-homeDirection.y, homeDirection.x, 0);
    this.perpendicular.normalize();
    this.perpendicular.mult(SPIN_RADIUS_RATIO);

    this.theta = 0.0F;
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

  public void updateDirection(double deltaMsec) {
    this.theta += (deltaMsec * RADIANS_PER_MSEC) % (2.0 * Math.PI);
    this.direction = this.perpendicular.copy();
    this.direction.rotate(this.theta,
            this.homeDirection.x, this.homeDirection.y, this.homeDirection.z);
    this.direction.add(this.homeDirection);
  }
}