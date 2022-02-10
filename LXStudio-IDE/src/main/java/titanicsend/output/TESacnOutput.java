package titanicsend.output;

import heronarts.lx.LX;
import heronarts.lx.output.StreamingACNDatagram;
import titanicsend.model.TEEdgeModel;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class TESacnOutput {
  private static class EdgeEntry {
    TEEdgeModel edge;
    int universeNum;
    int strandOffset;
    public EdgeEntry(TEEdgeModel edge, int universeNum, int strandOffset) {
      this.edge = edge;
      this.universeNum = universeNum;
      this.strandOffset = strandOffset;
    }
  }
  String ipAddress;
  static Map<String, TESacnOutput> ipMap = new HashMap<>();
  private final List<EdgeEntry> edgeEntries;
  private boolean activated;
  private HashMap<Integer,Integer> deviceLengths;
  private StreamingACNDatagram outputDevice;

  private TESacnOutput(String ipAddress) {
    this.ipAddress = ipAddress;
    this.edgeEntries = new ArrayList<>();
    this.activated = false;
    this.outputDevice = null;
  }

  public static TESacnOutput getOrMake(String ipAddress) {
    if (!ipMap.containsKey(ipAddress)) {
      ipMap.put(ipAddress, new TESacnOutput(ipAddress));
    }
    return ipMap.get(ipAddress);
  }

  public static void register(TEEdgeModel edge, String ipAddress, int deviceNum, int strandOffset) {
    assert deviceNum >= 1;
    assert deviceNum <= 4;
    assert strandOffset >= 0;
    TESacnOutput output = getOrMake(ipAddress);
    assert !output.activated;
    output.edgeEntries.add(new EdgeEntry(edge, deviceNum, strandOffset));
  }

  // Sort by device number, then by strand offset
  private static class SortEdgeEntries implements Comparator<EdgeEntry> {
    public int compare(EdgeEntry a, EdgeEntry b) {
      if (a.universeNum != b.universeNum) {
        return a.universeNum - b.universeNum;
      } else {
        return a.strandOffset - b.strandOffset;
      }
    }
  }

  private void activate(LX lx) {
    assert !this.activated;
    this.deviceLengths = new HashMap<>();
    this.edgeEntries.sort(new SortEdgeEntries());
    int currentUniverseNum = 0;
    int currentStrandOffset = -1;

    StringBuilder logString = new StringBuilder("sACN " + this.ipAddress + ": ");
    for (EdgeEntry edgeEntry : this.edgeEntries) {
      int edgeLength = edgeEntry.edge.points.length;
      if (edgeEntry.universeNum > currentUniverseNum) {
        currentUniverseNum = edgeEntry.universeNum;
        currentStrandOffset = 0;
        String deviceSummary = "#" + currentUniverseNum + " ";
        logString.append(deviceSummary);
      }
      assert edgeEntry.universeNum == currentUniverseNum;
      assert edgeEntry.strandOffset == currentStrandOffset;
      String edgeSummary = "[" + currentStrandOffset + ":Edge_" + edgeEntry.edge.id() + "=" + edgeLength + "] ";
      logString.append(edgeSummary);
      currentStrandOffset += edgeLength;
      this.deviceLengths.put(currentUniverseNum, currentStrandOffset);

      // FIXME: We're not doing anything with the strand offset.
      this.outputDevice = new StreamingACNDatagram(lx, edgeEntry.edge, currentUniverseNum);
      try {
        this.outputDevice.setAddress(InetAddress.getByName(this.ipAddress));
      } catch (UnknownHostException e) {
        throw new Error(e);
      }
      lx.addOutput(outputDevice);
    }
    LX.log(logString.toString());
    this.activated = true;
  }

  public static void activateAll(LX lx) {
    List<String> ips = new ArrayList<>(ipMap.keySet());
    Collections.sort(ips);
    for (String ip : ips) {
      ipMap.get(ip).activate(lx);
    }
  }
}
