package titanicsend.pattern.tom;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.LXParameter;
import titanicsend.pattern.PeriodicPattern;

import static processing.core.PApplet.lerpColor;

public class Fire extends PeriodicPattern {
    private float ROW_HEIGHT = 100000;
    private float COLUMN_WIDTH = 100000;
    private int NUM_COLUMNS;
    private int NUM_ROWS;
    private int[][] buffer;
    private int[] gradient;

    protected final CompoundParameter fuel = (CompoundParameter)
            new CompoundParameter("Fuel", 1);

    public Fire(LX lx) {
        super(lx);
        NUM_ROWS = calculateRows();
        NUM_COLUMNS = calculateColumns();
        gradient = calculateGradient(LXColor.rgb(0xcf, 0x6f, 0x0f), 36);
        buffer = new int[NUM_ROWS][NUM_COLUMNS];

        register(this::spreadFire, 100);
        addParameter("fuel", fuel);
    }

    public void spreadFire() {
        checkFuel();

        int[][] newBuffer = buffer.clone();
        for (int row = 1; row < NUM_ROWS; row++) {
            for (int column = 0; column < NUM_COLUMNS; column++) {
                int rand = (int) Math.round(Math.random() * 3.0) & 3;
                newBuffer[row][Math.min(NUM_COLUMNS - 1, Math.max(0, column - rand + 2))] = Math.max(0, buffer[row-1][column] - (1 & rand));
            }
        }

        render();
    }

    public void render() {
        for (LXPoint point : model.points) {
            colors[point.index] = color(point);
        }
    }

    private void checkFuel() {
        int value;
        if (fuel.getValue() > 0.5) {
            value = gradient.length - 1;
        } else {
            value = 0;
        }

        for (int i = 0; i < NUM_COLUMNS; i++) {
            buffer[0][i] = value;
        }
    }

    private int color(LXPoint point) {
        int row = (int) ((point.y - model.boundaryPoints.minYBoundaryPoint.y) / ROW_HEIGHT);
        int column = (int) ((point.z - model.boundaryPoints.minZBoundaryPoint.z) / COLUMN_WIDTH);
        return gradient[buffer[row][column]];
    }

    private int calculateRows() {
        return (int) ((model.boundaryPoints.maxYBoundaryPoint.y - model.boundaryPoints.minYBoundaryPoint.y) / ROW_HEIGHT) + 1;
    }

    private int calculateColumns() {
        return (int) ((model.boundaryPoints.maxZBoundaryPoint.z - model.boundaryPoints.minZBoundaryPoint.z) / COLUMN_WIDTH) + 1;
    }

    private int[] calculateGradient(int middle, int steps) {
        int[] gradient = new int[steps];
        for (int i = 0; i < steps / 2; i++) {
            gradient[i] = lerpColor(LXColor.BLACK, middle, (float) i / (steps/2), 3);
        }

        for (int i = steps / 2; i < steps; i++) {
            gradient[i] = lerpColor(middle, LXColor.WHITE, (float) (i - steps/2) / (steps/2), 3);
        }

        return gradient;
    }
}
