package titanicsend.app;

import java.util.*;
import heronarts.p4lx.ui.UI;
import heronarts.p4lx.ui.UI3dComponent;
import processing.core.PGraphics;
import titanicsend.model.*;

public class TEVisual extends UI3dComponent {
  TEVehicleModel model;

  public TEVisual(TEVehicleModel model) {
    super();
    this.model = model;
  }

  @Override
  public void onDraw(UI ui, PGraphics pg) {
    if (isVisible()) {
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
      endDraw(ui, pg);
    }
  }
}
