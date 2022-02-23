package titanicsend.gigglepixel;

import heronarts.lx.LX;
import heronarts.lx.LXLoopTask;
import heronarts.lx.color.LXSwatch;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GPListenerTask implements LXLoopTask {
  private static DatagramSocket socket = null;
  private static final int GP_PORT = 8080;
  private static final int GP_MAXLEN = 1000;

  private LX lx;

  public GPListenerTask(LX lx, String listenIP) {
    try {
      socket = new DatagramSocket(null);
      socket.setReuseAddress(true);
      socket.setBroadcast(true);
      socket.bind(new InetSocketAddress(GP_PORT));
      socket.setSoTimeout(1);
    } catch (SocketException e) {
      throw new Error(e);
    }
    this.lx = lx;
  }

  @Override
  public void loop(double deltaMs) {
    byte[] buf = new byte[GP_MAXLEN];
    DatagramPacket packet = new DatagramPacket(buf, GP_MAXLEN);
    try {
      socket.receive(packet);
    } catch (SocketTimeoutException e) {
      return;
    } catch (IOException e) {
      throw new Error(e);
    }

    byte[] minibuf = Arrays.copyOfRange(buf, 0, packet.getLength());

    GPPacket gp;
    try {
      gp = GPPacket.decode(minibuf);
    } catch (GPException e) {
      LX.log("Got invalid GigglePixel packet: " + e.getMessage());
      return;
    }

    if (gp.type == GPPacket.GPType.PALETTE) {
      GPPalettePacket pp = (GPPalettePacket) gp;
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
        swatch.colors.get(i).color.setColor(pp.entries.get(i).rgb());
      }
    }
  }
}
