package titanicsend.model;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

public class TEVehicleModel extends LXModel {
  public HashMap<Integer, TEVertex> vertexesById;
  public HashMap<String, TEEdgeModel> edgesById;

  private static class Geometry {
    public HashMap<Integer, TEVertex> vertexesById;
    public HashMap<String, TEEdgeModel> edgesById;
    /*public TEPanelModel[] panels;*/
    public LXModel[] children;
  }

  public TEVehicleModel() {
    this(loadGeometry());
  }

  private TEVehicleModel(Geometry geometry) {
    super(geometry.children);
    reindexPoints();
    this.vertexesById = geometry.vertexesById;
    this.edgesById = geometry.edgesById;
    //this.panels = geometry.panels;
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

  private static Geometry loadGeometry() {
    Geometry geometry = new Geometry();
    List<LXModel> childList = new ArrayList<LXModel>();

    loadVertexes(geometry);

    ArrayList<TEVertex> cloud = new ArrayList<TEVertex>(geometry.vertexesById.values());
    childList.add(new TEVertexCloudModel(cloud));

    loadEdges(geometry);

    childList.addAll(geometry.edgesById.values());

    geometry.children = childList.toArray(new LXModel[0]);
    return geometry;
  }
}
