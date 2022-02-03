package titanicsend.pattern;

import heronarts.lx.LX;
import heronarts.lx.pattern.LXModelPattern;
import titanicsend.model.TEVehicleModel;

public abstract class TEPattern extends LXModelPattern<TEVehicleModel> {
    protected TEPattern(LX lx) {
        super(lx);
    }
}