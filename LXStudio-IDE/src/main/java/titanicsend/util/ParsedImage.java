package titanicsend.util;

import titanicsend.app.TEVirtualColor;

// A ParsedImage is a representation of a two-dimensional image with color data mapped to pixels.
// The top-most left-hand corner is 0,0; so each row reads left to right, then top to bottom.
public static class ParsedImage {
    public TEVirtualColor[][] data;

    ParsedImage(int width, int height) {
        this.data = new TEVirtualColor[width][height];
    }

    public void setPixel(int x, int y, TEVirtualColor color) {
        this.data[x][y] = color;
    }
}