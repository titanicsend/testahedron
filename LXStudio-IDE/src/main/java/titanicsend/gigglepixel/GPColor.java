package titanicsend.gigglepixel;

import heronarts.lx.color.LXColor;

public class GPColor {
  int r;
  int g;
  int b;
  int frac;  // Fraction of 256

  public GPColor(int r, int g, int b, int frac) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.frac = frac;
  }

  public int rgb() {
    return LXColor.rgb(this.r, this.g, this.b);
  }

  public String repr() {
    return r + "," + g + "," + b + " frac";
  }
}
