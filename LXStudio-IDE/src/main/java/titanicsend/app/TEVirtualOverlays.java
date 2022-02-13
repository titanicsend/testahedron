package titanicsend.app;

import java.util.*;

import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.transform.LXVector;
import heronarts.p4lx.ui.UI;
import processing.core.PGraphics;
import titanicsend.model.*;

public class TEVirtualOverlays extends TEUIComponent {
  TEWholeModel model;
  public static final int LASER_DISTANCE = 10000000; // 10,000,000 microns ~= 33 feet

  public final BooleanParameter vertexSpheresVisible =
          new BooleanParameter("Vertex Spheres")
                  .setDescription("Toggle whether vertex spheres are visible")
                  .setValue(true);

  public final BooleanParameter vertexLabelsVisible =
          new BooleanParameter("Vertex Labels")
                  .setDescription("Toggle whether vertex labels are visible")
                  .setValue(true);

  public final BooleanParameter panelLabelsVisible =
          new BooleanParameter("Panel Labels")
                  .setDescription("Toggle whether panel labels are visible")
                  .setValue(true);

  public final BooleanParameter unknownPanelsVisible =
          new BooleanParameter("Unk Panels")
                  .setDescription("Toggle whether unknown panels are visible")
                  .setValue(true);


  public TEVirtualOverlays(TEWholeModel model) {
    super();
    this.model = model;
    addParameter("vertexSpheresVisible", this.vertexSpheresVisible);
    addParameter("vertexLabelsVisible", this.vertexLabelsVisible);
    addParameter("panelLabelsVisible", this.panelLabelsVisible);
    addParameter("unknownPanelsVisible", this.unknownPanelsVisible);
  }

  @Override
  public void onDraw(UI ui, PGraphics pg) {
    beginDraw(ui, pg);
    pg.noStroke();
    pg.textSize(40);
    for (Map.Entry<Integer, TEVertex> entry : model.vertexesById.entrySet()) {
      TEVertex v = entry.getValue();
      pg.pushMatrix();
      pg.translate(v.x, v.y, v.z);
      pg.ambientLight(255, 255, 255);
      if (this.vertexSpheresVisible.getValueb() && v.virtualColor != null) {
        pg.fill(v.virtualColor.rgb, v.virtualColor.alpha);
        pg.sphere(100000);
      }
      pg.noLights();
      pg.scale(10000, -10000);
      pg.fill(128, 128, 128);
      if (this.vertexLabelsVisible.getValueb())
        pg.text(entry.getKey().toString(), 0, -20, 0);
      pg.popMatrix();
    }
    for (Map.Entry<String, TEPanelModel> entry : model.panelsById.entrySet()) {
      TEPanelModel p = entry.getValue();
      if (p.virtualColor != null) {
        // respect unknown panel rendering ui toggle.
        if (p.panelType.equals(TEPanelModel.UNKNOWN) && !this.unknownPanelsVisible.isOn()) {
          continue;
        }
        pg.fill(p.virtualColor.rgb, p.virtualColor.alpha);
        pg.beginShape();
        pg.vertex(p.v0.x, p.v0.y, p.v0.z);
        pg.vertex(p.v1.x, p.v1.y, p.v1.z);
        pg.vertex(p.v2.x, p.v2.y, p.v2.z);
        pg.endShape();
      }

      // Label each panel
      if (this.panelLabelsVisible.getValueb()) {
        pg.pushMatrix();
        LXVector centroid = p.centroid;
        pg.translate(centroid.x, centroid.y, centroid.z);
        //pg.rotateY((float) (-Math.PI / 2.0));  // Face port (non-show) side
        pg.rotateY((float) (Math.PI / 2.0));  // Face starboard (show) side

        pg.scale(10000, -10000);
        pg.fill(255, 0, 0);
        pg.text(entry.getKey(), 0, 0, -100000);
        pg.popMatrix();
      }
    }
    for (TELaserModel laser : model.lasers) {
      // Tried checking for LXColor.BLACK and LXColor.rgb(0,0,0) but neither worked. Weird.
      if (laser.color == 0) continue;

      pg.stroke(laser.color);

      double targetX = laser.origin.x + LASER_DISTANCE * Math.sin(laser.azimuth) * Math.cos(laser.elevation);
      double targetY = laser.origin.y + LASER_DISTANCE * Math.sin(laser.elevation);
      double targetZ = laser.origin.z + LASER_DISTANCE * Math.cos(laser.azimuth) * Math.cos(laser.elevation);
      pg.line(laser.origin.x, laser.origin.y, laser.origin.z, (float)targetX, (float)targetY, (float)targetZ);
    }
    endDraw(ui, pg);
  }
}
