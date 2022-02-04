package titanicsend.app;

import java.util.*;

import heronarts.lx.parameter.BooleanParameter;
import heronarts.p4lx.ui.UI;
import heronarts.p4lx.ui.UI3dComponent;
import heronarts.lx.model.LXPoint;
import processing.core.PGraphics;
import titanicsend.model.*;

public class TEVisual extends TEUIComponent {
  TEVehicleModel model;

<<<<<<< HEAD
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
=======
  public final BooleanParameter vertexesVisible = new BooleanParameter("Vertexes Visible")
          .setDescription("Toggle whether vertexes are visible");
>>>>>>> a0f12aef4cde228efe075282a2cb7c8b32bf1799

  public TEVisual(TEVehicleModel model) {
    super();
    this.model = model;
<<<<<<< HEAD
    addParameter("vertexSpheresVisible", this.vertexSpheresVisible);
    addParameter("vertexLabelsVisible", this.vertexLabelsVisible);
    addParameter("panelLabelsVisible", this.panelLabelsVisible);
=======
    addParameter("vertexesVisible", this.vertexesVisible);
>>>>>>> a0f12aef4cde228efe075282a2cb7c8b32bf1799
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
        LXPoint centroid = p.centroid;
        pg.translate(centroid.x, centroid.y, centroid.z);
        pg.rotateY((float) (-Math.PI / 2.0));
        pg.scale(10000, -10000);
        pg.fill(255, 0, 0);
        pg.text(entry.getKey(), 0, 0, -100000);
        pg.popMatrix();
      }
    }
    endDraw(ui, pg);
  }
}
