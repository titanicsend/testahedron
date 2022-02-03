package titanicsend.app;

import heronarts.p4lx.ui.UI;
import heronarts.p4lx.ui.UI3dComponent;
import heronarts.p4lx.ui.UIObject;
import processing.core.PGraphics;

public class TEVisual extends UI3dComponent {
  @Override
  public void onDraw(UI ui, PGraphics pg) {
    //if (isVisible()) {
      //beginDraw(ui, pg);

      pg.ambientLight(40, 40, 40);
      pg.translate(1, 1, 1);
      pg.noStroke();
      pg.fill(0xff00ddff);
      pg.sphere(50);
      pg.noLights();
      pg.scale(1, -1);
      pg.textSize(4);
      pg.text("hi", 10, 10);
      //endDraw(ui, pg);
    }
  //}
}
