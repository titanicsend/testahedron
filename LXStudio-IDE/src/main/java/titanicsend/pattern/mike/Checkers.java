package titanicsend.pattern.mike;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXDeviceComponent;
import heronarts.lx.color.LXColor;
import heronarts.lx.color.LXSwatch;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.lx.pattern.color.SolidPattern;
import heronarts.lx.studio.LXStudio;
import heronarts.lx.studio.ui.device.UIDevice;
import heronarts.lx.studio.ui.device.UIDeviceControls;
import heronarts.lx.studio.ui.device.UIPatternDevice;
import heronarts.p4lx.ui.UI;
import heronarts.p4lx.ui.UI2dComponent;
import heronarts.p4lx.ui.UI2dContainer;
import heronarts.p4lx.ui.component.UIColorPicker;
import titanicsend.model.TEPanelModel;
import titanicsend.pattern.TEPattern;
import titanicsend.util.TEColor;

import java.util.HashMap;
import java.util.Map;

@LXCategory("Testahedron")
public class Checkers extends TEPattern implements UIDeviceControls<titanicsend.pattern.mike.Checkers> {
  private final HashMap<TEPanelModel, Integer> panelGroup;
  TEColor groupColor1;
  TEColor groupColor2;

  private class UIPaletteColor extends UI2dComponent {
    private UIPaletteColor(UI ui, Checkers pattern, TEColor teColor, float w, float h) {
      super(0, 0, w, h);
      setBorderColor(ui.theme.getControlDisabledColor());
      setBackgroundColor(teColor.getPaletteColor().getColor());
      addLoopTask((deltaMs) -> {
        setBackgroundColor(teColor.getPaletteColor().getColor());
      });
    }
  }

  public Checkers(LX lx) {
    super(lx);

    this.groupColor1 = new TEColor(lx);
    this.groupColor2 = new TEColor(lx);
    
    addParameter("color1", this.groupColor1.color);
    addParameter("color1Mode", this.groupColor1.colorMode);
    addParameter("color1PaletteIndex", this.groupColor1.paletteIndex);

    addParameter("color2", this.groupColor2.color);
    addParameter("color2Mode", this.groupColor2.colorMode);
    addParameter("color2PaletteIndex", this.groupColor2.paletteIndex);

    this.panelGroup = new HashMap<>();
    for (TEPanelModel panel : model.panelsById.values()) {
      if (!this.panelGroup.containsKey(panel)) {
        // If not yet grouped, put it in Group 0
        this.panelGroup.put(panel, 0);
      }
      int thisPanelGroup = this.panelGroup.get(panel);
      //LX.log("Panel " + panel.id + " is " + thisPanelGroup);
      int newColor = 1 - thisPanelGroup;  // Invert this panel's group
      for (TEPanelModel neighbor : panel.neighbors()) {
        if (this.panelGroup.containsKey(neighbor)) continue;  // Already grouped
        this.panelGroup.put(neighbor, newColor);
      }
    }
  }

  private void buildColorPicker(LXStudio.UI ui, UI2dContainer container, Checkers device, TEColor teColor) {
    final UI2dComponent
            paletteIndex,
            indexLabel,
            paletteColor,
            colorPicker,
            hueSlider,
            satSlider,
            brightSlider;

    container.addChildren(
            newDropMenu(teColor.colorMode),
            paletteColor = new UIPaletteColor(ui, device, teColor, COL_WIDTH, 28),
            paletteIndex = newIntegerBox(teColor.paletteIndex),
            indexLabel = controlLabel(ui, "Index"),

            colorPicker = new UIColorPicker(0, 0, COL_WIDTH, 28, teColor.color)
                    .setCorner(UIColorPicker.Corner.TOP_RIGHT),

            hueSlider = newHorizontalSlider(teColor.color.hue),
            satSlider = newHorizontalSlider(teColor.color.saturation),
            brightSlider = newHorizontalSlider(teColor.color.brightness)
    );

    final LXParameterListener update = (p) -> {
      boolean isCustom = teColor.colorMode.getEnum() == TEColor.ColorMode.CUSTOM;
      colorPicker.setVisible(isCustom);
      paletteColor.setVisible(!isCustom);
      paletteIndex.setVisible(!isCustom);
      indexLabel.setVisible(!isCustom);
      hueSlider.setVisible(isCustom);
      satSlider.setVisible(isCustom);
      brightSlider.setVisible(isCustom);
    };
    teColor.colorMode.addListener(update);
    update.onParameterChanged(null);
  }

  @Override
  public void buildDeviceControls(LXStudio.UI ui, UIDevice uiDevice, Checkers pattern) {
    uiDevice.setLayout(UI2dContainer.Layout.HORIZONTAL);
    uiDevice.setChildSpacing(6);
    uiDevice.setContentWidth(COL_WIDTH * 2 + 6);

    UI2dContainer column1 = UI2dContainer.newVerticalContainer(COL_WIDTH, 6);
    UI2dContainer column2 = UI2dContainer.newVerticalContainer(COL_WIDTH, 6);
    column1.addToContainer(uiDevice);
    column2.addToContainer(uiDevice);
    buildColorPicker(ui, column1, pattern, pattern.groupColor1);
    buildColorPicker(ui, column2, pattern, pattern.groupColor2);
  }

  public void run(double deltaMs) {
    setColors(this.groupColor1.getColor());
    setColors(this.groupColor2.getColor());

    for (Map.Entry<TEPanelModel, Integer> entry : this.panelGroup.entrySet()) {
      TEPanelModel panel = entry.getKey();
      int panelGroup = entry.getValue();
      TEColor groupColor = panelGroup == 0 ? this.groupColor1 : this.groupColor2;
      int rgb = groupColor.getColor();
      for (LXPoint point : panel.points) colors[point.index] = rgb;
    }
    this.updateVirtualColors();
  }
}