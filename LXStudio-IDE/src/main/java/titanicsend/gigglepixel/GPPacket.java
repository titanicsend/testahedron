package titanicsend.gigglepixel;

import java.util.Arrays;

public abstract class GPPacket {
  public int version;
  public GPType type;
  public int priority;
  public int flags;
  public int source;
  public int dest;
  public boolean hasCrc;

  public enum GPType {
    PALETTE("Palette");
    public final String label;
    GPType(String label) {
      this.label = label;
    }
    @Override
    public String toString() {
      return this.label;
    }
  }

  public static GPPacket decode(byte[] encoded) throws GPException {
    if (encoded.length < 14) throw new GPException("Header too short");
    if (encoded[0] != 'G' || encoded[1] != 'L' || encoded[2] != 'P' || encoded[3] != 'X')
    throw new GPException("Bad magic number");

    int version = ord8(encoded[4]);

    // Go ahead and try to parse packets from the future
    //if (version > 2) throw new GPException("Version from the future");

    int length = ord16(encoded[5], encoded[6]);

    if (length < 14) throw new GPException("Length too short");

    int type = ord8(encoded[7]);

    GPPacket packet;
    switch(type) {
      case 1:
        packet = new GPPalettePacket();
        break;
      default:
        throw new GPException("Unknown packet type");
    }

    packet.version = version;
    packet.priority = ord8(encoded[8]);
    packet.flags = ord8(encoded[9]);
    packet.source = ord16(encoded[10], encoded[11]);
    packet.dest = ord16(encoded[12], encoded[13]);

    if (encoded.length == length) {
      packet.hasCrc = false;
    } else if (encoded.length == length + 2){
      packet.hasCrc = true;
      byte[] sansPayload = Arrays.copyOfRange(encoded, 0, length);
      int calcCrc = GPCRC.forBytes(sansPayload);
      int foundCrc = ord16(encoded[length], encoded[length+1]);
      if (calcCrc != foundCrc) throw new GPException("Bad CRC");
    } else {
      throw new GPException("Length mismatch");
    }

    packet.decodePayload(Arrays.copyOfRange(encoded, 14, encoded.length));

    return packet;
  }

  protected static int ord8(byte b) {
    int rv = b;
    if (rv < 0) rv += 256;
    return rv;
  }

  protected static int ord16(byte b0, byte b1) {
    return ord8(b0) << 8 | ord8(b1);
  }

  public abstract void decodePayload(byte[] buf) throws GPException;
}
