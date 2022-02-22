package titanicsend.util;

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

    private ParsedImage parse() throws Exception {
        File file = new File(this.filepath);
        BufferedImage img = ImageIO.read(file);

        int width = img.getWidth();
        int height = img.getHeight();
        ParsedImage parsedImage = new ParsedImage(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // TODO: fetch alpha values from img as well
                TEVirtualColor color = new TEVirtualColor(img.getRGB(x,y), 1.0f);
                parsedImage.setPixel(x, y, color);
            }
        }
        return parsedImage;
    }
}