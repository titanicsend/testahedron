package titanicsend.util;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import titanicsend.model.TEVertex;

import java.util.*;

public class PanelStriper {
  public static final int MARGIN = 50000; // 50k microns ~= 2 inches
  public static final int DISTANCE_BETWEEN_PIXELS = 50000; // 50k microns ~= 2 inches

  public static String stripe(TEVertex v0, TEVertex v1, TEVertex v2, List<LXPoint> pointList) {
    TEVertex vStart;
    TEVertex vMid;
    TEVertex vEnd;

    double distance01 = v0.distanceTo(v1);
    double distance02 = v0.distanceTo(v2);
    double distance12 = v1.distanceTo(v2);

    assert distance01 != distance02;
    assert distance01 != distance12;
    // distance02 and distance12 are equal at fore and aft

    // Set vEnd to the vertex opposite the longest edge
    // Set vStart to the vertex closest to vEnd
    if (distance01 > distance02 && distance01 > distance12) {
      vEnd = v2;
      if (distance02 < distance12) {
        vStart = v0;
        vMid = v1;
      } else if (distance12 < distance02) {
        vStart = v1;
        vMid = v0;
      } else if (v0.id < v1.id) {
        vStart = v0;
        vMid = v1;
      } else {
        assert v0.id != v1.id;
        vStart = v1;
        vMid = v0;
      }
    } else if (distance02 > distance01 && distance02 > distance12) {
      vEnd = v1;
      if (distance01 < distance12) {
        vStart = v0;
        vMid = v2;
      } else {
        vStart = v2;
        vMid = v0;
      }
    } else if (distance12 > distance01 && distance12 > distance02) {
      vEnd = v0;
      if (distance01 < distance02) {
        vStart = v1;
        vMid = v2;
      } else {
        vStart = v2;
        vMid = v1;
      }
    } else {
      throw new Error("Math fail");
    }

    FloorTransform floorTransform = new FloorTransform(vStart, vMid, vEnd);

    List<FloorPoint> floorPoints = stripeFloor(
            floorTransform.f0, floorTransform.f1, floorTransform.f2);

    List<LXPoint> rv = new ArrayList<LXPoint>();
    for (FloorPoint f : floorPoints) {
      pointList.add(floorTransform.fly(f));
    }

    int distanceSM = (int)vStart.distanceTo(vMid);
    int distanceME = (int)vMid.distanceTo(vEnd);
    int distanceES = (int)vEnd.distanceTo(vStart);

    return distanceSM + "-" + distanceME + "-" + distanceES;
  }

  // Lays out all the pixels in a LIT panel, once it's been sent through FloorTransform
  // to lay it on the X-Z plane. Starts at fStart and finds the nearest point inside the
  // border margin, and that's where the first pixel goes, then it stripes back and forth,
  // one row at a time, until it runs out of triangle.
  private static List<FloorPoint> stripeFloor(FloorPoint fStart, FloorPoint fMid, FloorPoint fEnd) {
    FloorPoint currentPoint = findStartingPoint(fStart, fMid, fEnd);
    ArrayList<FloorPoint> rv = new ArrayList<FloorPoint>();

    double deltaX = DISTANCE_BETWEEN_PIXELS;
    while (currentPoint.z < fEnd.z) {
      if (triangleContains(fStart, fMid, fEnd, currentPoint) &&
              distanceToEdge(fStart, fMid, fEnd, currentPoint) >= MARGIN
      ) rv.add(currentPoint);

      double nextX = currentPoint.x + deltaX;
      double nextZ = currentPoint.z;

      boolean nextRow = false;

      while (nextX > fMid.x) {
        nextX -= DISTANCE_BETWEEN_PIXELS / 2.0;
        nextRow = true;
      }

      while (nextX < 0.0) {
        nextX += DISTANCE_BETWEEN_PIXELS / 2.0;
        nextRow = true;
      }

      if (nextRow) {
        nextZ += DISTANCE_BETWEEN_PIXELS * 0.5 * Math.sqrt(3.0);
        deltaX = -deltaX;
      }

      currentPoint = new FloorPoint(nextX, nextZ);
    }

    return rv;
  }

  private static double calcHeading(FloorPoint start, FloorPoint destination) {
    double dx = destination.x - start.x;
    double dz = destination.z - start.z;
    return Math.atan2(dz, dx);
  }

  // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
  private static double distanceBetPointAndLine(FloorPoint f0, FloorPoint f1, FloorPoint f) {
    double top = Math.abs((f1.x - f0.x) * (f0.z - f.z) - (f0.x - f.x) * (f1.z - f0.z));
    double bot = Math.sqrt(Math.pow(f1.x - f0.x, 2) + Math.pow(f1.z - f0.z, 2));
    return top / bot;
  }

  private static double sign(FloorPoint f0, FloorPoint f1, FloorPoint f2) {
    return (f0.x - f2.x) * (f1.z - f2.z) - (f1.x - f2.x) * (f0.z - f2.z);
  }

  private static boolean triangleContains(FloorPoint f0, FloorPoint f1, FloorPoint f2, FloorPoint f) {
    double d1, d2, d3;
    boolean has_neg, has_pos;

    d1 = sign(f, f0, f1);
    d2 = sign(f, f1, f2);
    d3 = sign(f, f2, f0);

    has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
    has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

    return !(has_neg && has_pos);
  }

  // Returns the distance from f to the nearest edge of the f0-f1-f2 triangle.
  private static double distanceToEdge(FloorPoint f0, FloorPoint f1, FloorPoint f2, FloorPoint f) {
    var d1 = distanceBetPointAndLine(f0, f1, f);
    var d2 = distanceBetPointAndLine(f0, f2, f);
    var d3 = distanceBetPointAndLine(f1, f2, f);
    return Math.min(Math.min(d1, d2), d3);
  }

  // Nudge a number toward a target, at most epsilon at a time
  private static double nudgeToward(double target, double current, double epsilon) {
    double delta = target - current;
    if (Math.abs(delta) < epsilon) {
      return target;
    } else if (current < target) {
      return current + epsilon;
    } else {
      return current - epsilon;
    }
  }

  private static FloorPoint findStartingPoint(FloorPoint f0, FloorPoint f1, FloorPoint f2) {
    FloorPoint floorCentroid = new FloorPoint(
            ((f0.x + f1.x + f2.x) / 3.0),
            ((f0.z + f1.z + f2.z) / 3.0));

    // Calculate heading (in radians) from p1 to centroid
    // This is the angle we set off at to get closer to it
    double heading = calcHeading(f0, floorCentroid);

    // Find starting point for pixel strand, bumped in from the edges by `margin`.
    // Best approach I could figure out so far is to keep nudging toward the
    // centroid until we're no longer in the margin. We'll try MAX_ITERATIONS times,
    // each time nudging by EPSILON, which is calculated as a ratio of the margin.
    final double EPSILON_MARGIN_RATIO = 10.0;
    final double EPSILON = MARGIN / EPSILON_MARGIN_RATIO;
    final double MAX_ITERATIONS = EPSILON_MARGIN_RATIO * 10.0; // For some of the sharpest panels
    double x = f0.x;
    double z = f0.z;
    int curIteration = 0;
    FloorPoint guess;
    while (true) {
      guess = new FloorPoint(x, z);

      // Are we at least MARGIN distance away from the nearest edge? We're done!
      if (distanceToEdge(f0, f1, f2, guess) >= MARGIN) return guess;

      if (curIteration++ > MAX_ITERATIONS) {
        throw new Error("Never found the starting point; impossible margins?");
      }

      x = nudgeToward(floorCentroid.x, x, EPSILON * Math.abs(Math.cos(heading)));
      z = nudgeToward(floorCentroid.z, z, EPSILON * Math.abs(Math.sin(heading)));
    }
  }
}