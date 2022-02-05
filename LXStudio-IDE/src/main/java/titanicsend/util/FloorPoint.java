package titanicsend.util;

// A point lying on the floor (its Y value is locked to 0, so we don't have to store it)
public class FloorPoint {
  double x;
  double z;
  FloorPoint(double x, double z) {
    this.x = x;
    this.z = z;
  }
}
