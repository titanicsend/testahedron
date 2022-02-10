package titanicsend.pattern.mike;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.color.LXSwatch;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.DiscreteParameter;
import titanicsend.model.TEPanelModel;
import titanicsend.pattern.TEPattern;
import titanicsend.util.TEColor;

import java.util.HashMap;
import java.util.Map;

@LXCategory("Testahedron")
public class Checkers extends TEPattern {
  private final HashMap<TEPanelModel, Integer> panelGroup;
  TEColor groupColor1;
  TEColor groupColor2;

  public Checkers(LX lx) {
    super(lx);

    this.groupColor1 = new TEColor(lx);
    this.groupColor2 = new TEColor(lx);
    
    addParameter("color1", this.groupColor1.color);
    addParameter("color1Mode", this.groupColor1.colorMode);
    addParameter("color1PaletteIndex", this.groupColor1.paletteIndex);

    addParameter("color2", this.groupColor2.color);
    addParameter("color2Mode", this.groupColor2.colorMode);
    addParameter("color2PaletteIndex", this.groupColor2.paletteIndex);

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
    setColors(this.groupColor1.getColor());
    setColors(this.groupColor2.getColor());

    for (Map.Entry<TEPanelModel, Integer> entry : this.panelGroup.entrySet()) {
      TEPanelModel panel = entry.getKey();
      int panelGroup = entry.getValue();
      TEColor groupColor = panelGroup == 0 ? this.groupColor1 : this.groupColor2;
      int rgb = groupColor.getColor();
      for (LXPoint point : panel.points) colors[point.index] = rgb;
    }
    this.updateVirtualColors();
  }
}