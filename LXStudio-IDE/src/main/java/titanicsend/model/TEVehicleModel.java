package titanicsend.model;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

public class TEVehicleModel extends LXModel {
  public HashMap<Integer, TEVertex> vertexesById;
  public HashMap<String, TEEdgeModel> edgesById;
  public HashMap<String, TEPanelModel> panelsById;
  public Set<LXPoint> edgePoints; // Points belonging to edges

  private static class Geometry {
    public HashMap<Integer, TEVertex> vertexesById;
    public HashMap<String, TEEdgeModel> edgesById;
    public HashMap<String, TEPanelModel> panelsById;
    public LXModel[] children;
  }

  public TEVehicleModel() {
    this(loadGeometry());
  }

  private TEVehicleModel(Geometry geometry) {
    super(geometry.children);
    this.vertexesById = geometry.vertexesById;
    this.edgesById = geometry.edgesById;
    this.panelsById = geometry.panelsById;
    this.edgePoints = new HashSet<LXPoint>();
    for (TEEdgeModel e : this.edgesById.values()) {
      this.edgePoints.addAll(Arrays.asList(e.points));
    }
    reindexPoints();
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
    Scanner s = loadFile("resources/vertexes.txt");

    while (s.hasNextLine()) {
      String line = s.nextLine();
      String[] tokens = line.split("\t");
      assert tokens.length == 5 : "Found " + tokens.length + " tokens";
      int id = Integer.parseInt(tokens[0]);
      int x = Integer.parseInt(tokens[1]);
      int y = Integer.parseInt(tokens[2]);
      int z = Integer.parseInt(tokens[3]);
      LXPoint p = new LXPoint(x, y, z);
      int numConnectedEdges = Integer.parseInt(tokens[4]);
      TEVertex v = new TEVertex(p, id, numConnectedEdges);
      geometry.vertexesById.put(id, v);
    }
    s.close();
  }

  private static void loadEdges(Geometry geometry) {
    geometry.edgesById = new HashMap<String, TEEdgeModel>();
    Scanner s = loadFile("resources/edges.txt");

    while (s.hasNextLine()) {
      String line = s.nextLine();
      String[] tokens = line.split("\t");
      assert tokens.length == 2 : "Found " + tokens.length + " tokens";

      String id = tokens[0];
      int num_connected_panels = Integer.parseInt(tokens[1]);

      tokens = id.split("-");
      if (tokens.length != 2) {
        throw new Error("Found " + tokens.length + " ID tokens");
      }
      int v0Id = Integer.parseInt(tokens[0]);
      int v1Id = Integer.parseInt(tokens[1]);
      TEVertex v0 = geometry.vertexesById.get(v0Id);
      TEVertex v1 = geometry.vertexesById.get(v1Id);
      TEEdgeModel e = new TEEdgeModel(v0, v1, num_connected_panels);
      v0.addEdge(e);
      v1.addEdge(e);

      geometry.edgesById.put(id, e);
    }
    s.close();
  }

  private static void loadPanels(Geometry geometry) {
    geometry.panelsById = new HashMap<String, TEPanelModel>();
    Scanner s = loadFile("resources/panels.txt");

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

      TEPanelModel p = TEPanelFactory.build(vertexes[0], vertexes[1], vertexes[2],
              e0, e1, e2, panelType);
      e0.connectedPanels.add(p);
      e1.connectedPanels.add(p);
      e2.connectedPanels.add(p);

      geometry.panelsById.put(id, p);
    }
    s.close();
  }

  private static Geometry loadGeometry() {
    Geometry geometry = new Geometry();
    List<LXModel> childList = new ArrayList<LXModel>();

    loadVertexes(geometry);

    ArrayList<TEVertex> cloud = new ArrayList<TEVertex>(geometry.vertexesById.values());
    childList.add(new TEVertexCloudModel(cloud));

    loadEdges(geometry);

    childList.addAll(geometry.edgesById.values());

    loadPanels(geometry);

    childList.addAll(geometry.panelsById.values());

    geometry.children = childList.toArray(new LXModel[0]);
    return geometry;
  }
}
