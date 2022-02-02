/**
 * @author Travis Cline <travis.cline@gmail.com>
 */

package titanicsend.pattern.tmc;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.pattern.LXPattern;

/**
 * SimpleSolidPattern shows a trivial pattern that accepts input to control the output color pattern.
 */
@LXCategory("Testahedron")
public class SimpleSolidPattern extends LXPattern {

    public final ColorParameter color =
            new ColorParameter("Color")
                    .setDescription("Color of the pattern");

    public SimpleSolidPattern(LX lx) {
        this(lx, LXColor.RED);
    }

    public SimpleSolidPattern(LX lx, int color) {
        super(lx);
        this.color.setColor(color);
        addParameter("color", this.color);
    }

    @Override
    public void run(double deltaMs) {
        setColors(LXColor.hsb(
                this.color.hue.getValue(),
                this.color.saturation.getValue(),
                this.color.brightness.getValue()
        ));
    }
}
