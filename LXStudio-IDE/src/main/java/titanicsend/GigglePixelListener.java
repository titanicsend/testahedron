package titanicsend;

import heronarts.lx.LX;
import heronarts.lx.LXLoopTask;
import playasystems.gigglepixel.*;
import heronarts.lx.color.LXColor;
import heronarts.lx.color.LXSwatch;

import java.io.IOException;

public class GigglePixelListener implements LXLoopTask {
  private static final int GP_PORT = 8080;

  private final LX lx;
  private final GPListenerTask gp;

  public GigglePixelListener(LX lx, String listenIP) {
    this.lx = lx;
    this.gp = new GPListenerTask(listenIP, GP_PORT);
  }

  @Override
  public void loop(double deltaMs) {
    GPPacket packet;

    try {
      packet = this.gp.loop();
    } catch (IOException e) {
      LX.log("Got I/O error in GigglePixel code: " + e.getMessage());
      return;
    } catch (GPException e) {
      LX.log("Got invalid GigglePixel packet: " + e.getMessage());
      return;
    }

    if (packet == null) return;

    if (packet.type == GPPacket.GPType.PALETTE) {
      GPPalettePacket pp = (GPPalettePacket) packet;
      int numColors = pp.entries.size();
      if (numColors < 1) {
        LX.log("Got empty palette packet");
        return;
      } else if (numColors > 5) {
        numColors = 5;  // TODO: Be smarter when getting big palette packets
      }
      LXSwatch swatch = lx.engine.palette.swatch;
      while(swatch.colors.size() > numColors) {
        swatch.removeColor();
      }
      while(swatch.colors.size() < numColors) {
        swatch.addColor();
      }
      for (int i = 0; i < numColors; i++) {
        GPColor gpColor = pp.entries.get(i);
        int color = LXColor.rgb(gpColor.r, gpColor.g, gpColor.b);
        swatch.colors.get(i).color.setColor(color);
      }
    }
  }
}
