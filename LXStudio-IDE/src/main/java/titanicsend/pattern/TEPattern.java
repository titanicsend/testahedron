package titanicsend.pattern;

import heronarts.lx.LX;
import heronarts.lx.pattern.LXModelPattern;
import titanicsend.model.TELaserModel;
import titanicsend.model.TEPanelModel;
import titanicsend.model.TEVehicleModel;

import java.util.Map;

public abstract class TEPattern extends LXModelPattern<TEVehicleModel> {
    protected TEPattern(LX lx) {
        super(lx);
    }

    // Make the virtual model's solid panels and lasers get rendered to match
    // their LXPoint color
    public void updateVirtualColors() {
        for (TEPanelModel panel : this.model.panelsById.values()) {
            if (panel.panelType.equals(TEPanelModel.SOLID)) {
                panel.virtualColor.rgb = colors[panel.points[0].index];
            }
        }
        for (TELaserModel laser : this.model.lasers) {
            laser.color = colors[laser.points[0].index];
        }
    }
}
