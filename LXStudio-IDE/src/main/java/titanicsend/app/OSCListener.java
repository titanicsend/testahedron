package titanicsend.app;

import heronarts.lx.LX;
import heronarts.lx.osc.LXOscListener;
import heronarts.lx.osc.OscMessage;

import titanicsend.model.TEWholeModel;
import titanicsend.pattern.mike.Bubbles;
import titanicsend.pattern.mike.Checkers;

public class OSCListener implements LXOscListener {
    private LX lx;
    private TEWholeModel model;
    private String currentClassName;

    public OSCListener(LX lx, TEWholeModel model) {
        this.lx = lx;
        this.model = model;
        this.currentClassName = Bubbles.class.getName();
        lx.instantiatePattern(this.currentClassName);
    }

    public void oscMessage(OscMessage message) {
        if (this.currentClassName == Bubbles.class.getName()) {
            this.currentClassName = Checkers.class.getName();
        } else {
            this.currentClassName = Bubbles.class.getName();
        }

        try {
            lx.instantiatePattern(this.currentClassName);
        } catch (Exception x ) {
            LX.error("Error instantiating pattern: " + x.toString());
        }
        LX.log(message.toString());
    }
}
