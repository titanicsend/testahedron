package titanicsend.app;

import heronarts.lx.LX;
import heronarts.lx.mixer.LXAbstractChannel;
import heronarts.lx.mixer.LXChannel;
import heronarts.lx.pattern.LXPattern;
import heronarts.lx.osc.LXOscListener;
import heronarts.lx.osc.OscMessage;

import titanicsend.model.TEWholeModel;

public class OSCListener implements LXOscListener {
    private LX lx;
    private TEWholeModel model;
    private long lastSeconds;

    public OSCListener(LX lx, TEWholeModel model) {
        this.lx = lx;
        this.model = model;
        this.lastSeconds = System.currentTimeMillis() / 1000l;
    }

    public void oscMessage(OscMessage message) {
        LX.log(String.format("Osc Message received: %s", message.toString()));

        long seconds = System.currentTimeMillis() / 1000l;
        if (seconds - this.lastSeconds <= 1) {
            return;
        }
        this.lastSeconds = System.currentTimeMillis() / 1000l;
        try {
            LXAbstractChannel abstractChannel = lx.engine.mixer.channels.get(0);
            LXChannel channel = (LXChannel)abstractChannel;

            channel.goRandomPattern();
        } catch (Exception x ) {
            LX.error("Error instantiating random pattern: " + x.toString());
        }
    }
}
