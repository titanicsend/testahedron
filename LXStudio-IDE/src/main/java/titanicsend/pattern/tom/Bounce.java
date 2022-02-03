package titanicsend.pattern.tom;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.parameter.LXParameter;
import titanicsend.model.Edge;
import titanicsend.pattern.TEPattern;

import static heronarts.lx.LX.TWO_PI;

@LXCategory("Testahedron")
public class Bounce extends TEPattern {
    protected final CompoundParameter rate = (CompoundParameter)
            new CompoundParameter("Rate", .25, .01, 2)
                    .setExponent(2)
                    .setUnits(LXParameter.Units.HERTZ)
                    .setDescription("Rate of the rotation");

    protected final SawLFO phase = new SawLFO(0, TWO_PI, new FunctionalParameter() {
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

        for (Edge edge : model.edges) {
            for (LXPoint point : edge.points) {
                colors[point.index] = LXColor.BLACK;
            }
            int onIndex = (int) (edge.size * phase) % edge.size;
            colors[edge.points[onIndex].index] = LXColor.WHITE;
        }
    }
}
