package titanicsend.gigglepixel;

import heronarts.lx.LX;

import java.util.*;

public class GPPalettePacket extends GPPacket {
  List<GPColor> entries;

  public GPPalettePacket() {
    super();
    this.type = GPType.PALETTE;
    this.entries = null;
  }

  @Override
  public void decodePayload(byte[] payload) throws GPException {
    if (payload.length < 2) throw new GPException("Short palette header");
    int numEntries = ord16(payload[0], payload[1]);
    if (payload.length != 2 + 4 * numEntries)
      throw new GPException("Palette length mismatch");
    this.entries = new ArrayList<>();

    for (int i = 0; i < numEntries; i++) {
      int startIndex = 2 + 4 * i;
      int frac = ord8(payload[startIndex]);
      int r = ord8(payload[startIndex+1]);
      int g = ord8(payload[startIndex+2]);
      int b = ord8(payload[startIndex+3]);
      GPColor gpc = new GPColor(r, g, b, frac);
      this.entries.add(gpc);
    }
  }
}