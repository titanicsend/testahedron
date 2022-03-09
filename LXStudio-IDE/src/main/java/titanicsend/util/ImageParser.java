package titanicsend.util;

import heronarts.lx.LX;

import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import titanicsend.app.TEVirtualColor;
import titanicsend.util.ParsedImage;

// ImageParser takes an image file and parses it into an array of color points that can be mapped
// to a two-dimensional array of pixels on a model.
public class ImageParser {
    private String filepath;

    public ImageParser(String filepath) {
        this.filepath = filepath;
    }

    public ParsedImage parse() throws Exception {
        File file = new File(this.filepath);
        BufferedImage img = ImageIO.read(file);

        int width = img.getWidth();
        int height = img.getHeight();
        LX.log("ImageParser.parse:");
        LX.log(String.format("Image height: %d; width: %d", height, width));
        ParsedImage parsedImage = new ParsedImage(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // TODO: fetch alpha values from img as well
                TEVirtualColor color = new TEVirtualColor(img.getRGB(i,j), 1.0f);
                parsedImage.setPixel(i, j, color);
            }
        }
        return parsedImage;
    }
}