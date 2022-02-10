/**
 * This file is basically lx/pattern/color/SolidPattern.java
 * but broken out so that it can be embedded into a pattern that can't
 * be a subclass of SolidPattern. Original copyright notice follows:
 *
 * Copyright 2013- Mark C. Slee, Heron Arts LLC
 *
 * This file is part of the LX Studio software library. By using
 * LX, you agree to the terms of the LX Studio Software License
 * and Distribution Agreement, available at: http://lx.studio/license
 *
 * Please note that the LX license is not open-source. The license
 * allows for free, non-commercial use.
 *
 * HERON ARTS MAKES NO WARRANTY, EXPRESS, IMPLIED, STATUTORY, OR
 * OTHERWISE, AND SPECIFICALLY DISCLAIMS ANY WARRANTY OF
 * MERCHANTABILITY, NON-INFRINGEMENT, OR FITNESS FOR A PARTICULAR
 * PURPOSE, WITH RESPECT TO THE SOFTWARE.
 *
 * @author Mark C. Slee <mark@heronarts.com>
 */

package titanicsend.util;

import heronarts.lx.LX;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.color.LXDynamicColor;
import heronarts.lx.color.LXSwatch;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.EnumParameter;

  /*
    Patterns creating a `TEColor color1` should call:
      addParameter("color1", color1.color);
      addParameter("color1Mode", color1.colorMode);
      addParameter("color1PaletteIndex", color1.paletteIndex);
    and then, in their run(),
      colors[somePoint] = color1.getColor();
   */

public class TEColor {
  LX lx;

  public enum ColorMode {
    CUSTOM("Custom"),
    PALETTE("Palette");

    public final String label;

    ColorMode(String label) {
      this.label = label;
    }

    @Override
    public String toString() {
      return this.label;
    }
  }

  public final EnumParameter<ColorMode> colorMode =
          new EnumParameter<ColorMode>("Color Mode", ColorMode.PALETTE)
                  .setDescription("Set the color here, or pull from the current palette?");

  public final ColorParameter color =
          new ColorParameter("Color")
                  .setDescription("Custom color to use instead of the palette");

  public final DiscreteParameter paletteIndex =
          new DiscreteParameter("Index", 1, LXSwatch.MAX_COLORS + 1)
                  .setDescription("Offset into the palette to start at");

  public TEColor(LX lx) {
    this.lx = lx;
  }

  public LXDynamicColor getPaletteColor() {
    return this.lx.engine.palette.getSwatchColor(this.paletteIndex.getValuei() - 1);
  }

  public int getColor() {
    switch (this.colorMode.getEnum()) {
      case PALETTE:
        return getPaletteColor().getColor();
      case CUSTOM:
        // Need to check every time, because there may be modulators applied
        return LXColor.hsb(
                this.color.hue.getValue(),
                this.color.saturation.getValue(),
                this.color.brightness.getValue()
        );
      default:
        throw new Error("Broken colorMode");
    }
  }
}