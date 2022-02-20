package titanicsend.model;

import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.Comparator;

// A SimpleScreen is a two-dimensional approximation of a monitor or TV screen against
// the side of Titanic's End.
// TODO: allow arbitrary height/width to the screen
// TODO: allow arbitrary positioning of this screen
public class SimpleScreen {
    private static final int SCREEN_WIDTH = 100;
    private static final int SCREEN_HEIGHT = 25;

    public ArrayList<LXPoint> screenGrid;

    // buildScreenGrid transforms 3-dimensional LXPoints (x,y,z) into points along a screen
    // with only an x and a theta property.
    private void buildScreenGrid(ArrayList<LXPoint> pointsList, double zLowerBound, double zUpperBound, double yLowerBound, double yUpperBound) {
        ArrayList<LXPoint> screenGrid = new ArrayList<LXPoint>(SCREEN_WIDTH);

        for (LXPoint point : pointsList) {
            if ((point.z <= zUpperBound && point.z >= zLowerBound) && (point.y <= yUpperBound && point.y >= yLowerBound)) {
                screenGrid.add(point);
            }
        }
        this.screenGrid = screenGrid;
    }

    public SimpleScreen(ArrayList<LXPoint> pointsList, double zLowerBound, double zUpperBound, double yLowerBound, double yUpperBound) {

        buildScreenGrid(pointsList, zLowerBound, zUpperBound, yLowerBound, yUpperBound);
    }
}
