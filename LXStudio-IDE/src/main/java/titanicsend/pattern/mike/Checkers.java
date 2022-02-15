package titanicsend.pattern.mike;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import titanicsend.model.TEPanelModel;
import titanicsend.util.TEColorGroupPattern;

import java.util.HashMap;
import java.util.Map;

@LXCategory("Testahedron")
public class Checkers extends TEColorGroupPattern {
  private final HashMap<TEPanelModel, Integer> panelGroup;

  public Checkers(LX lx) {
    super(lx, new String[]{"ColorA", "ColorB"});

    this.panelGroup = new HashMap<>();
    for (TEPanelModel panel : model.panelsById.values()) {
      if (!this.panelGroup.containsKey(panel)) {
        // If not yet grouped, put it in Group 0
        this.panelGroup.put(panel, 0);
      }
      int thisPanelGroup = this.panelGroup.get(panel);
      //LX.log("Panel " + panel.id + " is " + thisPanelGroup);
      int newColor = 1 - thisPanelGroup;  // Invert this panel's group
      for (TEPanelModel neighbor : panel.neighbors()) {
        if (this.panelGroup.containsKey(neighbor)) continue;  // Already grouped
        this.panelGroup.put(neighbor, newColor);
      }
    }
  }


  public void run(double deltaMs) {
    int color0 = this.teColors.get(0).getColor();
    int color1 = this.teColors.get(1).getColor();

    for (Map.Entry<TEPanelModel, Integer> entry : this.panelGroup.entrySet()) {
      TEPanelModel panel = entry.getKey();
      int panelGroup = entry.getValue();
      int rgb = panelGroup == 0 ? color0 : color1;
      for (LXPoint point : panel.points) colors[point.index] = rgb;
    }
    this.updateVirtualColors(deltaMs);
  }
}