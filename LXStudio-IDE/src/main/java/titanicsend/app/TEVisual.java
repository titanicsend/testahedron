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

  public final BooleanParameter vertexesVisible = new BooleanParameter("Vertexes Visible")
          .setDescription("Toggle whether vertexes are visible");

  public TEVisual(TEVehicleModel model) {
    super();
    this.model = model;
    addParameter("vertexesVisible", this.vertexesVisible);
  }

  @Override
  public void onDraw(UI ui, PGraphics pg) {
    boolean visible = vertexesVisible.getValueb();
    if (visible) {
      beginDraw(ui, pg);
      pg.noStroke();
      pg.textSize(40);
      for (Map.Entry<Integer,TEVertex> entry : model.vertexesById.entrySet()) {
        TEVertex v = entry.getValue();
        pg.pushMatrix();
        pg.translate(v.x, v.y, v.z);
        pg.ambientLight(255, 255, 255);
        pg.fill(255, 255, 255);
        pg.sphere(100000);
        pg.noLights();
        pg.scale(10000, -10000);
        pg.fill(128, 128, 128);
        pg.text(entry.getKey().toString(), 0, -20, 0);
        pg.popMatrix();
      }
      for (Map.Entry<String,TEPanelModel> entry : model.panelsById.entrySet()) {
        TEPanelModel p = entry.getValue();
        LXPoint centroid = p.points[0];
        pg.pushMatrix();
        pg.translate(centroid.x, centroid.y, centroid.z);
        pg.rotateY((float)(-Math.PI / 2.0));
        pg.scale(10000, -10000);
        pg.fill(255, 0, 0);
        pg.text(entry.getKey(), 0, 0, 0);
        pg.popMatrix();
      }
      endDraw(ui, pg);
    }
  }
}
