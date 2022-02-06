package titanicsend.pattern.tom;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.transform.LXVector;
import titanicsend.model.TEPanelModel;
import titanicsend.model.TEVertex;
import titanicsend.pattern.TEPattern;
import titanicsend.util.FloorPoint;
import titanicsend.util.PanelStriper;

import java.util.*;
import java.util.stream.Collectors;

public class Pulse extends TEPattern {
    private HashMap<String, LXPoint[][]> pointMap;

    protected final CompoundParameter rate = (CompoundParameter)
            new CompoundParameter("Rate", .25, .01, 2)
                    .setExponent(2)
                    .setUnits(LXParameter.Units.HERTZ)
                    .setDescription("Rate of the rotation");

    protected final SinLFO phase = new SinLFO(0, 1, new FunctionalParameter() {
        public double getValue() {
            return 1000 / rate.getValue();
        }
    });

    public Pulse(LX lx) {
        super(lx);
        startModulator(this.phase);
        addParameter("rate", this.rate);
        pointMap = buildPointMap(model.panelsById);
    }

    public void run(double deltaMs) {
        float phase = this.phase.getValuef();
        for (Map.Entry<String, TEPanelModel> entry : model.panelsById.entrySet()) {
            LXPoint[][] panelPoints = pointMap.get(entry.getKey());
            int litIndex = (int) (phase * (panelPoints.length - 1));
            LXPoint[] litSection = panelPoints[litIndex];
            for (LXPoint point : litSection) {
                colors[point.index] = LXColor.WHITE;
            }

            for (int i = 0; i < panelPoints.length; i++) {
                if (i == litIndex) continue;
                for (LXPoint point : panelPoints[i]) {
                    colors[point.index] = LXColor.BLACK;
                }
            }
        }
    }

    private HashMap<String, LXPoint[][]> buildPointMap(HashMap<String, TEPanelModel> panels) {
        return panels.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> buildPanelMap(e.getValue()),
                        (a, b) -> a,
                        HashMap::new
                ));
    }


    private LXPoint[][] buildPanelMap(TEPanelModel panel) {
        ArrayList<ArrayList<LXPoint>> points = new ArrayList<ArrayList<LXPoint>>();
        Set<LXPoint> unassignedPoints = Arrays.stream(panel.points).collect(Collectors.toSet());

        TEVertex[] currentVertices = {
                new TEVertex(new LXVector(panel.v0), -1),
                new TEVertex(new LXVector(panel.v1), -1),
                new TEVertex(new LXVector(panel.v2), -1)
        };

        int currentIndex = 0;
        while (currentVertices[0].distanceTo(panel.centroid) > (10 * PanelStriper.DISTANCE_BETWEEN_PIXELS)) {
            points.add(new ArrayList<LXPoint>());
            ArrayList<LXPoint> assignedPoints = new ArrayList<LXPoint>();
            LXVector[][] edges = {
                    {currentVertices[0], currentVertices[1]},
                    {currentVertices[1], currentVertices[2]},
                    {currentVertices[0], currentVertices[2]},
            };

            for (LXPoint point : unassignedPoints) {
                for (LXVector[] edge : edges) {
                    if (distanceBetPointAndLine(edge[0], edge[1], point) < 5 * PanelStriper.DISTANCE_BETWEEN_PIXELS) {
                        points.get(currentIndex).add(point);
                    }
                }
            }

            for (LXPoint point : points.get(currentIndex)) {
                unassignedPoints.remove(point);
            }

            for (TEVertex vertex : currentVertices) {
                vertex.nudgeToward(panel.centroid, (float) 0.05);
            }
            currentIndex++;
        }

        return points.stream()
                .map(l -> l.stream().toArray(LXPoint[]::new))
                .toArray(LXPoint[][]::new);
    }

    private static double distanceBetPointAndLine(LXVector line_p1, LXVector line_p2, LXPoint p) {
        LXVector v0 = new LXVector(p);
        LXVector v1 = new LXVector(line_p1);
        LXVector v2 = new LXVector(line_p2);
        double numerator = new LXVector(v0).sub(v1).cross(new LXVector(v0).sub(v2)).mag();
        double denominator = new LXVector(v2).sub(v1).mag();

        return numerator / denominator;
    }

}
