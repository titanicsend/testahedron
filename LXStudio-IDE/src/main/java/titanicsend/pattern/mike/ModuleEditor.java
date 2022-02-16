package titanicsend.pattern.mike;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.StringParameter;
import heronarts.lx.studio.LXStudio;
import heronarts.lx.studio.ui.device.UIDevice;
import heronarts.lx.studio.ui.device.UIDeviceControls;
import heronarts.p4lx.ui.UI2dComponent;
import heronarts.p4lx.ui.UI2dContainer;
import heronarts.p4lx.ui.component.UIButton;
import heronarts.p4lx.ui.component.UIParameterControl;
import heronarts.p4lx.ui.component.UISwitch;
import heronarts.p4lx.ui.component.UITextBox;
import titanicsend.model.TEEdgeModel;
import titanicsend.model.TEVertex;
import titanicsend.model.TEWholeModel;
import titanicsend.pattern.TEPattern;

import java.util.*;

public class ModuleEditor extends TEPattern implements UIDeviceControls<ModuleEditor> {
  Map<Integer, String> configsByModNum;
  Map<TEEdgeModel, Integer> modNumsByEdge;
  Map<Integer, List<TEEdgeModel>> edgesByModNum;

  public final DiscreteParameter moduleNumber =
          new DiscreteParameter("Mod #", 1, 99)
                  .setDescription("Module ID");

  public final StringParameter moduleParts =
          new StringParameter("Parts")
                  .setDescription("Vertex/Edges in this module");

  private UI2dComponent partsErr;
  private UI2dComponent dupeErr;

  public ModuleEditor(LX lx) {
    super(lx);
    this.configsByModNum = new HashMap<>();
    this.modNumsByEdge = new HashMap<>();
    this.edgesByModNum = new HashMap<>();
  }

  @Override
  public void buildDeviceControls(LXStudio.UI ui, UIDevice uiDevice, ModuleEditor pattern) {
    uiDevice.setLayout(UI2dContainer.Layout.VERTICAL);
    uiDevice.setChildSpacing(6);
    uiDevice.setContentWidth(COL_WIDTH);

    UI2dComponent ibModNumber;
    UITextBox tbModParts;
    UIParameterControl loadSwitch;

    uiDevice.addChildren(
            controlLabel(ui, "Mod #"),
            ibModNumber = newIntegerBox(moduleNumber),
            controlLabel(ui, "Parts"),
            tbModParts = new UITextBox(0, 0, COL_WIDTH, 16).setParameter(moduleParts),
            this.partsErr = controlLabel(ui, "Bad parts"),
            this.dupeErr = controlLabel(ui, "Dupe"),
            new UIButton(0, 0, COL_WIDTH, 20) {
              @Override
              public void onToggle(boolean on) {
                if (on) {
                  load();
                }
              }
            }
            .setLabel("Load").setMomentary(true)
    );

    tbModParts.setEmptyValueAllowed(true);
    this.partsErr.setVisible(false);
    this.dupeErr.setVisible(false);

    moduleNumber.addListener(this::loadParts);
    moduleParts.addListener(this::setParts);
  }

  private void load() {
    this.configsByModNum = new HashMap<>();
    this.modNumsByEdge = new HashMap<>();
    this.edgesByModNum = new HashMap<>();
    Scanner s = this.model.loadFile("modules.txt");
    while (s.hasNextLine()) {
      String line = s.nextLine();
      String[] tokens = line.split("\\s+");
      assert tokens.length >= 2;
      int modNum = Integer.parseInt(tokens[0]);
    }
  }

  public void loadParts(LXParameter unused) {
    Integer modNum = this.moduleNumber.getValuei();
    moduleParts.setValue(this.configsByModNum.getOrDefault(modNum, ""));
  }

  public void setParts(LXParameter unused) {
    String partStr = this.moduleParts.getString();

    this.dupeErr.setVisible(false);

    // Set to true so we can just return in the event of a problem
    this.partsErr.setVisible(true);

    List<TEEdgeModel> edges = new ArrayList<>();

    String[] tokens = partStr.split(",");
    for (String token : tokens) {
      if (token.equals("")) continue;
      token = token.strip();
      String[] subTokens = token.split("-");
      if (subTokens.length == 1) {
        int id;
        try {
          id = Integer.parseInt(token);
        } catch (NumberFormatException e) {
          return;
        }
        TEVertex vertex = this.model.vertexesById.getOrDefault(id, null);
        if (vertex == null) return;
        edges.addAll(vertex.edges);
      } else if (subTokens.length == 2) {
        TEEdgeModel edge = this.model.edgesById.getOrDefault(token, null);
        if (edge == null) return;
        edges.add(edge);
      } else {
        return;
      }
    }

    int modNum = this.moduleNumber.getValuei();

    for (TEEdgeModel edge : edges) {
      int existing = this.modNumsByEdge.getOrDefault(edge, modNum);
      if (modNum != existing) {
        this.dupeErr.setVisible(true);
        return;
      }
    }

    for (TEEdgeModel edge : this.edgesByModNum.getOrDefault(modNum, new ArrayList<>())) {
      this.modNumsByEdge.remove(edge);
    }

    for (TEEdgeModel edge : edges) {
      this.modNumsByEdge.put(edge, modNum);
    }

    this.edgesByModNum.put(modNum, edges);

    this.configsByModNum.put(modNum, partStr);
    this.partsErr.setVisible(false);

    repaint();
  }

  public void repaint() {
    this.clearPixels();
    for (Map.Entry<TEEdgeModel, Integer> entry : this.modNumsByEdge.entrySet()) {
      for (LXPoint point : entry.getKey().points) {
        int hue = (entry.getValue() * 15) % 360;
        colors[point.index] = LXColor.hsb(hue, 100, 100);
      }
    }
  }

  public void run(double deltaMs) {
  }
}