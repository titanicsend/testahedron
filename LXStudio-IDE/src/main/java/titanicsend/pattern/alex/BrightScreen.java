package titanicsend.pattern.alex;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import titanicsend.model.TEEdgeModel;
import titanicsend.model.TEPanelModel;
import titanicsend.util.TEColorGroupPattern;

import java.util.HashMap;
import java.util.Map;

@LXCategory("Testahedron")
public class BrightScreen extends TEColorGroupPattern {
    public BrightScreen(LX lx) {
        super(lx, new String[]{"ColorA"});
    }

    public void run(double deltaMs) {
        int color0 = this.teColors.get(0).getColor();

        for (TEEdgeModel edge : model.edgesById.values()) {
            for (LXPoint point : edge.points) {
                colors[point.index] = color0;
            }
        }

        for (TEPanelModel panel : model.panelsById.values()) {
            for (LXPoint point : panel.points) {
                colors[point.index] = color0;
            }
        }
    }
}