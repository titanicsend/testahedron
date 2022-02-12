package titanicsend.model;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXVector;
import titanicsend.output.TESacnOutput;

public class TEWholeModel extends LXModel {
  public String name;
  public HashMap<Integer, TEVertex> vertexesById;
  public HashMap<String, TEEdgeModel> edgesById;
  public HashMap<String, TEPanelModel> panelsById;
  public List<TELaserModel> lasers;
  public Set<LXPoint> edgePoints; // Points belonging to edges

  private String subdir;

  private static class Geometry {
    public String subdir;
    public String name;
    public HashMap<Integer, TEVertex> vertexesById;
    public HashMap<String, TEEdgeModel> edgesById;
    public HashMap<String, TEPanelModel> panelsById;
    public List<TELaserModel> lasers;
    public LXModel[] children;
  }

  public TEWholeModel(String subdir) {
    this(loadGeometry(subdir));
  }

  private TEWholeModel(Geometry geometry) {
    super(geometry.children);
    this.name = geometry.name;
    this.vertexesById = geometry.vertexesById;
    this.edgesById = geometry.edgesById;
    this.panelsById = geometry.panelsById;
    this.lasers = geometry.lasers;
    this.edgePoints = new HashSet<LXPoint>();
    for (TEEdgeModel e : this.edgesById.values()) {
      this.edgePoints.addAll(Arrays.asList(e.points));
    }
    reindexPoints();

    LX.log(this.name + " loaded. " +
           this.vertexesById.size() + " vertexes, " +
           this.edgesById.size() + " edges, " +
           this.panelsById.size() + " panels, " +
           this.points.length + " pixels");
  }

  private static Scanner loadFile(String filename) {
    Scanner s;
    try {
      File f = new File(filename);
      return new Scanner(f);
    } catch (FileNotFoundException e) {
      throw new Error(filename + " not found below " + System.getProperty("user.dir"));
    }
  }

  private static void loadVertexes(Geometry geometry) {
    geometry.vertexesById = new HashMap<Integer, TEVertex>();
    Scanner s = loadFile(geometry.subdir + "/vertexes.txt");

    while (s.hasNextLine()) {
      String line = s.nextLine();
      String[] tokens = line.split("\t");
      assert tokens.length == 4 : "Found " + tokens.length + " tokens";
      int id = Integer.parseInt(tokens[0]);
      int x = Integer.parseInt(tokens[1]);
      int y = Integer.parseInt(tokens[2]);
      int z = Integer.parseInt(tokens[3]);
      LXVector vector = new LXVector(x, y, z);
      TEVertex v = new TEVertex(vector, id);
      geometry.vertexesById.put(id, v);
    }
    s.close();
  }

  private static void registerController(TEModel subModel, String config) {
    String[] tokens = tokens = config.split("#");
    assert tokens.length == 2;
    String ipAddress = tokens[0];
    tokens = tokens[1].split(":");
    assert tokens.length == 2;
    int universeNum = Integer.parseInt(tokens[0]);
    int strandOffset = Integer.parseInt(tokens[1]);
    TESacnOutput.registerSubmodel(subModel, ipAddress, universeNum, strandOffset);
  }

  private static void loadEdges(Geometry geometry) {
    geometry.edgesById = new HashMap<String, TEEdgeModel>();
    Scanner s = loadFile(geometry.subdir + "/edges.txt");

    while (s.hasNextLine()) {
      String line = s.nextLine();
      String[] tokens = line.split("\t");
      assert tokens.length == 3 : "Found " + tokens.length + " tokens";

      String id = tokens[0];
      String edgeKind = tokens[1];
      String controller = tokens[2];

      boolean dark;
      switch (edgeKind) {
        case "default":
          dark = false;
          break;
        case "dark":
          dark = true;
          assert controller.equals("uncontrolled");
          break;
        default:
          throw new Error("Weird edge config: " + line);
      }

      tokens = id.split("-");
      if (tokens.length != 2) {
        throw new Error("Found " + tokens.length + " ID tokens");
      }
      int v0Id = Integer.parseInt(tokens[0]);
      int v1Id = Integer.parseInt(tokens[1]);
      assert v0Id < v1Id;
      TEVertex v0 = geometry.vertexesById.get(v0Id);
      TEVertex v1 = geometry.vertexesById.get(v1Id);
      TEEdgeModel e = new TEEdgeModel(v0, v1, dark);
      v0.addEdge(e);
      v1.addEdge(e);

      if (!controller.equals("uncontrolled")) {
        registerController(e, controller);
      }

      geometry.edgesById.put(id, e);
    }
    s.close();
  }

  private static void loadPanels(Geometry geometry) {
    geometry.panelsById = new HashMap<String, TEPanelModel>();
    Scanner s = loadFile(geometry.subdir + "/panels.txt");

    while (s.hasNextLine()) {
      String line = s.nextLine();
      String[] tokens = line.split("\t");
      assert tokens.length == 5 : "Found " + tokens.length + " tokens";

      String id = tokens[0];
      String e0Id = tokens[1];
      String e1Id = tokens[2];
      String e2Id = tokens[3];
      String panelType = tokens[4];

      TEEdgeModel e0 = geometry.edgesById.get(e0Id);
      TEEdgeModel e1 = geometry.edgesById.get(e1Id);
      TEEdgeModel e2 = geometry.edgesById.get(e2Id);

      HashSet<TEVertex> vh = new HashSet<TEVertex>();
      vh.add(e0.v0); vh.add(e0.v1);
      vh.add(e1.v0); vh.add(e1.v1);
      vh.add(e2.v0); vh.add(e2.v1);
      TEVertex[] vertexes = vh.toArray(new TEVertex[0]);
      assert vertexes.length == 3;

      boolean lit = panelType.contains(".");
      String outputConfig = panelType;

      if (lit) panelType = "lit";

      TEPanelModel p = TEPanelFactory.build(id, vertexes[0], vertexes[1], vertexes[2],
              e0, e1, e2, panelType);

      e0.connectedPanels.add(p);
      e1.connectedPanels.add(p);
      e2.connectedPanels.add(p);

      geometry.panelsById.put(id, p);

      if (lit) registerController(p, outputConfig);
    }
    s.close();
  }

  private static void loadGeneral(Geometry geometry) {
    Scanner s = loadFile(geometry.subdir + "/general.txt");

    while (s.hasNextLine()) {
      String line = s.nextLine();
      String[] tokens = line.split(":");
      assert tokens.length == 2 : "Found " + tokens.length + " tokens";
      switch (tokens[0].trim()) {
        case "name":
          geometry.name = tokens[1].trim();
          break;
        default:
          throw new Error("Weird line: " + line);
      }
    }
    s.close();
    assert geometry.name != null : "Model has no name";
  }

  private static Geometry loadGeometry(String subdir) {
    Geometry geometry = new Geometry();
    geometry.subdir = "resources/" + subdir;
    List<LXModel> childList = new ArrayList<LXModel>();

    loadGeneral(geometry);

    loadVertexes(geometry);

    // Vertexes aren't LXPoints (and thus, not LXModels) so they're not children

    // TODO: Store this in a config file
    geometry.lasers = new ArrayList<>();
    LXVector laserAnchor = geometry.vertexesById.get(48);
    if (laserAnchor != null) {
      double laserElevation = -Math.PI / 4.0;  // Shine down at a 45-degree angle
      double laserAzimuth = Math.PI / 2.0;  // ...towards the audience
      TELaserModel laser = new TELaserModel(laserAnchor, laserElevation, laserAzimuth);
      laser.color = LXColor.rgb(255, 0, 0);
      geometry.lasers.add(laser);
      childList.add(laser);
    }

    loadEdges(geometry);

    childList.addAll(geometry.edgesById.values());

    loadPanels(geometry);

    childList.addAll(geometry.panelsById.values());

    geometry.children = childList.toArray(new LXModel[0]);

    return geometry;
  }
}
