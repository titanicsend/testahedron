/**
 * @author Travis Cline <travis.cline@gmail.com>
 */

package titanicsend.pattern.tmc;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import titanicsend.model.TEPanelModel;
import titanicsend.pattern.TEPattern;

/**
 * SimpleSolidEdgePattern is a trivial pattern that accepts input to
 * control the output color of just the edges in the model.
 */
@LXCategory("Testahedron")
public class SimpleSolidEdgePattern extends TEPattern {

  public final ColorParameter color =
          new ColorParameter("Color")
                  .setDescription("Color of the pattern");

  public SimpleSolidEdgePattern(LX lx) {
    this(lx, LXColor.RED);
  }

  public SimpleSolidEdgePattern(LX lx, int color) {
    super(lx);
    this.color.setColor(color);
    addParameter("color", this.color);
  }

  @Override
  public void run(double deltaMs) {
    int color = LXColor.hsb(
            this.color.hue.getValue(),
            this.color.saturation.getValue(),
            this.color.brightness.getValue()
    );
    for (LXPoint point : this.model.edgePoints) {
      colors[point.index] = color;
    }
  }
}
