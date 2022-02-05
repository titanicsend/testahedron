package titanicsend.util;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import titanicsend.model.TEVertex;

import java.util.*;

public class PanelStriper {
  public static final int MARGIN = 75000; // 75k microns ~= 3 inches
  public static final int DISTANCE_BETWEEN_PIXELS = 50000; // 50k microns ~= 2 inches

  public static List<LXPoint> stripe(TEVertex v0, TEVertex v1, TEVertex v2) {
    FloorTransform floorTransform = new FloorTransform(v0, v1, v2);

    List<FloorPoint> floorPoints = stripeFloor(
            floorTransform.f0, floorTransform.f1, floorTransform.f2);

    List<LXPoint> rv = new ArrayList<LXPoint>();
    for (FloorPoint f : floorPoints) {
      rv.add(floorTransform.fly(f));
    }

    return rv;
  }

  // Lays out all the pixels in a LIT panel, once it's been sent through FloorTransform
  // to lay it on the X-Z plane. Starts at f0 and finds the nearest point inside the
  // border margin, and that's where the first pixel goes, then it stripes back and forth,
  // one row at a time, until it runs out of triangle.
  private static List<FloorPoint> stripeFloor(FloorPoint f0, FloorPoint f1, FloorPoint f2) {
    FloorPoint currentPoint = findStartingPoint(f0, f1, f2);
    ArrayList<FloorPoint> rv = new ArrayList<FloorPoint>();

    // Calc the angle we set off at to get from near f0 -> f1
    double heading = calcHeading(f0, f1);

    // When we get to f1 (or, at least, hit the margin),
    // we bump over in this direction (toward f2) before
    // spinning 180 degrees and making a new stripe.
    double endOfRowHeading = calcHeading(f1, f2);

    // And then when we reach the end of the second row,
    // we again bump towards f2, but now the angle will
    // be different because we're in the neighborhood of f0.
    double endOfRowHeadingNext = calcHeading(f0, f2);

    final int MAX_POINTS = 2500;  // Sanity check

    for (int i = 0; i < MAX_POINTS; i++) {
      rv.add(currentPoint);

      double nextX = currentPoint.x + DISTANCE_BETWEEN_PIXELS * Math.cos(heading);
      double nextZ = currentPoint.z + DISTANCE_BETWEEN_PIXELS * Math.sin(heading);
      FloorPoint nextPoint = new FloorPoint(nextX, nextZ);
      if (distanceToEdge(f0, f1, f2, nextPoint) >= MARGIN) {
        // We haven't yet reached the margin.
        currentPoint = nextPoint;
      } else {
        // Bump over a row
        nextX = currentPoint.x + DISTANCE_BETWEEN_PIXELS * Math.cos(endOfRowHeading);
        nextZ = currentPoint.z + DISTANCE_BETWEEN_PIXELS * Math.sin(endOfRowHeading);

        // And reverse the heading
        heading = (Math.PI + heading) % (2.0 * Math.PI);

        // TODO: Will we have to burn a pixel when we switch rows?

        // And swap end-of-row headings
        double tmp = endOfRowHeadingNext;
        endOfRowHeadingNext = endOfRowHeading;
        endOfRowHeading = tmp;

        // And get started on the next row... unless there's no room for it.
        currentPoint = new FloorPoint(nextX, nextZ);
        if (distanceToEdge(f0, f1, f2, currentPoint) < MARGIN) return rv;
      }
    }

    LX.log("Giving up on a panel after " + MAX_POINTS + " points");
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
    final double MAX_ITERATIONS = EPSILON_MARGIN_RATIO * 8.0; // For worst case, a 45deg angle
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