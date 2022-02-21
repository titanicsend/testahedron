package titanicsend.app;

import heronarts.lx.LX;
import heronarts.lx.mixer.LXAbstractChannel;
import heronarts.lx.mixer.LXChannel;
import heronarts.lx.osc.LXOscListener;
import heronarts.lx.osc.OscMessage;

import titanicsend.model.TEWholeModel;
import titanicsend.pattern.mike.Bubbles;
import titanicsend.pattern.mike.Checkers;

public class OSCListener implements LXOscListener {
    private LX lx;
    private TEWholeModel model;
    private int channelIndex;

    public OSCListener(LX lx, TEWholeModel model) {
        this.lx = lx;
        this.model = model;
        this.channelIndex = 0;
    }

    public void oscMessage(OscMessage message) {
        this.channelIndex = this.channelIndex + 1;
        try {
            LXAbstractChannel abstractChannel = lx.engine.mixer.channels.get(0);
            LXChannel channel = (LXChannel)abstractChannel;
            channel.focusedPattern.setValue(channelIndex);
        } catch (Exception x ) {
            LX.error("Error instantiating pattern: " + x.toString());
        }
        LX.log(message.toString());
    }
}
