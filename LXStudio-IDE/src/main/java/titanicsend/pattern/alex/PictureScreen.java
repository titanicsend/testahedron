package titanicsend.pattern.alex;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.StringParameter;
import heronarts.lx.studio.LXStudio;
import heronarts.p4lx.ui.component.UIButton;
import heronarts.lx.studio.ui.device.UIDevice;
import heronarts.lx.studio.ui.device.UIDeviceControls;
import heronarts.p4lx.ui.component.UITextBox;
import heronarts.p4lx.ui.UI2dContainer;

import titanicsend.pattern.TEPattern;
import titanicsend.util.ImageParser;
import titanicsend.util.ParsedImage;
import titanicsend.util.SimpleScreen;

import java.util.ArrayList;
import java.util.*;

@LXCategory("Testahedron")
// A PictureScreen displays an image loaded in the UI from `resources/<geometry>/` and displays it.
public class PictureScreen extends TEPattern implements UIDeviceControls<PictureScreen> {
    private SimpleScreen screen;
    private ParsedImage parsedImage;

    // Technically, we do have doubles, but the values are in microns, so if you really need a fraction
    // of a micron, you can figure out how to do this with BoundedParameters instead.
    // Note: extra +1 is because DiscreteParameters have an _exclusive_ bound on the upper end.
    private int roundedLowerYLimit = (int)this.model.boundaryPoints.minYBoundaryPoint.y;
    private int roundedUpperYLimit = (int)this.model.boundaryPoints.maxYBoundaryPoint.y + 1;
    private int roundedLowerZLimit = (int)this.model.boundaryPoints.minZBoundaryPoint.z;
    private int roundedUpperZLimit = (int)this.model.boundaryPoints.maxZBoundaryPoint.z + 1;

    public final DiscreteParameter lowerYBoundParam =
            new DiscreteParameter("Lower Y Bound", this.roundedLowerYLimit / 2, this.roundedLowerYLimit, this.roundedUpperYLimit)
                    .setDescription("Lower boundary for the Y coordinate of the screen");
    public final DiscreteParameter upperYBoundParam =
            new DiscreteParameter("Upper Y Bound", this.roundedUpperYLimit / 2, this.roundedLowerYLimit, this.roundedUpperYLimit)
                    .setDescription("Upper boundary for the Y coordinate of the screen");
    public final DiscreteParameter lowerZBoundParam =
            new DiscreteParameter("Lower Z Bound", this.roundedLowerZLimit / 2, this.roundedLowerZLimit, this.roundedUpperZLimit)
                    .setDescription("Lower boundary for the Z coordinate of the screen");
    public final DiscreteParameter upperZBoundParam =
            new DiscreteParameter("Upper Z Bound", this.roundedUpperZLimit / 2, this.roundedLowerZLimit, this.roundedUpperZLimit)
                    .setDescription("Upper boundary for the Z coordinate of the screen");
    public BooleanParameter doubleSidedParam =
            new BooleanParameter("Double Sided?")
                    .setDescription("Toggle whether screen is drawn on both sides of the car or not (Default false)")
                    .setValue(false);
    public BooleanParameter disableEdgesParam =
            new BooleanParameter("Disable Edges?")
                    .setDescription("Toggle whether screen is drawn on pixels along the edges (Default false)")
                    .setValue(false);
    public final StringParameter photoPathParam =
          new StringParameter("Photo path")
                  .setDescription("Relative file path to load image data from");


    private void toggleDoubleSided() {
        this.doubleSidedParam.setValue(!this.doubleSidedParam.getValueb());
    }
    private void toggleDisableEdges() {
        this.disableEdgesParam.setValue(!this.disableEdgesParam.getValueb());
    }

    @Override
    public void buildDeviceControls(LXStudio.UI ui, UIDevice uiDevice, PictureScreen pattern) {
        uiDevice.setLayout(UI2dContainer.Layout.VERTICAL);
        uiDevice.setChildSpacing(5);

        UITextBox pictureLoadPathBox;

        uiDevice.addChildren(
            // TODO: add back space for these
            // controlLabel(ui, "Lower Y Bound"),
            // newIntegerBox(this.lowerYBoundParam),
            // controlLabel(ui, "Upper Y Bound"),
            // newIntegerBox(this.upperYBoundParam),
            // controlLabel(ui, "Lower Z Bound"),
            // newIntegerBox(this.lowerZBoundParam), 
            // controlLabel(ui, "Upper Z Bound"),
            // newIntegerBox(this.upperZBoundParam),
            // controlLabel(ui, "Picture to Load"),
            pictureLoadPathBox = new UITextBox(0, 0, COL_WIDTH * 2, 16).setParameter(photoPathParam),
            new UIButton(0, 0, COL_WIDTH, 20) {
              @Override
              public void onToggle(boolean on) {
                if (on) {
                  load();
                }
              }
            }
            .setLabel("Load").setMomentary(true));
            // TODO: add space for this button
            // new UIButton(0, 0, COL_WIDTH, 20) {
            //     @Override
            //     public void onToggle(boolean unused) {
            //         toggleDoubleSided();
            //     }
            // }
            // .setLabel("Double Sided?"));
            // TODO: reconfigure UI to make this button fit
        //    new UIButton(0, 0, COL_WIDTH, 20) {
        //        @Override
        //        public void onToggle(boolean unused) {
        //            toggleDisableEdges();
        //        }
        //    }
        //    .setLabel("Disable Edges?"));


        pictureLoadPathBox.setEmptyValueAllowed(true);
        this.lowerYBoundParam.addListener(this::repaint);
        this.upperYBoundParam.addListener(this::repaint);
        this.lowerZBoundParam.addListener(this::repaint);
        this.upperZBoundParam.addListener(this::repaint);
        this.doubleSidedParam.addListener(this::repaint);
        this.disableEdgesParam.addListener(this::repaint);
        this.photoPathParam.addListener(this::repaint);
    }

    private void load() {
        if (photoPathParam.getString() == "") {
            return;
        }

        String filepath = this.model.subdir + "/" + photoPathParam.getString();
        LX.log(String.format("Loading file from filepath: %s", filepath));
        ImageParser parser = new ImageParser(filepath);
        try {
            this.parsedImage = parser.parse();
        } catch (Exception e) {
            throw new Error(e.toString());
        }
    }

    // TODO: refactor; too much duplication.
    private void paint(double deltaMs) {
        assert this.screen != null;
        assert this.screen.screenGrid != null;
        LX.log("PictureScreen.paint");
        LX.log(String.format("  this.screen.screenGrid.size: %d", this.screen.screenGrid.size()));

        if (!this.photoPathParam.getString().isEmpty()) {
            if (this.parsedImage == null) {
                return;
            }
            LX.log(String.format("  parsedImage.data.length: %d", parsedImage.data.length));
            LX.log(String.format("  parsedImage.data[0].length: %d", parsedImage.data[0].length));

            //assert this.parsedImage.data.length == this.screen.screenGrid.size();
            //assert this.parsedImage.data[0].length == this.screen.screenGrid.get(0).size();

            for (int z = 0; z < this.screen.screenGrid.size() - 1; z++) {
                LX.log(String.format("  this.screen.screenGrid.get(z).size(): %d", this.screen.screenGrid.get(z).size()));
                LX.log(String.format("  this.parsedImage[z].length %d", this.parsedImage.data[z].length));
                for (int y = 0; y < this.screen.screenGrid.get(z).size() - 1; y++) {
                    LXPoint currentPixel = this.screen.screenGrid.get(z).get(y);

                    if (this.disableEdgesParam.getValueb() && this.model.edgePoints.contains(currentPixel)) {
                        colors[currentPixel.index] = LXColor.BLACK;
                    } else {
                        colors[currentPixel.index] = this.parsedImage.data[z][y].rgb;
                    }
                }
            }

        } else {
            for (int z = 0; z < this.screen.screenGrid.size(); z++) {
                LX.log(String.format("  this.screen.screenGrid.get(z).size: %d", this.screen.screenGrid.get(z).size()));
                for (int y = 0; y < this.screen.screenGrid.get(z).size(); y++) {
                    LXPoint currentPixel = this.screen.screenGrid.get(z).get(y);

                    if (this.disableEdgesParam.getValueb() && this.model.edgePoints.contains(currentPixel)) {g
                        colors[currentPixel.index] = LXColor.BLACK;
                    } else {
                        colors[currentPixel.index] = LXColor.WHITE;
                    }

                }
            }
        }
    }

    private void sizeAndPaintScreen() {
        ArrayList<LXPoint> pointsList = new ArrayList<>(Arrays.asList(this.model.points));
        this.screen = new SimpleScreen(
            pointsList,
            this.lowerYBoundParam.getValuei(),
            this.upperYBoundParam.getValuei(),
            this.lowerZBoundParam.getValuei(),
            this.upperZBoundParam.getValuei(),
            this.doubleSidedParam.getValueb());

        this.paint(0);
    }

    public void repaint(LXParameter unused) {
        this.clearPixels();
        this.sizeAndPaintScreen();
    }

    public PictureScreen(LX lx) {
        super(lx);
        this.sizeAndPaintScreen();
    }

    public void run(double deltaMs) {
        this.paint(deltaMs);
    }
}