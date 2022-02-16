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
import titanicsend.app.TEVirtualColor;
import titanicsend.model.TEEdgeModel;
import titanicsend.model.TEVertex;
import titanicsend.pattern.PeriodicPattern;

import java.util.*;

public class ModuleEditor extends PeriodicPattern implements UIDeviceControls<ModuleEditor> {
  private final Map<Integer, String> configsByModNum;
  private final Map<TEEdgeModel, Integer> modNumsByEdge;
  private final Map<Integer, List<TEEdgeModel>> edgesByModNum;

  private static class Link {
    TEEdgeModel edge;
    boolean fwd;

    Link(TEEdgeModel edge, boolean fwd) {
      this.edge = edge;
      this.fwd = fwd;
    }
  }
  private final Map<Integer, List<List<Link>>> routesByModule;

  private static final double MOVE_PERIOD_MSEC = 50.0;

  public final DiscreteParameter moduleNumber =
          new DiscreteParameter("Mod #", 1, 99)
                  .setDescription("Module ID");

  public final StringParameter moduleParts =
          new StringParameter("Parts")
                  .setDescription("Vertex/Edges in this module");

  private UI2dComponent partsErr;
  private UI2dComponent dupeErr;
  private int phase;

  public ModuleEditor(LX lx) {
    super(lx);
    this.configsByModNum = new HashMap<>();
    this.modNumsByEdge = new HashMap<>();
    this.edgesByModNum = new HashMap<>();
    this.routesByModule = new HashMap<>();
    super.register(this::moveDots, MOVE_PERIOD_MSEC);
    phase = 0;
  }

  @Override
  public void buildDeviceControls(LXStudio.UI ui, UIDevice uiDevice, ModuleEditor pattern) {
    uiDevice.setLayout(UI2dContainer.Layout.VERTICAL);
    uiDevice.setChildSpacing(6);
    uiDevice.setContentWidth(COL_WIDTH * 2);

    UITextBox tbModParts;
    UIParameterControl loadSwitch;

    uiDevice.addChildren(
            controlLabel(ui, "Mod #"),
            newIntegerBox(moduleNumber),
            controlLabel(ui, "Parts"),
            tbModParts = new UITextBox(0, 0, COL_WIDTH * 2, 16).setParameter(moduleParts),
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

  private TEVertex vertexByString(String idStr) {
    int id;
    try {
      id = Integer.parseInt(idStr);
    } catch (NumberFormatException e) {
      return null;
    }
    return this.model.vertexesById.getOrDefault(id, null);
  }

  public void setParts(LXParameter unused) {
    String partStr = this.moduleParts.getString();

    this.dupeErr.setVisible(false);

    // Set to true so we can just return in the event of a problem
    this.partsErr.setVisible(true);

    List<TEEdgeModel> edges = new ArrayList<>();

    String[] tokens = partStr.split(",");
    List<List<Link>> routes = new ArrayList<>();
    for (String token : tokens) {
      List<Link> route = new ArrayList<>();

      token = token.strip();
      List<String> subTokens = new ArrayList<>(Arrays.asList(token.split("-")));
      if (subTokens.size() == 0) continue;
      TEVertex vCurr = vertexByString(subTokens.remove(0));
      if (vCurr == null) return;
      while (!subTokens.isEmpty()) {
        TEVertex vNext = vertexByString(subTokens.remove(0));
        if (vNext == null) return;

        boolean fwd;
        TEEdgeModel edge;
        if (vCurr.id < vNext.id) {
          fwd = true;
          edge = this.model.edgesById.getOrDefault(vCurr.id + "-" + vNext.id, null);
        } else {
          fwd = false;
          edge = this.model.edgesById.getOrDefault(vNext.id + "-" + vCurr.id, null);
        }
        if (edge == null) return;
        edges.add(edge);
        route.add(new Link(edge, fwd));
        vCurr = vNext;
      }
      routes.add(route);
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

    this.routesByModule.put(modNum, routes);
    this.edgesByModNum.put(modNum, edges);

    this.configsByModNum.put(modNum, partStr);
    this.partsErr.setVisible(false);
  }

  public void moveDots() {
    this.clearPixels();
    if (phase % 10 < 3) {
      for (TEEdgeModel edge : this.model.edgesById.values()) {
        for (LXPoint point : edge.points) {
          colors[point.index] = LXColor.rgb(150, 150, 150);
        }
      }
    }
    for (Map.Entry<Integer, List<List<Link>>> entry : this.routesByModule.entrySet()) {
      int hue = ((entry.getKey() - 1) * 15) % 360;
      int i = 0;
      for (List<Link> listOfLinks : entry.getValue()) {
        for (Link link : listOfLinks) {
          LXPoint[] points = link.edge.points;
          if (!link.fwd) { // Reverse the points list
            List<LXPoint> l = new ArrayList<>(Arrays.asList(points));
            Collections.reverse(l);
            points = l.toArray(new LXPoint[0]);
          }
          for (LXPoint point : points) {
            //int sat = ((i++ % 10) == (phase % 10)) ? 0 : 100;
            int sat = (i++ - phase) % 10 == 0 ? 0 : 100;
            colors[point.index] = LXColor.hsb(hue, sat, 100);
          }
        }
      }
    }
    phase++;
    for (TEVertex v : this.model.vertexesById.values()) {
      int unassignedEdgeCount = 0;
      for (TEEdgeModel e : v.edges) {
        if (!this.modNumsByEdge.containsKey(e)) unassignedEdgeCount++;
      }
      int color;
      if (unassignedEdgeCount == 0) color = LXColor.rgb(50,50,50);
      else if (unassignedEdgeCount % 3 == 0) color = LXColor.rgb(0,150,200);
      else if (unassignedEdgeCount % 3 == 1) color = LXColor.rgb(200,0,0);
      else color = LXColor.rgb(200,180,0);
      v.virtualColor = new TEVirtualColor(color);
    }
  }
}