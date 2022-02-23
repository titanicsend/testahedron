package titanicsend.util;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Comparator;

import titanicsend.util.ZYPointsSorter;

// A SimpleScreen is a two-dimensional approximation of a monitor or TV screen against
// the side of Titanic's End.
// TODO: allow drawing against any dimensions. Currently, we flatten the X axis (the axis
// the lasers will blast the crowd through) and preserve the Y and Z coordinates.
// TODO: allow arbitrary positioning of this screen
public class SimpleScreen {
    public ArrayList<ArrayList<LXPoint>> screenGrid;

    private void buildScreenGrid(
        ArrayList<LXPoint> pointsList,
        int yLowerBound,
        int yUpperBound,
        int zLowerBound,
        int zUpperBound,
        boolean doubleSided) {
        this.screenGrid = new ArrayList<ArrayList<LXPoint>>(1);

        LX.log("Inside SimpleScreen.buildScreenGrid");
        LX.log(String.format("  Lower Y: %d", yLowerBound));
        LX.log(String.format("  Upper Y: %d", yUpperBound));
        LX.log(String.format("  Lower Z: %d", zLowerBound));
        LX.log(String.format("  Upper Z: %d", zUpperBound));

        pointsList.sort(new ZYPointsSorter());

        int currentGridZ = 0;
        int currentGridY = 0;
        double previousGridZ = 0;

        // Start from ordered points and move left to right. Once a point's z value is lower than
        // the previous grid's z value, we move onto the next row in the grid.
        for (LXPoint point : pointsList) {
            if (pointInBounds(point, yLowerBound, yUpperBound, zLowerBound, zUpperBound)) {
                if (!doubleSided || point.x < 0) {
                    continue;
                }

                // Here we move onto the next line of the grid. (moving down the y axis)
                if (point.z < previousGridZ) {
                    currentGridY += 1;
                    currentGridZ = 0;
                }

                this.screenGrid.get(currentGridZ).add(currentGridY, point);
                currentGridZ += 1;
                previousGridZ = point.z;
            }
        }
    }

    private static boolean pointInBounds(
        LXPoint point,
        int yLowerBound,
        int yUpperBound,
        int zLowerBound,
        int zUpperBound) {
            return point.z <= zUpperBound && point.z >= zLowerBound && point.y <= yUpperBound && point.y >= yLowerBound;
        }

    public SimpleScreen(
        ArrayList<LXPoint> pointsList,
        int yLowerBound,
        int yUpperBound,
        int zLowerBound,
        int zUpperBound,
        boolean doubleSided) {
        buildScreenGrid(pointsList, yLowerBound, yUpperBound, zLowerBound, zUpperBound, doubleSided);
    }
}
