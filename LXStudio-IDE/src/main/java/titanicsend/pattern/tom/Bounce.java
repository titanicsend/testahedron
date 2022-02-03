package titanicsend.pattern.tom;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.parameter.LXParameter;
import titanicsend.model.TEEdgeModel;
import titanicsend.pattern.TEPattern;

@LXCategory("Testahedron")
public class Bounce extends TEPattern {
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

    public Bounce(LX lx) {
        super(lx);
        startModulator(this.phase);
        addParameter("rate", this.rate);
    }

    public void run(double deltaMs) {
        float phase = this.phase.getValuef();

        for (TEEdgeModel edge : model.edges) {
            for (LXPoint point : edge.points) {
                colors[point.index] = LXColor.BLACK;
            }
            int onIndex = (int) ((edge.size * phase) % edge.size);
            colors[edge.points[onIndex].index] = LXColor.WHITE;
        }
    }
}
