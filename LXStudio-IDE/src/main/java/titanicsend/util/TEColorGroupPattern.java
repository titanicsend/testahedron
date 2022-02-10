package titanicsend.util;

import heronarts.lx.LX;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.lx.studio.LXStudio;
import heronarts.lx.studio.ui.device.UIDevice;
import heronarts.lx.studio.ui.device.UIDeviceControls;
import heronarts.p4lx.ui.UI;
import heronarts.p4lx.ui.UI2dComponent;
import heronarts.p4lx.ui.UI2dContainer;
import heronarts.p4lx.ui.component.UIColorPicker;
import titanicsend.pattern.TEPattern;

import java.util.*;

public abstract class TEColorGroupPattern extends TEPattern implements UIDeviceControls<TEColorGroupPattern> {
  public List<TEColor> teColors;
  public int numColors;

  public TEColorGroupPattern(LX lx, int numColors) {
    super(lx);
    assert numColors <= 5;

    this.teColors = new ArrayList<>();
    this.numColors = numColors;
    for (int i = 0; i < numColors; i++) {
      TEColor teColor = new TEColor(lx);
      String prefix = "color" + i;
      addParameter(prefix, teColor.color);
      addParameter(prefix + "Mode", teColor.colorMode);
      addParameter(prefix + "PaletteIndex", teColor.paletteIndex);
      teColor.paletteIndex.setValue(i + 1);
      this.teColors.add(teColor);
    }
  }

  private static class UIPaletteColor extends UI2dComponent {
    private UIPaletteColor(UI ui, TEColorGroupPattern pattern, TEColor teColor, float w, float h) {
      super(0, 0, w, h);
      setBorderColor(ui.theme.getControlDisabledColor());
      setBackgroundColor(teColor.getPaletteColor().getColor());
      addLoopTask((deltaMs) -> {
        setBackgroundColor(teColor.getPaletteColor().getColor());
      });
    }
  }

  private void buildColorPicker(LXStudio.UI ui, UI2dContainer container, TEColor teColor) {
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
            paletteColor = new UIPaletteColor(ui, this, teColor, COL_WIDTH, 28),
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
  public void buildDeviceControls(LXStudio.UI ui, UIDevice uiDevice, TEColorGroupPattern pattern) {
    uiDevice.setLayout(UI2dContainer.Layout.HORIZONTAL);
    uiDevice.setChildSpacing(6);
    uiDevice.setContentWidth(COL_WIDTH * this.numColors + 6 * (this.numColors - 1));

    for (int i = 0; i < this.numColors; i++) {
      UI2dContainer column = UI2dContainer.newVerticalContainer(COL_WIDTH, 6);
      column.addToContainer(uiDevice);
      buildColorPicker(ui, column, this.teColors.get(i));
    }
  }
}