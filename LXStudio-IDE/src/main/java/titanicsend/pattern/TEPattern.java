package titanicsend.pattern;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.pattern.LXModelPattern;
import titanicsend.model.TEEdgeModel;
import titanicsend.model.TELaserModel;
import titanicsend.model.TEPanelModel;
import titanicsend.model.TEWholeModel;

import java.util.*;

public abstract class TEPattern extends LXModelPattern<TEWholeModel> {
  private final TEPanelModel sua;
  private final TEPanelModel sdc;

  protected TEPattern(LX lx) {
    super(lx);
    this.clearPixels();
    this.sua = this.model.panelsById.get("SUA");
    this.sdc = this.model.panelsById.get("SDC");
  }

  public void clearPixels() {
    for (LXPoint point : this.model.points) {
      if (point.equals(this.model.gapPoint)) {
        // During construction, make gap points show up in red
        colors[this.model.gapPoint.index] = LXColor.rgb(255, 0, 0);
      } else {
        colors[point.index] = 0; // Transparent
      }
    }
  }

  // Make the virtual model's solid panels and lasers get rendered to match
  // their LXPoint color
  public void updateVirtualColors(double deltaMsec) {
    for (TEPanelModel panel : this.model.panelsById.values()) {
      if (panel.panelType.equals(TEPanelModel.SOLID)) {
        panel.virtualColor.rgb = colors[panel.points[0].index];
      }
    }
    for (TELaserModel laser : this.model.lasersById.values()) {
      laser.control.update(deltaMsec);
      laser.color = colors[laser.points[0].index];
    }
  }

  public List<LXPoint> getGigglePixelPoints() {
    List<LXPoint> rv = new ArrayList<>();

    if (this.sua != null) {
      int halfway = this.sua.points.length / 2;
      if (halfway < this.sua.points.length) rv.add(this.sua.points[halfway]);

      halfway = this.sua.e0.points.length / 2;
      if (halfway < this.sua.e0.points.length) rv.add(this.sua.e0.points[halfway]);
    }

    if (this.sdc != null) {
      int halfway = this.sdc.points.length / 2;
      if (halfway < this.sdc.points.length) rv.add(this.sdc.points[halfway]);

      halfway = this.sdc.e0.points.length / 2;
      if (halfway < this.sdc.e0.points.length) rv.add(this.sdc.e0.points[halfway]);
    }
    return rv;
  }
}
