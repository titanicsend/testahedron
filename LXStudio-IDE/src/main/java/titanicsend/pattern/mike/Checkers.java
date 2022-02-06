package titanicsend.pattern.mike;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import titanicsend.model.TEPanelModel;
import titanicsend.pattern.TEPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@LXCategory("Testahedron")
public class Checkers extends TEPattern {
  private final HashMap<TEPanelModel, Integer> panelColors;

  public Checkers(LX lx) {
    super(lx);
    this.panelColors = new HashMap<>();
    for (TEPanelModel panel : model.panelsById.values()) {
      if (!this.panelColors.containsKey(panel)) {
        // If not yet colored, color it 0
        this.panelColors.put(panel, 0);
      }
      int thisPanelColor = this.panelColors.get(panel);
      //LX.log("Panel " + panel.id + " is " + thisPanelColor);
      int newColor = 1 - thisPanelColor;  // Invert this panel's color
      for (TEPanelModel neighbor : panel.neighbors()) {
        if (this.panelColors.containsKey(neighbor)) continue;  // Already colored
        this.panelColors.put(neighbor, newColor);
      }
    }
  }

  public void run(double deltaMs) {
    for (Map.Entry<TEPanelModel, Integer> entry : this.panelColors.entrySet()) {
      TEPanelModel panel = entry.getKey();
      int panelColor = entry.getValue();
      int rgb = panelColor == 0 ? LXColor.rgb(249, 64, 97) : LXColor.rgb(249, 229, 237);
      for (LXPoint point : panel.points) colors[point.index] = rgb;
    }
    this.updateVirtualColors();
  }
}