package titanicsend.app;

import heronarts.lx.LX;
import heronarts.lx.osc.LXOscListener;
import heronarts.lx.osc.OscMessage;

public class OSCListener implements LXOscListener {
    public void oscMessage(OscMessage message) {
        LX.log(message.toString());
    }
}
